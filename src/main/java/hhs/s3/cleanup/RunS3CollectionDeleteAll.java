package hhs.s3.cleanup;

import java.util.*;

/**
 * Delete all dummy/test collections in S3.  The list of *real* collections hard-coded here.  If a new *real*
 * collection is created, and you do not include it here, all S3 artifacts associated with it will be deleted.
 * Yikes!!
 * 
 * <p>NOTE: to run this, you must have AWS credentials set locally.
 * 
 * @author wjohnson000
 *
 */
public class RunS3CollectionDeleteAll {

    static final String[] realCollections = {
        "unassigned",  // Special collection ... do not remove!

        "MMM9-X78",    // Oxford Given
        "MMM9-FRZ",    // Oxford Surnames
        "MMM9-X7D",    // ROC First and Last
        "MMM9-DFC",    // French Geneanet
        "MMMS-7HK",    // ROC Praenominia
        "MMM3-G4V",    // AAM Trending

        "MMMS-8LV",    // Quizzes
        "MMMQ-BKC",    // Timeline data
        "MMMS-MRM",    // Load Test
        "MMM3-BVY",    // Testing, API development
    };

    static final S3CollectionServices thisCS = new S3CollectionServices();

    public static void main(String...arsg) {
        List<String> collIds = thisCS.getCollectionIds();

        for (String collId : collIds) {
            System.out.println("\n==============================================================");
            boolean isReal = Arrays.stream(realCollections).anyMatch(id -> id.equalsIgnoreCase(collId));

            System.out.println(collId + " >>> IS REAL ? " + isReal);
            if (isReal) {
//                RunListCollectionDetail.showCollectionDetails(thisCS, collId);
            } else {
                System.out.println("Deleting collection: " + collId);
                thisCS.deleteCollection(collId);
            }
        }
    }
}
