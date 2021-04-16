/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...
 * 
 * @author wjohnson000
 *
 */
public class AnalyzeOxfordNames {

    private static String BASE_DIR = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";

    private static NameDefParser parser;

    private static Map<String, NameDef> nameById = new HashMap<>();
    private static Map<String, List<NameDef>> nameByName = new HashMap<>();
    
    public static void main(String... args) throws Exception {
//        OUTPUT_FILE = "C:/temp/oxford-fn-plain.csv";
//        parser = new NameDefParserPlain();
//        OUTPUT_FILE = "C:/temp/oxford-fn-dom.csv";
//        parser = new NameDefParserDOM();
//        OUTPUT_FILE = "C:/temp/oxford-fn-sax.csv";
//        parser = new NameDefParserSAX();

        parser = new NameDefParserJSoup();
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
        Collections.sort(masterNames, (nd1, nd2) ->  {
            int compare = nd1.text.compareToIgnoreCase(nd2.text);
            return (compare != 0) ? compare : compareIds(nd1.id, nd2.id);
        });

        // Find ref-id for variants
        for (NameDef nameDef : masterNames) {
            for (NameDef varDef : nameDef.variants) {
                List<NameDef> matches = nameByName.get(varDef.text);
                if (matches != null  &&  ! matches.isEmpty()) {
                    varDef.id = matches.get(0).id;
                }
            }
        }

        boolean first = true;
        NameDef prevName = null;
        for (NameDef nameDef : masterNames) {
            if (prevName != null  &&  nameDef.text.equalsIgnoreCase(prevName.text)) {
                if (first) {
                    first = false;
                    System.out.println("\n\n\n" + prevName.text);
                    System.out.println(format(prevName));
                    prevName.variants.forEach(vr -> System.out.println(formatVariant(vr)));
                }
                System.out.println();
                System.out.println(format(nameDef));
                nameDef.variants.forEach(vr -> System.out.println(formatVariant(vr)));
            } else {
                first = true;
            }
            prevName = nameDef;
        }
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

        buff.append("|").append(nameDef.text);
        buff.append("|").append(nameDef.language);
        buff.append("|").append(nameDef.type);
        buff.append("|||").append(nameDef.id);
        return buff.toString();
    }

    static int compareIds(String id1, String id2) {
        int ndx1 = id1.lastIndexOf('-');
        int ndx2 = id1.lastIndexOf('-');
        try {
            int num1 = Integer.parseInt(id1.substring(ndx1+1));
            int num2 = Integer.parseInt(id2.substring(ndx1+1));
            return num1 - num2;
        } catch(NumberFormatException ex) {
            return 0;
        }
    }
}