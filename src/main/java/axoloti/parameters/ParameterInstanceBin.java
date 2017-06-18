package axoloti.parameters;

import axoloti.datatypes.Value;
import axoloti.datatypes.ValueInt32;
import axoloti.object.AxoObjectInstance;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author jtaelman
 */
public abstract class ParameterInstanceBin<T extends ParameterBin> extends ParameterInstance<T> {

    // used to be final, now it is mutating
    ValueInt32 value = new ValueInt32();

    @Attribute(name = "value", required = false)
    public int getValuex() {
        return value.getInt();
    }

    public ParameterInstanceBin() {
        super();
    }

    public ParameterInstanceBin(@Attribute(name = "value") int v) {
        value.setInt(v);
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
        int v = GetValueRaw();
        s += ", d: { bin: { finalvalue: 0"
                + ", value: " + v
                + ", modvalue: " + v
                + ", nbits: " + parameter.getNBits()
                + "}}},\n";
        return s;
    }

    @Override
    public ValueInt32 getValue() {
        return value;
    }

    @Override
    public void setValue(Value value) {
        Integer oldvalue = this.value.getInt();
        this.value = new ValueInt32(value);
        firePropertyChange(
                ParameterInstanceController.ELEMENT_PARAM_VALUE,
                oldvalue, (Integer) value.getInt());
    }

    public void setValue(ValueInt32 value) {
        Integer oldvalue = this.value.getInt();
        this.value = new ValueInt32(value);
        firePropertyChange(
                ParameterInstanceController.ELEMENT_PARAM_VALUE,
                oldvalue, (Integer) value.getInt());
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceBin) {
            ParameterInstanceBin p1 = (ParameterInstanceBin) p;
            presets = p1.presets;
            value.setRaw(p1.value.getRaw());
        }
    }
}
