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
package generatedobjects;

import axoloti.inlets.InletFrac32;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;
import axoloti.parameters.ParameterFrac32UMap;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Dynamics extends gentools {

    static void GenerateAll() {
        String catName = "dyn";
        WriteAxoObject(catName, CreateCompressor());
    }

    static AxoObject CreateCompressor() {
        AxoObject o = new AxoObject("comp", "Dynamic range compressor. Gain calculation only. Envelope follower, attack, decay and VCA are not included.");
        o.params.add(new ParameterFrac32UMap("tresh"));
        o.params.add(new ParameterFrac32UMap("ratio"));
        o.inlets.add(new InletFrac32("in", "in"));
        o.outlets.add(new OutletFrac32("out", "out"));
        o.sLocalData = "int32_t frac_log(int32_t a) {\n"
                + "	Float_t f;\n"
                + "	f.f = a;\n"
                + "	int32_t r1 = ((f.parts.exponent&0x7F)-18) << 24;\n"
                + "	int32_t r3 = logt[f.parts.mantissa>>15]<<10;\n"
                + "	return r1 + r3;\n"
                + "}\n"
                + "\n"
                + "int32_t frac_exp(int32_t a) {\n"
                + "	int8_t s = (a>>24)+4;\n"
                + "	uint8_t ei = a>>16;\n"
                + "	if (s>=0)\n"
                + "	     return expt[ei]<<s;\n"
                + "	else return expt[ei]>>(-s);\n"
                + "}\n";
        o.sKRateCode = "int32_t inlog = frac_log(%in%);\n"
                + "int32_t treshlog = frac_log(%tresh%);\n"
                + "int32_t over = inlog-treshlog;\n"
                + "int32_t gain;\n"
                + "if (over<0){\n"
                + "	gain = 0x80000;\n"
                + "} else {\n"
                + "	gain = frac_exp(-___SMMUL(over,%ratio%)<<5);\n"
                + "}\n"
                + "%out% = gain<<8;//___SMMUL(%in%,gain);\n";
        return o;
    }
}
