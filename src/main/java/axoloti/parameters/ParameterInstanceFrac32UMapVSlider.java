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
import axoloti.datatypes.ValueFrac32;
import components.control.ACtrlListener;
import components.control.ACtrlEvent;
import components.control.VSliderComponent;
import java.awt.Color;
import javax.swing.UIManager;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceFrac32UMapVSlider extends ParameterInstanceFrac32U {

    @Attribute(required = false)
    Integer MidiCC = null;
    protected VSliderComponent dial;
    //private JLabel lblCC;

    public ParameterInstanceFrac32UMapVSlider() {
    }

    public ParameterInstanceFrac32UMapVSlider(@Attribute(name = "value") double v) {
        super(v);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        dial = CreateControl();
        add(dial);
        SetMidiCC(MidiCC);

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
                    }
                }
            }
        });
    }

    @Override
    public void updateV() {
        if (dial != null) {
            dial.setValue(value.getDouble());
        }
    }

    void SetMidiCC(Integer cc) {
        if ((cc != null) && (cc >= 0)) {
            MidiCC = cc;
        } else {
            MidiCC = null;
        }
        doLayout();
        if (getParent() != null) {
            getParent().doLayout();
        }
    }

    @Override
    public String GenerateCodeInit(String vprefix, String StructAccces) {
        String s = /*"    SetKVP_IPVP(&" + StructAccces + KVPName(vprefix) + ",ObjectKvpRoot, \"" + KVPName(vprefix) + "\" ,"
                 + "&" + PExName(vprefix) + ","
                 + (((ParameterFrac32UMapVSlider) parameter).MinValue.getRaw()) + ","
                 + (((ParameterFrac32UMapVSlider) parameter).MaxValue.getRaw()) + ");\n"
                 + "  KVP_RegisterObject(&" + StructAccces + KVPName(vprefix) + ");\n"*/ "";
        if (modulators != null) {
            for (Modulation m : modulators) {
                Modulator mod = axoObj.patch.GetModulatorOfModulation(m);
                if (mod == null) {
                    System.out.println("modulator not found");
                    continue;
                }
                int modulation_index = mod.Modulations.indexOf(m);
                s += "  PExModulationSources[" + mod.getCName() + "][" + modulation_index + "].PEx = &" + PExName(vprefix) + ";\n";
                s += "  PExModulationSources[" + mod.getCName() + "][" + modulation_index + "].amount = " + m.getValue().getRaw() + ";\n";
                s += "  PExModulationSources[" + mod.getCName() + "][" + modulation_index + "].prod = 0;\n";
            }
        }
        return s;
    }

    @Override
    public String GenerateCodeDeclaration(String vprefix) {
        return "KeyValuePair " + KVPName(vprefix) + ";\n";
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, MidiCC, "val<<20");
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
        if ((presets != null) && (!presets.isEmpty())) {
//            lblPreset.setVisible(true);
        } else {
//            lblPreset.setVisible(false);
        }
    }

    @Override
    public VSliderComponent CreateControl() {
        return new VSliderComponent(0.0, 0.0, 63.5, 0.5);
    }

}
