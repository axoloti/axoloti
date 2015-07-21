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

import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.inlets.InletBool32Rising;
import axoloti.inlets.InletBool32RisingFalling;
import axoloti.inlets.InletFrac32;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32BufferBipolar;
import axoloti.outlets.OutletFrac32BufferPos;
import axoloti.parameters.ParameterFrac32UMap;

/**
 *
 * @author Johannes Taelman
 */
public class Impulses extends gentools {

    static void GenerateAll() {
        String catName = "pulse";
        WriteAxoObject(catName, Create_envd());
        WriteAxoObject(catName, Create_envd2());
        WriteAxoObject(catName, Create_envhd());
        WriteAxoObject(catName, Create_envhd2());
        WriteAxoObject(catName, Create_envahd());
        WriteAxoObject(catName, Create_envahd2());
        WriteAxoObject(catName, Create_lfsrburst4());
        WriteAxoObject(catName, Create_lfsrburst5());
        WriteAxoObject(catName, Create_lfsrburst6());
        WriteAxoObject(catName, Create_lfsrburst7());
        WriteAxoObject(catName, Create_lfsrburst8());
        WriteAxoObject(catName, Create_lfsrburst9());
        WriteAxoObject(catName, Create_lfsrburst4bp());
        WriteAxoObject(catName, Create_lfsrburst5bp());
        WriteAxoObject(catName, Create_lfsrburst6bp());
        WriteAxoObject(catName, Create_lfsrburst7bp());
        WriteAxoObject(catName, Create_lfsrburst8bp());
        WriteAxoObject(catName, Create_lfsrburst9bp());
        WriteAxoObject(catName, Create_dirac());
    }

