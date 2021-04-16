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
 * Compare data from all six tabs:
 *   - (1) Praenomina
 *   - (2) Family Names from the Irish...
 *   - (3) How to Name Baby
 *   - (4) Mrs. Clarke's Cookery Book
 *   - (5) What is Your Name
 *   - (6) Rose of Deseret
 *
 * @author wjohnson000
 */
public class ROCDuplicationNewPraenomim {

    static final String BASE_DIR = "C:/D-drive/homelands/names/final";
    static final String FILE_OLD = "roc-names-from-missionaries.xlsx";
    static final String FILE_NEW = "Names from Praenomin reviewed.xlsx";

    static final Map<Integer, String> tabNames = new HashMap<>();
    static {
        tabNames.put(0, "Praenomina");
        tabNames.put(1, "Family Names from the Irish...");
        tabNames.put(2, "How to Name Baby");
        tabNames.put(3, "Mrs. CLarke's Cookery Book");
        tabNames.put(4, "What is Your Name");
        tabNames.put(5, "Rose of Desert");
    }

    public static void main(String...args) throws Exception {
        ROCNameParser rocParser = new ROCNameParser();

        // Get the original name definitions
        byte[] rawDataOld = Files.readAllBytes(Paths.get(BASE_DIR, FILE_OLD));
        Map<Integer, List<List<String>>> nameDataOld = ExcelUtility.loadExcelData(rawDataOld, ".xlsx");
        List<List<List<String>>> sheetRowDataOld = nameDataOld.values().stream().collect(Collectors.toList());

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

        // Map the raw data to a list of "NameModel" instances for each set of data 
        List<Map<String, List<NameModel>>> listOfNames = sheetRowDataNew
                                .stream()
                                .map(lom -> rocParser.parse(Arrays.asList(lom)))
                                .collect(Collectors.toList());
        System.out.println("ListOfNames: " + listOfNames.size());
        listOfNames.forEach(lon -> System.out.println("  SZ: " + lon.keySet().size()));

        // Dump out a list of duplicates in the same sheet
        for (int i=0;  i<listOfNames.size();  i++) {
            System.out.println("== " + i + " ===================================");
            Map<String, List<NameModel>> mNames = listOfNames.get(i);
            for (List<NameModel> mName : mNames.values()) {
                if (mName.size() > 1) {
                    String defs = mName.stream()
                                       .map(nn -> nn.getDefinition())
                                       .map(def -> def.replace('\n', ' ').replace('\r', ' ').trim())
                                       .collect(Collectors.joining("|"));
                    System.out.println(tabNames.get(i) + "|" + mName.get(0).getText() + "|" + defs);
                }
            }
        }

        Set<String> allNames = listOfNames.stream()
                                          .map(mm -> mm.keySet())
                                          .flatMap(ll -> ll.stream())
                                          .collect(Collectors.toCollection(TreeSet::new));
        for (String aName : allNames) {
            int nCount = 0;
            List<String> defs = new ArrayList<>(6);
            for (int i=0;  i<listOfNames.size();  i++) {
                List<NameModel> names = listOfNames.get(i).get(aName);
                if (names == null) {
                    defs.add("");
                } else {
                    nCount++;
                    defs.add(names.get(0).getDefinition());
                }
            }
            if (nCount > 1) {
                System.out.println(aName + "|" + defs.stream().collect(Collectors.joining("|")));
            }
        }
    }
}
