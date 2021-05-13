package hhs.admin.name.load;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import org.familysearch.homelands.lib.common.util.JsonUtility;

/**
 * An attempt to fully automate a load of names.  The only pre-requisite is that the file[s] to import
 * must already be uploaded into the "raw" bucket in S3.  Also the collection can be created here, or
 * it can be created outside this process and simply referenced.
 * 
 * <p/>
 * The "verbose" switch determines the level of output created by this process.  The default is "true".
 * 
 * <ul>
 *   <li>Create/Verify a collection</li>
 *   <li>Verify source file</li>
 *   <li>Create an import (for raw-to-canonical)</li>
 *   <li>Start the import step</li>
 *   <li>Copy file to RAW</li>
 *   <li>Create an import (for canonical-load)</li>
 *   <li>Start the import step</li>
 *   <li>Start the variant update step</li>
 * </ul>
 * @author wjohnson000
 *
 */
public abstract class BaseNameWorkflow {

    private WorkflowConfig config;
    private LoadHelper     loadHelper;

    private boolean  importOK      = false;
    private String   collectionId  = null;
    private String   import01Id    = null;
    private String   step01Id      = null;
    private String   step01Status  = null;
    private String   import02Id    = null;
    private String   step02IdA     = null;
    private String   step02StatusA = null;
    private String   step02IdB     = null;
    private String   step02StatusB = null;
    private String[] newFilenames  = null;

    public BaseNameWorkflow(WorkflowConfig config) {
        this.config = config;
        if (config.getCollectionId() != null) {
            this.collectionId = config.getCollectionId();
        }
    }

    public abstract JsonNode buildCollectionNode();

    public void runLoad() {
        run00_setup();
        if (importOK) {
            printStep("Verify Source Files ...");
            run01_verifySourceFiles();
        }
        if (importOK) {
            printStep("Create/Verify Collection ...");
            run02_createCollection();
        }
        if (importOK) {
            printStep("Create Import for raw name files ...");
            run03_createRawImport();
        }
        if (importOK) {
            printStep("Run the 'raw-to-canonical' step ...");
            run04_startRawImport();
        }
        if (importOK) {
            printStep("Copy generated files to 'raw' bucket ...");
            run05_copyCanonicalToRaw();
        }
        if (importOK) {
            printStep("Create Import for loading canonical files ...");
            run06_createCanonicalImport();
        }
        if (importOK) {
            printStep("Run the 'load canonical' step ...");
            run07_startImportNames();
        }
        if (importOK) {
            printStep("Run the 'update variant name' step ...");
            run08_startUpdateVariants();
        }
    }

    void run00_setup() {
        if (config == null) {
            System.out.println("Config is missing.");
        } else if (config.getSessionId() == null) {
            System.out.println("SessionId must be specified");
        } else if (config.getImportType() == null) {
            System.out.println("IportType must be specified");
        } else if (config.getFilenames() == null  ||  config.getFilenames().length == 0) {
            System.out.println("At least one filename must be specified.");
        } else {
            importOK = true;
            loadHelper = new LoadHelper(config.getBaseUrl(), config.getSessionId());
        }
    }

    void run01_verifySourceFiles() {
        JsonNode fileNode = loadHelper.readRawFileList();
        if (fileNode == null) {
            importOK = false;
            System.out.println("Unable to get datafile list ...");
        } else {
            String[] files = JsonUtility.getArrayValue(fileNode, "files");
            if (files == null) {
                importOK = false;
                System.out.println("Unable to get datafile list ...");
            } else {
                System.out.println("Raw file count: " + files.length);
                for (String filename : config.getFilenames()) {
                    boolean found = Arrays.stream(files).anyMatch(file -> file.equalsIgnoreCase(filename));
                    importOK |= found;
                    System.out.println("  Datafile '" + filename + "' found: " + found);
                }
            }
        }
    }

    void run02_createCollection() {
        if (collectionId == null) {
            collectionId = loadHelper.createCollection(buildCollectionNode());
        }

        if (collectionId == null) {
            importOK = false;
            System.out.println("Unable to create collection ...");
        } else {
            JsonNode collectionNode = loadHelper.readCollection(collectionId);
            confirmReadResults("Collection", collectionId, collectionNode);
        }
    }

    void run03_createRawImport() {
        JsonNode importNodeNew = LoadHelper.buildImportJson(config.getImportType(), "RAW_FILE_UPLOADED", collectionId, config.getFilenames());
        import01Id = loadHelper.createImport(collectionId, importNodeNew);

        if (import01Id == null) {
            importOK = false;
            System.out.println("Unable to create first import ...");
        } else {
            JsonNode importNode = loadHelper.readImport(collectionId, import01Id);
            confirmReadResults("Import", import01Id, importNode);
        }
    }

    void run04_startRawImport() {
        step01Id = loadHelper.startStep(collectionId, import01Id, "RawNameToCanonical");
        if (step01Id == null) {
            importOK = false;
            System.out.println("Unable to create or get first step: RawNameToCanonical");
        } else {
            System.out.println("First step started: " + step01Id);
            step01Status = waitForStep(import01Id, step01Id, 300_000L);
            System.out.println("First step ended: " + step01Status);
        }
    }

