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
 * Generate the CQL needed to delete items from a given list of collections.  This was done when we needed to
 * delete lots of stuff from DEV.  Once the CQL was generated, we used the {@link ExecuteCQLFromFile} applciation
 * to actually perform the delete statements.
 * 
 * NOTE: since the endpoint used is protected, a valid session-id must be provided.
 * 
 * @author wjohnson000
 *
 */
public class GenerateItemDeleteCqlDev {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String authToken = "f07ba488-ab6a-447f-bb9a-9ed72d5a1c1e-integ";

    private static final List<String> languages = Arrays.asList("en", "es", "pt", "fr", "it", "ru", "de", "zh-hans", "ja", "ko", "zh");
    private static final List<String> collections = Arrays.asList(
            "MMM3-PLQ",
            "MMM3-P2V",
            "MMM3-RMZ",
            "MMM3-P26",
            "MMM3-G4B",
            "MMM3-P2N",
            "MMM3-PL7",
            "MMM3-PGS",
            "MMM3-PK5",
            "MMM3-G4V"
            );

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
    }

    public static void main(String...args) throws Exception {
        for (String collectionId : collections) {
            processCollection(collectionId);
        }
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

        Files.write(Paths.get("C:/temp/dev-delete-" + collectionId + ".cql"), cql, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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
