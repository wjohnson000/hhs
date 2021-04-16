/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.admin.parser.name.NameParser;
import org.familysearch.homelands.admin.parser.name.OxfordFirstNameEnParser;
import org.familysearch.homelands.admin.parser.transform.NameToCanonicalCsvTransformer;

/**
 * @author wjohnson000
 *
 */
public class TestOxfordFirstTransform {

    public static void main(String...args) {
        NameParser parser = new OxfordFirstNameEnParser();

        try {
            byte[] contents = Files.readAllBytes(Paths.get("C:/D-drive/homelands/names/final/first_acref_9780198610601.xml"));
            System.out.println("Contents size=" + contents.length);

            Map<String, List<NameModel>> nameDefMap = parser.parse(contents);
            Map<String, NameModel> bestNames  = parser.generateBestDefinition(nameDefMap);

            NameToCanonicalCsvTransformer transformer = new NameToCanonicalCsvTransformer();
            byte[] canonicalData = transformer.transform(bestNames.values());
            Files.write(Paths.get("C:/temp/oxford-first-out.csv"), canonicalData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch(Exception ex) {
            System.out.println("OOPS!! " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