    void run05_copyCanonicalToRaw() {
        String[] files;
        List<String> tempFilenames = new ArrayList<>();

        JsonNode stepNode = loadHelper.readImportStep(collectionId, import01Id, step01Id);
        files = JsonUtility.getArrayValue(stepNode, "files");
        for (String filename : files) {
            if (filename.endsWith(".csv")) {
                String newFilename = "step-" + step01Id + "-" + filename;
                System.out.println("NEW: " + newFilename);
                loadHelper.copyGeneratedFileToRaw(collectionId, import01Id, step01Id, filename, newFilename);
                tempFilenames.add(newFilename);
            }
        }

        JsonNode fileNode = loadHelper.readRawFileList();
        if (fileNode == null) {
            importOK = false;
            System.out.println("Unable to get datafile list for new filename ...");
        } else {
            files = JsonUtility.getArrayValue(fileNode, "files");
            if (files == null) {
                importOK = false;
                System.out.println("Unable to get datafile list for newfilename ...");
            } else {
                System.out.println("Raw file count: " + files.length);
                for (String filename : tempFilenames) {
                    boolean found = Arrays.stream(files).anyMatch(file -> file.equalsIgnoreCase(filename));
                    importOK |= found;
                    System.out.println("  Datafile '" + filename + "' found: " + found);
                }
            }
        }

        newFilenames = tempFilenames.toArray(new String[tempFilenames.size()]);
    }

    void run06_createCanonicalImport() {
        JsonNode importNodeNew = LoadHelper.buildImportJson(config.getImportType(), "CANONICAL_FILE_UPLOADED", collectionId, newFilenames);
        import02Id = loadHelper.createImport(collectionId, importNodeNew);

        if (import02Id == null) {
            importOK = false;
            System.out.println("Unable to create or get second import ...");
        } else {
            JsonNode importNode = loadHelper.readImport(collectionId, import02Id);
            confirmReadResults("Import", import02Id, importNode);
        }
    }

    void run07_startImportNames() {
        step02IdA = loadHelper.startStep(collectionId, import02Id, "ImportNames");
        if (step02IdA == null) {
            importOK = false;
            System.out.println("Unable to create or get second step [part A]: ImportNames");
        } else {
            System.out.println("Second step [part A] started: " + step02IdA);
            step02StatusA = waitForStep(import02Id, step02IdA, 1800_000L);
            System.out.println("Second step [part A] ended: " + step02StatusA);
        }
    }

    void run08_startUpdateVariants() {
        step02IdB = loadHelper.startStep(collectionId, import02Id, "UpdateNameVariants");
        if (step02IdB == null) {
            importOK = false;
            System.out.println("Unable to create or get second step [part B]: UpdateNameVariants");
        } else {
            System.out.println("Second step [part B] started: " + step02IdB);
            step02StatusB = waitForStep(import02Id, step02IdB, 3600_000L);
            System.out.println("Second step [part B] ended: " + step02StatusB);
        }
    }

    void printStep(String label) {
        System.out.println("");
        System.out.println("==============================================================================================");
        System.out.println("Start of step: '" + label + "'");
        System.out.println("==============================================================================================");
    }

    /**
     * Confirm whether the target object (collection, import, step, ...) was successfully read or not.
     * 
     * @param label a value to display in the output
     * @param id the identifier of object
     * @param node the {@link JsonNode} value returned
     */
    void confirmReadResults(String label, String id, JsonNode node) {
        if (node == null) {
            importOK = false;
            System.out.println(label + " not found: " + id);
        } else {
            System.out.println(label + " found: " + id);
            if (config.isVerbose()) {
                System.out.println(node.toPrettyString());
            }
        }
    }

    /**
     * Wait for a step to finish.  Wait as long as the status is null or "PENDING" or "RUNNING".  If the status
     * is either "SUCCESS" or "PARTIAL", it ran successfully.  Otherwise it failed.
     * 
     * @param importId import ID
     * @param stepId step ID
     * @param waitTimeMillis maximum wait time in milliseconds
     * @return
     */
    String waitForStep(String importId, String stepId, long waitTimeMillis) {
        String  status = "unknown";
        boolean done   = false;
        long startTime = System.currentTimeMillis();

        while (!done  &&  ((System.currentTimeMillis() - startTime) < waitTimeMillis)) {
            JsonNode stepNode = loadHelper.readImportStep(collectionId, importId, stepId);
            confirmReadResults("Step", stepId, stepNode);

            if (stepNode == null) {
                done = true;
            } else {
                status = JsonUtility.getStringValue(stepNode, "status");
                if (status == null  ||  "RUNNING".equals(status)  ||  "PENDING".equals(status)) {
                    try { Thread.sleep(2500L); } catch(Exception ex) { }
                } else if ("SUCCESS".equals(status)  ||  "PARTIAL".equals(status)) {
                    done = true;
                } else {
                    done = true;
                    importOK = false;
                }
            }
        }

        return status;
    }
}
