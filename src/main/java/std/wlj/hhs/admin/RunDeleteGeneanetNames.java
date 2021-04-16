/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import hhs.utility.SimpleHttpClient;

/**
 * @author wjohnson000
 *
 */
public class RunDeleteGeneanetNames {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";


    private static final ScheduledExecutorService service =
                            Executors.newScheduledThreadPool(
                                8,
                                runn -> {
                                    Thread thr = Executors.defaultThreadFactory().newThread(runn);
                                    thr.setDaemon(true);
                                    thr.setName("delete-name");
                                    return thr;
                                });

    /**
     * Delete names based on the detailed report of the previous load.  The rows with three fields have the new "nameId" as
     * the third field.
     */
    public static void main(String...arsg) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer 15a20bae-1d90-4651-95cf-4ca100acbf75-integ");
        headers.put("Accept-Language", "fr");

        List<String> nameids = Files.readAllLines(Paths.get("C:/temp/name-id-to-delete.txt"), StandardCharsets.UTF_8);
        for (String nameid : nameids) {
            if (! nameid.trim().isEmpty()) {
                System.out.println("NN: " + nameid);
                service.execute(() ->  SimpleHttpClient.doDelete(BASE_URL + "/name/" + nameid.trim(), headers));
            }
        }

        service.shutdown();
        service.awaitTermination(30, TimeUnit.MINUTES);
        System.exit(0);
    }
}
