/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
