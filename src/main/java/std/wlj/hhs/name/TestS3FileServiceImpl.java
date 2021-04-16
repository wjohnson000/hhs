/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.familysearch.homelands.admin.client.HasS3Client;
import org.familysearch.homelands.admin.client.impl.S3FileServiceImpl;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author wjohnson000
 *
 */
public class TestS3FileServiceImpl {

    static final String BUCKET = "ps-services-us-east-1-074150922133-homelands-admin";

    public static void main(String...args) {
        S3Client s3Client = S3Client.builder()
                                .region(Region.US_EAST_1)
                                .credentialsProvider(DefaultCredentialsProvider.create())
                                .build();
        ForkJoinPool fjp = new ForkJoinPool(10);
        HasS3Client  hasS3Client =new HasS3Client(s3Client, BUCKET);
        S3FileServiceImpl fileSvc = new S3FileServiceImpl(hasS3Client, fjp);

        List<String> files = fileSvc.listFiles();
        List<String> folders = fileSvc.listFolders();
        System.out.println("FILES: " + files);
        System.out.println("FOLDERS: " + folders);

        List<String> import01Folders = fileSvc.listFolders("collection/MMM9-DFC/25/");
        List<String> import01Files = fileSvc.listFiles("collection/MMM9-DFC/25/");
        System.out.println("FILES: " + import01Files);
        System.out.println("FOLDERS: " + import01Folders);

        List<String> import01FoldersX = fileSvc.listFolders("collection/MMM9-DFC/25/staged-json");
        List<String> import01FilesX = fileSvc.listFiles("collection/MMM9-DFC/25/staged-json");
        System.out.println("FILES: " + import01FilesX);
        System.out.println("FOLDERS: " + import01FoldersX);
    }
}
