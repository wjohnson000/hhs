/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.ParseSettings;

/**
 * Implement the {@link NameDefParser} using the "JSoup" framework.  This is similar to a "DOM" parser, in that the full
 * document is parsed and individual elements can be selected via a "path".
 */
public class NameDefParserJSoup implements NameDefParser {

    @Override
    public NameDef parseXml(String xml) {
        NameDef nameDef = new NameDef();

        Parser parser = Parser.htmlParser();
        parser.settings(new ParseSettings(true, true)); // tag, attribute preserve case

        Document htmlDoc = Jsoup.parse(xml, "", parser);
        Element eTag = htmlDoc.selectFirst("e");
        if (eTag != null) {
            nameDef.id = eTag.attr("id");
            nameDef.text = getNameText(eTag);
            nameDef.language = "en";
            nameDef.refId = getRefText(eTag);
            nameDef.definition = getDefinition(eTag);
            nameDef.type = getType(eTag);
            nameDef.isMale = xml.indexOf(MALE_CHAR) > 0;
            nameDef.isFemale = xml.indexOf(FEMALE_CHAR) > 0;
            nameDef.variants = getVariants(eTag);
        }

        return (nameDef.id == null) ? null : nameDef;
    }

    String getNameText(Element eTag) {
        Element headword = eTag.selectFirst("headword");
        return (headword == null) ? "" : headword.ownText();
    }

    String getRefText(Element eTag) {
        Element xrefGrp = eTag.selectFirst("div1 xrefGrp xref");
        return (xrefGrp == null) ? null : xrefGrp.attr("ref");
    }

    String getDefinition(Element eTag) {
        Element textMatter = eTag.selectFirst("textMatter");
        return (textMatter == null) ? null : cleanup(textMatter.html()); 
    }

    String getType(Element eTag) {
        Element div1 = eTag.selectFirst("div1");
        return (div1 == null) ? null : this.extractTypeFromDefinition(div1.html());
    }

    /**
     * A bit of an ugly method ... loop through every element (except for the "dev1" node) to look
     * for "sc" tags (for variant type) and "nameGrp" tags (for variant names).
     * 
     * @param eTag
     * @return
     */
    List<NameDef> getVariants(Element eTag) {
        boolean noteFound = false;
        String  scType = null;
        List<NameDef> varNames = new ArrayList<>();
        List<Element> toProcess = new ArrayList<>();
        toProcess.add(eTag);

        while (!toProcess.isEmpty()) {
            Element elem = toProcess.remove(0);
            if (elem.tagName().equalsIgnoreCase("note")) {
                noteFound = true;
            } else if (noteFound) {
                if (elem.tagName().equalsIgnoreCase("sc")) {
                    scType = this.extractTypeFromDefinitionVariant(elem.text());
                } else if(elem.tagName().equals("nameGrp")) {
                    if (scType == null) {
                        scType = this.extractTypeFromDefinitionVariant("UKNOWNN");
                    }
                    NameDef varDef = new NameDef();
                    varDef.id = "";
                    varDef.text = elem.text();
                    varDef.language = "";
                    varDef.type = scType;
                    varNames.add(varDef);
                }
            }
            if (!elem.tagName().equalsIgnoreCase("div1")) {
                elem.children().stream().forEach(child -> toProcess.add(child));
            }
        }

        return varNames;
    }
}
