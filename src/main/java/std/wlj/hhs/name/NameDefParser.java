/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * @author wjohnson000
 *
 */
public interface NameDefParser {

    // MALE and FEMALE characters, indicating gender of name
    static final String MALE_CHAR = "♂";
    static final String FEMALE_CHAR = "♀";

    // HTML tags that can be kept in text fields
    static final Set<String> OK_TEXT_TAGS = new HashSet<>(
                 Arrays.asList("i", "b", "p", "nameGrp", "namegrp"));

    // List of HTML tags and attributes that are to be kept.  NOTE: for now the "nameGrp" appears in both its
    // original form and lower-cased since "JSoup" likes to mess with the case.
    static Whitelist whiteList = new Whitelist()
                            .addTags("i", "b", "p", "nameGrp", "namegrp")
                            .addAttributes("nameGrp", "foreNames", "mainName")
                            .addAttributes("namegrp", "forenames", "mainname");

    NameDef parseXml(String xml);

    /**
     * The name definition may contain "pet name" or "short name" or some such variant.  If so, flag the name
     * accordingly.
     * 
     * @param nameDefn name definition
     * @return
     */
    default String extractTypeFromDefinition(String nameDefn) {
        if (nameDefn != null) {
            if (nameDefn.toLowerCase().contains(" pet ")  ||  nameDefn.toLowerCase().contains(">pet ")  ||  nameDefn.toLowerCase().startsWith("pet ")) {
                return "PET";
            } else if (nameDefn.toLowerCase().contains(" short ")  ||  nameDefn.toLowerCase().contains(">short ")  ||  nameDefn.toLowerCase().startsWith("short ")) {
                return "SHORT";
            }
        }
        return "REGULAR";
    }

    /**
     * The cognate definition may contain "pet name" or "short name" or some such variant.  If so, flag the name
     * accordingly.
     * 
     * @param cognateDefn name definition
     * @return
     */
    default String extractTypeFromDefinitionVariant(String cognateDefn) {
        if (cognateDefn != null) {
            if (cognateDefn.toLowerCase().contains("pet")) {
                return "PET";
            } else if (cognateDefn.toLowerCase().contains("short")) {
                return "SHORT";
            }
        }
        return "COGNATE";
    }

    /**
     * Remove unwanted HTML tags from a text string.
     * 
     * @param htmlWithTags String with embedded HTML tags
     * @return string w/out HTML tags
     */
    default String cleanup(String htmlWithTags) {
        return Jsoup.clean(htmlWithTags, whiteList).replace('\n', ' ').replace('\r', ' ').trim();
    }
}
