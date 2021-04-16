/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import hhs.utility.SimpleHttpClient;

/**
 * @author wjohnson000
 *
 */
public class RunDeleteOxfordNamesWLJ {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";

    /**
     * Delete names based on the detailed report of the previous load.  The rows with three fields have the new "nameId" as
     * the third field.
     */
    public static void main(String...arsg) throws Exception {
        List<String> report = Files.readAllLines(Paths.get("C:/temp/oxford-delete.log"), StandardCharsets.UTF_8);
        for (String line : report) {
            int ndx = line.indexOf("nameId=");
            if (ndx > 0) {
                String nameId = line.substring(ndx+7).trim();
                System.out.println("NN: " + nameId);
                SimpleHttpClient.doDelete(BASE_URL + "/name/" + nameId, Collections.emptyMap());
            }
        }
    }
}
