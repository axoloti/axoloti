/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
import axoloti.datatypes.Value;
import components.AssignMidiCCComponent;
import components.AssignMidiCCMenuItems;
import components.AssignModulatorComponent;
import components.AssignModulatorMenuItems;
import components.AssignPresetComponent;
import components.control.DialComponent;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceFrac32UMap<T extends ParameterFrac32> extends ParameterInstanceFrac32U<T> {
 
    AssignModulatorComponent modulationAssign;
    AssignPresetComponent presetAssign;

    public ParameterInstanceFrac32UMap() {
        super();
    }

    public ParameterInstanceFrac32UMap(@Attribute(name = "value") double v) {
        super(v);
    }

    @Override
    public DialComponent CreateControl() {
        return new DialComponent(0.0, getMin(), getMax(), getTick());
    }

    @Override
    public Preset AddPreset(int index, Value value) {
        Preset p = super.AddPreset(index, value);
        presetAssign.repaint();
        return p;
    }

    @Override
    public void RemovePreset(int index) {
        super.RemovePreset(index);
        presetAssign.repaint();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        JPanel btns = new JPanel();
        btns.setLayout(new BoxLayout(btns, BoxLayout.PAGE_AXIS));

        //lblCC = new LabelComponent("C");
        //btns.add(lblCC);
        midiAssign = new AssignMidiCCComponent(this);
        btns.add(midiAssign);
        modulationAssign = new AssignModulatorComponent(this);
        btns.add(modulationAssign);
        presetAssign = new AssignPresetComponent(this);
        btns.add(presetAssign);
        add(btns);

//        setComponentPopupMenu(new ParameterInstanceUInt7MapPopupMenu3(this));
        addMouseListener(popupMouseListener);
        updateV();
    }

    @Override
    public void setOnParent(Boolean b) {
        super.setOnParent(b);
        if ((b != null) && b) {
            setForeground(Color.blue);
        } else {
            setForeground(Color.black);
        }
    }

    @Override
    public void updateV() {
        super.updateV();
        if (ctrl != null) {
            ctrl.setValue(value.getDouble());            
        }
    }

    String pfunction;

    public void SetPFunction(String s) {
        pfunction = s;
    }

    @Override
    public String GetPFunction() {
        if (pfunction == null) {
            return "pfun_unsigned_clamp";
        } else {
            return pfunction;
        }
    }

    @Override
    public String GenerateCodeInit(String vprefix, String StructAccces) {
        String n;
        if (axoObj.parameterInstances.size() == 1) {
            n = axoObj.getInstanceName();
        } else {
            n = axoObj.getInstanceName() + ":" + name;
        }
        String s = PExName(vprefix) + ".pfunction = " + GetPFunction() + ";\n"
                + "  SetKVP_IPVP(&" + StructAccces + KVPName(vprefix) + ",ObjectKvpRoot, \"" + n + "\" ,"
                + "&" + PExName(vprefix) + ","
                + " 0,"
                + " 1<<27);\n"
                + "  KVP_RegisterObject(&" + StructAccces + KVPName(vprefix) + ");\n";
        if (modulators != null) {
            for (Modulation m : modulators) {
                Modulator mod = axoObj.patch.GetModulatorOfModulation(m);
                if (mod == null) {
                    System.out.println("modulator not found");
                    continue;
                }
                int modulation_index = mod.Modulations.indexOf(m);
                s += "  parent->PExModulationSources[parent->" + mod.getCName() + "][" + modulation_index + "].parameterIndex = " + indexName() + ";\n";
                s += "  parent->PExModulationSources[parent->" + mod.getCName() + "][" + modulation_index + "].amount = " + m.getValue().getRaw() + ";\n";
            }
        }
        return s;
    }

    @Override
    public void updateModulation(int index, double amount) {
        super.updateModulation(index, amount);
        if (modulationAssign != null) {
            modulationAssign.repaint();
        }
    }

    @Override
    public String GenerateCodeDeclaration(String vprefix) {
        return "KeyValuePair " + KVPName(vprefix) + ";\n";
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, "(data2!=127)?data2<<20:0x07FFFFFF");
    }

    /*
     *  Preset logic
     */
    @Override
    public void ShowPreset(int i) {
        this.presetEditActive = i;
        if (i > 0) {
            Preset p = GetPreset(presetEditActive);
            if (p != null) {
                setBackground(Color.yellow);
                ctrl.setValue(p.value.getDouble());
            } else {
                setBackground(UIManager.getColor("Panel.background"));
                ctrl.setValue(value.getDouble());
            }
        } else {
            setBackground(UIManager.getColor("Panel.background"));
            ctrl.setValue(value.getDouble());
        }
        presetAssign.repaint();
        /*
         if ((presets != null) && (!presets.isEmpty())) {            
         lblPreset.setVisible(true);
         } else {
         lblPreset.setVisible(false);
         }
         */
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        new AssignMidiCCMenuItems(this, m1);
        m.add(m1);
        JMenu m2 = new JMenu("Modulation");
        new AssignModulatorMenuItems((ParameterInstanceFrac32UMap<ParameterFrac32>)this, m2);
        m.add(m2);
    }

    @Override
    public DialComponent getControlComponent() {
        return (DialComponent) ctrl;
    }
}
