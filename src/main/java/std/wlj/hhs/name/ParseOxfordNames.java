/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...
 * 
 * @author wjohnson000
 *
 */
public class ParseOxfordNames {

    private static String BASE_DIR = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";
    private static String OUTPUT_FILE;

    private static NameDefParser parser;

    private static Map<String, NameDef> nameById = new HashMap<>();
    private static Map<String, List<NameDef>> nameByName = new HashMap<>();

    private static List<String> results = new ArrayList<>(50_000);
    
    public static void main(String... args) throws Exception {
//        OUTPUT_FILE = "C:/temp/oxford-fn-plain.csv";
//        parser = new NameDefParserPlain();
        OUTPUT_FILE = "C:/temp/oxford-fn-dom.csv";
        parser = new NameDefParserDOM();
//        OUTPUT_FILE = "C:/temp/oxford-fn-sax.csv";
//        parser = new NameDefParserSAX();
//        OUTPUT_FILE = "C:/temp/oxford-fn-jsoup.csv";
//        parser = new NameDefParserJSoup();

        process(FIRST_FILE);
//        process(LAST_FILE);
    }

    static void process(String file) throws Exception {
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
            results.add("");
            results.add("");
            results.add(format(nameDef));
            if (nameDef.refId != null) {
                NameDef refDef = nameById.get(nameDef.refId);
                if (refDef == null) {
                    System.out.println(">>>> Invalid REF for " + nameDef.id + " :: " + nameDef.refId);
                } else {
                    results.add(formatVariant(refDef));
                }
            }
            nameDef.variants.forEach(nd -> results.add(formatVariant(nd)));
        }

        Files.write(Paths.get(OUTPUT_FILE), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String format(NameDef nameDef) {
        StringBuilder buff = new StringBuilder(1024);

        buff.append(nameDef.id);
        buff.append("|").append(nameDef.text);
        buff.append("|").append(nameDef.language);
        buff.append("|").append(nameDef.type);
        buff.append("|").append(nameDef.isMale);
        buff.append("|").append(nameDef.isFemale);
        buff.append("|").append(nameDef.definition);

        return buff.toString();
    }

    static String formatVariant(NameDef nameDef) {
        StringBuilder buff = new StringBuilder(1024);

        buff.append(nameDef.id);
        buff.append("|").append(nameDef.text);
        buff.append("|").append(nameDef.language);
        buff.append("|").append(nameDef.type);
        return buff.toString();
    }
}