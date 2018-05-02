
package axoloti.codegen.patch.object;

import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Object instances that do not generate any source code :
 * comments and hyperlink objects
 * 
 * @author jtaelman
 */
class AxoObjectInstanceDummyCodegenView implements IAxoObjectInstanceCodegenView {

    final ObjectInstanceController controller;

    AxoObjectInstanceDummyCodegenView(ObjectInstanceController controller) {
        this.controller = controller;
    }

    @Override
    public IAxoObjectInstance getModel() {
        return controller.getModel();
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public ObjectInstanceController getController() {
        return controller;
    }

    @Override
    public String GenerateUICode(int[] count) {
        return "";
    }

    @Override
    public String GenerateInitCodePlusPlus(String vprefix, boolean enableOnParent) {
        return "";
    }

    @Override
    public String GenerateDisposeCodePlusPlus(String vprefix) {
        return "";
    }

    @Override
    public String GenerateClass(String ClassName, String OnParentAccess, Boolean enableOnParent) {
        return "";
    }

    @Override
    public String GenerateCallMidiHandler() {
        return "";
    }

    @Override
    public void dispose() {
    }

    @Override
    public List<ParameterInstanceView> getParameterInstanceViews() {
        return new ArrayList<>();
    }

    @Override
    public List<DisplayInstanceView> getDisplayInstanceViews() {
        return new ArrayList<>();
    }

}
