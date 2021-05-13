package hhs.admin.name.load.roc;

import com.fasterxml.jackson.databind.JsonNode;

import hhs.admin.name.load.BaseNameWorkflow;
import hhs.admin.name.load.LoadHelper;
import hhs.admin.name.load.WorkflowConfig;

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
public class Workflow03WhatIsYourName extends BaseNameWorkflow {

    public Workflow03WhatIsYourName(WorkflowConfig config) {
        super(config);
    }

    public static void main(String...args) {
        WorkflowConfig config = new WorkflowConfig()
                                      .setIsVerbose(true)
                                      .setIsProd(false)
                                      .setSessionId("xxxxx-integ")
                                      .setImportType("ROC_NAME_ENGLISH")
                                      .setFilename("ROC-what-is-your-name-final.xlsx");
        Workflow03WhatIsYourName workflow = new Workflow03WhatIsYourName(config);
        workflow.runLoad();
    }

    @Override
    public JsonNode buildCollectionNode() {
        return LoadHelper.buildCollectionJson(
                   "ROC What is Your Name",  // name
                   "What-is-Your-Name names from the ROC missionaries",  // description
                   "en",  // language
                   null,  // attribution not needed
                   30,    // priority
                   "ROC Missionaries",  // source
                   "Family Search",  // partner
                   "LEASE");  // contract-type
    }
}
