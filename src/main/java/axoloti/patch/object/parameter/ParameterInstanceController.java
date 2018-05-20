package axoloti.patch.object.parameter;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.patch.object.parameter.preset.Preset;
import java.util.ArrayList;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceController extends AbstractController<ParameterInstance, IView<ParameterInstance>> {

    protected ParameterInstanceController(ParameterInstance model) {
        super(model);
    }

    public Preset addPreset(int index, Object value) {
        if (getModel().getPresets() == null) {
            ArrayList<Preset> new_presets = new ArrayList<>();
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

    public void removePreset(int index) {
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
            // FIXME: errors when changing parameter type frac->int
            setModelUndoableProperty(ParameterInstance.VALUE, d);
        } else {
            // FIXME: Integer 0 or Double 0.0 ?
            setModelUndoableProperty(ParameterInstance.VALUE, d);
        }
    }

    public void changeOnParent(boolean onParent) {
        setModelUndoableProperty(ParameterInstance.ON_PARENT, onParent);
    }

    public void changeValue(Object value) {
        setModelUndoableProperty(ParameterInstance.VALUE, value);
    }

}
