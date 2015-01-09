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
package generatedobjects;

import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletBool32Pulse;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Pos;
import axoloti.outlets.OutletInt32;
import axoloti.parameters.Parameter4LevelX16;
import axoloti.parameters.ParameterBin16;
import axoloti.parameters.ParameterBin32;
import axoloti.parameters.ParameterFrac32SMapVSlider;
import axoloti.parameters.ParameterFrac32UMap;
import axoloti.parameters.ParameterFrac32UMapVSlider;
import axoloti.parameters.ParameterInt32BoxSmall;

/**
 *
 * @author Johannes Taelman
 */
public class Sequencer extends gentools {

    static void GenerateAll() {
        String catName = "sel";
        WriteAxoObject(catName, Create_SelectBool16());
        WriteAxoObject(catName, Create_SelectBool16v2());
        WriteAxoObject(catName, Create_SelectBool16v2x2());
        WriteAxoObject(catName, Create_SelectBool16v2x4());
        WriteAxoObject(catName, Create_SelectBool16v2x8());

        WriteAxoObject(catName, Create_SelectBool16v2_pulse());
        WriteAxoObject(catName, Create_SelectBool16v2x2_pulse());
        WriteAxoObject(catName, Create_SelectBool16v2x4_pulse());
        WriteAxoObject(catName, Create_SelectBool16v2x8_pulse());

        WriteAxoObject(catName, Create_SelectBool32());
        WriteAxoObject(catName, Create_SelectBool32x2());
        WriteAxoObject(catName, Create_SelectBool32x4());
        WriteAxoObject(catName, Create_SelectBool32x8());

        WriteAxoObject(catName, Create_SelectInt16());
        WriteAxoObject(catName, Create_SelectInt32());

        WriteAxoObject(catName, Create_Select4L16());
        WriteAxoObject(catName, Create_Select4L16_2track());
        WriteAxoObject(catName, Create_Select4L16_4track());
        WriteAxoObject(catName, Create_Select4L16_8track());
        WriteAxoObject(catName, Create_Select4L16_8track_s());

        WriteAxoObject(catName, Create_SelectUFrac16b());
        WriteAxoObject(catName, Create_SelectSFrac16b());
        WriteAxoObject(catName, Create_SelectUFrac32b());
        WriteAxoObject(catName, Create_SelectSFrac32b());

        WriteAxoObject(catName, Create_selectc2());
        WriteAxoObject(catName, Create_selectc4());
        WriteAxoObject(catName, Create_selectc8());
        WriteAxoObject(catName, Create_selectc16());

        catName = "seq";
        WriteAxoObject(catName, Create_LfsrSeq());
    }

    static AxoObject Create_SelectBool16() {
        AxoObject o = new AxoObject("sel b 16 old", "select one out of 16 booleans");
        o.inlets.add(new InletInt32("in", "in"));
        o.params.add(new ParameterBin16("b16"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "%o%=%b16%&(1<<%in%);\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2() {
        AxoObject o = new AxoObject("sel b 16", "select one out of 16 booleans, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.params.add(new ParameterBin16("b16"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16))"
                + "   %o%=%b16%&(1<<%in%);\n"
                + "else %o% = %def%;\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2_pulse() {
        AxoObject o = new AxoObject("sel b 16 pulse", "select one out of 16 booleans, chainable. Pulse output.");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32Pulse("o", "output"));
        o.params.add(new ParameterBin16("b16"));
        o.sLocalData = "int in_prev;\n";
        o.sInitCode = "in_prev = 0;\n";
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16))"
                + "   %o%=(in_prev!=%in%)&&(%b16%&(1<<%in%));\n"
                + "else %o% = %def%;\n"
                + "%chain% = %in%-16;\n"
                + "in_prev = %in%;\n";
        return o;
    }

