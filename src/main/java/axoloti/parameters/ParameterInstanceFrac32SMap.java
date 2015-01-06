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
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterInstanceFrac32SMap extends ParameterInstanceFrac32UMap {

    public ParameterInstanceFrac32SMap() {
        super();
    }

    public ParameterInstanceFrac32SMap(@Attribute(name = "value") double v) {
        super(v);
    }

    @Override
    double getMin() {
        return -64.0;
    }

    @Override
    double getMax() {
        return 63.0;
    }

    @Override
    double getTick() {
        return 1.0;
    }

    @Override
    public String GetPFunction() {
        if (pfunction == null) {
            return "pfun_signed_clamp";
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
                + (((ParameterFrac32SMap) parameter).MinValue.getRaw()) + ","
                + (((ParameterFrac32SMap) parameter).MaxValue.getRaw()) + ");\n"
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
    public String GenerateCodeMidiHandler(String vprefix) {
        return GenerateMidiCCCodeSub(vprefix, "(data2-64)<<21");
    }
}
