/**
 * Copyright (C) 2013, 2014 Johannes Taelman
 *
 * This file is part of Axoloti.
 *
 * Axoloti is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Axoloti is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Axoloti. If not, see <http://www.gnu.org/licenses/>.
 */
package axoloti.parameters;

import axoloti.Modulation;
import axoloti.Modulator;
import axoloti.PresetDouble;
import axoloti.object.AxoObjectInstance;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterInstanceFrac32<Tx extends ParameterFrac32> extends ParameterInstance<Tx, Double> {

    @Attribute(name = "value", required = false)
    public double getValuex() {
        return value;
    }
    @ElementList(required = false)
    ArrayList<Modulation> modulators;

    @ElementListUnion({
        @ElementList(entry = "Preset", type = PresetDouble.class, inline = false, required = false)
    })
    ArrayList<PresetDouble> presets;

    Double value = 0.0;

    public ParameterInstanceFrac32(@Attribute(name = "value") double v) {
        value = v;
    }

    public ParameterInstanceFrac32() {
        //value = new ValueFrac32();
    }

    public abstract double getMin();

    public abstract double getMax();

    public abstract double getTick();

    public ParameterInstanceFrac32(Tx param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        //value = new ValueFrac32();
    }

    @Override
    public ByteBuffer getValueBB() {
        ByteBuffer bb = super.getValueBB();
        bb.putInt((int) ((1 << 21) * (double) value));
        return bb;
    }

    @Override
    public int valToInt32(Double v) {
        int f2i = 1 << 21;
        return (int) Math.round(v * f2i);
    }

    @Override
    public Double int32ToVal(int v) {
        double i2f = 1.0 / (1 << 21);
        return v * i2f;
    }

    @Override
    public PresetDouble presetFactory(int index, Double value) {
        return new PresetDouble(index, value);
    }

    @Override
    public ArrayList<PresetDouble> getPresets() {
        if (presets != null) {
            return presets;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void setPresets(Object presets) {
        ArrayList<PresetDouble> prevValue = getPresets();
        this.presets = (ArrayList<PresetDouble>) presets;
        firePropertyChange(PRESETS, prevValue, this.presets);
    }

    @Override
    public PresetDouble getPreset(int i) {
        return (PresetDouble) super.getPreset(i);
    }

    public void updateModulation(int index, double amount) {
        //System.out.println("updatemodulation1:" + index);
        if (amount != 0.0) {
            // existing modulation
            if (modulators == null) {
                modulators = new ArrayList<Modulation>();
            }
            Modulator modulator = axoObjectInstance.getPatchModel().getPatchModulators().get(index);
            //System.out.println("updatemodulation2:" + modulator.name);
            Modulation n = null;
            for (Modulation m : modulators) {
                if (m.source == modulator.objinst) {
                    if ((modulator.name == null) || (modulator.name.isEmpty())) {
                        n = m;
                        break;
                    } else if (modulator.name.equals(m.modName)) {
                        n = m;
                        break;
                    }
                }
            }
            if (n == null) {
                n = new Modulation();
                //System.out.println("updatemodulation3:" + n.sourceName);
                modulators.add(n);
            }
            n.source = modulator.objinst;
            n.sourceName = modulator.objinst.getInstanceName();
            n.modName = modulator.name;
            n.setValue(amount);
            n.destination = this;
            axoObjectInstance.getPatchModel().updateModulation(n);
        } else {
            // remove modulation target if exists
            Modulator modulator = axoObjectInstance.getPatchModel().getPatchModulators().get(index);
            if (modulator == null) {
                return;
            }
            for (int i = 0; i < modulator.Modulations.size(); i++) {
                Modulation n = modulator.Modulations.get(index);
                if (n.destination == this) {
                    modulator.Modulations.remove(n);
                }
            }
            for (int i = 0; i < modulators.size(); i++) {
                Modulation n = modulators.get(i);
                if (n.destination == this) {
                    modulators.remove(n);
                }
                axoObjectInstance.getPatchModel().updateModulation(n);
            }
            if (modulators.isEmpty()) {
                modulators = null;
            }
        }
    }

    @Override
    public ArrayList<Modulation> getModulators() {
        return modulators;
    }

    public void removeModulation(Modulation m) {
        modulators.remove(m);
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceFrac32) {
            ParameterInstanceFrac32 p1 = (ParameterInstanceFrac32) p;
            modulators = p1.getModulators();
            setValue(p1.getValue());
        }
    }

    @Override
    public String GenerateParameterInitializer() {
// { type: param_type_frac, unit: param_unit_abstract, signals: 0, pfunction: 0, d: { frac: { finalvalue:0,  0,  0,  0,  0}}},
//        String pname = GetUserParameterName();
        String s = "{ type: " + parameter.GetCType()
                + ", unit: " + parameter.GetCUnit()
                + ", signals: 0"
                + ", pfunction: " + ((GetPFunction() == null) ? "0" : GetPFunction());
        double v = getValue();
        s += ", d: { frac: { finalvalue: 0"
                + ", value: " + valToInt32(v)
                + ", modvalue: " + valToInt32(v)
                + ", offset: " + GetCOffset()
                + ", multiplier: " + GetCMultiplier()
                + "}}},\n";
        return s;
    }

    @Override
    public String variableName(String vprefix, boolean enableOnParent) {
        if (getOnParent() && (enableOnParent)) {
            return "%" + ControlOnParentName() + "%";
        } else {
            return PExName(vprefix) + ".d.frac.finalvalue";
        }
    }

    @Override
    public String valueName(String vprefix) {
        return PExName(vprefix) + ".value";
    }

    @Override
    public String GenerateCodeInitModulator(String vprefix, String StructAccces) {
        return "";
    }

    /**
     * **
     */
    @Override
    public Double getValue() {
        if (value == null) {
            return 0.0;
        } else {
            return value;
        }
    }

    @Override
    public void setValue(Object value) {
        Double oldvalue = this.value;
        this.value = (Double)value;
        needsTransmit = true;
        firePropertyChange(
                VALUE,
                oldvalue, value);
        if (paramOnParent != null) {
            paramOnParent.setDefaultValue((Double)value);
        }
    }

}
