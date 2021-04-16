/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.parser.helper.ExcelUtility;
import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.admin.parser.name.ROCNameParser;

/**
 * Compare the "Praenomin" name data from the original spreadsheet (roc-names-from-missionaries.xlxs) and the
 * updated list (Names from Praenomin reviewed.xlsx).
 * @author wjohnson000
 *
 */
public class PraenominDuplicates {

    static final String BASE_DIR = "C:/D-drive/homelands/names/final";
    static final String FILE_OLD = "roc-names-from-missionaries.xlsx";
    static final String FILE_NEW = "Names from Praenomin reviewed.xlsx";

    public static void main(String...args) throws Exception {
        ROCNameParser rocParser = new ROCNameParser();

        // Get the original name definitions
        byte[] rawDataOld = Files.readAllBytes(Paths.get(BASE_DIR, FILE_OLD));
        Map<Integer, List<List<String>>> nameDataOld = ExcelUtility.loadExcelData(rawDataOld, ".xlsx");
        List<List<List<String>>> sheetRowDataOld = nameDataOld.values().stream().collect(Collectors.toList());
        Map<String, List<NameModel>> modelDefOld = rocParser.parse(sheetRowDataOld);
        Map<String, NameModel> bestNameOld = rocParser.generateBestDefinition(modelDefOld);

        // Get the original name definitions but replace the "praenomin" set with the new data (first sheet)
        byte[] rawDataNew = Files.readAllBytes(Paths.get(BASE_DIR, FILE_NEW));
        Map<Integer, List<List<String>>> nameDataNew = ExcelUtility.loadExcelData(rawDataNew, ".xlsx");
        List<List<List<String>>> sheetRowDataNew = nameDataNew.values().stream().collect(Collectors.toList());
        if (sheetRowDataNew.size() == 1) {
            System.out.println("Adding the old data to the new ...");
            sheetRowDataOld.stream()
                           .skip(1)
                           .forEach(listOfList -> sheetRowDataNew.add(listOfList));
        }
        Map<String, List<NameModel>> modelDefNew = rocParser.parse(sheetRowDataNew);
        Map<String, NameModel> bestNameNew = rocParser.generateBestDefinition(modelDefNew);

        System.out.println("OLD-count: " + bestNameOld.size());
        System.out.println("NEW-count: " + bestNameNew.size());

        Set<String> allNames = new TreeSet<>();
        allNames.addAll(bestNameOld.keySet());
        allNames.addAll(bestNameNew.keySet());

        for (String name : allNames) {
            NameModel nameOld = bestNameOld.get(name);
            NameModel nameNew = bestNameNew.get(name);
            if (nameOld == null) {
                System.out.println(nameNew.getText() + "|add");
            } else if (nameNew == null) {
                System.out.println(nameOld.getText() + "|delete");
            } else {
                String change = "|";
                if (! nameOld.getDefinition().equalsIgnoreCase(nameNew.getDefinition())) {
                    change += "change defintion";
                }
                change += "|";
                if (! formatVariants(nameOld).equalsIgnoreCase(formatVariants(nameNew))) {
                    change += "change variants";
                }
                System.out.println(nameNew.getText() + change);
            }
        }
    }

    static String formatVariants(NameModel nameDef) {
        return nameDef.getVariants().stream()
                            .map(vv -> vv.getText().trim())
                            .collect(Collectors.joining(", "));
    }
}
