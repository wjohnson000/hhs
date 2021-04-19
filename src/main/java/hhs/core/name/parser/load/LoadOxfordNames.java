package hhs.core.name.parser.load;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import hhs.core.name.parser.NameDef;
import hhs.core.name.parser.NameDefParser;
import hhs.core.name.parser.NameDefParserJSoup;
import hhs.utility.SimpleHttpClient;

import org.apache.commons.io.IOUtils;
import org.familysearch.homelands.lib.common.model.NameType;
import org.familysearch.homelands.lib.common.util.JsonUtility;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...  This would create a collection if
 * needed, and create all the "first" names.
 * 
 * @author wjohnson000
 *
 */
public class LoadOxfordNames {

    private static String COLL_ID = "MMMM-98Y";
    private static final String baseUrl = "http://core.homelands.service.dev.us-east-1.dev.fslocal.org/";

    private static String BASE_DIR = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";

    private static Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer " + "");

    private static NameDefParser parser;

    private static Map<String, NameDef> nameById = new HashMap<>();
    private static Map<String, List<NameDef>> nameByName = new HashMap<>();
    private static Map<String, String> refIdToNameId = new HashMap<>();
    
    public static void main(String... args) throws Exception {
        parser = new NameDefParserJSoup();

        createCollectionIfNecessary();
        loadNames(FIRST_FILE);
        for (Map.Entry<String, NameDef> entry : nameById.entrySet()) {
            System.out.println(entry.getKey() + "|" + entry.getValue().text + "|" + refIdToNameId.getOrDefault(entry.getKey(), ""));
        }
    }

    static void createCollectionIfNecessary() throws Exception {
        if (COLL_ID != null) {
            String collJson = SimpleHttpClient.doGetJSON(baseUrl + "/collection/" + COLL_ID, headers);
            if (collJson == null  ||  collJson.isEmpty()) {
                COLL_ID = null;
            }
        }

        if (COLL_ID == null) {
            String json = IOUtils.toString(LoadOxfordNames.class.getResourceAsStream("collection.json"), StandardCharsets.UTF_8);
            String result = SimpleHttpClient.doPostJson(baseUrl + "/collection", json, headers);
            System.out.println("RESULT: " + result);
        }
    }

    static void loadNames(String file) throws Exception {
        List<String> rows = Files.readAllLines(Paths.get(BASE_DIR, file), StandardCharsets.UTF_8);

        for (String row : rows) {
            if (row.startsWith("<e ")) {
                NameDef nameDef = parser.parseXml(row);
                if (nameDef != null) {
                    if (nameById.containsKey(nameDef.id)) {
                        System.out.println("Duplicate [id].key: " + nameDef.id);
                    } else {
                        nameById.put(nameDef.id, nameDef);
                    }
                    
                    List<NameDef> tNames = nameByName.computeIfAbsent(nameDef.text, kk -> new ArrayList<>());
                    tNames.add(nameDef);
                }
            }
        }

        // Sort-Sort and then Dump-dump
        List<NameDef> masterNames = nameById.values().stream()
                              .filter(nd -> nd.id != null  &&  nd.text != null)
                              .collect(Collectors.toList());
        Collections.sort(masterNames, (nd1, nd2) -> nd1.text.compareToIgnoreCase(nd2.text));

        // Find ref-id for variants
        for (NameDef nameDef : masterNames) {
            for (NameDef varDef : nameDef.variants) {
                List<NameDef> matches = nameByName.get(varDef.text);
                if (matches != null  &&  ! matches.isEmpty()) {
                    varDef.id = matches.get(0).id;
                }
            }
        }

        for (NameDef nameDef : masterNames) {
            if (!nameDef.text.equalsIgnoreCase("June")) {
                continue;
            }

            JsonNode node = createJson(nameDef);
            System.out.println("NN: " + JsonUtility.prettyPrint(node));
            String result = SimpleHttpClient.doPostJson(baseUrl + "/name", JsonUtility.prettyPrint(node), headers);
            System.out.println("RESULT: " + result);
            if (result != null) {
                int ndx = result.lastIndexOf('/');
                String nameId = result.substring(ndx+1);
                refIdToNameId.put(nameDef.id, nameId);
            }
        }
    }

    static JsonNode createJson(NameDef nameDef) {
        List<JsonNode> variants = new ArrayList<>();
        if (nameDef.refId != null) {
            NameDef refDef = nameById.get(nameDef.refId);
            if (refDef != null) {
                variants.add(createVariant(refDef));
            }
        }
        nameDef.variants.forEach(var -> variants.add(createVariant(var)));

        JsonNode node = JsonUtility.emptyNode();

        JsonUtility.addField(node, "id", "");
        JsonUtility.addField(node, "name", nameDef.text);
        JsonUtility.addField(node, "gender", (nameDef.isMale && nameDef.isFemale ? "BOTH" : (nameDef.isMale ? "MALE" : "FEMALE")));
        JsonUtility.addField(node, "collectionId", COLL_ID);
        JsonUtility.addField(node, "type", NameType.FIRST.name());
        JsonUtility.addField(node, "language", "en");
        JsonUtility.addField(node, "originLanguage", "en");
        JsonUtility.addField(node, "definition", createDefinition(nameDef.definition));
        JsonUtility.addField(node, "createUserId", "wjohnson000");
        JsonUtility.addField(node, "modifyUserId", "wjohnson000");
        if (!variants.isEmpty()) {
            JsonUtility.addArray(node, "variants", variants);
        }

        return node;
    }

    static JsonNode createDefinition(String definition) {
        JsonNode content = JsonUtility.emptyNode();
        JsonUtility.addField(content, "style", "HTML");
        JsonUtility.addField(content, "text", definition);

        JsonNode formattedString = JsonUtility.emptyNode();
        JsonUtility.addField(formattedString, "type", "PARAGRAPH");
        JsonUtility.addArray(formattedString, "content", Arrays.asList(content));

        JsonNode formattedData = JsonUtility.emptyNode();
        JsonUtility.addArray(formattedData, "formattedString", Arrays.asList(formattedString));

        return formattedData;
    }

    static JsonNode createVariant(NameDef nameDef) {
        JsonNode node = JsonUtility.emptyNode();

        JsonUtility.addField(node, "nameId", "");
        JsonUtility.addField(node, "usage", nameDef.type);
        JsonUtility.addField(node, "name", nameDef.text);
        JsonUtility.addField(node, "language", (nameDef.language == null ? "en" : nameDef.language));

        return node;
    }
}