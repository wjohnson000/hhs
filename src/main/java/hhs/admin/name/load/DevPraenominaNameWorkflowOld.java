/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.admin.name.load;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;
import org.familysearch.homelands.lib.common.util.JsonUtility;

/**
 * An attempt to fully automate a ROC name load, first on DEV then on PROD (?).  The steps involved are:
 * <ul>
 *   <li>Create a collection -- here or in Postman?</li>
 *   <li>Update source file -- done in Postman</li>
 *   <li>Create an import -- show results</li>
 *   <li>Start the import -- show results</li>
 *   <li>Copy file to RAW</li>
 *   <li>Create an import -- show results</li>
 *   <li>Start the import -- show results</li>
 *   <li>Start the variant update -- show results</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class DevPraenominaNameWorkflowOld {

    protected static final String DEV_URL    = "http://admin.homelands.service.dev.us-east-1.dev.fslocal.org";
    protected static final String SESSION_ID = "44498db2-80c1-40c4-a6e8-f59861635685-integ";

    protected static LoadHelper loadHelper;

    // Generated and/or pre-set values
    protected static boolean verbose        = true;
    protected static boolean importOK       = true;
    protected static String  datafileName   = "Names from Praenomin reviewed.xlsx";
    protected static String  newFilename    = "step-2147-Names from Praenomin reviewed.xlsx.csv";

    protected static String collectionId    = "MMM7-3VY";
    protected static String import01Id      = "1624";
    protected static String step01Id        = "2147";
    protected static String step01Status    = "SUCCESS";
    protected static String import02Id      = "1626";
    protected static String step02IdA       = "2149";
    protected static String step02StatusA   = "SUCCESS";
    protected static String step02IdB       = "2150";
    protected static String step02StatusB   = null;

    public static void main(String ...args) {
        run00_setup();
        run01_createCollection();
        run02_verifySourceFile();
        run03_createRawImport();
        run04_startRawImport();
        run05_copyCanonicalToRaw();
        run06_createCanonicalImport();
        run07_startImportNames();
        run08_startUpdateVariants();
    }

    static void run00_setup() {
        loadHelper = new LoadHelper(DEV_URL, SESSION_ID);
    }

    static void run01_createCollection() {
        if (collectionId == null) {
            JsonNode collNode = loadHelper.buildCollectionJson("ROC Praenomina Names", "Praenomina names from the ROC missionaries", "en", null, 10, "ROC Missionaries", "Family Search", "LEASE");
            collectionId = loadHelper.createCollection(collNode);
        }

        if (collectionId == null) {
            importOK = false;
            System.out.println("Unable to create or get collection ...");
        } else {
            JsonNode collectionNode = loadHelper.readCollection(collectionId);
            System.out.println("Collection found:" + (collectionNode != null));
            if (verbose) {
                System.out.println("   " + collectionNode);
            }
        }
    }

    static void run02_verifySourceFile() {
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
                importOK |= Arrays.stream(files).anyMatch(file -> file.equalsIgnoreCase(datafileName));
                System.out.println("Datafile found: " + importOK);
            }
        }
    }

    static void run03_createRawImport() {
        if (! importOK) {
            return;
        }

        if (import01Id == null) {
            JsonNode importNode = loadHelper.buildImportJson("ROC_NAME_ENGLISH", "RAW_FILE_UPLOADED", collectionId, datafileName);
            import01Id = loadHelper.createImport(collectionId, importNode);
        }

        if (import01Id == null) {
            importOK = false;
            System.out.println("Unable to create or get first import ...");
        } else {
            JsonNode importNode = loadHelper.readImport(collectionId, import01Id);
            System.out.println("Import found:" + (importNode != null));
            if (verbose) {
                System.out.println("   " + importNode);
            }
        }
    }

    static void run04_startRawImport() {
        if (! importOK) {
            return;
        }

        if (step01Id == null) {
            step01Id = loadHelper.startStep(collectionId, import01Id, "RawNameToCanonical");
            System.out.println("Step01 started: " + step01Id);
        }

        if (step01Id == null) {
            importOK = false;
            System.out.println("Unable to create or get first step: RawNameToCanonical");
        } else {
            int     count = 0;
            boolean done  = false;
            while (!done  &&  count < 12) {
                count++;
                JsonNode stepNode = loadHelper.readImportStep(collectionId, import01Id, step01Id);
                if (stepNode == null) {
                    importOK = false;
                    done = true;
                } else {
                    System.out.println("Step found:" + (stepNode != null));
                    if (verbose) {
                        System.out.println("   " + stepNode);
                    }
                    step01Status = JsonUtility.getStringValue(stepNode, "status");
                    String completeDate = JsonUtility.getStringValue(stepNode, "completeDate");
                    if (completeDate != null) {
                        done = true;
                        if (! ("SUCCESS".equals(step01Status) || "PARTIAL".equals(step01Status))) {
                            importOK = false;
                        }
                    } else {
                        try { Thread.sleep(2500L); } catch(Exception ex) { }
                    }
                }
            }
        }
    }

    static void run05_copyCanonicalToRaw() {
        if (! importOK) {
            return;
        }

        if (newFilename == null) {
            JsonNode stepNode = loadHelper.readImportStep(collectionId, import01Id, step01Id);
            String[] files = JsonUtility.getArrayValue(stepNode, "files");
            for (String file : files) {
                if (file.endsWith(".csv")) {
                    newFilename = "step-" + step01Id + "-" + file;
                    System.out.println("NEW: " + newFilename);
                    loadHelper.copyGeneratedFileToRaw(collectionId, import01Id, step01Id, file, newFilename);
                    break;
                }
            }
        }

        JsonNode fileNode = loadHelper.readRawFileList();
        if (fileNode == null) {
            importOK = false;
            System.out.println("Unable to get datafile list for new filename ...");
        } else {
            String[] files = JsonUtility.getArrayValue(fileNode, "files");
            if (files == null) {
                importOK = false;
                System.out.println("Unable to get datafile list for newfilename ...");
            } else {
                importOK |= Arrays.stream(files).anyMatch(file -> file.equalsIgnoreCase(newFilename));
                System.out.println("New datafile found: " + importOK);
            }
        }
    }

    static void run06_createCanonicalImport() {
        if (! importOK) {
            return;
        }

        if (import02Id == null) {
            JsonNode importNode = loadHelper.buildImportJson("ROC_NAME_ENGLISH", "CANONICAL_FILE_UPLOADED", collectionId, newFilename);
            import02Id = loadHelper.createImport(collectionId, importNode);
        }

        if (import02Id == null) {
            importOK = false;
            System.out.println("Unable to create or get second import ...");
        } else {
            JsonNode importNode = loadHelper.readImport(collectionId, import02Id);
            System.out.println("Import found:" + (importNode != null));
            if (verbose) {
                System.out.println("   " + importNode);
            }
        }
    }

    static void run07_startImportNames() {
        if (! importOK) {
            return;
        }

        if (step02IdA == null) {
            step02IdA = loadHelper.startStep(collectionId, import02Id, "ImportNames");
            System.out.println("Step02 started: " + step02IdA);
        }

        if (step02IdA == null) {
            importOK = false;
            System.out.println("Unable to create or get second step: ImportNames");
        } else {
            int     count = 0;
            boolean done  = false;
            while (!done  &&  count < 100) {
                count++;
                JsonNode stepNode = loadHelper.readImportStep(collectionId, import02Id, step02IdA);
                if (stepNode == null) {
                    importOK = false;
                    done = true;
                } else {
                    System.out.println("Step found:" + (stepNode != null));
                    if (verbose) {
                        System.out.println("   " + stepNode);
                    }
                    step02StatusA = JsonUtility.getStringValue(stepNode, "status");
                    String completeDate = JsonUtility.getStringValue(stepNode, "completeDate");
                    if (completeDate != null) {
                        done = true;
                        if (! ("SUCCESS".equals(step02StatusA) || "PARTIAL".equals(step02StatusA))) {
                            importOK = false;
                        }
                    } else {
                        try { Thread.sleep(2500L); } catch(Exception ex) { }
                    }
                }
            }
        }
    }

    static void run08_startUpdateVariants() {
        if (! importOK) {
            return;
        }

        if (step02IdB == null) {
            step02IdB = loadHelper.startStep(collectionId, import02Id, "UpdateNameVariants");
            System.out.println("Step02 started: " + step02IdB);
        }

        if (step02IdB == null) {
            importOK = false;
            System.out.println("Unable to create or get second step: UpdateNameVariants");
        } else {
            int     count = 0;
            boolean done  = false;
            while (!done  &&  count < 100) {
                count++;
                JsonNode stepNode = loadHelper.readImportStep(collectionId, import02Id, step02IdB);
                if (stepNode == null) {
                    importOK = false;
                    done = true;
                } else {
                    System.out.println("Step found:" + (stepNode != null));
                    if (verbose) {
                        System.out.println("   " + stepNode);
                    }
                    step02StatusB = JsonUtility.getStringValue(stepNode, "status");
                    String completeDate = JsonUtility.getStringValue(stepNode, "completeDate");
                    if (completeDate != null) {
                        done = true;
                        if (! ("SUCCESS".equals(step02StatusB) || "PARTIAL".equals(step02StatusB))) {
                            importOK = false;
                        }
                    } else {
                        try { Thread.sleep(2500L); } catch(Exception ex) { }
                    }
                }
            }
        }
    }
}
