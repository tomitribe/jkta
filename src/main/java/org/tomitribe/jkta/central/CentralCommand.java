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
package org.tomitribe.jkta.central;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.Out;
import org.tomitribe.crest.api.PrintOutput;
import org.tomitribe.crest.api.Required;
import org.tomitribe.jkta.usage.Dir;
import org.tomitribe.jkta.usage.Format;
import org.tomitribe.jkta.usage.UsageCommand;
import org.tomitribe.jkta.util.Paths;
import org.tomitribe.jkta.Version;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

@Command("central")
public class CentralCommand {

    /**
     * Scan jars in the specified directory and stream the results into
     * the specified Amazon S3 bucket.
     *
     * SCAN
     *
     * The specified directory is walked recursively and all archives matching
     * the --include and --exclude pattern are read and scanned for references
     * to the affected `javax` and `jakarta` namespaces.
     *
     * It's very important the paths in the resulting report contain the full
     * groupId, artifactId and version.  Either of the following two methods
     * will work to ensure the paths are complete and start with the groupId:
     *
     *   - Change to the root of the maven repository before executing the
     *     command, ensuring `$PWD` evaluates to the base of the repository itself.
     *   - Use the --repository option to specify the location of the repository
     *     on the system.
     *
     * STREAM
     *
     * At the start of a scan an entry is made in the Amazon S3 bucket identified
     * via the --region and --bucket options.
     *
     * During the scan the tsv results are streamed directly into the S3 entry
     * without any buffering on the the disk.  This allows a scan to run several
     * hours or days without concerns of either running out of disk or losing
     * hours of work.  Roughly 5MB is buffered in memory at a time before being
     * flushed to the S3 entry, thus several dozen or hundreds of scan processes
     * can be run on the same machine without memory concerns.
     *
     * The entry name of the scan is both date-stamped and includes a random suffix
     * to avoid collisions when doing parallel processing.  For example a scan at
     * May 7, 2020 4:11 PM:
     *
     *      scan-2020_05_07_16_11-a6f0usy-0.4.tsv.gz
     *
     * The suffix on the end is a Base32 encoded 32-bit hash of random data plus
     * the version number of the jkta binary.  For example a scan kicked off in
     * parallel also at May 7, 2020 4:11 PM may look as follows:
     *
     *      scan-2020_05_07_16_11-reqsd6a-0.4.tsv.gz
     *
     * The S3 entry name is printed to the console output at the start of the scan.
     *
     * The use of a version number in the file name is intended to help identify
     * any scans that might need to be rerun due to updates in the scanning tool
     * as well as bring transparency to ensure the scanning tool is being updated.
     *
     * ENVIRONMENT
     *
     * Credentials to access the Amazon S3 bucket can be specified using the following
     * two environment variables.
     *
     *  - `JKTA_ACCESS_KEY` the AWS Access Key ID tied to the user in AWS AIM.  If unspecified
     *    the command will immediately terminate with status code 21.
     *
     *  - `JKTA_SECRET_KEY` the AWS Secret Access Key corresponding to the Access Key ID.  If
     *    unspecified the command will immediately terminate with status code 22.
     *
     * @param include A Java regular expression indicating which files should be
     *                scanned in the specified directory.  The --include is applied
     *                before the --exclude pattern allowing the include to serve
     *                as a course-grained select.
     * @param exclude A Java regular expression indicating which files should be
     *                excluded from the scan.  The --exclude is applied after the --include
     *                allowing the exclude to further refine any files matched.
     * @param bucket The AWS S3 bucket where the scan tsv.gz files will be uploaded
     * @param region The AWS region where the S3 bucket lives.  S3 bucket names
     *               are unique per region
     * @param dir The directory inside the local maven repository that should be recursively
     *           walked and all matching artifacts scanned.
     * @param repository The path to the local maven repository itself.  Used to ensure
     *                   only the path starting at the groupId is reported in the tsv.
     *                   Defaults to the current working directory.
     */
    @Command("scan-and-stream")
    public void scanAndStream(@Out PrintStream stdout,
                              @Option("include") Pattern include,
                              @Option("exclude") Pattern exclude,
                              @Option("bucket") @Required final String bucket,
                              @Option("region") @Required final Regions region,
                              @Option("repository") @Default("${user.dir}") Dir repository,
                              final Dir dir
    ) throws Exception {
        final UsageCommand usage = new UsageCommand();
        final PrintOutput results = usage.dir(Format.tsv, include, exclude, repository, dir);

        final String accessKey = System.getenv("JKTA_ACCESS_KEY");
        final String secretKey = System.getenv("JKTA_SECRET_KEY");

        if (accessKey == null) throw new AccessKeyNotSpecified();
        if (secretKey == null) throw new SecretKeyNotSpecified();

        final AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withRegion(region)
                .build();

        final String date = new SimpleDateFormat("yyyy_MM_dd_HH_mm").format(new Date());

        final String keyName = String.format("scan-%s-%s-%s.tsv.gz", date, Id.generate().get(), Version.VERSION);
        stdout.printf("Scanning '%s' to %s %s/%s%n", Paths.childPath(repository.dir(), dir.dir()), region, bucket, keyName);
        final Bucket javax2jakarta = new Bucket(client, bucket);
        final OutputStream entry = javax2jakarta.upload(keyName);
        final GZIPOutputStream gzip = new GZIPOutputStream(entry);
        try (final PrintStream out = new PrintStream(gzip)) {
            results.write(out);
        }
    }
}
