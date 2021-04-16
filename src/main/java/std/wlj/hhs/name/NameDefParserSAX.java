/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...  The data is formated as XML with HTML tags
 * such used for links, text highlighting, etc.  This code uses DOM to parse the document.
 * 
 * The tags of interest include:
 * <ul>
 *   <li><strong>e</strong> - presumably for "entry".  It has an "id" attribute that uniquely identifies a name.</li>
 *   <li><strong>headwordGroup</strong> - contains the name and gender.  A "♂" symbol is used to designate a male
 *               name, a "♀" is used to designate a female name.  Names may be associated with both.</li>
 *   <li><strong>headword</strong> - can appear in a few places, but mainly inside a "headwordGroup" tag.  It's
 *               value is the name.</li>
 *   <li><strong>span</strong> - inside the name definition a "span" tag with "ency" attribute contains the language
 *               of origin.</li>
 *   <li><strong>textMatter</strong> - this contains the name definition with a number of HTML tags allowed.  For
 *               now the code allows "i", "b", "p" and "span" tags to remain in the code.  All other HTML tags are
 *               removed from the definition.  However we do look for a "xref" tag with a "ref" attribute to determin
 *               if the name is associated with another name.</li>
 *   <li><strong>xrefGroup + xref</strong> - this pair signals that the current name is tied to another name.  The
 *               related name's unique identifier is defined in the "ref" attribute of the "xref" tag, which is in
 *               turn inside the "xrefGroup" tag.</li>
 * </ul>
 *  
 * The basic operation of this class is:
 * <ol>
 * <li>Read all lines from the input file, process them a line-at-a-time</li>
 * <li>For each line, create an 'XmlStreamReader' for that name definition</li>
 * <li>Extract all information about that name (ignore lines that are NOT name definitions)</li>
 * <li>Associate each "derived" or "variant" name with its parent</li>
 * <li>Sort the parent names by name text</li>
 * <li>Dump out all names, keeping the derived names with their parent name.  Fields are pipe-delimited as:</li>
 *   <ul>
 *     <li>name identifier</li>
 *     <li>text (of name)</li>
 *     <li>language of origin (currently not being set)</li>
 *     <li>type: "1" refers to a short form or pet name, "2" to a variant or cognate</li>
 *     <li>is this name male, "true" or "false"</li>
 *     <li>is this name female, "true" or "false"</li>
 *     <li>name meaning, with "i", "b", "p" and "span" HTML tags embedded</li>
 *   </ul>
 * </ol>
 * 
 * @author wjohnson000
 *
 */
public class NameDefParserSAX implements NameDefParser {

    private static XMLInputFactory XML_FACTORY = XMLInputFactory.newInstance();

    @Override
    public NameDef parseXml(String xml) {
        NameDef nameDef = new NameDef();
        nameDef.language = "en";

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
            XMLStreamReader parser = XML_FACTORY.createXMLStreamReader(bais, "UTF-8");

            boolean inHWGroup = false;
            boolean inNote = false;
            boolean inSC = false;
            boolean inNameGrp = false;
            boolean htmlOK = false;
            boolean captureMain = false;
            StringBuilder mainBuff = new StringBuilder();
            String scText = "";
            String nameGrpText = "";

            boolean more = true;
            while (more) {
                int event = parser.next();
                switch(event) {
                case XMLStreamConstants.END_DOCUMENT:
                    more = false;
                    break;

                case XMLStreamConstants.START_ELEMENT:
                    if (parser.getLocalName().equals("e")) {
                        nameDef.id = getAttrValue(parser, "id");
                    } else if (parser.getLocalName().equals("xref")) {
                        if (nameDef.refId == null) {
                            nameDef.refId = getAttrValue(parser, "ref");
                        }
                    } else if (parser.getLocalName().equals("headwordGroup")) {
                        inHWGroup = true;
                    } else if (parser.getLocalName().equals("headword")  &&  inHWGroup) {
                        htmlOK = false;
                        captureMain = true;
                    } else if (parser.getLocalName().equals("textMatter")) {
                        htmlOK = true;
                        captureMain = true;
                    } else if (parser.getLocalName().equals("note")) {
                        inNote = true;
                    } else if (inNote  &&  parser.getLocalName().equals("sc")) {
                        inSC = true;
                    } else if (inNote  &&  parser.getLocalName().equals("nameGrp")) {
                        inNameGrp = true;
                    } else if (captureMain  &&  htmlOK  &&  OK_TEXT_TAGS.contains(parser.getLocalName())) {
                        mainBuff.append("<").append(parser.getLocalName());
                        for (int ndx=0;  ndx<parser.getAttributeCount();  ndx++) {
                            mainBuff.append(" ").append(parser.getAttributeLocalName(ndx));
                            mainBuff.append("=\"").append(parser.getAttributeValue(ndx)).append("\"");
                        }
                        mainBuff.append(">");
                    }
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    if (parser.getLocalName().equals("headwordGroup")  &&  inHWGroup) {
                        inHWGroup = false;
                    } else if (parser.getLocalName().equals("headword")  &&  inHWGroup) {
                        captureMain = false;
                        nameDef.text = mainBuff.toString();
                        mainBuff = new StringBuilder();
                    } else if (parser.getLocalName().equals("textMatter")) {
                        htmlOK = false;
                        captureMain = false;
                        nameDef.definition = mainBuff.toString();
                        mainBuff = new StringBuilder();
                    } else if (parser.getLocalName().equals("div1")) {
                        nameDef.type = this.extractTypeFromDefinition(mainBuff.toString());
                    } else if (captureMain  &&  htmlOK  &&  OK_TEXT_TAGS.contains(parser.getLocalName())) {
                        mainBuff.append("</").append(parser.getLocalName()).append(">");
                    } else if (parser.getLocalName().equals("note")) {
                        inNote = false;
                    } else if (inNote  &&  parser.getLocalName().equals("sc")) {
                        inSC = false;
                    } else if (inNote  &&  parser.getLocalName().equals("nameGrp")) {
                        inNameGrp = false;
                        NameDef varDef = new NameDef();
                        varDef.id = "";
                        varDef.text = nameGrpText;
                        varDef.language = "";
                        varDef.type = this.extractTypeFromDefinitionVariant(scText);
                        nameDef.variants.add(varDef);
                    }
                    break;

                case XMLStreamConstants.CHARACTERS:
                    String text = parser.getText();
                    nameDef.isMale |= text.contains(MALE_CHAR);
                    nameDef.isFemale |= text.contains(FEMALE_CHAR);
 
                    if (captureMain) {
                        mainBuff.append(text);
                    }
                    if (inSC) {
                        scText = text;
                    } else if (inNameGrp) {
                        nameGrpText = text;
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("OOPS ... " + ex.getMessage());
            System.out.println("         " + xml);
            return null;
        }

        return (nameDef.id == null) ? null : nameDef;
    }

    String getAttrValue(XMLStreamReader parser, String key) {
        for (int ndx=0;  ndx<parser.getAttributeCount();  ndx++) {
            if (parser.getAttributeLocalName(ndx).equalsIgnoreCase(key)) {
                return parser.getAttributeValue(ndx);
            }
        }
        return null;
    }
}
