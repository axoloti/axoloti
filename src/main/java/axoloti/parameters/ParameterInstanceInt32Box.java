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

import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.NumberBoxComponent;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceInt32Box extends ParameterInstanceInt32 {

    protected NumberBoxComponent dial;

    public ParameterInstanceInt32Box() {
    }

    public ParameterInstanceInt32Box(@Attribute(name = "value") int v) {
        super(v);
    }

    int min = 0;
    int max = 64;

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        dial = CreateControl();
        add(dial);
        updateV();
        dial.addMouseListener(popupMouseListener);

        dial.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                if (value.getInt() != dial.getValue()) {
                    value.setInt((int) dial.getValue());
                    needsTransmit = true;
                }
            }
        });
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceInt32Box) {
            ParameterInstanceInt32Box p1 = (ParameterInstanceInt32Box) p;
//            min = p1.min;
//            max = p1.max;
        }
    }

    @Override
    public void updateV() {
        dial.setValue(value.getInt());
    }

    @Override
    public String GenerateCodeInit(String vprefix, String StructAccces) {
        String s = /*"    " + variableName(vprefix) + " = " + (value.getInt()) + ";\n"
                 + "    " + valueName(vprefix) + " = " + (value.getInt()) + ";\n"
                 + "    " + signalsName(vprefix) + " = 0;\n"
                 + "    SetKVP_IPVP(&" + StructAccces + KVPName(vprefix) + ",ObjectKvpRoot, \"" + KVPName(vprefix) + "\" ,"
                 + "&" + PExName(vprefix) + ","
                 + 0 + ","
                 + ((1<<16)-1) + ");\n"
                 + "  KVP_RegisterObject(&" + StructAccces + KVPName(vprefix) + ");\n"*/ "";
        return s;
    }

    @Override
    public String GenerateCodeDeclaration(String vprefix) {
        return "KeyValuePair " + KVPName(vprefix) + ";\n";
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return "";
    }

    @Override
    public void ShowPreset(int i) {
    }

    @Override
    public void IncludeInPreset() {
    }

    @Override
    public void ExcludeFromPreset() {
    }

    @Override
    public NumberBoxComponent CreateControl() {
        return new NumberBoxComponent(0.0, min, max, 1.0);
    }

}
