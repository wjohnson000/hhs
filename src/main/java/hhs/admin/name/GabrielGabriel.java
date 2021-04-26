package hhs.admin.name;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.admin.parser.name.ROCNameParser;

/**
 * Look through the name source files for every definition of "Gabriel".
 * 
 * @author wjohnson000
 *
 */
public class GabrielGabriel {

    private static final String BASE_DIR = "C:/D-drive/homelands/names/final";

    public static void main(String...args) throws Exception {
        ROCNameParser parser = new ROCNameParser();

        List<File> files = Files.list(Paths.get(BASE_DIR))
                                .map(Path::toFile)
                                .filter(file -> file.getName().endsWith(".xlsx"))
                                .collect(Collectors.toList());

        for (File file : files) {
//            System.out.println("===============================================================");
//            System.out.println("File: " + file);
            byte[] contents = Files.readAllBytes(file.toPath());
            Map<String, List<NameModel>> nameMap = parser.parse(contents, ".xlsx");
            List<NameModel> gabriels = nameMap.getOrDefault("gabriel", Collections.emptyList());
            for (NameModel name : gabriels) {
                System.out.println(file.getName() + "|" + name.getText() + "|" + name.getDefinition() + "|" + name.getVariants().stream().map(nn -> nn.getText()).collect(Collectors.joining(",")));
            }
        }
    }
}
