package axoloti.parameters;

import axoloti.Preset;
import axoloti.datatypes.Value;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectInstanceController;
import axoloti.parameterviews.IParameterInstanceView;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceController extends AbstractController<ParameterInstance, IParameterInstanceView, ObjectInstanceController> {

    
    public ParameterInstanceController(ParameterInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

//    public void changeRawValue(int rawValue) {
//        setModelUndoableProperty(ELEMENT_PARAM_VALUE, new ValueInt32((Integer) rawValue));
//    }
    
    public Preset AddPreset(int index, Value value) {
        if (getModel().getPresets() == null) {
            ArrayList<Preset> new_presets = new ArrayList<Preset>();
            Preset p = new Preset(index, value);
            new_presets.add(p);
            setModelUndoableProperty(ParameterInstance.ELEMENT_PARAM_PRESETS, new_presets);
            return p;
        }
        Preset p = getModel().GetPreset(index);
        ArrayList<Preset> new_presets = (ArrayList<Preset>) getModel().getPresets().clone();
        if (p != null) {
            new_presets.remove(p);
        }
        Preset pnew = new Preset(index, value);
        new_presets.add(pnew);
        setModelUndoableProperty(ParameterInstance.ELEMENT_PARAM_PRESETS, new_presets);
        return pnew;
    }

    public void RemovePreset(int index) {
        Preset p = getModel().GetPreset(index);
        if (p != null) {
            ArrayList<Preset> presets = (ArrayList<Preset>) getModel().getPresets().clone();
            presets.remove(p);
            setModelUndoableProperty(ParameterInstance.ELEMENT_PARAM_PRESETS, presets);
        }
    }

    public void applyDefaultValue() {
        Value d = (getModel().parameter).getDefaultValue();
        if (d != null) {
            setModelUndoableProperty(ParameterInstance.ELEMENT_PARAM_VALUE, d);
        } else {
            setModelUndoableProperty(ParameterInstance.ELEMENT_PARAM_VALUE, d);
        }
    }    

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        /*
        if (evt.getPropertyName().equals(ELEMENT_PARAM_PARAM_ON_PARENT)) {
            PatchController pc = getParent().getParent();
            ObjectInstancePatcherController oipc = (ObjectInstancePatcherController) pc.getParent();
            AxoObjectPatcher aop = (AxoObjectPatcher) oipc.getModel().getController().getModel();
            ArrayList<Parameter> ps = new ArrayList<>(aop.getParameters());
            if (evt.getNewValue() != null) {
                ps.add((Parameter) evt.getNewValue());
                // TODO: sort
            }
            if (evt.getOldValue() != null) {
                ps.remove((Parameter) evt.getOldValue());                
            }
            aop.setParameters(ps);
        }
        */
    }

}
