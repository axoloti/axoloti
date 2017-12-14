package axoloti.parameters;

import axoloti.PresetInt;
import axoloti.object.AxoObjectInstance;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

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

    @ElementListUnion({
        @ElementList(entry = "Preset", type = PresetInt.class, inline = false, required = false)
    })
    ArrayList<PresetInt> presets;

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
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getOnParent() && (enableOnParent)) {
            return "%" + ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.bin.finalvalue";
        }
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
    public ArrayList<PresetInt> getPresets() {
        if (presets != null) {
            return presets;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setPresets(Object presets) {
        ArrayList<PresetInt> prevValue = getPresets();
        this.presets = (ArrayList<PresetInt>) presets;
        firePropertyChange(ParameterInstance.PRESETS, prevValue, this.presets);
    }

    @Override
    public PresetInt getPreset(int i) {
        return (PresetInt) super.getPreset(i);
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".d.bin.value";
    }

    @Override
    public String GenerateParameterInitializer() {
// { type: param_type_frac, unit: param_unit_abstract, signals: 0, pfunction: 0, d: { frac: { finalvalue:0,  0,  0,  0,  0}}},
//        String pname = GetUserParameterName();
        String s = "{ type: " + parameter.GetCType()
                + ", unit: " + parameter.GetCUnit()
                + ", signals: 0"
                + ", pfunction: " + ((GetPFunction() == null) ? "0" : GetPFunction());
        int v = getValue();
        s += ", d: { bin: { finalvalue: 0"
                + ", value: " + v
                + ", modvalue: " + v
                + ", nbits: " + parameter.getNBits()
                + "}}},\n";
        return s;
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceBin) {
            ParameterInstanceBin p1 = (ParameterInstanceBin) p;
            setValue(p1.getValue());
        }
    }

    @Override
    public ByteBuffer getValueBB() {
        ByteBuffer bb = super.getValueBB();
        bb.putInt(value);
        return bb;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        Integer oldvalue = this.value;
        this.value = (Integer)value;
        needsTransmit = true;
        firePropertyChange(
                ParameterInstance.VALUE,
                oldvalue, value);
    }
}
