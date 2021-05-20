/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.client.helper;

import org.familysearch.homelands.admin.persistence.dao.ImportDao;
import org.familysearch.homelands.admin.persistence.model.ImportData;
import org.familysearch.homelands.admin.persistence.model.ImportType;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author wjohnson000
 *
 */
public class LocalImportDao extends ImportDao {

    /**
     * @param jdbcTemplate
     */
    public LocalImportDao(JdbcTemplate jdbcTemplate) {
        super(null);
    }

    @Override
    public ImportData get(int importId) {
        ImportData importData = new ImportData();

        importData.setCollectionId("AAAA-999");
        importData.setId(importId);
        importData.setImportType(ImportType.TIMELINE_ITEM);

        return importData;
    }
}
