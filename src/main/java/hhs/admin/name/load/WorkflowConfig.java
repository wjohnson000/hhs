package hhs.admin.name.load;

/**
 * Configuration for running a "Name" import.
 * @author wjohnson000
 *
 */
public class WorkflowConfig {

    protected static final String DEV_URL  = "http://admin.homelands.service.dev.us-east-1.dev.fslocal.org";
    protected static final String PROD_URL = "http://admin.homelands.service.prod.us-east-1.prod.fslocal.org";

    private boolean  isVerbose    = true;
    private boolean  isProd       = false;
    private String   sessionId    = null;
    private String   importType   = null;
    private String   collectionId = null;
    private String[] filenames    = null;

    public boolean isVerbose() {
        return isVerbose;
    }

    public WorkflowConfig setIsVerbose(boolean isVerbose) {
        this.isVerbose = isVerbose;
        return this;
    }

    public boolean isProd() {
        return isProd;
    }

    public WorkflowConfig setIsProd(boolean isProd) {
        this.isProd = isProd;
        return this;
    }

    public String getSessionId() {
        return sessionId;
    }

    public WorkflowConfig setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    public String getImportType() {
        return importType;
    }

    public WorkflowConfig setImportType(String importType) {
        this.importType = importType;
        return this;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public WorkflowConfig setCollectionId(String collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    public String[] getFilenames() {
        return filenames;
    }

    public WorkflowConfig setFilename(String filename) {
        this.filenames = new String[] { filename };
        return this;
    }

    public WorkflowConfig setFilenames(String[] filenames) {
        this.filenames = filenames;
        return this;
    }

    public String getBaseUrl() {
        return (isProd()) ? PROD_URL : DEV_URL;
    }
}
