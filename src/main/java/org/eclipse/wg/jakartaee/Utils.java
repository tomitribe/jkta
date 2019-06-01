package org.eclipse.wg.jakartaee;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Utils {
    private Utils() {
    }

    public static ClassOrInterfaceDeclaration getClazz(final CompilationUnit unit) {
        return (ClassOrInterfaceDeclaration) unit.getTypes().stream()
                .filter(typeDeclaration -> typeDeclaration instanceof ClassOrInterfaceDeclaration)
                .findFirst().orElse(null);
    }

    // TODO move to an annotations static class
    public static Map<String, MemberValuePair> pairs(final NormalAnnotationExpr annotation) {
        final Map<String, MemberValuePair> map = new LinkedHashMap<>();
        for (final MemberValuePair pair : annotation.getPairs()) {
            map.put(pair.getNameAsString(), pair);
        }
        return map;
    }

    // TODO move static methods to a Methods static class
    public static boolean isResourceMethod(final MethodDeclaration method) {
        return httpMethod(method) != null;
    }

    public static boolean isGET(final MethodDeclaration method) {
        return "GET".equals(httpMethod(method));
    }


    public static boolean isPUT(final MethodDeclaration method) {
        return "PUT".equals(httpMethod(method));
    }


    public static boolean isPOST(final MethodDeclaration method) {
        return "POST".equals(httpMethod(method));
    }


    public static boolean isDELETE(final MethodDeclaration method) {
        return "DELETE".equals(httpMethod(method));
    }


    public static boolean isHEAD(final MethodDeclaration method) {
        return "HEAD".equals(httpMethod(method));
    }


    public static boolean isOPTIONS(final MethodDeclaration method) {
        return "OPTIONS".equals(httpMethod(method));
    }


    public static boolean isPATCH(final MethodDeclaration method) {
        return "PATCH".equals(httpMethod(method));
    }

    public static String httpMethod(final MethodDeclaration method) {
        final List<String> methods = Arrays.asList("POST", "PUT", "GET", "DELETE", "OPTIONS", "HEAD", "PATCH");
        for (final String m : methods) {
            if (method.getAnnotationByName(m).isPresent()) return m;
        }
        return null;
    }

    public static NormalAnnotationExpr getApiOperation(final MethodDeclaration method) {
        return getAnnotation(method, "ApiOperation");
    }

    public static NormalAnnotationExpr getApi(final MethodDeclaration method) {
        return getAnnotation(method, "Api");
    }

    public static NormalAnnotationExpr getApiParam(final MethodDeclaration method) {
        return getAnnotation(method, "ApiParam");
    }

    public static NormalAnnotationExpr getAnnotation(final NodeWithAnnotations node, final String annotationName) {
        final Optional<AnnotationExpr> annotationByName = node.getAnnotationByName(annotationName);
        return (NormalAnnotationExpr) annotationByName.orElse(null);
    }

    public static Function<MethodDeclaration, NormalAnnotationExpr> getAnnotation(final String annotationName) {
        return method -> getAnnotation(method, annotationName);
    }

    // TODO move to a Nodes static class
    public static <N extends Node> void sortNodes(final Supplier<NodeList<N>> listSupplier, final Function<N, String> classifier, final String... patterns) {
        sortNodes(listSupplier.get(), classifier, patterns);
    }

    public static <N extends Node> void sortNodes(final NodeList<N> ns, final Function<N, String> classifier, final String... patterns) {
        ns.sort(Comparator.comparing(annotation -> sort(classifier.apply(annotation),
                patterns
        )));
    }

    public static int sort(final String name, final String... patterns) {
        for (int i = 0; i < patterns.length; i++) {
            if (name.matches(patterns[i])) return i;
        }
        return patterns.length + 1;
    }

    private static CompilationUnit getCompilationUnit(final Node node) {
        if (node instanceof CompilationUnit) {
            return (CompilationUnit) node;
        }
        return getCompilationUnit(node.findRootNode());
    }

    public static Predicate<NormalAnnotationExpr> has(final String member, final String value) {
        return normalAnnotationExpr -> has(normalAnnotationExpr, member, value);
    }

    public static boolean has(NormalAnnotationExpr normalAnnotationExpr, final String member, final String value) {
        if (normalAnnotationExpr == null) return false;
        final Map<String, MemberValuePair> pairs = Utils.pairs(normalAnnotationExpr);
        final MemberValuePair code = pairs.get(member);
        return code != null && value.equals(code.getValue().toString());
    }

    public static NodeList<NormalAnnotationExpr> arrayValue(Expression expression) {
        final NodeList<NormalAnnotationExpr> annotations = new NodeList<>();
        if (expression instanceof ArrayInitializerExpr) {
            final ArrayInitializerExpr arrayInitializerExpr = (ArrayInitializerExpr) expression;
            for (final Expression exp2 : arrayInitializerExpr.getValues()) {
                annotations.add((NormalAnnotationExpr) exp2);
            }
        } else if (expression instanceof NormalAnnotationExpr) {
            annotations.add((NormalAnnotationExpr) expression);
        } else {
            throw new IllegalStateException("Unsupported Expression " + expression.getClass().getName());
        }
        return annotations;
    }

    public static Predicate<? super MethodDeclaration> hasApiOperation(final String code, final String value) {
        return methodDeclaration -> {
            final NormalAnnotationExpr apiOperation = getAnnotation(methodDeclaration, "ApiOperation");
            return has(apiOperation, code, value);
        };
    }

    public static void removeApiResponse(final MethodDeclaration method, final int code) {
        final NormalAnnotationExpr apiResponses = getAnnotation(method, "ApiResponses");
        if (apiResponses == null) return;

        final MemberValuePair value = pairs(apiResponses).get("value");
        if (value == null) return;

        final NodeList<NormalAnnotationExpr> annotations = arrayValue(value.getValue());

        final NormalAnnotationExpr match = annotations.stream()
                .filter(has("code", "" + code))
                .findFirst().orElse(null);

        // nothing to remove
        if (match == null) return;

        // ok, we do have an ApiResponse to remove, let's get started

        // There's more than one ApiResponse, just return the one we don't want
        if (annotations.size() > 1) {
            value.remove(match);
            return;
        }

        // The only ApiResponse is the one we're removing, so remove the 'value' entirely
        apiResponses.remove(value);

        // If the wrapper ApiResponses doesn't have any fields anymore, remove it too
        if (apiResponses.getPairs().size() == 0) {
            method.getAnnotations().remove(apiResponses);
        }
    }

    public static boolean hasPathParameter(final NodeWithAnnotations node) {
        final String v = getPath(node);
        return v != null && v.matches(".*\\{[^}]+\\}.*");
    }

    public static String getPath(NodeWithAnnotations node) {
        final NormalAnnotationExpr path = getAnnotation(node, "Path");
        if (path == null) return null;

        final MemberValuePair value = pairs(path).get("value");
        return (value != null) ? value.getValue().toString() : null;
    }
}
