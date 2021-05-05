package hhs.admin.name.load;

import com.fasterxml.jackson.databind.JsonNode;

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
public class NameWorkflowPraenomina extends BaseNameWorkflow {

    public NameWorkflowPraenomina(WorkflowConfig config) {
        super(config);
    }

    public static void main(String...args) {
        WorkflowConfig config = new WorkflowConfig()
                                      .setIsVerbose(true)
                                      .setIsProd(false)
                                      .setSessionId("xxxxx-integ")
                                      .setImportType("ROC_NAME_ENGLISH")
                                      .setFilename("Names from Praenomin reviewed.xlsx");
        NameWorkflowPraenomina workflow = new NameWorkflowPraenomina(config);
        workflow.runLoad();
    }

    @Override
    JsonNode buildCollectionNode() {
        return LoadHelper.buildCollectionJson(
                   "ROC Praenomina Names",  // name
                   "Praenomina names from the ROC missionaries",  // description
                   "en",  // language
                   null,  // attribution not needed
                   10,    // priority
                   "ROC Missionaries",  // source
                   "Family Search",  // partner
                   "LEASE");  // contract-type
    }
}
