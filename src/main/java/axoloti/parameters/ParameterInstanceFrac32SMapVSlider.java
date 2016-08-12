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

import axoloti.parameterviews.ParameterInstanceViewFrac32SMapVSlider;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceFrac32SMapVSlider extends ParameterInstanceFrac32S<ParameterFrac32SMapVSlider> {

    public ParameterInstanceFrac32SMapVSlider() {
        super();
    }

    public ParameterInstanceFrac32SMapVSlider(@Attribute(name = "value") double v) {
        super(v);
    }

    @Override
    public String GenerateCodeInit(String vprefix, String StructAccces) {
        String s = /*"    SetKVP_IPVP(&" + StructAccces + KVPName(vprefix) + ",ObjectKvpRoot, \"" + KVPName(vprefix) + "\" ,"
                 + "&" + PExName(vprefix) + ","
                 + (((ParameterFrac32SMapVSlider) parameter).MinValue.getRaw()) + ","
                 + (((ParameterFrac32SMapVSlider) parameter).MaxValue.getRaw()) + ");\n"
                 + "  KVP_RegisterObject(&" + StructAccces + KVPName(vprefix) + ");\n"*/ "";
        return s;
    }

    @Override
    public String GenerateCodeDeclaration(String vprefix) {
        return "KeyValuePair " + KVPName(vprefix) + ";\n";
    }

    @Override
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, "(data2!=127)?(data2-64)<<21:0x07FFFFFF");
    }

    public ParameterInstanceViewFrac32SMapVSlider ViewFactory() {
        return new ParameterInstanceViewFrac32SMapVSlider(this);
    }
}
