package hhs.admin.name.load;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.http.HttpHeaders;
import org.familysearch.homelands.lib.common.util.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import hhs.utility.SimpleHttpClient;

/**
 * @author wjohnson000
 *
 */
public final class LoadHelper {

    private String baseUrl;
    private Map<String, String> headers;

    public LoadHelper(String baseUrl, String sessionId) {
        this.baseUrl = baseUrl;
        this.headers = Collections.singletonMap(HttpHeaders.AUTHORIZATION, "Bearer " + sessionId);
    }

    public JsonNode buildCollectionJson(
           String  name,
           String  description,
           String  origLang,
           String  attribution,
           Integer priority,
           String  source,
           String  partner,
           String  contractType) {

        JsonNode collNode = JsonUtility.emptyNode();
        JsonUtility.addField(collNode, "name", name);
        JsonUtility.addField(collNode, "description", description);
        JsonUtility.addField(collNode, "originLanguage", origLang);
        JsonUtility.addArray(collNode, "availableLanguages", origLang);
        JsonUtility.addField(collNode, "visibility", "PUBLIC");
        JsonUtility.addArray(collNode, "types", "NAME");
        if (attribution != null) {
            JsonNode attrNode = JsonUtility.emptyNode();
            JsonUtility.addField(attrNode, origLang, attribution);
            JsonUtility.addField(collNode, "attribution", attrNode);
        }
        if (priority != null) {
            JsonNode priorNode = JsonUtility.emptyNode();
            JsonUtility.addField(priorNode, origLang, priority);
            JsonUtility.addField(collNode, "priority", priorNode);
        }
        JsonUtility.addField(collNode, "source", source);
        JsonUtility.addField(collNode, "partner", partner);
        JsonUtility.addField(collNode, "contractType", contractType);
        JsonUtility.addField(collNode, "expirationDate", "2036-01-01T01:01:01.000Z");

        return collNode;
    }

    public JsonNode buildImportJson(
           String importType,
           String currentState,
           String collectionId,
           String... files) {

        JsonNode importNode = JsonUtility.emptyNode();
        JsonUtility.addField(importNode, "id", 0);
        JsonUtility.addField(importNode, "importType", importType);
        JsonUtility.addField(importNode, "currentState", currentState);
        JsonUtility.addField(importNode, "collectionId", collectionId);
        List<JsonNode> fileNodes = new ArrayList<>();
        for (String file : files) {
            JsonNode fileNode = JsonUtility.emptyNode();
            JsonUtility.addField(fileNode, "name", file);
            JsonUtility.addField(fileNode, "type", "RAW");
            fileNodes.add(fileNode);
        }
        JsonUtility.addArray(importNode, "files", fileNodes);

        return importNode;
    }

    public JsonNode readCollection(String collectionId) {
        String url = makeUrl("collection", collectionId);
        String body = SimpleHttpClient.doGetJSON(url, headers);
        return parseJson(body);
    }

    public String createCollection(JsonNode body) {
        String url = makeUrl("collection");
        String location = SimpleHttpClient.doPostJson(url, body.toPrettyString(), headers);
        if (location == null) {
            return null;
        } else {
            int ndx = location.lastIndexOf('/');
            return location.substring(ndx+1);
        }
    }

    public JsonNode readImport(String collectionId, String importId) {
        String url = makeUrl("collection", collectionId, "import", importId);
        String body = SimpleHttpClient.doGetJSON(url, headers);
        return parseJson(body);
    }

    public String createImport(String collectionId, JsonNode body) {
        String url = makeUrl("collection", collectionId, "import");
        String location = SimpleHttpClient.doPostJson(url, body.toPrettyString(), headers);
        if (location == null) {
            return null;
        } else {
            int ndx = location.lastIndexOf('/');
            return location.substring(ndx+1);
        }
    }

    public JsonNode readImportSteps(String collectionId, String importId) {
        String url = makeUrl("collection", collectionId, "import", importId, "step");
        String body = SimpleHttpClient.doGetJSON(url, headers);
        return parseJson(body);
    }

    public JsonNode readImportStep(String collectionId, String importId, String stepId) {
        String url = makeUrl("collection", collectionId, "import", importId, "step", stepId);
        String body = SimpleHttpClient.doGetJSON(url, headers);
        return parseJson(body);
    }

    public String startStep(String collectionId, String importId, String stepName) {
        String url = makeUrl("collection", collectionId, "import", importId, stepName);
        SimpleHttpClient.doPostJson(url, null, headers);

        JsonNode stepsNode = readImportSteps(collectionId, importId);
        List<JsonNode> steps = JsonUtility.getArrayValueAsNodes(stepsNode, "steps");
        if (steps.isEmpty()) {
            return null;
        } else {
            int stepId = JsonUtility.getIntValue(steps.get(steps.size()-1), "id");
            return (stepId <= 0) ? null : String.valueOf(stepId);
        }
    }

    public JsonNode readRawFileList() {
        String url = makeUrl("collection", "datafile");
        String body = SimpleHttpClient.doGetJSON(url, headers);
        return parseJson(body);
    }

    public void copyGeneratedFileToRaw(String collectionId, String importId, String stepId, String filename, String newFilename) {
        String url = makeUrl("collection", collectionId, "import", importId, "step", stepId, "file", filename, "copyToUnassigned");
        url += "?newFilename=" + newFilename.trim().replaceAll(" ", "%20");
        SimpleHttpClient.doPostJson(url, null, headers);
    }

    String makeUrl(String... paths) {
        return baseUrl + Arrays.stream(paths)
                                .map(pp -> pp.replace('/', ' ').trim())
                                .map(pp -> pp.replaceAll(" ", "%20"))
                                .collect(Collectors.joining("/", "/", ""));
    }

    JsonNode parseJson(String json) {
        if (json == null) {
            return null;
        } else {
            try {
                return JsonUtility.parseJson(json);
            } catch(Exception ex) {
                System.out.println("Unable to parse JSON!! " + ex.getMessage());
                return null;
            }
        }
    }
}
