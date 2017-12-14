package axoloti.parameters;

import axoloti.Preset;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.object.ObjectInstanceController;
import axoloti.parameterviews.IParameterInstanceView;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceController extends AbstractController<ParameterInstance, IParameterInstanceView, ObjectInstanceController> {

    public ParameterInstanceController(ParameterInstance model, AbstractDocumentRoot documentRoot, ObjectInstanceController parent) {
        super(model, documentRoot, parent);
    }

    public Preset AddPreset(int index, Object value) {
        if (getModel().getPresets() == null) {
            ArrayList<Preset> new_presets = new ArrayList<Preset>();
            Preset p = getModel().presetFactory(index, value);
            new_presets.add(p);
            setModelUndoableProperty(ParameterInstance.PRESETS, new_presets);
            return p;
        }
        Preset p = getModel().getPreset(index);
        ArrayList<Preset> new_presets = (ArrayList<Preset>) getModel().getPresets().clone();
        if (p != null) {
            new_presets.remove(p);
        }
        Preset pnew = getModel().presetFactory(index, value);
        new_presets.add(pnew);
        setModelUndoableProperty(ParameterInstance.PRESETS, new_presets);
        return pnew;
    }

    public void RemovePreset(int index) {
        Preset p = getModel().getPreset(index);
        if (p != null) {
            ArrayList<Preset> presets = (ArrayList<Preset>) getModel().getPresets().clone();
            presets.remove(p);
            setModelUndoableProperty(ParameterInstance.PRESETS, presets);
        }
    }

    public void applyDefaultValue() {
        Object d = (getModel().parameter).getDefaultValue();
        if (d != null) {
            setModelUndoableProperty(ParameterInstance.VALUE, d);
        } else {
            setModelUndoableProperty(ParameterInstance.VALUE, d);
        }
    }

}
