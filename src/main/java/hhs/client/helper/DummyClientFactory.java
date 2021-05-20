/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.client.helper;

import org.familysearch.homelands.admin.client.ClientFactory;
import org.familysearch.homelands.admin.client.FileService;

/**
 * @author wjohnson000
 *
 */
public class DummyClientFactory {

    public static ClientFactory get(String basePath) {
        FileService fileService = new LocalFileService(basePath);
        return new ClientFactory(fileService, null, null, null, null);
    }
}
