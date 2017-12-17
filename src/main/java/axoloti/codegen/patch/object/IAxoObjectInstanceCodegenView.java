package axoloti.codegen.patch.object;

import axoloti.mvc.IView;
import axoloti.patch.object.IAxoObjectInstance;

/**
 *
 * @author jtaelman
 */
public interface IAxoObjectInstanceCodegenView extends IView {
   
    public String GenerateUICode(int count[]);

    public String GenerateInitCodePlusPlus(String vprefix, boolean enableOnParent);

    public String GenerateDisposeCodePlusPlus(String vprefix);

    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent);

    public String GenerateCallMidiHandler();
    
    public IAxoObjectInstance getModel();
}
