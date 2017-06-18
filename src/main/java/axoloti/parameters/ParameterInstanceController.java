package axoloti.parameters;

import axoloti.datatypes.ValueInt32;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectInstanceController;
import axoloti.parameterviews.IParameterInstanceView;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceController extends AbstractController<ParameterInstance, IParameterInstanceView, ObjectInstanceController> {

    public static final String ELEMENT_PARAM_VALUE = "Value";
    public static final String ELEMENT_PARAM_ON_PARENT = "OnParent";

    public ParameterInstanceController(ParameterInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

    public void changeRawValue(int rawValue) {
        setModelUndoableProperty(ELEMENT_PARAM_VALUE, new ValueInt32((Integer) rawValue));
    }

}
