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
public interface IAxoObjectInstanceCodegenView extends IView {

    String GenerateUICode(int count[]);

    String GenerateInitCodePlusPlus(String vprefix, boolean enableOnParent);

    String GenerateDisposeCodePlusPlus(String vprefix);

    String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent);

    String GenerateCallMidiHandler();

    IAxoObjectInstance getModel();

    List<ParameterInstanceView> getParameterInstanceViews();

    List<DisplayInstanceView> getDisplayInstanceViews();
}
