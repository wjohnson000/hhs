package hhs.s3.cleanup;

/**
 * Simple model class that represents an entry from S3: the file name, the path to the file, the file size.
 * 
 * @author wjohnson000
 *
 */
public class S3File {

    public String   name;
    public String[] path;
    public long     size;

}
