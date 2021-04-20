package hhs.admin.name;

import java.util.*;

import hhs.utility.SimpleHttpClient;

/**
 * Ping the old "Discovery" service to retrieve name definitions.
 * 
 * <p>NOTE: this service may no longer be active!.
 * 
 * @author wjohnson000
 *
 */
public class PingDiscoveryNames {

    private static final String baseUrl = "https://beta.familysearch.org/service/discovery/name/meaning/first/";
    private static final String authToken = "d553496b-29cc-4d79-81d5-768e2c3fd7af-beta";

    private static final Set<String> tags = new TreeSet<>();

    private static final String[] names = {
        "Abe",
        "Abraham",
        "Abram",
        "Avrom",
        "Abigail",
        "Abbie",
        "Abbi",
        "Abby",
        "Abi",
        "Angela",
        "Bedelia",
        "Benedict",
        "Chelsea",
        "Dimity",
        "Dora",
        "Giselle",
        "Grace",
        "Margaret",
        "Isaac",
        "Paul",
        "Paula",
        "Peter",
        "Pippa",
        "Robert",
        "Wayne",
        "Wendell",
    };

    public static void main(String...args) throws Exception {
        Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer " + authToken);

        for (String name : names) {
            String json = SimpleHttpClient.doGetJSON(baseUrl + name, headers);
            System.out.println("=======================================================================");
            System.out.println("Name: " + name);
            System.out.println(json);
            parseJson(json);
        }
        System.out.println("\n\n");
        tags.forEach(System.out::println);
    }

    static void parseJson(String json) {
        String tJson = "" + json;
        while (! tJson.isEmpty()) {
            int ndx0 = tJson.indexOf("<");
            int ndx1 = tJson.indexOf('>', ndx0);
            int ndx2 = tJson.indexOf(' ', ndx0);
            int ndx9 = Math.min(ndx1, ndx2);
            if (ndx9 == -1) {
                ndx9 = Math.max(ndx1, ndx2);
            }

            if (ndx0 == -1) {
                tJson = "";
            } else {
                String tag = tJson.substring(ndx0+1, ndx9);
                if (! tag.startsWith("/")  &&  ! tag.startsWith("!--")) {
                    tags.add(tag);
                }
                tJson = tJson.substring(ndx9+1);
            }
        }
    }
}
