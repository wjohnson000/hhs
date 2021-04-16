package hhs.s3.cleanup;

/**
 * Delete a bucket in S3 associated with a collection identifier.  Be very careful to NOT delete *real* data!
 * 
 * <p>NOTE: to run this, you must have AWS credentials set locally.
 * 
 * @author wjohnson000
 *
 */
public class RunS3CollectionDelete {

    public static void main(String...args) {
        String collectionId = "AAAA-wlj";
        S3CollectionServices thisCS = new S3CollectionServices();

        boolean delOK = thisCS.deleteCollection(collectionId);
        System.out.println("DELETE? " + delOK);

        System.exit(0);
    }
}
