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

import axoloti.inlets.InletBool32;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletBool32Pulse;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import axoloti.outlets.OutletInt32Pos;
import axoloti.parameters.ParameterFrac32UMapKLineTimeReverse;
import static generatedobjects.gentools.WriteAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
public class Logic extends gentools {

    static void GenerateAll() {
        String catName = "logic";
        WriteAxoObject(catName, Create_toggle());
        WriteAxoObject(catName, Create_flipflop());
        WriteAxoObject(catName, Create_flipflop_toggle());
        WriteAxoObject(catName, Create_until());
        WriteAxoObject(catName, Create_and2());
        WriteAxoObject(catName, Create_xor2());
        WriteAxoObject(catName, Create_or2());
        WriteAxoObject(catName, Create_not());
        WriteAxoObject(catName + "/decode", Create_decode_bin8());
        WriteAxoObject(catName + "/decode", Create_decode_int4());
        WriteAxoObject(catName + "/decode", Create_decode_int8());
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateChangeI());
            c.add(CreateChangeF());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateChangeSpeedlimI());
            c.add(CreateChangeSpeedlimF());
            WriteAxoObject(catName, c);
        }

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_SampleHoldK());
            c.add(Create_SampleHoldS());
            WriteAxoObject(catName, c);
        }
    }

    static AxoObject Create_toggle() {
        AxoObject o = new AxoObject("toggle", "toggle");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sLocalData = "   int ntrig;\n"
                + "   int op;\n";
        o.sInitCode = "ntrig = 0;\n"
                + "op = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {op = !op; ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n"
                + "%o%= op;\n";
        return o;
    }

    static AxoObject Create_flipflop() {
        AxoObject o = new AxoObject("flipflop", "flipflop");
        o.inlets.add(new InletBool32Rising("set", "set"));
        o.inlets.add(new InletBool32Rising("reset", "reset"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sLocalData = "   int p_set;\n"
                + "   int p_reset;\n"
                + "   int op;\n";
        o.sInitCode = "p_set = 0;\n"
                + "p_reset = 0;\n"
                + "op = 0;\n";
        o.sKRateCode = "if ((%set%>0) && !p_set) {op = 1; p_set=1;}\n"
                + "if ((%reset%>0) && !p_reset) {op = 0; p_reset=1;}\n"
                + "if (!(%set%>0)) p_set=0;\n"
                + "if (!(%reset%>0)) p_reset=0;\n"
                + "%o%= (op<<27);\n";
        return o;
    }

    static AxoObject Create_flipflop_toggle() {
        AxoObject o = new AxoObject("flipflop toggle", "toggle flipflop");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletBool32Rising("set", "set"));
        o.inlets.add(new InletBool32Rising("reset", "reset"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sLocalData = "   int ntrig;\n"
                + "   int p_set;\n"
                + "   int p_reset;\n"
                + "   int op;\n";
        o.sInitCode = "p_set = 0;\n"
                + "p_reset = 0;\n"
                + "op = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {op = !op; ntrig=1;}\n"
                + "if (!(%trig%>0)) ntrig=0;\n"
                + "if ((%set%>0) && !p_set) {op = 1; p_set=1;}\n"
                + "if ((%reset%>0) && !p_reset) {op = 0; p_reset=1;}\n"
                + "if (!(%set%>0)) p_set=0;\n"
                + "if (!(%reset%>0)) p_reset=0;\n"
                + "%o%= (op<<27);\n";
        return o;
    }

    static AxoObject Create_until() {
        AxoObject o = new AxoObject("until", "rapid trigger pulses until rising edge on stop");
        o.inlets.add(new InletBool32Rising("start", "start"));
        o.inlets.add(new InletBool32Rising("stop", "stop"));
        o.inlets.add(new InletBool32Rising("pause", "pause triggering when true"));
        o.outlets.add(new OutletBool32Pulse("o", "repetitive pulse"));
        o.outlets.add(new OutletInt32Pos("count", "repetitive pulse"));
        o.sLocalData = "   int pstart;\n"
                + "   int pstop;\n"
                + "   int po;\n"
                + "   int state;\n";
        o.sInitCode = "pstart = 0;\n"
                + "pstop=0;\n"
                + "po=0;\n"
                + "state=0;\n";
        o.sKRateCode = "if ((%start%>0)&&!pstart) {\n"
                + "  state = 1;\n"
                + "  pstart = 1;\n"
                + "}\n"
                + "if (pstart && !(%start%>0)) pstart = 0;\n"
                + "if ((%stop%>0)&&!pstop) {\n"
                + "  state = 0;\n"
                + "  pstop = 1;\n"
                + "}\n"
                + "if (pstop && !(%stop%>0)) pstop = 0;\n"
                + "%o% = (state&1)<<27;\n"
                + "if (!(%pause%>0)) if (state>0) state++;\n"
                + "%count% = (state>>1);\n";
        return o;
    }

    static AxoObject Create_and2() {
        AxoObject o = new AxoObject("and 2", "logic AND with 2 inputs");
        o.inlets.add(new InletBool32("i1", "input 1"));
        o.inlets.add(new InletBool32("i2", "input 2"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o% = (%i1%)&&(%i2%);\n";
        return o;
    }

    static AxoObject Create_or2() {
        AxoObject o = new AxoObject("or 2", "logic OR with 2 inputs");
        o.inlets.add(new InletBool32("i1", "input 1"));
        o.inlets.add(new InletBool32("i2", "input 2"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o% = (%i1%)||(%i2%);\n";
        return o;
    }

    static AxoObject Create_xor2() {
        AxoObject o = new AxoObject("xor 2", "logic XOR with 2 inputs");
        o.inlets.add(new InletBool32("i1", "input 1"));
        o.inlets.add(new InletBool32("i2", "input 2"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o% = (%i1%>0)^(%i2%>0);\n";
        return o;
    }

    static AxoObject Create_not() {
        AxoObject o = new AxoObject("inv", "invert");
        o.inlets.add(new InletBool32("i", "input"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o% = (%i%>0)?0:1;\n";
        return o;
    }

    static AxoObject Create_decode_bin4() {
        AxoObject o = new AxoObject("decode.bin 4", "binary decoder");
        o.inlets.add(new InletInt32("i1", "input 1"));
        o.outlets.add(new OutletBool32("o0", "output 0"));
        o.outlets.add(new OutletBool32("o1", "output 1"));
        o.outlets.add(new OutletBool32("o2", "output 2"));
        o.outlets.add(new OutletBool32("o3", "output 3"));
        o.outlets.add(new OutletInt32("chain", "chain output"));
        o.sKRateCode = "%o0% = (%i1% & 0x01)?1:0;\n";
        o.sKRateCode += "%o1% = (%i1% & 0x02)?1:0;\n";
        o.sKRateCode += "%o2% = (%i1% & 0x04)?1:0;\n";
        o.sKRateCode += "%o3% = (%i1% & 0x08)?1:0;\n";
        o.sKRateCode += "%chain% = (%i1% >> 4);\n";
        return o;
    }

    static AxoObject Create_decode_bin8() {
        AxoObject o = new AxoObject("bin 8", "binary decoder");
        o.inlets.add(new InletInt32("i1", "input 1"));
        o.outlets.add(new OutletBool32("o0", "output 0"));
        o.outlets.add(new OutletBool32("o1", "output 1"));
        o.outlets.add(new OutletBool32("o2", "output 2"));
        o.outlets.add(new OutletBool32("o3", "output 3"));
        o.outlets.add(new OutletBool32("o4", "output 4"));
        o.outlets.add(new OutletBool32("o5", "output 5"));
        o.outlets.add(new OutletBool32("o6", "output 6"));
        o.outlets.add(new OutletBool32("o7", "output 7"));
        o.outlets.add(new OutletInt32("chain", "chain output"));
        o.sKRateCode = "%o0% = (%i1% & 0x01)?1:0;\n";
        o.sKRateCode += "%o1% = (%i1% & 0x02)?1:0;\n";
        o.sKRateCode += "%o2% = (%i1% & 0x04)?1:0;\n";
        o.sKRateCode += "%o3% = (%i1% & 0x08)?1:0;\n";
        o.sKRateCode += "%o4% = (%i1% & 0x10)?1:0;\n";
        o.sKRateCode += "%o5% = (%i1% & 0x20)?1:0;\n";
        o.sKRateCode += "%o6% = (%i1% & 0x40)?1:0;\n";
        o.sKRateCode += "%o7% = (%i1% & 0x80)?1:0;\n";
        o.sKRateCode += "%chain% = (%i1% >> 8);\n";
        return o;
    }

    static AxoObject Create_decode_int4() {
        AxoObject o = new AxoObject("int 4", "integer decoder");
        o.inlets.add(new InletInt32("i1", "input 1"));
        o.outlets.add(new OutletBool32("o0", "output 0"));
        o.outlets.add(new OutletBool32("o1", "output 1"));
        o.outlets.add(new OutletBool32("o2", "output 2"));
        o.outlets.add(new OutletBool32("o3", "output 3"));
        o.outlets.add(new OutletInt32("chain", "chain output"));
        o.sKRateCode = "%o0% = (%i1% == 0)?1:0;\n";
        o.sKRateCode += "%o1% = (%i1% == 1)?1:0;\n";
        o.sKRateCode += "%o2% = (%i1% == 2)?1:0;\n";
        o.sKRateCode += "%o3% = (%i1% == 3)?1:0;\n";
        o.sKRateCode += "%chain% = (%i1% - 4);\n";
        return o;
    }

    static AxoObject Create_decode_int8() {
        AxoObject o = new AxoObject("int 8", "integer decoder");
        o.inlets.add(new InletInt32("i1", "input 1"));
        o.outlets.add(new OutletBool32("o0", "output 0"));
        o.outlets.add(new OutletBool32("o1", "output 1"));
        o.outlets.add(new OutletBool32("o2", "output 2"));
        o.outlets.add(new OutletBool32("o3", "output 3"));
        o.outlets.add(new OutletBool32("o4", "output 4"));
        o.outlets.add(new OutletBool32("o5", "output 5"));
        o.outlets.add(new OutletBool32("o6", "output 6"));
        o.outlets.add(new OutletBool32("o7", "output 7"));
        o.outlets.add(new OutletInt32("chain", "chain output"));
        o.sKRateCode = "%o0% = (%i1% == 0)?1:0;\n";
        o.sKRateCode += "%o1% = (%i1% == 1)?1:0;\n";
        o.sKRateCode += "%o2% = (%i1% == 2)?1:0;\n";
        o.sKRateCode += "%o3% = (%i1% == 3)?1:0;\n";
        o.sKRateCode += "%o4% = (%i1% == 4)?1:0;\n";
        o.sKRateCode += "%o5% = (%i1% == 5)?1:0;\n";
        o.sKRateCode += "%o6% = (%i1% == 6)?1:0;\n";
        o.sKRateCode += "%o7% = (%i1% == 7)?1:0;\n";
        o.sKRateCode += "%chain% = (%i1% - 8);\n";
        return o;
    }

    static AxoObject CreateChangeI() {
        AxoObject o = new AxoObject("change", "Generates a trigger pulse when current input value is different from previous value.");
        o.inlets.add(new InletInt32("in", "in"));
        o.outlets.add(new OutletBool32("trig", "trigger pulse"));
        o.sLocalData = "   int ptrig;\n"
                + "   int32_t pval;\n";
        o.sInitCode = "ptrig = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "if ((pval != %in%)&(!ptrig)) { \n"
                + "  %trig% = 1;\n"
                + "  pval = %in%;\n"
                + "  ptrig = 1;\n"
                + "} else {\n"
                + "  ptrig = 0;\n"
                + "  %trig% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateChangeF() {
        AxoObject o = new AxoObject("change", "Generates a trigger pulse when current input value is different from previous value.");
        o.inlets.add(new InletFrac32("in", "in"));
        o.outlets.add(new OutletBool32("trig", "trigger pulse"));
        o.sLocalData = "   int ptrig;\n"
                + "   int32_t pval;\n";
        o.sInitCode = "ptrig = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "if ((pval != %in%)&(!ptrig)) { \n"
                + "  %trig% = 1;\n"
                + "  pval = %in%;\n"
                + "  ptrig = 1;\n"
                + "} else {\n"
                + "  ptrig = 0;\n"
                + "  %trig% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateChangeSpeedlimI() {
        AxoObject o = new AxoObject("change speedlim", "Generates a trigger pulse when current input value is different from previous value, with a minimum time interval between pulses.");
        o.inlets.add(new InletInt32("in", "in"));
        o.outlets.add(new OutletBool32("trig", "trigger pulse"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("d"));
        o.sLocalData = "   int ptrig;\n"
                + "   int32_t pval;\n";
        o.sInitCode = "ptrig = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "if ((pval != %in%)&(!ptrig)) { \n"
                + "  %trig% = 1;\n"
                + "  pval = %in%;\n"
                + "  ptrig = 1 << 27;\n"
                + "} else {\n"
                + "  if (ptrig>0) {\n"
                + "    ptrig -= %d% >> 7;\n"
                + "  } else ptrig = 0;\n"
                + "  %trig% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateChangeSpeedlimF() {
        AxoObject o = new AxoObject("change speedlim", "Generates a trigger pulse when current input value is different from previous value, with a minimum time interval between pulses.");
        o.inlets.add(new InletFrac32("in", "in"));
        o.outlets.add(new OutletBool32("trig", "trigger pulse"));
        o.params.add(new ParameterFrac32UMapKLineTimeReverse("d"));
        o.sLocalData = "   int ptrig;\n"
                + "   int32_t pval;\n";
        o.sInitCode = "ptrig = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "if ((pval != %in%)&(!ptrig)) { \n"
                + "  %trig% = 1;\n"
                + "  pval = %in%;\n"
                + "  ptrig = 1 << 27;\n"
                + "} else {\n"
                + "  if (ptrig>0) {\n"
                + "    ptrig -= %d% >> 7;\n"
                + "  } else ptrig = 0;\n"
                + "  %trig% = 0;\n"
                + "}\n";
        return o;
    }

    static AxoObject Create_SampleHoldK() {
        AxoObject o = new AxoObject("samplehold", "Pass through when s_h input is less or equal than zero or hold when positive.");
        o.inlets.add(new InletFrac32("in", "input"));
        o.inlets.add(new InletFrac32("s_h", "pass through when less or equal than zero or hold when positive"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.sLocalData = "   int32_t hold;\n";
        o.sInitCode = "hold = 0;\n";
        o.sKRateCode = "if (%s_h%<=0)\n"
                + "   hold = %in%;\n"
                + "%out% = hold;\n";
        return o;
    }

    static AxoObject Create_SampleHoldS() {
        AxoObject o = new AxoObject("samplehold", "Pass through when s_h input is less or equal than zero or hold when positive.");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletFrac32Buffer("s_h", "pass through when less or equal than zero or hold when positive"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.sLocalData = "   int32_t hold;\n";
        o.sInitCode = "hold = 0;\n";
        o.sSRateCode = "if (%s_h%<=0)\n"
                + "   hold = %in%;\n"
                + "%out% = hold;\n";
        return o;
    }
}
