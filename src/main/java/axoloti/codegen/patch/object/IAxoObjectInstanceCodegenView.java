package axoloti.codegen.patch.object;

import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.mvc.IView;
import axoloti.patch.object.IAxoObjectInstance;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public interface IAxoObjectInstanceCodegenView extends IView<IAxoObjectInstance> {

    String generateUICode(int count[]);

    String generateInitCodePlusPlus(String vprefix, boolean enableOnParent);

    String generateDisposeCodePlusPlus(String vprefix);

    String generateClass(String ClassName, String OnParentAccess, Boolean enableOnParent);

    String generateCallMidiHandler();

    List<ParameterInstanceView> getParameterInstanceViews();

    List<DisplayInstanceView> getDisplayInstanceViews();
}
