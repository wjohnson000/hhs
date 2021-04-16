package hhs.s3.cleanup;

import java.util.List;

/**
 * List all collections in S3.  This is done by listing all FILES on S3 (HHS bucket) and looking for
 * files in the ".../collection" sub-folder.
 * 
 * <p>NOTE: to run this, you must have AWS credentials set locally.
 * 
 * @author wjohnson000
 *
 */
public class RunS3CollectionList {

    public static void main(String...args) {
        S3CollectionServices thisCS = new S3CollectionServices();
        List<String> collectionIds = thisCS.getCollectionIds();

        System.out.println("Collections:\n");
        collectionIds.forEach(System.out::println);

        System.exit(0);
    }
}
