package hhs.core.cleanup;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.lib.common.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;

import hhs.utility.SimpleHttpClient;

/**
 * Since there is no easy (or any) access to the production Cassandra database, deleting items for a collection
 * involves multiple steps:
 * <ul>
 *   <li>Query production to get a list of all item ids + associated type</li>
 *   <li>For each item, generate two "DELETE" statements: "item" and "item_search"</li>
 *   <li>Save the DELETE statements in a file</li>
 *   <li>Create a new JIRA for the DBAs, and attach the CQL file[s]</li>
 * </ul>
 * 
 * NOTE: since the endpoint used is protected, a valid session-id must be provided.
 * 
 * @author wjohnson000
 *
 */
public class GenerateItemDeleteCqlProd {

    private static final String BASE_URL = "http://core.homelands.service.prod.us-east-1.prod.fslocal.org";
    private static final String authToken = "6535249a-94ef-43a1-b895-ecabe61b0f93-prod";

    private static final List<String> languages = Arrays.asList("en", "es", "pt", "fr", "it", "ru", "de", "zh-hans", "ja", "ko", "zh");

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
    }

    public static void main(String...args) throws Exception {
        processCollection("MMMM-9BZ");
    }

    public static void processCollection(String collectionId) throws Exception {
        List<String> cql = new ArrayList<>(10_000);

        System.out.println();
        for (String language : languages) {
            List<String[]> idsAndType = getAllItems(collectionId, language);
            System.out.println(collectionId + ".Language: " + language + ";  Item-Count: " + idsAndType.size());
            
            for (String[] it : idsAndType) {
                cql.add("DELETE FROM hhs.item_search WHERE itemid = '" + it[0] + "';");
                cql.add("DELETE FROM hhs.item WHERE id = '" + it[0] + "' AND type = '" + it[1] + "';");
            }
        }

        Files.write(Paths.get("C:/temp/prod-delete-" + collectionId + ".cql"), cql, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<String[]> getAllItems(String collId, String language) throws Exception {
        List<String[]> results = new ArrayList<>(5_000);

        headers.put("Accept-Language", language);

        int start = 0;
        String   json  = SimpleHttpClient.doGetJSON(BASE_URL + "/item?collectionId=" + collId + "&start=" + start + "&count=100", headers);
        JsonNode node  = JsonUtility.parseJson(json);
        int      count = JsonUtility.getIntValue(node, "count");

        while (count > 0) {
            List<JsonNode> items = JsonUtility.getArrayValueAsNodes(node, "items");
            for (JsonNode iNode : items) {
                String id   = JsonUtility.getStringValue(iNode, "id");
                String type = JsonUtility.getStringValue(iNode, "type");
                results.add(new String[] { id, type });
            }

            start += 99;
            json  = SimpleHttpClient.doGetJSON(BASE_URL + "/item?collectionId=" + collId + "&start=" + start + "&count=100", headers);
            node  = JsonUtility.parseJson(json);
            count = JsonUtility.getIntValue(node, "count");
        }

        return results;
    }
}
