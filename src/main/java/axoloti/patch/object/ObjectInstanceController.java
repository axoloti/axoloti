package axoloti.patch.object;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.patch.PatchModel;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<IAxoObjectInstance, IView> {

    public ObjectInstanceController(IAxoObjectInstance model) {
        super(model);
    }

    public void changeLocation(int x, int y) {
        if ((getModel().getX() != x) || (getModel().getY() != y)) {
            Point p = new Point(x, y);
            setModelUndoableProperty(AxoObjectInstanceAbstract.OBJ_LOCATION, p);
        }
    }

    public void changeSelected(boolean selected) {
        setModelUndoableProperty(AxoObjectInstanceAbstract.OBJ_SELECTED, selected);
    }

    /*
     * Change the instance name of an object in a patch
     *
     * @param  instanceName new name
     * @return true if successful
     */
    public boolean changeInstanceName(String instanceName) {
        if (getModel().getInstanceName().equals(instanceName)) {
            return false;
        }
        PatchModel patch = getModel().getParent();
        if (patch != null) {
            IAxoObjectInstance o1 = patch.findObjectInstance(instanceName);
            if ((o1 != null) && (o1 != getModel())) {
                Logger.getLogger(ObjectInstanceController.class.getName()).log(Level.SEVERE, "Object name \"{0}\" already exists in patch!", instanceName);
                return false;
            }
        }
        setModelUndoableProperty(AxoObjectInstance.OBJ_INSTANCENAME, instanceName);
        return true;
    }

    public void changeOutletInstances(List<OutletInstance> outletInstances) {
        setModelUndoableProperty(AxoObjectInstance.OBJ_OUTLET_INSTANCES, outletInstances);
    }

    public void changeInletInstances(List<InletInstance> inletInstances) {
        setModelUndoableProperty(AxoObjectInstance.OBJ_INLET_INSTANCES, inletInstances);
    }

    public void changeParameterInstances(List<ParameterInstance> parameterInstances) {
        setModelUndoableProperty(AxoObjectInstance.OBJ_PARAMETER_INSTANCES, parameterInstances);
    }

    public void changeAttributeInstances(List<AttributeInstance> attributeInstances) {
        setModelUndoableProperty(AxoObjectInstance.OBJ_ATTRIBUTE_INSTANCES, attributeInstances);
    }

    public void changeDisplayInstances(List<DisplayInstance> displayInstances) {
        setModelUndoableProperty(AxoObjectInstance.OBJ_DISPLAY_INSTANCES, displayInstances);
    }

    public void changeComment(String comment) {
        setModelUndoableProperty(AxoObjectInstanceComment.COMMENT, comment);
    }

}
