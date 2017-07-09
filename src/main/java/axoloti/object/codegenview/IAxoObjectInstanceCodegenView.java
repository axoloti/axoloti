package axoloti.object.codegenview;

import axoloti.mvc.IView;
import axoloti.object.IAxoObjectInstance;

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