    static AxoObject Create_envd() {
        AxoObject o = new AxoObject("d", "decay envelope, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMap("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { val =1<<27; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0; }\n";
        o.sSRateCode = "   %env% = val;\n"
                + "val -= ___SMMUL(val, param_d>>1);\n";
        return o;
    }

    static AxoObject Create_envd2() {
        AxoObject o = new AxoObject("d m", "decay envelope with decay time modulation input, audio rate");
        o.inlets.add(new InletFrac32("d", "decay time"));
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMap("d"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { val =1<<27; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "   %env% = val;\n"
                + "val -= ___SMMUL(val, (param_d+inlet_d)>>1);\n";
        return o;
    }

    static AxoObject Create_envhd() {
        AxoObject o = new AxoObject("hd", "hold/decay envelope, audio rate");
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMap("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sSRateCode = "   if (%trig%>0) val =1<<27;\n"
                + "   else val -= ___SMMUL(val, param_d>>1);\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envhd2() {
        AxoObject o = new AxoObject("hd m", "hold/decay envelope with decay time modulation input, audio rate");
        o.inlets.add(new InletFrac32("d", "decay time"));
        o.inlets.add(new InletBool32RisingFalling("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMap("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sSRateCode = "   if (%trig%>0) val =1<<27;\n"
                + "   else val -= ___SMMUL(val, (param_d+inlet_d)>>1);\n"
                + "   %env% = val;\n";
        return o;
    }

    static AxoObject Create_envahd() {
        AxoObject o = new AxoObject("ahd", "attack hold decay envelope, audio rate");
        o.inlets.add(new InletBool32RisingFalling("gate", "gate"));
        o.outlets.add(new OutletFrac32BufferPos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMap("a"));
        o.params.add(new ParameterFrac32UMap("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sSRateCode = "   if (%gate%>0) val = ___SMMLA((1<<27)-val,(1<<26)-(param_a>>1),val);\n"
                + "   else val = ___SMMLA(val, (-1<<26)+(param_d>>1),val);\n"
                + "   %env%= val;\n";
        return o;
    }

    static AxoObject Create_envahd2() {
        AxoObject o = new AxoObject("ahd m", "attack hold decay envelope with modulation inputs, audio rate");
        o.inlets.add(new InletFrac32("a", "attack time"));
        o.inlets.add(new InletFrac32("d", "decay time"));
        o.inlets.add(new InletBool32RisingFalling("gate", "gate"));
        o.outlets.add(new OutletFrac32BufferPos("env", "envelope output"));
        o.params.add(new ParameterFrac32UMap("a"));
        o.params.add(new ParameterFrac32UMap("d"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "   val = 0;\n";
        o.sSRateCode = "   if (%gate%>0) val = ___SMMLA((1<<27)-val,(1<<26)-(param_a>>1)-(inlet_a>>1),val);\n"
                + "   else val = ___SMMLA(val,(-1<<26)+(param_d>>1)+(inlet_d>>1),val);\n"
                + "   %env%= val;\n";
        return o;
    }

    static AxoObject Create_lfsrburst4() {
        AxoObject o = new AxoObject("lfsrburst 4", "lfsr burst, 4bit = 15 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs burst"));
        String mentries[] = {"0x9", "0xC"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=15; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = 0;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst5() {
        AxoObject o = new AxoObject("lfsrburst 5", "lfsr burst, 5bit = 31 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs burst"));
        String mentries[] = {"0x12",
            "0x14",
            "0x17",
            "0x1B",
            "0x1D",
            "0x1E"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=31; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = 0;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst6() {
        AxoObject o = new AxoObject("lfsrburst 6", "lfsr burst, 6bit = 63 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs burst"));
        String mentries[] = {"0x21",
            "0x2D",
            "0x30",
            "0x33",
            "0x36",
            "0x39"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=63; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = 0;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst7() {
        AxoObject o = new AxoObject("lfsrburst 7", "lfsr burst, 7bit = 127 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs burst"));
        String mentries[] = {"0x41",
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
            "0x7E"};

        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=127; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = 0;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst8() {
        AxoObject o = new AxoObject("lfsrburst 8", "lfsr burst, 8bit = 255 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs burst"));
        String mentries[] = {"0x8E",
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
            "0xFA"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=255; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = 0;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst9() {
        AxoObject o = new AxoObject("lfsrburst 9", "lfsr burst, 9bit = 511 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs burst"));
        String mentries[] = {"0x108",
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
            "0x1FD"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=511; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = 0;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst4bp() {
        AxoObject o = new AxoObject("lfsrburst 4 b", "lfsr burst, 4bit = 15 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs burst"));
        String mentries[] = {"0x9", "0xC"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=15; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst5bp() {
        AxoObject o = new AxoObject("lfsrburst 5 b", "lfsr burst, 5bit = 31 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs burst"));
        String mentries[] = {"0x12",
            "0x14",
            "0x17",
            "0x1B",
            "0x1D",
            "0x1E"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=31; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst6bp() {
        AxoObject o = new AxoObject("lfsrburst 6 b", "lfsr burst, 6bit = 63 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs burst"));
        String mentries[] = {"0x21",
            "0x2D",
            "0x30",
            "0x33",
            "0x36",
            "0x39"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=63; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst7bp() {
        AxoObject o = new AxoObject("lfsrburst 7 b", "lfsr burst, 7bit = 127 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs burst"));
        String mentries[] = {"0x41",
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
            "0x7E"};

        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=127; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst8bp() {
        AxoObject o = new AxoObject("lfsrburst 8 b", "lfsr burst, 8bit = 255 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs burst"));
        String mentries[] = {"0x8E",
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
            "0xFA"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=255; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_lfsrburst9bp() {
        AxoObject o = new AxoObject("lfsrburst 9 b", "lfsr burst, 9bit = 511 samples, audio rate");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs burst"));
        String mentries[] = {"0x108",
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
            "0x1FD"};
        o.attributes.add(new AxoAttributeComboBox("polynomial", mentries, mentries));
        o.sLocalData = "uint32_t state;\n"
                + "int32_t count;\n"
                + "int32_t ntrig;\n";
        o.sInitCode = "state = 0;\n"
                + "count = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((%trig%>0) && !ntrig) { state = 1; count=511; ntrig=1;}\n"
                + "   else { if (!(%trig%>0)) ntrig=0;}\n";
        o.sSRateCode = "if (count>0) {\n"
                + "  count--;\n"
                + "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n"
                + "} else %out% = 0;\n";
        return o;
    }

    static AxoObject Create_dirac() {
        AxoObject o = new AxoObject("dirac", "generates a single sample impulse (dirac) by triggering");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32BufferPos("out", "dirac impulse"));
        o.sLocalData = "int ntrig;\n";
        o.sInitCode = "ntrig = 0;\n";
        o.sKRateCode = "if ((%trig%>0) && !ntrig) {\n"
                + "   ntrig = 1;\n"
                + "   int i;\n"
                + "   %out%[0]=1<<27;\n"
                + "   for(i=1;i<BUFSIZE;i++) %out%[i]=0;\n"
                + "} else {\n"
                + "   int i;\n"
                + "   for(i=0;i<BUFSIZE;i++) %out%[i]=0;\n"
                + "   if (!(%trig%>0)) ntrig=0;\n"
                + "}\n";
        return o;
    }

}
