package hhs.core.timeline;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Screen-scrape "Chronology" data from the church history website.  The input files and output
 * files will be in "BASE_DIR".  You must first run the {@link ChurchHistoryScreenScrape} application.
 * @author wjohnson000
 *
 */
public class ChurchHistoryScreenScrapeChronology {

    static final String BASE_DIR = "C:/D-drive/homelands/church-history";

    static final char DIVIDER = '•';
    static final char HSPACE  = ' ';
    static List<String> contents = new ArrayList<>();

    public static void main(String... args) {
        try {
            for (Path path : Files.newDirectoryStream(Paths.get(BASE_DIR), 
                        path -> path.toFile().isFile())) {
                if (path.getFileName().toString().contains("chronology-body")) {
                    processFile(path.getFileName());
                }
            }
        } catch (Exception ex) {
            System.out.println("Oops -- " + ex.getMessage());
            ex.printStackTrace();
        }

        contents.forEach(System.out::println);
    }

    static void processFile(Path path) throws Exception {
        String filename = path.getFileName().toString();
        int ndx = filename.indexOf("-chron");
        String country =  filename.substring(0, ndx).replace('-', ' ');

        contents.add("");
        String body = new String(Files.readAllBytes(Paths.get(BASE_DIR, filename)));
        splitDD(body).stream()
             .map(it -> formatItem(country, it))
             .forEach(it -> contents.add(it));
    }

    /**
     * Split a string into multiple chunks based on a "<dd>...</dd>" section
     * @param contents
     * @return
     */
    static List<String> splitDD(String contents) {
        List<String> results = new ArrayList<>();

        String tContents = contents;
        int ndx0 = tContents.indexOf("<dd>");
        while (ndx0 > -1) {
            int ndx1 = tContents.indexOf("</dd>");
            results.add(tContents.substring(ndx0+4, ndx1));

            tContents = tContents.substring(ndx1+4);
            ndx0 = tContents.indexOf("<dd>");
        }

        return results;
    }

    static String formatItem(String country, String rawItem) {
        String[] itemData = unpackItem(rawItem);

        StringBuilder buff = new StringBuilder();
        buff.append(country);
        Arrays.stream(itemData).forEach(it -> buff.append("|").append(it));

        return buff.toString();
    }

    /**
     * Return values as such: place, date, year (of date), text
     * 
     * @param rawItem
     * @return
     */
    static String[] unpackItem(String rawItem) {
        int ndx0, ndx1;

        ndx0 = rawItem.indexOf("</span>");
        if (ndx0 == -1) {
            return new String[] { "", "", "", "", "" };
        }

        String datePlace = rawItem.substring(0, ndx0);
        String text      = rawItem.substring(ndx0+7);

        // Get place and date and year from "placeDate"
        ndx0 = datePlace.indexOf("<span");
        ndx1 = datePlace.indexOf(">", ndx0+1);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            datePlace = datePlace.substring(0, ndx0) + datePlace.substring(ndx1+1);
        }

        ndx0 = datePlace.indexOf(DIVIDER);
        String date  = (ndx0 == -1) ? datePlace : datePlace.substring(0, ndx0).replace(HSPACE, ' ').trim();
        String place = (ndx0 == -1) ? "" : datePlace.substring(ndx0+1).replace(HSPACE, ' ').trim();


        ndx0 = date.indexOf('<');
        ndx1 = date.indexOf('>');
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            date = date.substring(ndx1+1);
        }

        ndx0 = date.lastIndexOf(' ');
        String year = (ndx0 == -1) ? "" : date.substring(ndx0).replace(HSPACE, ' ').trim();

        year = (year.isEmpty()) ? date : year;
        String fromYear = year;
        String toYear   = "";
        ndx0 = year.indexOf('–');
        if (ndx0 > 0) {
            fromYear    = year.substring(0, ndx0).trim();
            String temp = year.substring(ndx0+1).trim();
            if (temp.length() < 4) {
                if (temp.endsWith("s")) {
                    toYear = fromYear.substring(0, 5-temp.length()) + temp;
                } else {
                    toYear = fromYear.substring(0, 4-temp.length()) + temp;
                }
            }
        }

        // Get text from "text"
        ndx0 = text.indexOf(">");
        ndx1 = text.indexOf("</p>", ndx0);
        text = text.substring(ndx0+1, ndx1);
        text = text.replaceAll("<span>", "");
        text = text.replaceAll("</span>", "");
        ndx0 = text.indexOf("<span");
        ndx1 = text.indexOf(">", ndx0+1);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            text = text.substring(0, ndx0) + text.substring(ndx1+1);
        }
        text = text.replaceAll("<cite>", "");
        text = text.replaceAll("</cite>", "");

        return new String[] { place, date, fromYear, toYear, text };
    }
}
