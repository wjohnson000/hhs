package hhs.core.name.parser.load;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import hhs.utility.SimpleHttpClient;

import org.apache.commons.io.IOUtils;
import org.familysearch.homelands.lib.common.util.JsonUtility;

/**
 * Update variant names for OXFORD data.  Some variant names have a separate, independent definition.  Once all names have
 * been loaded, take a second pass to see if any variants of a name have a definition.  If so, associate the name-id of
 * that name to the variant.
 * 
 * @author wjohnson000
 *
 */
public class LoadOxfordNamesUpdate {

    private static final String baseUrl = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org/";

    private static final Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer " + "");

    public static void main(String... args) throws Exception {
        Map<String, String> nameToIdMap = loadSavedIds();

        for (String id : nameToIdMap.values()) {
            JsonNode nameNode = readName(id);
            if (nameNode == null) {
                System.out.println("Unable to read: " + id);
            } else {
                JsonNode variantNode = JsonUtility.getJsonNode(nameNode, "variants");
                if (variantNode instanceof ArrayNode) {
                    boolean variantChanged = false;

                    ArrayNode variants = (ArrayNode)variantNode;
                    for (int i=0;  i<variants.size();  i++) {
                        JsonNode variant = variants.get(i);
                        String name = JsonUtility.getStringValue(variant, "name");
                        String varNameId = nameToIdMap.get(name);
                        if (varNameId != null) {
                            variantChanged = true;
                            JsonUtility.addField(variant, "nameId", varNameId);
                        }
                    }

                    if (variantChanged) {
                        System.out.println("PUT " + baseUrl + "/name/" + id);
                        SimpleHttpClient.doPutJson(baseUrl + "/name/" + id, JsonUtility.prettyPrint(nameNode), headers);
                    }
                }
            }
        }
    }

    static Map<String, String> loadSavedIds() throws Exception {
        List<String> rawData = IOUtils.readLines(LoadOxfordNamesUpdate.class.getResourceAsStream("oxford-refid-to-nameid.txt"), StandardCharsets.UTF_8);
        return rawData.stream()
                      .map(line -> line.split("\\|"))
                      .filter(row -> row.length == 3)
                      .collect(Collectors.toMap(val -> val[1], val -> val[2], (e, r) -> e));
    }

    static JsonNode readName(String id) throws Exception {
        String json = SimpleHttpClient.doGetJSON(baseUrl + "name/" +  id, headers);
        return JsonUtility.parseJson(json);
    }
}