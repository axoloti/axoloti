/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    final ValueInt32 value = new ValueInt32();

    @Attribute(name = "value", required = false)
    public int getValuex() {
        return value.getInt();
    }

    public ParameterInstanceBin() {
    }

    public ParameterInstanceBin(@Attribute(name = "value") int v) {
        value.setInt(v);
    }

    public ParameterInstanceBin(T param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (isOnParent() && (enableOnParent)) {
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
        this.value.setInt(value.getInt());
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
