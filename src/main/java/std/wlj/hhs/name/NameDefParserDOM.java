/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class NameDefParserDOM implements NameDefParser {

    private static DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

    @Override
    public NameDef parseXml(String xml) {
        NameDef nameDef = new NameDef();

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(bais);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            nameDef.id = getTagAttr(doc, "e", "id");
            nameDef.text = getTagText(doc, "headword");
            nameDef.language = "en";
            nameDef.refId = getXref(doc);
            nameDef.definition = getDefinition(doc);
            nameDef.type = this.extractTypeFromDefinition(getTagText(doc, "div1"));
            nameDef.isMale = xml.contains(MALE_CHAR);
            nameDef.isFemale = xml.contains(FEMALE_CHAR);
            nameDef.variants = getVariants(doc);
        } catch(Exception ex) {
            System.out.println("OOPS ... " + ex.getMessage());
            System.out.println("         " + xml);
            nameDef.id = null; 
        }

        return (nameDef.id == null) ? null : nameDef;
    }

    String getTagText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        } else {
            return ((Element)nodes.item(0)).getTextContent();
        }
    }

    String getTagAttr(Document doc, String tagName, String attrName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        } else {
            return ((Element)nodes.item(0)).getAttribute(attrName);
        }
    }

    String getXref(Document doc) {
        NodeList nodes = doc.getElementsByTagName("xrefGrp");

        for (int ndx=0;  ndx<nodes.getLength();  ndx++) {
            Node node = nodes.item(ndx);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)node;
                NodeList children = element.getElementsByTagName("xref");
                if (children.getLength() > 0) {
                    return ((Element)children.item(0)).getAttribute("ref");
                }
            }
        }

        return null;
    }

    String getDefinition(Document doc) {
        NodeList nodes = doc.getElementsByTagName("textMatter");
        if (nodes.getLength() == 0) {
            return "";
        } else {
            return getTextWithTags((Element)nodes.item(0));
        }
    }

    List<NameDef> getVariants(Document doc) {
        List<NameDef> variants = new ArrayList<>();

        NodeList nodes = doc.getElementsByTagName("note");
        for (int i=0;  i<nodes.getLength();  i++) {
            Element node = (Element)nodes.item(i);
            NodeList sc = node.getElementsByTagName("sc");
            NodeList names = node.getElementsByTagName("nameGrp");

            String type = "COGNATE";
            if (sc.getLength() > 0) {
                Element scNode = (Element)sc.item(0);
                type = this.extractTypeFromDefinitionVariant(scNode.getTextContent());
            }

            for (int j=0;  j<names.getLength();  j++) {
                Element name = (Element)names.item(j);
                NameDef varDef = new NameDef();
                varDef.id = "";
                varDef.text = name.getTextContent().replaceAll("<b>", "").replaceAll("</b>", "").trim();
                varDef.language = "";
                varDef.type = type;
                variants.add(varDef);
            }
        }

        return variants;
    }

    String getTextWithTags(Element head) {
        if (! head.hasChildNodes()) {
            return head.getTextContent(); 
        } else  {
            StringBuilder buff = new StringBuilder();
            NodeList children = head.getChildNodes();
            for (int ndx=0;  ndx<children.getLength();  ndx++) {
                Node childNode = children.item(ndx);
                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element)children.item(ndx);
                    if (OK_TEXT_TAGS.contains(child.getTagName().toLowerCase())) {
                        buff.append("<").append(child.getNodeName());
                        NamedNodeMap attrs = child.getAttributes();
                        if (attrs != null  &&  attrs.getLength() > 0) {
                            for (int cdx=0;  cdx<attrs.getLength();  cdx++) {
                                buff.append(" ").append(attrs.item(cdx).getNodeName());
                                buff.append("=\"").append(attrs.item(cdx).getNodeValue()).append("\"");
                            }
                        }
                        buff.append(">");
                    }

                    buff.append(getTextWithTags(child));

                    if (OK_TEXT_TAGS.contains(child.getTagName().toLowerCase())) {
                        buff.append("</").append(child.getTagName()).append(">");
                    }
                } else {
                    buff.append(childNode.getNodeValue());
                }
            }

            return buff.toString();
        }
    }

}
