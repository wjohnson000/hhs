package hhs.s3.cleanup;

/**
 * Based on a list of all *real* collections, dump the S3 details
 * 
 * <p>NOTE: to run this, you must have AWS credentials set locally.
 * 
 * @author wjohnson000
 *
 */
public class RunS3CollectionDetails {

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
        "MMMQ-171",    // ROC Names, First and Last
        "MMMS-MRM",    // Load Test
        "MMM3-BVY",    // Testing, API development
    };

    static final S3CollectionServices thisCS = new S3CollectionServices();

    public static void main(String...arsg) {
        for (String collId : realCollections) {
            System.out.println("\n==============================================================================");
            S3EntryFormatter.showCollectionDetails(thisCS, collId);
       }
    }
}
