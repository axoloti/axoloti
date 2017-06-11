package axoloti.object.codegenview;

import axoloti.mvc.AbstractView;
import axoloti.object.AxoObjectInstanceAbstract;

/**
 *
 * @author jtaelman
 */
public abstract class AxoObjectInstanceAbstractCodegenView implements AbstractView {
   
    abstract public String GenerateUICode(int count[]);

    abstract public String GenerateInitCodePlusPlus(String vprefix, boolean enableOnParent);

    abstract public String GenerateDisposeCodePlusPlus(String vprefix);

    abstract public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent);

    abstract public AxoObjectInstanceAbstract getModel();
}
