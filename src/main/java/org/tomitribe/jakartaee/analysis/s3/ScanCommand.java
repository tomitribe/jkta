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
package org.tomitribe.jakartaee.analysis.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.PrintOutput;
import org.tomitribe.crest.api.Required;
import org.tomitribe.jakartaee.analysis.s3.Bucket;
import org.tomitribe.jakartaee.analysis.usage.Dir;
import org.tomitribe.jakartaee.analysis.usage.Format;
import org.tomitribe.jakartaee.analysis.usage.UsageCommand;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

public class ScanCommand {

    /**
     * Scan jars in the specified directory and stream the results into
     * the specified Amazon S3 bucket.  Contents will be a gzip compressed
     * TSV file detailing which javax and jakarta packages are used.
     *
     * 
     * @param include
     * @param exclude
     * @param bucket
     * @param region
     * @param dir
     * @throws Exception
     */
    @Command("scan-and-stream")
    public void scanAndStream(@Option("include") Pattern include,
                              @Option("exclude") Pattern exclude,
                              @Option("bucket") @Required final String bucket,
                              @Option("region") @Required final String region,
                              final Dir dir
    ) throws Exception {
        final UsageCommand usage = new UsageCommand();
        final PrintOutput results = usage.dir(Format.tsv, include, exclude, dir);

        final String accessKey = System.getenv("JKTA_ACCESS_KEY");
        final String secretKey = System.getenv("JKTA_SECRET_KEY");

        final AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(region)
                .build();

        final Bucket javax2jakarta = new Bucket(client, bucket);
        final OutputStream entry = javax2jakarta.upload("foo.tsv.gz"); // TODO format the name
        final GZIPOutputStream gzip = new GZIPOutputStream(entry);
        try (final PrintStream out = new PrintStream(gzip)) {
            results.write(out);
        }
    }
}
