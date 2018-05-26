package axoloti.patch.object.parameter;

import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.patch.object.parameter.preset.Preset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class ParameterInstanceController extends AbstractController<ParameterInstance, IView<ParameterInstance>> {

    protected ParameterInstanceController(ParameterInstance model) {
        super(model);
    }

    public Preset addPreset(int index, Object value) {
        Preset p = getModel().getPreset(index);
        List<Preset> new_presets = new ArrayList<>(getModel().getPresets());
        if (p != null) {
            new_presets.remove(p);
        }
        Preset pnew = getModel().presetFactory(index, value);
        new_presets.add(pnew);
        Collections.sort(new_presets, new Comparator<Preset>() {
            @Override
            public int compare(Preset o1, Preset o2) {
                return Integer.compare(o1.getIndex(), o2.getIndex());
            }
        });
        setModelUndoableProperty(ParameterInstance.PRESETS, new_presets);
        return pnew;
    }

    public void removePreset(int index) {
        Preset p = getModel().getPreset(index);
        if (p != null) {
            List<Preset> new_presets = new ArrayList<>(getModel().getPresets());
            new_presets.remove(p);
            setModelUndoableProperty(ParameterInstance.PRESETS, new_presets);
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
