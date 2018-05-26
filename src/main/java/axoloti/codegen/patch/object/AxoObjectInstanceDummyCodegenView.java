
package axoloti.codegen.patch.object;

import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.patch.object.IAxoObjectInstance;
import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

/**
 * Object instances that do not generate any source code :
 * comments and hyperlink objects
 * 
 * @author jtaelman
 */
class AxoObjectInstanceDummyCodegenView implements IAxoObjectInstanceCodegenView {

    final IAxoObjectInstance objectInstance;

    AxoObjectInstanceDummyCodegenView(IAxoObjectInstance objectInstance) {
        this.objectInstance = objectInstance;
    }

    @Override
    public IAxoObjectInstance getDModel() {
        return objectInstance;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public String generateUICode(int[] count) {
        return "";
    }

    @Override
    public String generateInitCodePlusPlus(String vprefix, boolean enableOnParent) {
        return "";
    }

    @Override
    public String generateDisposeCodePlusPlus(String vprefix) {
        return "";
    }

    @Override
    public String generateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        return "";
    }

    @Override
    public String generateCallMidiHandler() {
        return "";
    }

    @Override
    public void dispose() {
    }

    @Override
    public List<ParameterInstanceView> getParameterInstanceViews() {
        return Collections.emptyList();
    }

    @Override
    public List<DisplayInstanceView> getDisplayInstanceViews() {
        return Collections.emptyList();
    }

}
