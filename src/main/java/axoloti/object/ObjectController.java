package axoloti.object;

import axoloti.atom.AtomDefinitionController;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.displays.Display;
import axoloti.inlets.Inlet;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.outlets.Outlet;
import axoloti.parameters.Parameter;
import axoloti.mvc.IView;
import axoloti.mvc.array.ArrayController;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author jtaelman
 */
public class ObjectController extends AbstractController<IAxoObject, IView, AbstractController> {


    public ArrayController<AtomDefinitionController, Inlet, ObjectController> inlets;
    public ArrayController<AtomDefinitionController, Outlet, ObjectController> outlets;
    public ArrayController<AtomDefinitionController, AxoAttribute, ObjectController> attrs;
    public ArrayController<AtomDefinitionController, Parameter, ObjectController> params;
    public ArrayController<AtomDefinitionController, Display, ObjectController> disps;

    public ObjectController(IAxoObject model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot, null);
        inlets = new ArrayController<AtomDefinitionController, Inlet, ObjectController>(this, AxoObject.OBJ_INLETS) {

            @Override
            public AtomDefinitionController createController(Inlet model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        outlets = new ArrayController<AtomDefinitionController, Outlet, ObjectController>(this, AxoObject.OBJ_OUTLETS) {

            @Override
            public AtomDefinitionController createController(Outlet model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        attrs = new ArrayController<AtomDefinitionController, AxoAttribute, ObjectController>(this, AxoObject.OBJ_ATTRIBUTES) {

            @Override
            public AtomDefinitionController createController(AxoAttribute model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        params = new ArrayController<AtomDefinitionController, Parameter, ObjectController>(this, AxoObject.OBJ_PARAMETERS) {

            @Override
            public AtomDefinitionController createController(Parameter model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        disps = new ArrayController<AtomDefinitionController, Display, ObjectController>(this, AxoObject.OBJ_DISPLAYS) {

            @Override
            public AtomDefinitionController createController(Display model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(AxoObject.OBJ_INLETS)) {
            inlets.syncControllers();
        } else if (propertyName.equals(AxoObject.OBJ_OUTLETS)) {
            outlets.syncControllers();
        } else if (propertyName.equals(AxoObject.OBJ_ATTRIBUTES)) {
            attrs.syncControllers();
        } else if (propertyName.equals(AxoObject.OBJ_PARAMETERS)) {
            params.syncControllers();
        } else if (propertyName.equals(AxoObject.OBJ_DISPLAYS)) {
            disps.syncControllers();
        }
        super.propertyChange(evt);
    }

}
