/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.List;

import org.familysearch.homelands.admin.client.HasS3Client;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * @author wjohnson000
 *
 */
public class TestHasS3Client {

    static final String BUCKET = "ps-services-us-east-1-074150922133-homelands-admin";

    public static void main(String...args) {
        S3Client s3Client = S3Client.builder()
                                .region(Region.US_EAST_1)
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .build();
        HasS3Client  hasS3Client = new HasS3Client(s3Client, BUCKET);

        List<S3Object> s3Objs = hasS3Client.getS3Keys();
        for (S3Object s3Obj : s3Objs) {
            System.out.println("KK: " + s3Obj.key());
        }
     }
}
