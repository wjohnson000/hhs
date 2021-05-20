/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.client.helper;

import org.familysearch.homelands.admin.persistence.dao.StepDao;
import org.familysearch.homelands.admin.persistence.model.StepData;
import org.familysearch.homelands.admin.persistence.model.StepType;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author wjohnson000
 *
 */
public class LocalStepDao extends StepDao {

    /**
     * @param jdbcTemplate
     */
    public LocalStepDao(JdbcTemplate jdbcTemplate) {
        super(null);
    }

    @Override
    public StepData get(String collectionId, int importId, int stepId) {
        StepData stepData = new StepData();

        stepData.setCollectionId("AAAA-999");
        stepData.setImportId(importId);
        stepData.setId(stepId);
        stepData.setType(StepType.RAW_TRANSFORM);

        return stepData;
    }
}
