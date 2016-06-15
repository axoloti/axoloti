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

import axoloti.datatypes.Value;
import components.control.CheckboxComponent;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceBin12 extends ParameterInstanceInt32 {

    public ParameterInstanceBin12() {
    }

    public ParameterInstanceBin12(@Attribute(name = "value") int v) {
        super(v);
    }

    @Override
    public CheckboxComponent CreateControl() {
        return new CheckboxComponent(0, 12);
    }

    @Override
    public String GenerateCodeInit(String vprefix, String StructAccces) {
        String s = /*"    " + variableName(vprefix) + " = " + (value.getInt()) + ";\n"
                 + "    " + valueName(vprefix) + " = " + (value.getInt()) + ";\n"
                 + "    " + signalsName(vprefix) + " = 0;\n"
                 +*/ "    SetKVP_IPVP(&" + StructAccces + KVPName(vprefix) + ",ObjectKvpRoot, \"" + KVPName(vprefix) + "\" ,"
                + "&" + PExName(vprefix) + ","
                + 0 + ","
                + ((1 << 12) - 1) + ");\n"
                + "  KVP_RegisterObject(&" + StructAccces + KVPName(vprefix) + ");\n";
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
    public void updateV() {
        ctrl.setValue(value.getInt());
    }

    @Override
    public void setValue(Value value) {
        super.setValue(value);
        updateV();
    }

    @Override
    public CheckboxComponent getControlComponent() {
        return (CheckboxComponent) ctrl;
    }
}
