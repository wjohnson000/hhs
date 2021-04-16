/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.admin.parser.name.ROCNameParser;
import org.familysearch.homelands.lib.common.util.JsonUtility;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author wjohnson000
 *
 */
public class TestROCParsing {

    public static void main(String...args) throws Exception {
        byte[] rawData = Files.readAllBytes(Paths.get("C:/D-drive/homelands/names/final/roc-names-from-missionaries.xlsx"));
        System.out.println("size: " + rawData.length);

        ROCNameParser rocParser = new ROCNameParser();
        Map<String, List<NameModel>> names = rocParser.parse(rawData);
        Map<String, NameModel> bestNames = rocParser.generateBestDefinition(names);

        for (Map.Entry<String, NameModel> entry : bestNames.entrySet()) {
            JsonNode name = JsonUtility.parseObject(entry.getValue());
            System.out.println("\n" + entry.getKey() + " --> " + name.toPrettyString());
        }
    }
}
