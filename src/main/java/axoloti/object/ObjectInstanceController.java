package axoloti.object;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<AxoObjectInstanceAbstract, AbstractView> {

    public static final String OBJ_LOCATION = "Location";
    public static final String OBJ_INSTANCENAME = "InstanceName";
    public static final String OBJ_PARAMETER_INSTANCES = "ParameterInstances";
    public static final String OBJ_ATTRIBUTE_INSTANCES = "AttributeInstances";
    public static final String OBJ_INLET_INSTANCES = "InletInstances";
    public static final String OBJ_OUTLET_INSTANCES = "OutletInstances";
    public static final String OBJ_DISPLAY_INSTANCES = "DisplayInstances";
    public static final String OBJ_COMMENT = "CommentText";

    public ArrayController attributeInstanceControllers;
    public ArrayController parameterInstanceControllers;
    public ArrayController inletInstanceControllers;
    public ArrayController outletInstanceControllers;
    public ArrayController displayInstanceControllers;

    public ObjectInstanceController(AxoObjectInstanceAbstract model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        attributeInstanceControllers = new ArrayController(getModel().getAttributeInstances(), documentRoot);
        parameterInstanceControllers = new ArrayController(getModel().getParameterInstances(), documentRoot);
        inletInstanceControllers = new ArrayController(getModel().getInletInstances(), documentRoot);
        outletInstanceControllers = new ArrayController(getModel().getOutletInstances(), documentRoot);
        displayInstanceControllers = new ArrayController(getModel().getDisplayInstances(), documentRoot);
    }

    public void changeLocation(int x, int y) {
        if ((getModel().getX() != x) || (getModel().getY() != y)) {
            Point p = new Point(x, y);
            setModelUndoableProperty(OBJ_LOCATION, p);
        }
    }

    public void changeInstanceName(String s) {
        setModelUndoableProperty(OBJ_INSTANCENAME, s);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(OBJ_PARAMETER_INSTANCES)) {
            parameterInstanceControllers.syncControllers();
        }
        if (evt.getPropertyName().equals(OBJ_ATTRIBUTE_INSTANCES)) {
            attributeInstanceControllers.syncControllers();
        }
        if (evt.getPropertyName().equals(OBJ_INLET_INSTANCES)) {
            inletInstanceControllers.syncControllers();
        }
        if (evt.getPropertyName().equals(OBJ_OUTLET_INSTANCES)) {
            outletInstanceControllers.syncControllers();
        }
        if (evt.getPropertyName().equals(OBJ_DISPLAY_INSTANCES)) {
            displayInstanceControllers.syncControllers();
        }
        super.propertyChange(evt);
    }

}
