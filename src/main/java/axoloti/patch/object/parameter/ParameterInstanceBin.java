package axoloti.patch.object.parameter;

import axoloti.object.parameter.ParameterBin;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.parameter.preset.Preset;
import axoloti.patch.object.parameter.preset.PresetInt;
import axoloti.utils.ListUtils;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterInstanceBin<T extends ParameterBin> extends ParameterInstance<T, Integer> {

    Integer value = 0;

    @Attribute(name = "value", required = false)
    public int getValuex() {
        return value;
    }

    @Path("presets")
    @ElementListUnion({
        @ElementList(entry = "preset", type = PresetInt.class, inline = true, required = false)
    })
    List<Preset> presets = new ArrayList<>();

    public ParameterInstanceBin() {
        super();
    }

    public ParameterInstanceBin(@Attribute(name = "value") int v) {
        value = v;
    }

    public ParameterInstanceBin(T param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public int valToInt32(Integer v) {
        return (int) v;
    }

    @Override
    public Integer int32ToVal(int v) {
        return v;
    }

    @Override
    public PresetInt presetFactory(int index, Integer value) {
        return new PresetInt(index, value);
    }

    @Override
    public List<Preset> getPresets() {
        return ListUtils.export(presets);
    }

    @Override
    public void setPresets(List<Preset> presets) {
        List<Preset> prevValue = getPresets();
        this.presets = presets;
        firePropertyChange(ParameterInstance.PRESETS, prevValue, this.presets);
    }

    @Override
    public PresetInt getPreset(int i) {
        return (PresetInt) super.getPreset(i);
    }

    @Override
    public void copyValueFrom(ParameterInstance p) {
        super.copyValueFrom(p);
        if (p instanceof ParameterInstanceBin) {
            ParameterInstanceBin p1 = (ParameterInstanceBin) p;
            setValue(p1.getValue());
        }
    }


    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        Integer oldvalue = this.value;
        this.value = (Integer)value;
        firePropertyChange(
                ParameterInstance.VALUE,
                oldvalue, value);
    }
}
