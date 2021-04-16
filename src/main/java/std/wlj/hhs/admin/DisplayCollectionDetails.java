/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;

import hhs.core.cleanup.CollectionResource;
import hhs.utility.SimpleHttpClient;

/**
 * @author wjohnson000
 *
 */
public class DisplayCollectionDetails {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String authToken = "49d49034-17c4-49ec-8fe2-99c216dab68d-integ";

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
        headers.put("Accept-Language", "en");
    }

    public static void main(String...args) throws Exception {
        List<JsonNode> collectionJsons = retrieveAllCollection();
        collectionJsons.stream()
                        .map(node -> (CollectionResource)JsonUtility.createObject(node, CollectionResource.class))
                        .forEach(DisplayCollectionDetails::shouldDelete);
    }

    static List<JsonNode> retrieveAllCollection() throws Exception {
        String json = SimpleHttpClient.doGetJSON(BASE_URL + "/collection", headers);
        JsonNode all = JsonUtility.parseJson(json);
        return JsonUtility.getArrayValueAsNodes(all, "collections");
    }

    static void shouldDelete(CollectionResource resource) {
        StringBuilder buff = new StringBuilder();
        buff.append(resource.getId());
        buff.append("|").append(resource.getName());
        buff.append("|").append(resource.getDescription());
        buff.append("|").append(resource.getCreateUserId());
        buff.append("|").append(resource.getCreateDate());
        buff.append("|").append(resource.getModifyDate());
        buff.append("|").append(resource.getAvailableLanguages());

        try {
            String idsRaw = SimpleHttpClient.doGetJSON(BASE_URL + "/name/id?collection=" + resource.getId(), headers);
            JsonNode idsJson = JsonUtility.parseJson(idsRaw);
            int count = JsonUtility.getIntValue(idsJson, "size");
            buff.append("|").append(count);
        } catch(Exception ex) {
            System.out.println("  Unable to get IDS: " + ex.getMessage());
            buff.append("|0");
        }

        System.out.println(buff.toString());
    }
}
