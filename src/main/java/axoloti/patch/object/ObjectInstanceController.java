package axoloti.patch.object;

import axoloti.patch.PatchController;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;
import axoloti.mvc.array.ArrayController;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.patch.object.parameter.ParameterInstanceController;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<IAxoObjectInstance, IView, PatchController> {


    public final ArrayController<AttributeInstanceController, AttributeInstance, ObjectInstanceController> attributeInstanceControllers;
    public final ArrayController<ParameterInstanceController, ParameterInstance, ObjectInstanceController> parameterInstanceControllers;
    public final ArrayController<IoletInstanceController, InletInstance, ObjectInstanceController> inletInstanceControllers;
    public final ArrayController<IoletInstanceController, OutletInstance, ObjectInstanceController> outletInstanceControllers;
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
        inletInstanceControllers = new ArrayController<IoletInstanceController, InletInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_INLET_INSTANCES) {

            @Override
            public IoletInstanceController createController(InletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new IoletInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(IoletInstanceController controller) {
                if (getParent() != null) {
                    getParent().disconnect(controller.getModel());
                }
            }
        };
        outletInstanceControllers = new ArrayController<IoletInstanceController, OutletInstance, ObjectInstanceController>(this, AxoObjectInstance.OBJ_OUTLET_INSTANCES) {

            @Override
            public IoletInstanceController createController(OutletInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
                return new IoletInstanceController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(IoletInstanceController controller) {
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