    static AxoObject Create_SelectBool32() {
        AxoObject o = new AxoObject("sel b 32", "select one out of 32 booleans, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-32)"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.params.add(new ParameterBin32("b32"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<32))"
                + "   %o%=%b32%&(1<<%in%);\n"
                + "else %o% = %def%;\n"
                + "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_SelectBool32_2rows() {
        AxoObject o = new AxoObject("sel b 32 2t", "select one out of 32 booleans, 2 tracks, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o", "output"));
        ParameterBin16 p = new ParameterBin16("b1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("b2");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16))\n"
                + "   %o%=%b1%&(1<<%in%);\n"
                + "else if ((%in%>=16)&&(%in%<32))\n"
                + "   %o%=%b2%&(1<<(%in%-16));\n"
                + "else %o% = %def%;\n"
                + "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_SelectBool64() {
        AxoObject o = new AxoObject("sel b 32 4t", "select one out of 64 booleans, 4 tracks, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-54)"));
        o.outlets.add(new OutletBool32("o", "output"));
        ParameterBin16 p = new ParameterBin16("b1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("b2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("b3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("b4");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16))\n"
                + "   %o%=%b1%&(1<<%in%);\n"
                + "else if ((%in%>=16)&&(%in%<32))\n"
                + "   %o%=%b2%&(1<<(%in%-16));\n"
                + "else if ((%in%>=32)&&(%in%<48))\n"
                + "   %o%=%b3%&(1<<(%in%-32));\n"
                + "else if ((%in%>=48)&&(%in%<64))\n"
                + "   %o%=%b4%&(1<<(%in%-48));\n"
                + "else %o% = %def%;\n"
                + "%chain% = %in%-64;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2x2() {
        AxoObject o = new AxoObject("sel b 16 2t", "select one out of 16 booleans, chainable, 2 tracks");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        ParameterBin16 p = new ParameterBin16("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p2");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   %o1%=%p1%&(1<<%in%);\n"
                + "   %o2%=%p2%&(1<<%in%);\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2x2_pulse() {
        AxoObject o = new AxoObject("sel b 16 2t pulse", "select one out of 16 booleans, chainable, 2 tracks, pulse output");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32Pulse("o1", "output channel 1"));
        o.outlets.add(new OutletBool32Pulse("o2", "output channel 2"));
        ParameterBin16 p = new ParameterBin16("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p2");
        p.noLabel = true;
        o.params.add(p);
        o.sLocalData = "int in_prev;\n";
        o.sInitCode = "in_prev = 0;\n";
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   %o1%=(%in%!=in_prev)&&(%p1%&(1<<%in%));\n"
                + "   %o2%=(%in%!=in_prev)&&(%p2%&(1<<%in%));\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n"
                + "in_prev = %in%;\n";
        return o;
    }

    static AxoObject Create_SelectBool32x2() {
        AxoObject o = new AxoObject("sel b 32 2t", "select one out of 32 booleans, chainable, 2 tracks");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-32)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        ParameterBin32 p = new ParameterBin32("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p2");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<32)) {"
                + "   %o1%=%p1%&(1<<%in%);\n"
                + "   %o2%=%p2%&(1<<%in%);\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "}\n"
                + "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2x4() {
        AxoObject o = new AxoObject("sel b 16 4t", "select one out of 16 booleans, chainable, 4 tracks");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.inlets.add(new InletBool32("def3", "default value channel 3"));
        o.inlets.add(new InletBool32("def4", "default value channel 4"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        o.outlets.add(new OutletBool32("o3", "output channel 3"));
        o.outlets.add(new OutletBool32("o4", "output channel 4"));
        ParameterBin16 p = new ParameterBin16("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p4");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {"
                + "   %o1%=%p1%&(1<<%in%);\n"
                + "   %o2%=%p2%&(1<<%in%);\n"
                + "   %o3%=%p3%&(1<<%in%);\n"
                + "   %o4%=%p4%&(1<<%in%);\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2x4_pulse() {
        AxoObject o = new AxoObject("sel b 16 4t pulse", "select one out of 16 booleans, chainable, 4 tracks, pulse output");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.inlets.add(new InletBool32("def3", "default value channel 3"));
        o.inlets.add(new InletBool32("def4", "default value channel 4"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32Pulse("o1", "output channel 1"));
        o.outlets.add(new OutletBool32Pulse("o2", "output channel 2"));
        o.outlets.add(new OutletBool32Pulse("o3", "output channel 3"));
        o.outlets.add(new OutletBool32Pulse("o4", "output channel 4"));
        ParameterBin16 p = new ParameterBin16("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p4");
        p.noLabel = true;
        o.params.add(p);
        o.sLocalData = "int in_prev;\n";
        o.sInitCode = "in_prev = 0;\n";
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   %o1%=(%in%!=in_prev)&&(%p1%&(1<<%in%));\n"
                + "   %o2%=(%in%!=in_prev)&&(%p2%&(1<<%in%));\n"
                + "   %o3%=(%in%!=in_prev)&&(%p3%&(1<<%in%));\n"
                + "   %o4%=(%in%!=in_prev)&&(%p4%&(1<<%in%));\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n"
                + "in_prev = %in%;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2x8() {
        AxoObject o = new AxoObject("sel b 16 8t", "select one out of 16 booleans, chainable, 8 tracks");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.inlets.add(new InletBool32("def3", "default value channel 3"));
        o.inlets.add(new InletBool32("def4", "default value channel 4"));
        o.inlets.add(new InletBool32("def5", "default value channel 5"));
        o.inlets.add(new InletBool32("def6", "default value channel 6"));
        o.inlets.add(new InletBool32("def7", "default value channel 7"));
        o.inlets.add(new InletBool32("def8", "default value channel 8"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        o.outlets.add(new OutletBool32("o3", "output channel 3"));
        o.outlets.add(new OutletBool32("o4", "output channel 4"));
        o.outlets.add(new OutletBool32("o5", "output channel 5"));
        o.outlets.add(new OutletBool32("o6", "output channel 6"));
        o.outlets.add(new OutletBool32("o7", "output channel 7"));
        o.outlets.add(new OutletBool32("o8", "output channel 8"));
        ParameterBin16 p = new ParameterBin16("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p4");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p5");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p6");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p7");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p8");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {"
                + "   %o1%=%p1%&(1<<%in%);\n"
                + "   %o2%=%p2%&(1<<%in%);\n"
                + "   %o3%=%p3%&(1<<%in%);\n"
                + "   %o4%=%p4%&(1<<%in%);\n"
                + "   %o5%=%p5%&(1<<%in%);\n"
                + "   %o6%=%p6%&(1<<%in%);\n"
                + "   %o7%=%p7%&(1<<%in%);\n"
                + "   %o8%=%p8%&(1<<%in%);\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "   %o5% = %def5%;\n"
                + "   %o6% = %def6%;\n"
                + "   %o7% = %def7%;\n"
                + "   %o8% = %def8%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_SelectBool16v2x8_pulse() {
        AxoObject o = new AxoObject("sel b 16 8t pulse", "select one out of 16 booleans, chainable, 8 tracks, pulse output");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.inlets.add(new InletBool32("def3", "default value channel 3"));
        o.inlets.add(new InletBool32("def4", "default value channel 4"));
        o.inlets.add(new InletBool32("def5", "default value channel 5"));
        o.inlets.add(new InletBool32("def6", "default value channel 6"));
        o.inlets.add(new InletBool32("def7", "default value channel 7"));
        o.inlets.add(new InletBool32("def8", "default value channel 8"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        o.outlets.add(new OutletBool32("o3", "output channel 3"));
        o.outlets.add(new OutletBool32("o4", "output channel 4"));
        o.outlets.add(new OutletBool32("o5", "output channel 5"));
        o.outlets.add(new OutletBool32("o6", "output channel 6"));
        o.outlets.add(new OutletBool32("o7", "output channel 7"));
        o.outlets.add(new OutletBool32("o8", "output channel 8"));
        ParameterBin16 p = new ParameterBin16("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p4");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p5");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p6");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p7");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin16("p8");
        p.noLabel = true;
        o.params.add(p);
        o.sLocalData = "int in_prev;\n";
        o.sInitCode = "in_prev = 0;\n";
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {"
                + "   %o1%=(in_prev!=%in%)&&(%p1%&(1<<%in%));\n"
                + "   %o2%=(in_prev!=%in%)&&(%p2%&(1<<%in%));\n"
                + "   %o3%=(in_prev!=%in%)&&(%p3%&(1<<%in%));\n"
                + "   %o4%=(in_prev!=%in%)&&(%p4%&(1<<%in%));\n"
                + "   %o5%=(in_prev!=%in%)&&(%p5%&(1<<%in%));\n"
                + "   %o6%=(in_prev!=%in%)&&(%p6%&(1<<%in%));\n"
                + "   %o7%=(in_prev!=%in%)&&(%p7%&(1<<%in%));\n"
                + "   %o8%=(in_prev!=%in%)&&(%p8%&(1<<%in%));\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "   %o5% = %def5%;\n"
                + "   %o6% = %def6%;\n"
                + "   %o7% = %def7%;\n"
                + "   %o8% = %def8%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n"
                + "in_prev = %in%;\n";
        return o;
    }

    static AxoObject Create_SelectBool32x4() {
        AxoObject o = new AxoObject("sel b 32 4t", "select one out of 32 booleans, chainable, 4 tracks");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.inlets.add(new InletBool32("def3", "default value channel 3"));
        o.inlets.add(new InletBool32("def4", "default value channel 4"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        o.outlets.add(new OutletBool32("o3", "output channel 3"));
        o.outlets.add(new OutletBool32("o4", "output channel 4"));
        ParameterBin32 p = new ParameterBin32("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p4");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<32)) {"
                + "   %o1%=%p1%&(1<<%in%);\n"
                + "   %o2%=%p2%&(1<<%in%);\n"
                + "   %o3%=%p3%&(1<<%in%);\n"
                + "   %o4%=%p4%&(1<<%in%);\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "}\n"
                + "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_SelectBool32x8() {
        AxoObject o = new AxoObject("sel b 32 8t", "select one out of 32 booleans, chainable, 8 tracks");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletBool32("def1", "default value channel 1"));
        o.inlets.add(new InletBool32("def2", "default value channel 2"));
        o.inlets.add(new InletBool32("def3", "default value channel 3"));
        o.inlets.add(new InletBool32("def4", "default value channel 4"));
        o.inlets.add(new InletBool32("def5", "default value channel 5"));
        o.inlets.add(new InletBool32("def6", "default value channel 6"));
        o.inlets.add(new InletBool32("def7", "default value channel 7"));
        o.inlets.add(new InletBool32("def8", "default value channel 8"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletBool32("o1", "output channel 1"));
        o.outlets.add(new OutletBool32("o2", "output channel 2"));
        o.outlets.add(new OutletBool32("o3", "output channel 3"));
        o.outlets.add(new OutletBool32("o4", "output channel 4"));
        o.outlets.add(new OutletBool32("o5", "output channel 5"));
        o.outlets.add(new OutletBool32("o6", "output channel 6"));
        o.outlets.add(new OutletBool32("o7", "output channel 7"));
        o.outlets.add(new OutletBool32("o8", "output channel 8"));
        ParameterBin32 p = new ParameterBin32("p1");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p2");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p3");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p4");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p5");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p6");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p7");
        p.noLabel = true;
        o.params.add(p);
        p = new ParameterBin32("p8");
        p.noLabel = true;
        o.params.add(p);
        o.sKRateCode = "if ((%in%>=0)&&(%in%<32)) {"
                + "   %o1%=%p1%&(1<<%in%);\n"
                + "   %o2%=%p2%&(1<<%in%);\n"
                + "   %o3%=%p3%&(1<<%in%);\n"
                + "   %o4%=%p4%&(1<<%in%);\n"
                + "   %o5%=%p5%&(1<<%in%);\n"
                + "   %o6%=%p6%&(1<<%in%);\n"
                + "   %o7%=%p7%&(1<<%in%);\n"
                + "   %o8%=%p8%&(1<<%in%);\n"
                + "} else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "   %o5% = %def5%;\n"
                + "   %o6% = %def6%;\n"
                + "   %o7% = %def7%;\n"
                + "   %o8% = %def8%;\n"
                + "}\n"
                + "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_Select4L16() {
        AxoObject o = new AxoObject("sel 4l 16", "select one out of 16 4-levels, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletInt32("o", "output"));
        o.params.add(new Parameter4LevelX16("t1"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16))\n"
                + "   %o%=(%t1%>>(%in%*2))&3;\n"
                + "else %o% = %def%;\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_Select4L16_2track() {
        AxoObject o = new AxoObject("sel 4l 16 2t", "select one out of 16 4-levels, 2 tracks, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("def1", "default value track 1"));
        o.inlets.add(new InletInt32("def2", "default value track 2"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletInt32("o1", "output track 1"));
        o.outlets.add(new OutletInt32("o2", "output track 2"));
        o.params.add(new Parameter4LevelX16("t1"));
        o.params.add(new Parameter4LevelX16("t2"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   %o1%=(%t1%>>(%in%*2))&3;\n"
                + "   %o2%=(%t2%>>(%in%*2))&3;\n"
                + "}else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_Select4L16_4track() {
        AxoObject o = new AxoObject("sel 4l 16 4t", "select one out of 16 4-levels, 4 tracks, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("def1", "default value track 1"));
        o.inlets.add(new InletInt32("def2", "default value track 2"));
        o.inlets.add(new InletInt32("def3", "default value track 3"));
        o.inlets.add(new InletInt32("def4", "default value track 4"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletInt32("o1", "output track 1"));
        o.outlets.add(new OutletInt32("o2", "output track 2"));
        o.outlets.add(new OutletInt32("o3", "output track 3"));
        o.outlets.add(new OutletInt32("o4", "output track 4"));
        o.params.add(new Parameter4LevelX16("t1"));
        o.params.add(new Parameter4LevelX16("t2"));
        o.params.add(new Parameter4LevelX16("t3"));
        o.params.add(new Parameter4LevelX16("t4"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   %o1%=(%t1%>>(%in%*2))&3;\n"
                + "   %o2%=(%t2%>>(%in%*2))&3;\n"
                + "   %o3%=(%t3%>>(%in%*2))&3;\n"
                + "   %o4%=(%t4%>>(%in%*2))&3;\n"
                + "}else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_Select4L16_8track() {
        AxoObject o = new AxoObject("sel 4l 16 8t", "select one out of 16 4-levels, 8 tracks, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("def1", "default value track 1"));
        o.inlets.add(new InletInt32("def2", "default value track 2"));
        o.inlets.add(new InletInt32("def3", "default value track 3"));
        o.inlets.add(new InletInt32("def4", "default value track 4"));
        o.inlets.add(new InletInt32("def5", "default value track 5"));
        o.inlets.add(new InletInt32("def6", "default value track 6"));
        o.inlets.add(new InletInt32("def7", "default value track 7"));
        o.inlets.add(new InletInt32("def8", "default value track 8"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletInt32("o1", "output track 1"));
        o.outlets.add(new OutletInt32("o2", "output track 2"));
        o.outlets.add(new OutletInt32("o3", "output track 3"));
        o.outlets.add(new OutletInt32("o4", "output track 4"));
        o.outlets.add(new OutletInt32("o5", "output track 5"));
        o.outlets.add(new OutletInt32("o6", "output track 6"));
        o.outlets.add(new OutletInt32("o7", "output track 7"));
        o.outlets.add(new OutletInt32("o8", "output track 8"));
        o.params.add(new Parameter4LevelX16("t1"));
        o.params.add(new Parameter4LevelX16("t2"));
        o.params.add(new Parameter4LevelX16("t3"));
        o.params.add(new Parameter4LevelX16("t4"));
        o.params.add(new Parameter4LevelX16("t5"));
        o.params.add(new Parameter4LevelX16("t6"));
        o.params.add(new Parameter4LevelX16("t7"));
        o.params.add(new Parameter4LevelX16("t8"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   %o1%=(%t1%>>(%in%*2))&3;\n"
                + "   %o2%=(%t2%>>(%in%*2))&3;\n"
                + "   %o3%=(%t3%>>(%in%*2))&3;\n"
                + "   %o4%=(%t4%>>(%in%*2))&3;\n"
                + "   %o5%=(%t5%>>(%in%*2))&3;\n"
                + "   %o6%=(%t6%>>(%in%*2))&3;\n"
                + "   %o7%=(%t7%>>(%in%*2))&3;\n"
                + "   %o8%=(%t8%>>(%in%*2))&3;\n"
                + "}else {\n"
                + "   %o1% = %def1%;\n"
                + "   %o2% = %def2%;\n"
                + "   %o3% = %def3%;\n"
                + "   %o4% = %def4%;\n"
                + "   %o5% = %def5%;\n"
                + "   %o6% = %def6%;\n"
                + "   %o7% = %def7%;\n"
                + "   %o8% = %def8%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_Select4L16_8track_s() {
        AxoObject o = new AxoObject("sel 4l 16 8t s", "select one out of 16 4-levels, 8 tracks, row selector, chainable");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("row", "row selector"));
        o.inlets.add(new InletInt32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        o.outlets.add(new OutletInt32("chainrow", "chain out (row-8)"));
        o.outlets.add(new OutletInt32("o", "output"));
        o.params.add(new Parameter4LevelX16("t0"));
        o.params.add(new Parameter4LevelX16("t1"));
        o.params.add(new Parameter4LevelX16("t2"));
        o.params.add(new Parameter4LevelX16("t3"));
        o.params.add(new Parameter4LevelX16("t4"));
        o.params.add(new Parameter4LevelX16("t5"));
        o.params.add(new Parameter4LevelX16("t6"));
        o.params.add(new Parameter4LevelX16("t7"));
        o.sKRateCode = "if ((%in%>=0)&&(%in%<16)) {\n"
                + "   switch(%row%){\n"
                + "    case 0: %o%=(%t0%>>(%in%*2))&3; break;\n"
                + "    case 1: %o%=(%t1%>>(%in%*2))&3; break;\n"
                + "    case 2: %o%=(%t2%>>(%in%*2))&3; break;\n"
                + "    case 3: %o%=(%t3%>>(%in%*2))&3; break;\n"
                + "    case 4: %o%=(%t4%>>(%in%*2))&3; break;\n"
                + "    case 5: %o%=(%t5%>>(%in%*2))&3; break;\n"
                + "    case 6: %o%=(%t6%>>(%in%*2))&3; break;\n"
                + "    case 7: %o%=(%t7%>>(%in%*2))&3; break;\n"
                + "    default: %o% = %def%;\n"
                + "   }\n"
                + "}else {\n"
                + "   %o% = %def%;\n"
                + "}\n"
                + "%chain% = %in%-16;\n"
                + "%chainrow% = %row%-8;\n";
        return o;
    }

    static AxoObject Create_SelectInt16() {
        AxoObject o = new AxoObject("sel i 16", "select one out of 16 integers, with chain i/o");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        for (int i = 0; i < 16; i++) {
            ParameterInt32BoxSmall p = new ParameterInt32BoxSmall("i" + i, 0, 99);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "switch(%in%){\n";
        for (int i = 0; i < 16; i++) {
            o.sKRateCode += "case " + i + ": %o% = %i" + i + "%; break;\n";
        }
        o.sKRateCode += "default: %o% = %def%;\n"
                + "}\n";
        o.sKRateCode += "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_SelectInt32() {
        AxoObject o = new AxoObject("sel i 32", "select one out of 32 integers, with chain i/o");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletInt32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-32)"));
        for (int i = 0; i < 32; i++) {
            ParameterInt32BoxSmall p = new ParameterInt32BoxSmall("i" + i, 0, 99);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "switch(%in%){\n";
        for (int i = 0; i < 32; i++) {
            o.sKRateCode += "case " + i + ": %o% = %i" + i + "%; break;\n";
        }
        o.sKRateCode += "default: %o% = %def%;\n"
                + "}\n";
        o.sKRateCode += "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_SelectUFrac16b() {
        AxoObject o = new AxoObject("sel fp 16", "select one out of 16 positive fractionals, with chain i/o");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletFrac32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        for (int i = 0; i < 16; i++) {
            ParameterFrac32UMapVSlider p = new ParameterFrac32UMapVSlider("b" + i);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.outlets.add(new OutletFrac32Pos("o", "output"));
        o.sKRateCode = "switch(%in%){\n";
        for (int i = 0; i < 16; i++) {
            o.sKRateCode += "case " + i + ": %o% = %b" + i + "%; break;\n";
        }
        o.sKRateCode += "default: %o% = %def%;\n"
                + "}\n";
        o.sKRateCode += "%chain% = %in%-16;\n";
        return o;
    }

    static AxoObject Create_SelectSFrac16b() {
        AxoObject o = new AxoObject("sel fb 16", "select one out of 16 bipolar fractionals, with chain i/o");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletFrac32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-16)"));
        for (int i = 0; i < 16; i++) {
            ParameterFrac32SMapVSlider p = new ParameterFrac32SMapVSlider("b" + i);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "switch(%in%){\n";
        for (int i = 0; i < 16; i++) {
            o.sKRateCode += "case " + i + ": %o% = %b" + i + "%; break;\n";
        }
        o.sKRateCode += "default: %o% = %def%;\n"
                + "}\n";
        o.sKRateCode += "%chain% = %in% - 16;\n";
        return o;
    }

    static AxoObject Create_SelectUFrac32b() {
        AxoObject o = new AxoObject("sel fp 32", "select one out of 32 positive fractionals, with chain i/o");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletFrac32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-32)"));
        for (int i = 0; i < 32; i++) {
            ParameterFrac32UMapVSlider p = new ParameterFrac32UMapVSlider("b" + i);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.outlets.add(new OutletFrac32Pos("o", "output"));
        o.sKRateCode = "switch(%in%){\n";
        for (int i = 0; i < 32; i++) {
            o.sKRateCode += "case " + i + ": %o% = %b" + i + "%; break;\n";
        }
        o.sKRateCode += "default: %o% = %def%;\n"
                + "}\n";
        o.sKRateCode += "%chain% = %in%-32;\n";
        return o;
    }

    static AxoObject Create_SelectSFrac32b() {
        AxoObject o = new AxoObject("sel fb 32", "select one out of 32 bipolar fractionals, with chain i/o");
        o.inlets.add(new InletInt32("in", "in"));
        o.inlets.add(new InletFrac32("def", "default value"));
        o.outlets.add(new OutletInt32("chain", "chain out (in-32)"));
        for (int i = 0; i < 32; i++) {
            ParameterFrac32SMapVSlider p = new ParameterFrac32SMapVSlider("b" + i);
            p.noLabel = true;
            o.params.add(p);
        }
        o.setRotatedParams(true);
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "switch(%in%){\n";
        for (int i = 0; i < 32; i++) {
            o.sKRateCode += "case " + i + ": %o% = %b" + i + "%; break;\n";
        }
        o.sKRateCode += "default: %o% = %def%;\n"
                + "}\n";
        o.sKRateCode += "%chain% = %in% - 32;\n";
        return o;
    }

    static AxoObject Create_LfsrSeq() {
        AxoObject o = new AxoObject("lfsrseq", "linear feedback shift register cyclic pattern, cycled with trigger input.");
        o.outlets.add(new OutletBool32("out", "lfs pattern"));
        String mentries[] = {"0x9", "0xC", "0x12",
            "0x14",
            "0x17",
            "0x1B",
            "0x1D",
            "0x1E",
            "0x21",
            "0x2D",
            "0x30",
            "0x33",
            "0x36",
            "0x39",
            "0x41",
            "0x44",
            "0x47",
            "0x48",
            "0x4E",
            "0x53",
            "0x55",
            "0x5C",
            "0x5F",
            "0x60",
            "0x65",
            "0x69",
            "0x6A",
            "0x72",
            "0x77",
            "0x78",
            "0x7B",
            "0x7E",
            "0x8E",
            "0x95",
            "0x96",
            "0xA6",
            "0xAF",
            "0xB1",
            "0xB2",
            "0xB4",
            "0xB8",
            "0xC3",
            "0xC6",
            "0xD4",
            "0xE1",
            "0xE7",
            "0xF3",
            "0xFA",
            "0x108",
            "0x10D",
            "0x110",
            "0x116",
            "0x119",
            "0x12C",
            "0x12F",
            "0x134",
            "0x137",
            "0x13B",
            "0x13E",
            "0x143",
            "0x14A",
            "0x151",
            "0x152",
            "0x157",
            "0x15B",
            "0x15E",
            "0x167",
            "0x168",
            "0x16D",
            "0x17A",
            "0x17C",
            "0x189",
            "0x18A",
            "0x18F",
            "0x191",
            "0x198",
            "0x19D",
            "0x1A7",
            "0x1AD",
            "0x1B0",
            "0x1B5",
            "0x1B6",
            "0x1B9",
            "0x1BF",
            "0x1C2",
            "0x1C7",
            "0x1DA",
            "0x1DC",
            "0x1E3",
            "0x1E5",
            "0x1E6",
            "0x1EA",
            "0x1EC",
            "0x1F1",
            "0x1F4",
            "0x1FD",
            "0x204",
            "0x20D",
            "0x213",
            "0x216",
            "0x232",
            "0x237",
            "0x240",
            "0x245",
            "0x262",
            "0x26B",
            "0x273",
            "0x279",
            "0x27F",
            "0x286",
            "0x28C",
            "0x291",
            "0x298",
            "0x29E",
            "0x2A1",
            "0x2AB",
            "0x2B5",
            "0x2C2",
            "0x2C7",
            "0x2CB",
            "0x2D0",
            "0x2E3",
            "0x2F2",
            "0x2FB",
            "0x2FD",
            "0x309",
            "0x30A",
            "0x312",
            "0x31B",
            "0x321",
            "0x327",
            "0x32D",
            "0x33C",
            "0x33F",
            "0x344",
            "0x35A",
            "0x360",
            "0x369",
            "0x36F",
            "0x37E",
            "0x38B",
            "0x38E",
            "0x390",
            "0x39C",
            "0x3A3",
            "0x3A6",
            "0x3AA",
            "0x3AC",
            "0x3B1",
            "0x3BE",
            "0x3C6",
            "0x3C9",
            "0x3D8",
            "0x3ED",
            "0x3F9",
            "0x3FC"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.inlets.add(new InletBool32Rising("r", "reset"));
        o.inlets.add(new InletInt32("lval", "load value"));
        o.inlets.add(new InletBool32Rising("load", "load trigger"));
        o.sLocalData = "   uint32_t state;\n"
                + "   int ntrig;\n"
                + "   int rtrig;\n"
                + "   int ltrig;\n";
        o.sInitCode = "   state = 1;\n"
                + "   ntrig = 0;\n"
                + "   rtrig = 0;\n"
                + "   ltrig = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {\n"
                + "  ntrig=1;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "  }\n"
                + "} else if (!(%trig%>0)) ntrig=0;\n"
                + "if ((%r%>0) && !rtrig) {state=1; rtrig = 1;}\n"
                + "else if (!(%r%>0)) rtrig=0;\n"
                + "if ((%load%>0) && !ltrig) {state=%lval%; ltrig = 1;}\n"
                + "else if (!(%load%>0)) ltrig=0;\n"
                + "%out% = state & 1;\n";
        return o;
    }

    static AxoObject Create_selectc2() {
        AxoObject o = new AxoObject("sel dial 2", "selectable constant. Output is v1 if s>0, otherwise v0.");
        o.inlets.add(new InletBool32("s", "select"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.sKRateCode = "   %o%= (%s% > 0)?%v1%:%v0%;\n";
        return o;
    }

    static AxoObject Create_selectc4() {
        AxoObject o = new AxoObject("sel dial 4", "selectable constant. Output is v0 if s<1. v1 if s<2. v2 if s<3. v3 if more.");
        o.inlets.add(new InletInt32("s", "select"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.params.add(new ParameterFrac32UMap("v3"));
        o.sKRateCode = "   switch(%s%>0?%s%:0) {\n"
                + "      case 0: %o%= %v0%; break;\n"
                + "      case 1: %o%= %v1%; break;\n"
                + "      case 2: %o%= %v2%; break;\n"
                + "      case 3: default: %o%= %v3%; break;\n"
                + "   }\n";
        return o;
    }

    static AxoObject Create_selectc8() {
        AxoObject o = new AxoObject("sel dial 8", "selectable constant");
        o.inlets.add(new InletInt32("s", "select"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.params.add(new ParameterFrac32UMap("v3"));
        o.params.add(new ParameterFrac32UMap("v4"));
        o.params.add(new ParameterFrac32UMap("v5"));
        o.params.add(new ParameterFrac32UMap("v6"));
        o.params.add(new ParameterFrac32UMap("v7"));
        o.sKRateCode = "   switch(%s%>0?%s%:0) {\n"
                + "      case 0: %o%= %v0%; break;\n"
                + "      case 1: %o%= %v1%; break;\n"
                + "      case 2: %o%= %v2%; break;\n"
                + "      case 3: %o%= %v3%; break;\n"
                + "      case 4: %o%= %v4%; break;\n"
                + "      case 5: %o%= %v5%; break;\n"
                + "      case 6: %o%= %v6%; break;\n"
                + "      case 7: default: %o%= %v7%; break;\n"
                + "   }\n";
        return o;
    }

    static AxoObject Create_selectc16() {
        AxoObject o = new AxoObject("sel dial 16", "selectable constant");
        o.inlets.add(new InletInt32("s", "select"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.params.add(new ParameterFrac32UMap("v0"));
        o.params.add(new ParameterFrac32UMap("v1"));
        o.params.add(new ParameterFrac32UMap("v2"));
        o.params.add(new ParameterFrac32UMap("v3"));
        o.params.add(new ParameterFrac32UMap("v4"));
        o.params.add(new ParameterFrac32UMap("v5"));
        o.params.add(new ParameterFrac32UMap("v6"));
        o.params.add(new ParameterFrac32UMap("v7"));
        o.params.add(new ParameterFrac32UMap("v8"));
        o.params.add(new ParameterFrac32UMap("v9"));
        o.params.add(new ParameterFrac32UMap("v10"));
        o.params.add(new ParameterFrac32UMap("v11"));
        o.params.add(new ParameterFrac32UMap("v12"));
        o.params.add(new ParameterFrac32UMap("v13"));
        o.params.add(new ParameterFrac32UMap("v14"));
        o.params.add(new ParameterFrac32UMap("v15"));
        o.sKRateCode = "   switch(%s%>0?%s%:0) {\n"
                + "      case 0: %o%= %v0%; break;\n"
                + "      case 1: %o%= %v1%; break;\n"
                + "      case 2: %o%= %v2%; break;\n"
                + "      case 3: %o%= %v3%; break;\n"
                + "      case 4: %o%= %v4%; break;\n"
                + "      case 5: %o%= %v5%; break;\n"
                + "      case 6: %o%= %v6%; break;\n"
                + "      case 7: %o%= %v7%; break;\n"
                + "      case 8: %o%= %v8%; break;\n"
                + "      case 9: %o%= %v9%; break;\n"
                + "      case 10: %o%= %v10%; break;\n"
                + "      case 11: %o%= %v11%; break;\n"
                + "      case 12: %o%= %v12%; break;\n"
                + "      case 13: %o%= %v13%; break;\n"
                + "      case 14: %o%= %v14%; break;\n"
                + "      case 15: default: %o%= %v15%; break;\n"
                + "   }\n";
        return o;
    }

}
