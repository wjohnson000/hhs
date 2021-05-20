/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.client.helper;

import org.familysearch.homelands.admin.persistence.dao.DaoFactory;

/**
 * @author wjohnson000
 *
 */
public class DummyDaoFactory {

    public static DaoFactory get() {
        return new DaoFactory(new LocalImportDao(null), new LocalStepDao(null));
    }
}
