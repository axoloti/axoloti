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
import axoloti.mvc.IView;
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
public class ObjectInstanceController extends AbstractController<IAxoObjectInstance, IView, PatchController> {


    public final ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController> attributeInstanceControllers;
    public final ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController> parameterInstanceControllers;
    public final ArrayController<InletInstanceController, InletInstance, ObjectInstanceController> inletInstanceControllers;
    public final ArrayController<OutletInstanceController, OutletInstance, ObjectInstanceController> outletInstanceControllers;
    public final ArrayController<DisplayInstanceController, DisplayInstance, ObjectInstanceController> displayInstanceControllers;

    public ObjectInstanceController(IAxoObjectInstance model, AbstractDocumentRoot documentRoot, PatchController parent) {
        super(model, documentRoot, parent);

        attributeInstanceControllers = new ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_ATTRIBUTE_INSTANCES) {

            @Override
            public AttributeInstanceController createController(AttributeInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new AttributeInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AttributeInstanceController controller) {
            }
        };
        parameterInstanceControllers = new ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_PARAMETER_INSTANCES) {

            @Override
            public ParameterInstanceController createController(ParameterInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new ParameterInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(ParameterInstanceController controller) {
            }
        };
        inletInstanceControllers = new ArrayController<InletInstanceController, InletInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_INLET_INSTANCES) {

            @Override
            public InletInstanceController createController(InletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new InletInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(InletInstanceController controller) {
                if (getParent() != null) {
                    getParent().disconnect(controller.getModel());
                }
            }
        };
        outletInstanceControllers = new ArrayController<OutletInstanceController, OutletInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_OUTLET_INSTANCES) {

            @Override
            public OutletInstanceController createController(OutletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new OutletInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(OutletInstanceController controller) {
                if (getParent() != null) {
                    getParent().disconnect(controller.getModel());
                }
            }
        };
        displayInstanceControllers = new ArrayController<DisplayInstanceController, DisplayInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_DISPLAY_INSTANCES) {

            @Override
            public DisplayInstanceController createController(DisplayInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new DisplayInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(DisplayInstanceController controller) {
            }
        };
    }

    public void changeLocation(int x, int y) {
        if ((getModel().getX() != x) || (getModel().getY() != y)) {
            Point p = new Point(x, y);
            setModelUndoableProperty(AxoObjectInstance.OBJ_LOCATION, p);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (AxoObjectInstance.OBJ_PARAMETER_INSTANCES.is(evt)) {
            parameterInstanceControllers.syncControllers();
        } else if (AxoObjectInstance.OBJ_ATTRIBUTE_INSTANCES.is(evt)) {
            attributeInstanceControllers.syncControllers();
        } else if (AxoObjectInstance.OBJ_DISPLAY_INSTANCES.is(evt)) {
            displayInstanceControllers.syncControllers();
        } else if (AxoObjectInstance.OBJ_INLET_INSTANCES.is(evt)) {
            inletInstanceControllers.syncControllers();
        } else if (AxoObjectInstance.OBJ_OUTLET_INSTANCES.is(evt)) {
            outletInstanceControllers.syncControllers();
        }
        super.propertyChange(evt);
        if (getParent()!=null){
            getParent().checkCoherency();
        }
    }

}
