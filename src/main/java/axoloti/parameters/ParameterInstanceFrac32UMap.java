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
import axoloti.datatypes.Value;
import axoloti.datatypes.ValueFrac32;
import components.AssignMidiCCComponent;
import components.AssignMidiCCMenuItems;
import components.AssignModulatorComponent;
import components.AssignModulatorMenuItems;
import components.AssignPresetComponent;
import components.control.ACtrlComponent;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.DialComponent;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class ParameterInstanceFrac32UMap extends ParameterInstanceFrac32U implements ActionListener {

    @Attribute(required = false)
    Integer MidiCC = null;
    DialComponent dial;
    AssignMidiCCComponent midiAssign;
    AssignModulatorComponent modulationAssign;
    AssignPresetComponent presetAssign;

    public ParameterInstanceFrac32UMap() {
        super();
    }

    public ParameterInstanceFrac32UMap(@Attribute(name = "value") double v) {
        super(v);
    }

    @Override
    public ACtrlComponent CreateControl() {
        ACtrlComponent ACtrl = new DialComponent(0.0, getMin(), getMax(), getTick());
        return ACtrl;
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceFrac32UMap) {
            ParameterInstanceFrac32UMap p1 = (ParameterInstanceFrac32UMap) p;
            SetMidiCC(p1.MidiCC);
        }
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
        dial = new DialComponent(0.0, getMin(), getMax(), getTick());
        add(dial);
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
        SetMidiCC(MidiCC);
        add(btns);

//        setComponentPopupMenu(new ParameterInstanceUInt7MapPopupMenu3(this));
        addMouseListener(popupMouseListener);
        dial.addMouseListener(popupMouseListener);
        dial.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                Preset p = GetPreset(presetEditActive);
                if (p != null) {
                    p.value = new ValueFrac32(dial.getValue());
                } else {
                    if (value.getDouble() != dial.getValue()) {
                        value.setDouble(dial.getValue());
                        needsTransmit = true;
                        UpdateUnit();
                    }
                }
            }
        });

        setOnParent(isOnParent());
        updateV();
    }

    @Override
    public void setOnParent(boolean b) {
        super.setOnParent(b);
        if (b) {
            setForeground(Color.blue);
        } else {
            setForeground(Color.black);
        }
    }

    @Override
    public void updateV() {
        super.updateV();
        if (dial != null) {
            dial.setValue(value.getDouble());
        }
    }

    void SetMidiCC(Integer cc) {
        if ((cc != null) && (cc >= 0)) {
            MidiCC = cc;
            if (midiAssign != null) {
                midiAssign.setCC(cc);
            }
        } else {
            MidiCC = null;
            if (midiAssign != null) {
                midiAssign.setCC(-1);
            }
        }
    }

    public int getMidiCC() {
        if (MidiCC == null) {
            return -1;
        } else {
            return MidiCC;
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
                + PExName(vprefix) + ".pfunction(&" + PExName(vprefix) + ");\n"
                + "  SetKVP_IPVP(&" + StructAccces + KVPName(vprefix) + ",ObjectKvpRoot, \"" + n + "\" ,"
                + "&" + PExName(vprefix) + ","
                + (((ParameterFrac32UMap) parameter).MinValue.getRaw()) + ","
                + (((ParameterFrac32UMap) parameter).MaxValue.getRaw()) + ");\n"
                + "  KVP_RegisterObject(&" + StructAccces + KVPName(vprefix) + ");\n";
        if (modulators != null) {
            for (Modulation m : modulators) {
                Modulator mod = axoObj.patch.GetModulatorOfModulation(m);
                if (mod == null) {
                    System.out.println("modulator not found");
                    continue;
                }
                int modulation_index = mod.Modulations.indexOf(m);
                s += "  parent2->PExModulationSources[" + mod.getCName() + "][" + modulation_index + "].PEx = &" + PExName(vprefix) + ";\n";
                s += "  parent2->PExModulationSources[" + mod.getCName() + "][" + modulation_index + "].amount = " + m.getValue().getRaw() + ";\n";
                s += "  parent2->PExModulationSources[" + mod.getCName() + "][" + modulation_index + "].prod = 0;\n";
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
        return GenerateMidiCCCodeSub(vprefix, MidiCC, "data2<<20");
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
                dial.setValue(p.value.getDouble());
            } else {
                setBackground(UIManager.getColor("Panel.background"));
                dial.setValue(value.getDouble());
            }
        } else {
            setBackground(UIManager.getColor("Panel.background"));
            dial.setValue(value.getDouble());
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
    public void actionPerformed(ActionEvent e) {
        String s = e.getActionCommand();
        //                System.out.println(e.getActionCommand(  ));
        if (s.startsWith("CC")) {
            int i = Integer.parseInt(s.substring(2));
            SetMidiCC(i);
        } else if (s.equals("none")) {
            SetMidiCC(-1);
        }
    }

    @Override
    public void populatePopup(JPopupMenu m) {
        super.populatePopup(m);
        JMenu m1 = new JMenu("Midi CC");
        new AssignMidiCCMenuItems(this, m1);
        m.add(m1);
        JMenu m2 = new JMenu("Modulation");
        new AssignModulatorMenuItems(this, m2);
        m.add(m2);
    }

}
