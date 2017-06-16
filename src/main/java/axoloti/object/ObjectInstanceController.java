package axoloti.object;

import axoloti.PatchController;
import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceController;
import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceController;
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInstanceController;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInstanceController;
import axoloti.parameters.ParameterInstance;
import axoloti.parameters.ParameterInstanceController;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<AxoObjectInstanceAbstract, AbstractView, PatchController> {

    public static final String OBJ_LOCATION = "Location";
    public static final String OBJ_INSTANCENAME = "InstanceName";
    public static final String OBJ_PARAMETER_INSTANCES = "ParameterInstances";
    public static final String OBJ_ATTRIBUTE_INSTANCES = "AttributeInstances";
    public static final String OBJ_INLET_INSTANCES = "InletInstances";
    public static final String OBJ_OUTLET_INSTANCES = "OutletInstances";
    public static final String OBJ_DISPLAY_INSTANCES = "DisplayInstances";
    public static final String OBJ_COMMENT = "CommentText";

    public final ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController> attributeInstanceControllers;
    public final ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController> parameterInstanceControllers;
    public final ArrayController<InletInstanceController, InletInstance, ObjectInstanceController> inletInstanceControllers;
    public final ArrayController<OutletInstanceController, OutletInstance, ObjectInstanceController> outletInstanceControllers;
    public final ArrayController<DisplayInstanceController, DisplayInstance, ObjectInstanceController> displayInstanceControllers;

    public ObjectInstanceController(AxoObjectInstanceAbstract model, AbstractDocumentRoot documentRoot, PatchController parent) {
        super(model, documentRoot, parent);
        attributeInstanceControllers = new ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController>(getModel().getAttributeInstances(), documentRoot, this) {

            @Override
            public AttributeInstanceController createController(AttributeInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new AttributeInstanceController(model, documentRoot, parent);
            }
        };
        parameterInstanceControllers = new ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController>(getModel().getParameterInstances(), documentRoot, this) {

            @Override
            public ParameterInstanceController createController(ParameterInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new ParameterInstanceController(model, documentRoot, parent);
            }
        };
        inletInstanceControllers = new ArrayController<InletInstanceController, InletInstance, ObjectInstanceController>(getModel().getInletInstances(), documentRoot, this) {

            @Override
            public InletInstanceController createController(InletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new InletInstanceController(model, documentRoot, parent);
            }
        };
        outletInstanceControllers = new ArrayController<OutletInstanceController, OutletInstance, ObjectInstanceController>(getModel().getOutletInstances(), documentRoot, this) {

            @Override
            public OutletInstanceController createController(OutletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new OutletInstanceController(model, documentRoot, parent);
            }
        };
        displayInstanceControllers = new ArrayController<DisplayInstanceController, DisplayInstance, ObjectInstanceController>(getModel().getDisplayInstances(), documentRoot, this) {

            @Override
            public DisplayInstanceController createController(DisplayInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new DisplayInstanceController(model, documentRoot, parent);
            }
        };
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
