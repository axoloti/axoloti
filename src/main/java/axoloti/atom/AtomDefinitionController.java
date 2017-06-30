package axoloti.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectController;

/**
 *
 * @author jtaelman
 */
public class AtomDefinitionController extends AbstractController<AtomDefinition, AtomInstance, ObjectController> {

    public static final String ATOM_NAME = "Name";
    public static final String ATOM_DESCRIPTION = "Description";
    public static final String ATOM_MINVALUE = "MinValue";
    public static final String ATOM_MAXVALUE = "MaxValue";
    public static final String ATOM_DEFAULTVALUE = "DefaultValue";
    public static final String ATOM_MENUENTRIES = "MenuEntries";
    public static final String ATOM_CENTRIES = "CEntries";    

    public static final String[] PROPERTY_NAMES = {ATOM_NAME, ATOM_DESCRIPTION};

    @Override
    public String[] getPropertyNames() {
        return PROPERTY_NAMES;
    }

    public AtomDefinitionController(AtomDefinition model, AbstractDocumentRoot documentRoot, ObjectController parent) {
        super(model, documentRoot, parent);
    }

}
