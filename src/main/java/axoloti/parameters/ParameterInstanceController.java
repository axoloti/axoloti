package axoloti.parameters;

import axoloti.datatypes.ValueInt32;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.parameterviews.IParameterInstanceView;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceController extends AbstractController<ParameterInstance, IParameterInstanceView> {

    public static final String ELEMENT_PARAM_VALUE = "Value";

    public ParameterInstanceController(ParameterInstance model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }

    public void changeRawValue(int rawValue) {
        setModelUndoableProperty(ELEMENT_PARAM_VALUE, new ValueInt32((Integer) rawValue));
    }

}
