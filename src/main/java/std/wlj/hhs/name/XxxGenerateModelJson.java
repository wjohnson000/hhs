/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.*;

import org.familysearch.homelands.admin.parser.model.NameModel;
import org.familysearch.homelands.lib.common.util.JsonUtility;
import org.familysearch.homelands.lib.common.util.TextUtility;

/**
 * @author wjohnson000
 *
 */
public class XxxGenerateModelJson {

    public static void main(String...args) throws Exception {
        NameModel nameModel;
        NameModel var1Model, var2Model, var3Model;
        List<NameModel> models = new ArrayList<>();

        /** HENRY */
        nameModel = new NameModel();
        nameModel.setId(null);
        nameModel.setExternalId("ext-001");
        nameModel.setLanguage("en");
        nameModel.setType("FIRST");
        nameModel.setText("Henry");
        nameModel.setNormalText(TextUtility.normalize(nameModel.getText()));
        nameModel.setDefinition("<p>A perennially popular given name, from <i>haim</i> ‘home’ + <i>rīc</i> ‘power, ruler’. It was an Old French name, adopted by the Normans and introduced by them to Britain. It has been borne by eight kings of England. Not until the 17th century did the form <i>Henry</i> (as opposed to Harry) become the standard vernacular form, mainly under the influence of the Latin form <i>Henricus</i> and French <i>Henri</i>.");
        nameModel.setMale(true);

        var1Model = new NameModel();
        var1Model.setLanguage("en");
        var1Model.setText("Harry");
        var1Model.setNormalText(TextUtility.normalize(var1Model.getText()));
        var1Model.setUsageType("SHORT");
        var1Model.setPreText("From French:");
        var1Model.setHtmlText("<b>Henri</b>");
        var1Model.setPostText(", or something");

        var2Model = new NameModel();
        var2Model.setLanguage("en");
        var2Model.setText("Hank");
        var2Model.setNormalText(TextUtility.normalize(var1Model.getText()));
        var2Model.setUsageType("PET");

        nameModel.getVariants().add(var1Model);
        nameModel.getVariants().add(var2Model);
        models.add(nameModel);

        /** JOCELYN */
        nameModel = new NameModel();
        nameModel.setId("AAAA-123");
        nameModel.setExternalId("ext-002");
        nameModel.setLanguage("en");
        nameModel.setType("FIRST");
        nameModel.setText("Jocylyn");
        nameModel.setNormalText(TextUtility.normalize(nameModel.getText()));
        nameModel.setDefinition("<p>Now normally a girl's name, but in earlier times more often given to boys. It is a transferred use of the English surname, which in turn is derived from an Old French masculine personal name introduced to Britain by the Normans in the form <i>Joscelin</i>. This was originally a derivative, <i>Gautzelin</i>, of the name of a Germanic tribe, the <i>Gauts</i>. The spelling of the first syllable was altered because the name was taken as a double diminutive (with the Old French suffixes <i>-el</i> and <i>-in</i>) of <i>Josce</i> (see Joyce).</p>");
        nameModel.setFemale(true);

        var1Model = new NameModel();
        var1Model.setLanguage("en");
        var1Model.setText("Jocelyne");
        var1Model.setNormalText(TextUtility.normalize(var1Model.getText()));
        var1Model.setUsageType("VARIANT");

        var2Model = new NameModel();
        var2Model.setLanguage("en");
        var2Model.setText("Joselyn");
        var2Model.setNormalText(TextUtility.normalize(var1Model.getText()));
        var2Model.setUsageType("PET");

        var3Model = new NameModel();
        var3Model.setLanguage("en");
        var3Model.setText("Joslyn");
        var3Model.setNormalText(TextUtility.normalize(var1Model.getText()));
        var3Model.setUsageType("SHORT");

        nameModel.getVariants().add(var1Model);
        nameModel.getVariants().add(var2Model);
        nameModel.getVariants().add(var3Model);
        models.add(nameModel);

        /** JOCELYN */
        nameModel = new NameModel();
        nameModel.setId(null);
        nameModel.setExternalId("ext-003");
        nameModel.setLanguage("en");
        nameModel.setType("FIRST");
        nameModel.setText("Siegrun");
        nameModel.setNormalText(TextUtility.normalize(nameModel.getText()));
        nameModel.setDefinition("<p>from <i>sige</i> ‘victory’ + <i>rūn</i> ‘rune’ or ‘magic’.</p><p>Also: Sigrun.");
        nameModel.setMale(true);
        models.add(nameModel);

        System.out.println(JsonUtility.parseObject(models).toPrettyString());
    }
}
