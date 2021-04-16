package hhs.client;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.parser.model.ItemModel;
import org.familysearch.homelands.admin.parser.timeline.TimelineParser;

import com.amazonaws.util.IOUtils;

/**
 * Test the {@link TimelineParser} against real files in the given directory.  This presupposes that the
 * correct files are in the given directory ... duh!
 * 
 * @author wjohnson000
 *
 */
public class TestTimelineParser {

    static final String BASE_DIR = "C:/D-drive/homelands/Decades-Project";

    public static void main(String...args) throws Exception {
        List<File> files = Files.list(Paths.get(BASE_DIR))
                                .filter(Files::isRegularFile)
                                .map(Path::toFile)
                                .filter(file -> file.getName().endsWith(".xlsx"))
                                .filter(file -> ! file.getName().startsWith("~"))
                                .collect(Collectors.toList());

        TimelineParser parser = new TimelineParser();
        for (File file : files) {
            System.out.println();
            System.out.println("========================================================================");
            System.out.println("FILE: " + file);
            System.out.println("========================================================================");

            byte[] rawData = IOUtils.toByteArray(new FileInputStream(file));
            List<ItemModel> mapFields = parser.parse(rawData);
            System.out.println("  Count: " + (mapFields == null ? 0 : mapFields.size()));
            if (mapFields != null  &&  mapFields.size() > 1) {
                System.out.println(file);
                System.out.println("rep: " + mapFields.get(0).getPlaceRepIds());
                System.out.println("reg: " + mapFields.get(0).getRegion());
            }
        }
    }
}
