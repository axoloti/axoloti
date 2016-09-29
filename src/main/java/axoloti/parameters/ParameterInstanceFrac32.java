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
import axoloti.Preset;
import axoloti.datatypes.Frac32;
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueFrac32;
import axoloti.object.AxoObjectInstance;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

    abstract double getMin();

    abstract double getMax();

    abstract double getTick();

    public ParameterInstanceFrac32(Tx param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        //value = new ValueFrac32();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        if (modulators != null) {
            for (Modulation m : modulators) {
                System.out.println("mod amount " + m.getValue().getDouble());
                m.PostConstructor(this);
            }
        }
    }

    @Override
    public Value<Frac32> getValue() {
        return value;
    }

    @Override
    public void setValue(Value value) {
        super.setValue(value);
        this.value.setDouble(value.getDouble());
        updateV();
    }

    @Override
    public void applyDefaultValue() {
        if (((ParameterFrac32) parameter).DefaultValue != null) {
            value.setRaw(((ParameterFrac32) parameter).DefaultValue.getRaw());
        } else {
            value.setRaw(0);
        }
        updateV();
        needsTransmit = true;
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenuItem m_default = new JMenuItem("Reset to default value");
        m_default.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyDefaultValue();
                getControlComponent().setValue(value.getDouble());
                handleAdjustment();
            }
        });
        m.add(m_default);
    }

    public void updateModulation(int index, double amount) {
        //System.out.println("updatemodulation1:" + index);
        if (amount != 0.0) {
            // existing modulation
            if (modulators == null) {
                modulators = new ArrayList<Modulation>();
            }
            Modulator modulator = axoObj.patch.Modulators.get(index);
            //System.out.println("updatemodulation2:" + modulator.name);
            Modulation n = null;
            for (Modulation m : modulators) {
                if (m.source == modulator.objinst) {
                    if ((modulator.name == null) || (modulator.name.isEmpty())) {
                        n = m;
                        break;
                    } else {
                        if (modulator.name.equals(m.modName)) {
                            n = m;
                            break;
                        }
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
            axoObj.patch.updateModulation(n);
        } else {
            // remove modulation target if exists
            Modulator modulator = axoObj.patch.Modulators.get(index);
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
                axoObj.patch.updateModulation(n);
            }
            if (modulators.isEmpty()) {
                modulators = null;
            }
        }
    }

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
            updateV();
        }
    }

    @Override
    public boolean handleAdjustment() {
        Preset p = GetPreset(presetEditActive);
        if (p != null) {
            p.value = new ValueFrac32(getControlComponent().getValue());
        } else {
            if (value.getDouble() != getControlComponent().getValue()) {
                value.setDouble(getControlComponent().getValue());
                needsTransmit = true;
                UpdateUnit();
            } else {
                return false;
            }

        }
        return true;
    }
    
    @Override
    public String GenerateCodeInitModulator(String vprefix, String StructAccces) {
        return "";
    }    
    
}
