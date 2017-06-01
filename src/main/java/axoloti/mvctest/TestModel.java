package axoloti.mvctest;

import axoloti.attribute.AttributeInstance;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletFrac32Bipolar;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.array.ArrayModel;
import axoloti.parameters.Parameter;
import axoloti.parameters.ParameterBin16;
import axoloti.parameters.ParameterFrac32SMap;
import axoloti.parameters.ParameterInstance;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class TestModel extends AbstractModel {

    int spinvalue;
    ArrayList<Parameter> params;
    ArrayModel<ParameterInstance> paramInstances;

    ArrayList<AxoAttribute> attributes;
    ArrayModel<AttributeInstance> attributeInstances;

    ArrayList<Inlet> inlets;
    ArrayModel<InletInstance> inletInstances;

    
    public TestModel() {
        // define some test parameters
        params = new ArrayList<>();
        params.add(new ParameterBin16("cccc"));
        params.add(new ParameterFrac32SMap("pitch"));
        params.add(new ParameterFrac32SMap("pitch2"));
        
        paramInstances = new ArrayModel();
        for (Parameter param : params) {
            ParameterInstance pi = param.CreateInstance(null);
            pi.parameter = param;
            paramInstances.add(pi);
        }
        
        // define some test attributes
        attributes = new ArrayList<>();
        attributes.add(new AxoAttributeSpinner("int32", 0, 7, 1));
        
        attributeInstances = new ArrayModel();
        for (AxoAttribute attr : attributes) {
            AttributeInstance pi = attr.CreateInstance(null);
            attributeInstances.add(pi);
        }
        
        // define some test attributes
        inlets = new ArrayList<>();
        inlets.add(new InletFrac32Bipolar("inlet", "test inlet"));
        
        inletInstances = new ArrayModel();
        for (Inlet inl : inlets) {
            InletInstance ii = inl.CreateInstance(null);
            inletInstances.add(ii);
        }
    }

    public void setSpin(Integer value) {
        Integer oldvalue = spinvalue;
        spinvalue = value;
        firePropertyChange(
                TestController.ELEMENT_SPIN_VALUE,
                oldvalue, value);
    }

    public Integer getSpin() {
        return spinvalue;
    }

    @Override
    public AbstractController createController(AbstractDocumentRoot documentRoot) {
        return new TestController(this, documentRoot);
    }

}
