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

import axoloti.parameterviews.ParameterInstanceViewInt32Box;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceInt32Box extends ParameterInstanceInt32 {

    private int min = 0;
    private int max = 64;

    public ParameterInstanceInt32Box() {
    }

    public ParameterInstanceInt32Box(@Attribute(name = "value") int v) {
        super(v);
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
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

    public ParameterInstanceViewInt32Box ViewFactory() {
        return new ParameterInstanceViewInt32Box(this);
    }
}
