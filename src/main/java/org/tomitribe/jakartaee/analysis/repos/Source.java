/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.tomitribe.jakartaee.analysis.repos;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Name;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Source {

    private final String packageName;
    private final String className;
    private final File file;
    private final List<String> imports;

    public Source(final String packageName, final String className, final File file, final List<String> imports) {
        this.packageName = packageName;
        this.className = className;
        this.file = file;
        this.imports = imports;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public File getFile() {
        return file;
    }

    public List<String> getImports() {
        return imports;
    }

    public static Source parse(final File file) {

        try {
            final String code = slurp(file);

            final CompilationUnit unit = parse(code);

            final List<String> imports = unit.getImports().stream()
                    .map(ImportDeclaration::getName)
                    .map(Name::asString)
                    .filter(Paths::isNonStandard)
                    .collect(Collectors.toList());

            final String packageName = unit.getPackageDeclaration()
                    .map(packageDeclaration -> packageDeclaration.getName().asString())
                    .orElse("");

            final String className = packageName + "." + (file.getName().replace(".java", ""));


            return new Source(packageName, className, file, imports);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot parse: " + file.getAbsolutePath(), e);
        }
    }

    public static CompilationUnit parse(String code) {
        final ParserConfiguration configuration = new ParserConfiguration();
        configuration.setPreprocessUnicodeEscapes(true);
        final JavaParser javaParser = new JavaParser(configuration);
        return (CompilationUnit) handleResult(javaParser.parse(code));
    }

    private static <T extends Node> T handleResult(ParseResult<T> result) {
        if (result.isSuccessful()) {
            return (T) result.getResult().get();
        } else {
            throw new ParseProblemException(result.getProblems());
        }
    }

    public static String slurp(final File file) {
        try {
            return IO.slurp(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        final String s = "Source{" +
                "packageName='" + packageName + '\'' +
                ", className='" + className + '\'' +
                ", file=" + file.getAbsolutePath() +
                '}';

        if (imports.size() > 0) {
            return s + "\n   " + Join.join("\n   ", imports);
        }
        return s;
    }

    public boolean isJavax() {
        return packageName.startsWith("javax.");
    }
}
