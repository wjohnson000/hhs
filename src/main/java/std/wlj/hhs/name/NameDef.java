/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class NameDef {
    String  id;
    String  text;
    String  normalText;
    String  language;
    String  refId;
    String  type;
    String  definition;
    boolean isMale;
    boolean isFemale;
    List<NameDef> variants = new ArrayList<>();
}
