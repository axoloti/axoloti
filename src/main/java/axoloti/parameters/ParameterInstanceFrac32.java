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
import axoloti.datatypes.Frac32;
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueFrac32;
import axoloti.object.AxoObjectInstance;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterInstanceFrac32<Tx extends ParameterFrac32> extends ParameterInstance<Tx> {

    @Attribute(name = "value", required = false)
    public double getValuex() {
        return value.getDouble();
    }
    @ElementList(required = false)
    ArrayList<Modulation> modulators;

    final ValueFrac32 value = new ValueFrac32();

    public ParameterInstanceFrac32(@Attribute(name = "value") double v) {
        value.setDouble(v);
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
    public Value<Frac32> getValue() {
        return value;
    }

    @Override
    public void setValue(Value value) {
        super.setValue(value);
        this.value.setDouble(value.getDouble());
    }

    @Override
    public void applyDefaultValue() {
        if (((ParameterFrac32) parameter).DefaultValue != null) {
            value.setRaw(((ParameterFrac32) parameter).DefaultValue.getRaw());
        } else {
            value.setRaw(0);
        }
        needsTransmit = true;
    }

    public void updateModulation(int index, double amount) {
        //System.out.println("updatemodulation1:" + index);
        if (amount != 0.0) {
            // existing modulation
            if (modulators == null) {
                modulators = new ArrayList<Modulation>();
            }
            Modulator modulator = axoObjectInstance.getPatchModel().getModulators().get(index);
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
            n.getValue().setDouble(amount);
            n.destination = this;
            axoObjectInstance.getPatchModel().updateModulation(n);
        } else {
            // remove modulation target if exists
            Modulator modulator = axoObjectInstance.getPatchModel().getModulators().get(index);
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
    public Parameter getParameterForParent() {
        Parameter p = super.getParameterForParent();
        ((ParameterFrac32) p).DefaultValue = value;
        return p;
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceFrac32) {
            ParameterInstanceFrac32 p1 = (ParameterInstanceFrac32) p;
            modulators = p1.getModulators();
            presets = p1.presets;
            value.setRaw(p1.value.getRaw());
        }
    }

    @Override
    public String GenerateCodeInitModulator(String vprefix, String StructAccces) {
        return "";
    }
}
