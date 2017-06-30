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

    public static final String OBJ_ID = "Id";
    public static final String OBJ_DESCRIPTION = "Description";
    public static final String OBJ_LICENSE = "License";
    public static final String OBJ_PATH = "Path";
    public static final String OBJ_AUTHOR = "Author";
    public static final String OBJ_HELPPATCH = "HelpPatch";

    public static final String OBJ_INLETS = "Inlets";
    public static final String OBJ_OUTLETS = "Outlets";
    public static final String OBJ_ATTRIBUTES = "Attributes";
    public static final String OBJ_PARAMETERS = "Parameters";
    public static final String OBJ_DISPLAYS = "Displays";

    public static final String OBJ_INIT_CODE = "InitCode";
    public static final String OBJ_DISPOSE_CODE = "DisposeCode";
    public static final String OBJ_LOCAL_DATA = "LocalData";
    public static final String OBJ_KRATE_CODE = "KRateCode";
    public static final String OBJ_SRATE_CODE = "SRateCode";
    public static final String OBJ_MIDI_CODE = "MidiCode";

    @Override
    public String[] getPropertyNames() {
        return new String[]{
            OBJ_ID,
            OBJ_DESCRIPTION,
            OBJ_LICENSE,
            OBJ_PATH,
            OBJ_AUTHOR,
            OBJ_HELPPATCH,
            OBJ_INLETS,
            OBJ_OUTLETS,
            OBJ_ATTRIBUTES,
            OBJ_PARAMETERS,
            OBJ_DISPLAYS,
            OBJ_INIT_CODE,
            OBJ_DISPOSE_CODE,
            OBJ_LOCAL_DATA,
            OBJ_KRATE_CODE,
            OBJ_SRATE_CODE,
            OBJ_MIDI_CODE
        };
    }

    public ArrayController<AtomDefinitionController, Inlet, ObjectController> inlets;
    public ArrayController<AtomDefinitionController, Outlet, ObjectController> outlets;
    public ArrayController<AtomDefinitionController, AxoAttribute, ObjectController> attrs;
    public ArrayController<AtomDefinitionController, Parameter, ObjectController> params;
    public ArrayController<AtomDefinitionController, Display, ObjectController> disps;

    public ObjectController(IAxoObject model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot, null);
        inlets = new ArrayController<AtomDefinitionController, Inlet, ObjectController>(this, OBJ_INLETS) {

            @Override
            public AtomDefinitionController createController(Inlet model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        outlets = new ArrayController<AtomDefinitionController, Outlet, ObjectController>(this, OBJ_OUTLETS) {

            @Override
            public AtomDefinitionController createController(Outlet model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        attrs = new ArrayController<AtomDefinitionController, AxoAttribute, ObjectController>(this, OBJ_ATTRIBUTES) {

            @Override
            public AtomDefinitionController createController(AxoAttribute model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        params = new ArrayController<AtomDefinitionController, Parameter, ObjectController>(this, OBJ_PARAMETERS) {

            @Override
            public AtomDefinitionController createController(Parameter model, AbstractDocumentRoot documentRoot, ObjectController parent) {
                return new AtomDefinitionController(model, documentRoot, parent);
            }

            @Override
            public void disposeController(AtomDefinitionController controller) {
            }
        };
        disps = new ArrayController<AtomDefinitionController, Display, ObjectController>(this, OBJ_DISPLAYS) {

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
        if (propertyName.equals(OBJ_INLETS)) {
            inlets.syncControllers();
        } else if (propertyName.equals(OBJ_OUTLETS)) {
            outlets.syncControllers();
        } else if (propertyName.equals(OBJ_ATTRIBUTES)) {
            attrs.syncControllers();
        } else if (propertyName.equals(OBJ_PARAMETERS)) {
            params.syncControllers();
        } else if (propertyName.equals(OBJ_DISPLAYS)) {
            disps.syncControllers();
        }
        super.propertyChange(evt);
    }

}
