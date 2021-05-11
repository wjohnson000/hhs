package hhs.s3.cleanup;

import java.util.List;

/**
 * Delete all buckets in S3 that are tied to "test" collections.  Be very careful to NOT delete *real* data!
 * 
 * <p>NOTE: to run this, you must have AWS credentials set locally.
 * 
 * @author wjohnson000
 *
 */
public class RunS3CollectionDeleteTest {

    public static void main(String...args) {
        S3CollectionServices thisCS = new S3CollectionServices();
        List<String> testCollIds = thisCS.getTestCollectionIds();

        testCollIds.forEach(id -> {
            boolean delOK = thisCS.deleteCollection(id);
            System.out.println("DELETE" + id + "? " + delOK);
        });

        System.exit(0);
    }
}
