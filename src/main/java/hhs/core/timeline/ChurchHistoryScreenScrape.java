package hhs.core.timeline;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import hhs.utility.SimpleHttpClient;

/**
 * Screen-scrape data from the church history website ... save the results to the "OUTPUT_DIR".
 * 
 * @author wjohnson000
 *
 */
public class ChurchHistoryScreenScrape {

    static final String OUTPUT_DIR = "C:/D-drive/homelands/church-history";

    static final String BASE_URL   = "https://www.churchofjesuschrist.org";
    static final String GLOBAL_URL = "https://www.churchofjesuschrist.org/study/history/global-histories?lang=eng";

    public static void main(String... args) throws Exception {
        String body;
        String contents;

        contents = SimpleHttpClient.doGetHTML(GLOBAL_URL, Collections.emptyMap());
        save("AA-Base-html.txt", contents);
        Map<String, String> urls = extractUrls(contents);
        urls.entrySet().forEach(System.out::println);

        for (Map.Entry<String, String> entry : urls.entrySet()) {
            String name = entry.getKey();
            String url  = entry.getValue();

            contents = SimpleHttpClient.doGetHTML(BASE_URL + url, Collections.emptyMap());
            body = extractBody(contents);
            save(name+"-overview-full.txt", contents);
            save(name+"-overview-body.txt", body);

            url = url.replaceFirst("overview", "chronology");
            contents = SimpleHttpClient.doGetHTML(BASE_URL + url, Collections.emptyMap());
            body = extractBody(contents);
            save(name+"-chronology-full.txt", contents);
            save(name+"-chronology-body.txt", body);
        }
    }

    static void save(String filename, String contents) throws Exception {
        Files.write(Paths.get(OUTPUT_DIR, filename), contents.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static Map<String, String> extractUrls(String contents) {
        Map<String, String> countryUrls = new TreeMap<>();

        String body = extractBody(contents);
        while (! body.isEmpty()) {
            int ndx0 = body.indexOf("<a href=");
            int ndx1 = body.indexOf("\">", ndx0);
            int ndx2 = body.indexOf(">", ndx1+3);
            int ndx3 = body.indexOf("<", ndx2+1);
            if (ndx0 >= 0  &&  ndx1 > ndx0  &&  ndx2 > ndx1  &&  ndx3 > ndx2) {
                String url  = body.substring(ndx0+9, ndx1);
                String name = body.substring(ndx2+1, ndx3).replace(' ', '-');
                countryUrls.put(name, url);
                body = body.substring(ndx3);
            } else {
                body = "";
            }
        }

        return countryUrls;
    }

    static String extractBody(String contents) {
        int ndx0 = contents.indexOf("<div class=body");
        int ndx1 = ndx0;

        int divCount = (ndx0 == -1) ? 0 : 1;
        while (divCount > 0) {
            int ndx2 = contents.indexOf("<", ndx1+1);
            if (contents.substring(ndx2, ndx2+4).equals("<div")) {
                divCount++;
                ndx1 = ndx2;
            } else if (contents.substring(ndx2, ndx2+5).equals("</div")) {
                divCount--;
                ndx1 = ndx2;
            } else if (ndx2 > 0) {
                ndx1 = ndx2;
            } else {
                divCount = 0;
                ndx1 = contents.length();
            }
        }

        if (ndx0 > 0  &&  ndx1 > ndx0) {
            return contents.substring(ndx0, ndx1);
        } else {
           return "";
        }
    }
}
