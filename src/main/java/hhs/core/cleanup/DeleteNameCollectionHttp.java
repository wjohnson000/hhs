package hhs.core.cleanup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;

import hhs.utility.SimpleHttpClient;
/**
 * Delete all names associated with a collection, and the collection itself.  This is a broad approach, assuming
 * that no languages other than "en", "es", "de", "da" or "ko" are used.
 * <p>
 * It uses the web service rather than Cassandra database calls to do the dirty work.  Obviously you have to
 * specify a valid "authToken" for the DEV system.
 * 
 * @author wjohnson000
 *
 */
public class DeleteNameCollectionHttp {

    private static final String BASE_URL = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org";
    private static final String authToken = "49d49034-17c4-49ec-8fe2-99c216dab68d-integ";

    private static Map<String, String> headers = new HashMap<>();
    static {
        headers.put("Authorization", "Bearer " + authToken);
        headers.put("Accept-Language", "en");
    }

    public static void main(String...args) throws Exception {
        String collectionId = "MMM3-V92";
        CollectionResource collection = retrieveCollection(collectionId);
        List<String> nameIds = retrieveNameIds(collectionId);

        System.out.println("ID: " + collection.getId());
        System.out.println("   Name: " + collection.getName());
        System.out.println("  Descr: " + collection.getDescription());
        System.out.println("  Creat: " + collection.getCreateDate());
        System.out.println("  Modif: " + collection.getModifyDate());
        nameIds.forEach(id -> System.out.println(" NameId: " + id));

        for (String nameId : nameIds) {
            deleteName(nameId, "en");
            deleteName(nameId, "es");
            deleteName(nameId, "de");
            deleteName(nameId, "da");
            deleteName(nameId, "ko");
        }

        deleteCollection(collectionId, "en");
        deleteCollection(collectionId, "es");
        deleteCollection(collectionId, "de");
        deleteCollection(collectionId, "da");
        deleteCollection(collectionId, "ko");
    }

    static CollectionResource retrieveCollection(String id) throws Exception {
        String json = SimpleHttpClient.doGetJSON(BASE_URL + "/collection/" + id, headers);
        JsonNode node = JsonUtility.parseJson(json);
        return JsonUtility.createObject(node, CollectionResource.class);
    }

    static List<String> retrieveNameIds(String id) throws Exception {
        String idsRaw = SimpleHttpClient.doGetJSON(BASE_URL + "/name/id?collection=" + id, headers);
        JsonNode idsJson = JsonUtility.parseJson(idsRaw);
        System.out.println("IDS: " + idsJson);
        return Arrays.asList(JsonUtility.getArrayValue(idsJson, "itemIds"));
    }

    static void deleteName(String nameId, String language) throws Exception {
        Map<String, String> headersX = new HashMap<>();
        headersX.put("Authorization", "Bearer " + authToken);
        headersX.put("Accept-Language", language);

        SimpleHttpClient.doDelete(BASE_URL + "/name/" + nameId, headersX);
    }

    static void deleteCollection(String collId, String language) throws Exception {
        Map<String, String> headersX = new HashMap<>();
        headersX.put("Authorization", "Bearer " + authToken);
        headersX.put("Accept-Language", language);

        SimpleHttpClient.doDelete(BASE_URL + "/collection/" + collId, headersX);
    }
}
