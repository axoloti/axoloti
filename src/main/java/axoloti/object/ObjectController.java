package axoloti.object;

import axoloti.atom.AtomDefinitionController;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.displays.Display;
import axoloti.inlets.Inlet;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import axoloti.outlets.Outlet;
import axoloti.parameters.Parameter;
import axoloti.parameters.ParameterInstanceFactory;

/**
 *
 * @author jtaelman
 */
public class ObjectController extends AbstractController<AxoObjectAbstract, AbstractView, AbstractController> {

    public ArrayController<AtomDefinitionController, Inlet, ObjectController> inlets;
    public ArrayController<AtomDefinitionController, Outlet, ObjectController> outlets;
    public ArrayController<AtomDefinitionController, AxoAttribute, ObjectController> attrs;
    public ArrayController<AtomDefinitionController, Parameter, ObjectController> params;
    public ArrayController<AtomDefinitionController, Display, ObjectController> disps;

    public ObjectController(AxoObjectAbstract model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot, null);
        AbstractController c = null;
        if (model instanceof AxoObject) {
            inlets = new ArrayController<AtomDefinitionController, Inlet, ObjectController>(model.getInlets(), documentRoot, this) {

                @Override
                public AtomDefinitionController createController(Inlet model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                    return model.createController(documentRoot, parent);
                }
            };
            outlets = new ArrayController<AtomDefinitionController, Outlet, ObjectController>(model.getOutlets(), documentRoot, this) {

                @Override
                public AtomDefinitionController createController(Outlet model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                    return model.createController(documentRoot, parent);
                }
            };
            AxoObject m = (AxoObject) model;
            attrs = new ArrayController<AtomDefinitionController, AxoAttribute, ObjectController>(m.attributes, documentRoot, this) {

                @Override
                public AtomDefinitionController createController(AxoAttribute model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                    return model.createController(documentRoot, parent);
                }
            };
            params = new ArrayController<AtomDefinitionController, Parameter, ObjectController>(m.params, documentRoot, this) {

                @Override
                public AtomDefinitionController createController(Parameter model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                    return model.createController(documentRoot, parent);
                }
            };
            disps = new ArrayController<AtomDefinitionController, Display, ObjectController>(m.displays, documentRoot, this) {

                @Override
                public AtomDefinitionController createController(Display model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                    return model.createController(documentRoot, parent);
                }
            };
        }
    }

}
