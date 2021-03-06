package hhs.admin.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;

import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.admin.parser.name.GeneanetFirstNameParser;
import org.familysearch.homelands.admin.parser.name.NameParser;
import org.familysearch.homelands.admin.parser.transform.NameToCanonicalCsvTransformer;

/**
 * Test the "Geneanet" first name parser.
 * 
 * @author wjohnson000
 *
 */
public class TestGeneanetTransform {

    public static void main(String...args) {
        NameParser parser = new GeneanetFirstNameParser();

        try {
            byte[] contents = Files.readAllBytes(Paths.get("C:/Users/wjohnson000/Downloads/geneanet-first-small.csv"));
            System.out.println("Contents size=" + contents.length);

            Map<String, List<NameModel>> nameDefMap = parser.parse(contents);
            Map<String, NameModel> bestNames  = parser.generateBestDefinition(nameDefMap);
            NameToCanonicalCsvTransformer transformer = new NameToCanonicalCsvTransformer();
            byte[] canonicalData = transformer.transform(bestNames.values());
            Files.write(Paths.get("C:/temp/geneanet-out.csv"), canonicalData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch(Exception ex) {
            System.out.println("OOPS!! " + ex.getMessage());
        }
    }
}
