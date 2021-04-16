/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin.ui.helper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import std.wlj.hhs.admin.ui.model.*;

/**
 * @author wjohnson000
 *
 */
public class S3Helper {

    private static final String bucketName = "ps-services-us-east-1-074150922133-homelands-admin";

    private AmazonS3 s3Client;

    public S3Helper() {
        s3Client = AmazonS3ClientBuilder.defaultClient();
    }

    public List<FolderNode> getDetails() {
        ListObjectsRequest ListRequest =
                new ListObjectsRequest()
                        .withBucketName(bucketName)
                        .withPrefix("collection")
                        .withMaxKeys(1000);

        boolean hasMore = true;
        List<String> folderDetails = new ArrayList<>();

        ObjectListing objListing = s3Client.listObjects(ListRequest);
        while (hasMore) {
            for (S3ObjectSummary fileSummary : objListing.getObjectSummaries()) {
                System.out.println(fileSummary.getKey());
                folderDetails.add(fileSummary.getKey());
            }

            if (objListing.getNextMarker() == null  ||  objListing.getObjectSummaries().isEmpty()) {
                hasMore = false;
            } else {
                ListRequest.setMarker(objListing.getNextMarker());
                objListing = s3Client.listObjects(ListRequest);
            }
        }

        return getDetails(folderDetails);
    }

    public List<FolderNode> getDetailsSaved() {
        List<String> folderDetails = new ArrayList<>();

        try {
            List<String> s3Files = IOUtils.readLines(this.getClass().getResourceAsStream("/hhs-s3-files.txt"), StandardCharsets.UTF_8);
            folderDetails.addAll(s3Files);
        } catch (IOException ex) {
            System.out.println("Unable to read files ... " + ex.getMessage());
        }

        return getDetails(folderDetails);
    }

    List<FolderNode> getDetails(List<String> s3Files) {
        List<FolderNode> folderDetails = new ArrayList<>();

        FolderNode collNode   = null;
        FolderNode importNode = null;
        FolderNode stepNode   = null;

        System.out.println("FILES: " + s3Files.size());
        for (String s3File : s3Files) {
            String[] chunks = s3File.split("/");
            if (chunks[0].equals("collection")) {
                // Create the collection, if necessary
                if (chunks.length > 1) {
                    if (collNode == null  ||  ! collNode.getId().equalsIgnoreCase(chunks[1])) {
                        collNode = new FolderNode(FolderType.COLLECTION, chunks[1], "");
                        folderDetails.add(collNode);
                    }
                }

                // Create the import, if necessary
                if (chunks.length > 2) {
                    if (importNode == null  ||  ! importNode.getId().equalsIgnoreCase(chunks[2])) {
                        importNode = new FolderNode(FolderType.IMPORT, chunks[2], "");
                        collNode.addChild(importNode);
                    }
                }

                // Create the step, if necessary
                if (chunks.length > 3) {
                    if (stepNode == null  ||  ! stepNode.getId().equalsIgnoreCase(chunks[3])) {
                        stepNode = new FolderNode(FolderType.STEP, chunks[3], "");
                        importNode.addChild(stepNode);
                    }
                }

                if (chunks.length > 4) {
                    FolderNode fileNode = new FolderNode(FolderType.FILE, chunks[4], s3File);
                    stepNode.addChild(fileNode);
                }
            }

        }

        return folderDetails;
    }
}
