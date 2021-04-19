package hhs.core.name.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple model for name definitions.  Since this is *test* code, for now the instance variables are public, and
 * no SET-ter or GET-ter methods are needed.
 * 
 * @author wjohnson000
 *
 */
public class NameDef {
    public String  id;
    public String  text;
    public String  normalText;
    public String  language;
    public String  refId;
    public String  type;
    public String  definition;
    public boolean isMale;
    public boolean isFemale;
    public List<NameDef> variants = new ArrayList<>();
}
