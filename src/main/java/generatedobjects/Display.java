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

import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.display.DisplayFrac32SChart;
import axoloti.object.display.DisplayFrac32SDial;
import axoloti.object.display.DisplayFrac32UChart;
import axoloti.object.display.DisplayFrac32UDial;
import axoloti.object.display.DisplayFrac32VBar;
import axoloti.object.display.DisplayFrac32VU;
import axoloti.object.display.DisplayFrac4ByteVBar;
import axoloti.object.display.DisplayFrac8S128VBar;
import axoloti.object.display.DisplayFrac8U128VBar;
import axoloti.object.display.DisplayInt32Bar16;
import axoloti.object.display.DisplayInt32Bar32;
import axoloti.object.display.DisplayInt32HexLabel;
import axoloti.object.display.DisplayInt32Label;
import axoloti.object.display.DisplayNoteLabel;
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletCharPtr32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Bipolar;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletFrac32BufferBipolar;
import axoloti.object.inlet.InletFrac32Pos;
import axoloti.object.inlet.InletInt32;
import axoloti.object.inlet.InletInt32Pos;
import axoloti.object.outlet.OutletInt32Pos;
import axoloti.object.parameter.ParameterFrac32UMap;
import static generatedobjects.GenTools.writeAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
class Display extends GenTools {

    static void generateAll() {
        String catName = "disp";
//        WriteAxoObject(catName, createKScope());
//        WriteAxoObject(catName, createKScope2());
//        WriteAxoObject(catName, createSScope());
//        WriteAxoObject(catName, createIntDisplay());
//        Objects.add(createPitchDisplay());
//        WriteAxoObject(catName, createFractDisplay());
//        WriteAxoObject(catName, createFreqDisplay());
        writeAxoObject(catName, createU7Display());
        writeAxoObject(catName, createS8Display());
        writeAxoObject(catName, createU7VBar());
        writeAxoObject(catName, createU7Scope());
        writeAxoObject(catName, createS8Scope());
        writeAxoObject(catName, createAScope());
        //WriteAxoObject(catName, createBScope());
        writeAxoObject(catName, createBScope_v2());
        //WriteAxoObject(catName, createCScope());
        writeAxoObject(catName, createCScope_v2());
        writeAxoObject(catName, createDScope_v2());
        writeAxoObject(catName, createEScope_v2());
        writeAxoObject(catName, createFScope_v2());
        writeAxoObject(catName, createGScope_v2());
        writeAxoObject(catName, createDisplayI());
        writeAxoObject(catName, createDisplayIBar16());
        writeAxoObject(catName, createDisplayIBar32());
        writeAxoObject(catName, createDisplayIBar64());
        writeAxoObject(catName, createDisplayVU());
        writeAxoObject(catName, createDisplayBool32());
        writeAxoObject(catName, createDisplayNote());

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<>();
            c.add(createHexDisplayFrac());
            c.add(createHexDisplayInt());
            c.add(createHexDisplayString());
            writeAxoObject(catName, c);
        }

    }

    static AxoObject createKScope() {
        AxoObject o = new AxoObject("scope0", "simple k-rate oscilloscope, displays one sample-per-pixel");
        o.inlets.add(new InletFrac32("in", "input"));
        /*
         ParameterInt32 p1 = new ParameterInt32();
         p1.MinValue = 0;
         p1.MaxValue = 8;
         p1.DefaultValue = 0;
         p1.name = "timescale";
         o.params.add(p1);
         */
        o.helpPatch = "kscope.axh";
        o.sLocalData = "   int32_t data[64];\n"
                + "   int32_t index;\n"
                + "   KeyValuePair kvp;\n"
                + "const char NAME[] = \"attr_name\";\n";
        o.sInitCode = "  index = 0;\n"
                + "  kvp.kvptype = KVP_TYPE_CUSTOM;\n"
                + "  kvp.keyname = %name%NAME;\n"
                + "  kvp.parent =  ObjectKvpRoot;\n"
                + "  kvp.custom.displayFunction = &k_scope_DisplayFunction; \n"
                + "  kvp.custom.userdata = (void *)%name%data;\n"
                + "  KVP_RegisterObject(&kvp);\n";
        o.sKRateCode = "  {\n"
                + "    if ( (index)||(!Btn_Nav_CurStates.btn_nav_Enter)) {\n"
                + "        data[index] = %in%;\n"
                + "        index = (index+1)&0x3F;\n"
                + "    }\n"
                + "}\n";
        return o;
    }

    static AxoObject createKScope2() {
        AxoObject o = new AxoObject("scope1", "k-rate oscilloscope, displays minimum/maximum of n samples perpixel");
        o.inlets.add(new InletFrac32("in", "input"));
        o.params.add(new ParameterFrac32UMap("timescale"));

        o.helpPatch = "kscope.axh";
        o.sLocalData = "   int32_t data[128];\n"
                + "   int32_t index;\n"
                + "   int32_t vmin;\n"
                + "   int32_t vmax;\n"
                + "   int32_t subindex;\n"
                + "   KeyValuePair kvp;\n"
                + "const char NAME[] = \"%name%\";\n";

        o.sInitCode = "  index = 0;\n"
                + "  subindex = 0;\n"
                + "  kvp.kvptype = KVP_TYPE_CUSTOM;\n"
                + "  kvp.keyname = %name%NAME;\n"
                + "  kvp.parent =  ObjectKvpRoot;\n"
                + "  kvp.custom.displayFunction = &k_scope_DisplayFunction2; \n"
                + "  kvp.custom.userdata = (void *)data;\n"
                + "  KVP_RegisterObject(&kvp);\n";
        o.sKRateCode = "  {"
                + "    if ( (index)||(!Btn_Nav_CurStates.btn_nav_Enter)) {\n"
                + "        if (subindex >= (%timescale%)>>20) {\n"
                + "             subindex = 0;\n"
                + "             if ( %in%< vmin) vmin = %in%;\n"
                + "             if ( %in%> vmax) vmax = %in%;\n"
                + "             data[index++] = vmin;\n"
                + "             data[index++] = vmax;\n"
                + "             index = index&0x7F;\n"
                + "             vmin = %in%;\n"
                + "             vmax = %in%;\n"
                + "         } else {\n"
                + "             subindex++;\n"
                + "             if ( %in%< vmin) vmin = %in%;\n"
                + "             if ( %in%> vmax) vmax = %in%;\n"
                + "         }\n"
                + "    }\n"
                + "}\n";
        return o;
    }

    static AxoObject createSScope() {
        AxoObject o = new AxoObject("scope~", "simple audio oscilloscope, displays one sample per pixel");
        o.inlets.add(new InletFrac32BufferBipolar("in", "input"));
        /*
         ParameterInt32 p1 = new ParameterInt32();
         p1.MinValue = 0;
         p1.MaxValue = 8;
         p1.DefaultValue = 0;
         p1.name = "timescale";
         o.params.add(p1);
         */
       o.helpPatch = "scope.axh";
       o.sLocalData = "   int32_t data[64];\n"
                + "   int32_t index;\n"
                + "   KeyValuePair kvp;\n"
                + "const char NAME[] = \"%name%\";\n";
        o.sInitCode = "  index = 0;\n"
                + "  kvp.kvptype = KVP_TYPE_CUSTOM;\n"
                + "  kvp.keyname = %name%NAME;\n"
                + "  kvp.parent =  ObjectKvpRoot;\n"
                + "  kvp.custom.displayFunction = &k_scope_DisplayFunction; \n"
                + "  kvp.custom.userdata = (void *)data;\n"
                + "  KVP_RegisterObject(&%name%kvp);\n";
        o.sKRateCode = "  {\n"
                + "    if ( (index)||(!Btn_Nav_CurStates.btn_nav_Enter)) {\n"
                + "        for(i=0;i<BUFSIZE;i++){\n"
                + "            data[index] = %in%[i];\n"
                + "            index = (index+1)&0x3F;\n"
                + "        }\n"
                + "    }\n"
                + "}\n";
        return o;
    }
    /*
     static AxoObject createIntDisplay() {
     AxoObject o = new AxoObject("displayint", "k-rate display, value as ////broken////");
     o.inlets.add(new InletFrac32("in", "input"));
     o.sLocalData = "KeyValuePair kvp; \n"
     + "int32_t value;\n"
     + "const char NAME[] = \"%name%\";\n";
     o.sInitCode = ""
     + "  kvp.kvptype = KVP_TYPE_INTDISPLAY;\n"
     + "  kvp.keyname = NAME;\n"
     + "  kvp.idv.value = &value; \n"
     + "  kvp.parent =  ObjectKvpRoot;\n"
     + "  KVP_RegisterObject(&kvp);\n";
     o.sKRateCode = "if (!Btn_Nav_CurStates.btn_nav_Enter) value = %in%;";
     return o;
     }*/
    /*
     static AxoObject createPitchDisplay(){

     }
     static AxoObject createFractDisplay(){

     }
     */
    /*
     static AxoObject createFreqDisplay() {
     AxoObject o = new AxoObject("displayfreq", "displays k-rate signals, value in Hertz");
     o.inlets.add(new InletFrac32("in", "input"));
     o.sLocalData = "KeyValuePair kvp; \n"
     + "int32_t value;\n"
     + "const char NAME[] = \"%name%\";\n";
     o.sInitCode = ""
     + "  kvp.kvptype = KVP_TYPE_FREQDISPLAY;\n"
     + "  kvp.keyname = NAME;\n"
     + "  kvp.idv.value = &value; \n"
     + "  kvp.parent =  ObjectKvpRoot;\n"
     + "  KVP_RegisterObject(&kvp);\n";
     o.sKRateCode = "if (!Btn_Nav_CurStates.btn_nav_Enter) value = %in%;";
     return o;
     }*/
    /*
     static AxoObject createFractDisplay() {
     AxoObject o = new AxoObject("displayfract", "displays k-rate signals, value as fraction");
     o.inlets.add(new InletFrac32("in", "input"));
     o.sLocalData = "KeyValuePair kvp; \n"
     + "int32_t value;\n"
     + "const char NAME[] = \"%name%\";\n";
     o.sInitCode = ""
     + "  kvp.kvptype = KVP_TYPE_FRACTDISPLAY;\n"
     + "  kvp.keyname = NAME;\n"
     + "  kvp.idv.value = &value; \n"
     + "  kvp.parent =  ObjectKvpRoot;\n"
     + "  KVP_RegisterObject(&kvp);\n";
     o.sKRateCode = "if (!Btn_Nav_CurStates.btn_nav_Enter) value = %in%;";
     return o;
     }
     */

    static AxoObject createU7Display() {
        AxoObject o = new AxoObject("dial p", "displays positive k-rate signals");
        o.inlets.add(new InletFrac32Pos("in", "input"));
        o.displays.add(new DisplayFrac32UDial("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createS8Display() {
        AxoObject o = new AxoObject("dial b", "displays bipolar k-rate signals");
        o.inlets.add(new InletFrac32Bipolar("in", "input"));
        o.displays.add(new DisplayFrac32SDial("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createU7VBar() {
        AxoObject o = new AxoObject("vbar", "displays positive k-rate signals");
        o.inlets.add(new InletFrac32("in", "input"));
        o.displays.add(new DisplayFrac32VBar("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createU7Scope() {
        AxoObject o = new AxoObject("chart p", "positive k-rate signal chart plotter");
        o.inlets.add(new InletFrac32Pos("in", "input"));
        o.displays.add(new DisplayFrac32UChart("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createS8Scope() {
        AxoObject o = new AxoObject("chart b", "bipolar k-rate signal chart plotter");
        o.inlets.add(new InletFrac32Bipolar("in", "input"));
        o.displays.add(new DisplayFrac32SChart("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createAScope() {
        AxoObject o = new AxoObject("scope buffer", "bipolar audio rate signal oscilloscope (time domain), showing 1 sample buffer of 16 samples");
        o.inlets.add(new InletFrac32BufferBipolar("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        for (int i = 0; i < 4; i++) {
            DisplayFrac4ByteVBar v = new DisplayFrac4ByteVBar("v" + i);
            v.noLabel = true;
            o.displays.add(v);
        }
        o.helpPatch = "scope.axh";
        o.setRotatedParams(true);
        o.sKRateCode = "if (!%hold%){\n"
                + "int8_t t[16];\n"
                + "int i;\n"
                + "for(i=0;i<16;i++)\n"
                + "   t[i] = (uint8_t)(%in%[i]>>21);\n"
                + "%v0%=*((int32_t*)(&t[0]));\n"
                + "%v1%=*((int32_t*)(&t[4]));\n"
                + "%v2%=*((int32_t*)(&t[8]));\n"
                + "%v3%=*((int32_t*)(&t[12]));\n"
                + "}\n";
        return o;
    }

    static AxoObject createBScope_v2() {
        AxoObject o = new AxoObject("scope 128 b", "bipolar audio rate signal oscilloscope (time domain), showing 128 consecutive samples");
        int n = 128;
        o.inlets.add(new InletFrac32BufferBipolar("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        o.displays.add(new DisplayFrac8S128VBar("scope"));
        o.setRotatedParams(true);
        o.helpPatch = "scope.axh";
        o.sLocalData = "int8_t t[" + n + "];\n"
                + "int index;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
                + "index = 0;\n";
        o.sKRateCode = "int i;\n"
                + "for(i=0;i<16;i++)\n"
                + "   t[index++] = (uint8_t)(%in%[i]>>21);\n"
                + "if (index == " + n + "){\n"
                + "  index = 0;\n"
                + "  if (!%hold%){\n"
                + "    for(i=0;i<128;i++)\n"
                + "      %scope%[i]=t[i];\n"
                + "  }\n"
                + "}\n";
        return o;
    }

    static AxoObject createDScope_v2() {
        AxoObject o = new AxoObject("kscope 128 b", "bipolar control rate signal oscilloscope (time domain), showing 128 consecutive samples");
        int n = 128;
        o.inlets.add(new InletFrac32Bipolar("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        o.displays.add(new DisplayFrac8S128VBar("scope"));
        o.setRotatedParams(true);
        o.helpPatch = "kscope.axh";
        o.sLocalData = "int8_t t[" + n + "];\n"
                + "int index;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
                + "index = 0;\n";
        o.sKRateCode = "int i;\n"
                + "t[index++] = (uint8_t)(%in%>>21);\n"
                + "if (index == " + n + "){\n"
                + "  index = 0;\n"
                + "  if (!%hold%){\n"
                + "    for(i=0;i<128;i++)\n"
                + "      %scope%[i]=t[i];\n"
                + "  }\n"
                + "}\n";
        return o;
    }
    /*
     static AxoObject createCScope() {
     AxoObject o = new AxoObject("scope 128 b trig", "bipolar audio rate signal oscilloscope (time domain), showing 128 consecutive samples after rising through zero-crossing");
     int n = 128;
     o.inlets.add(new InletFrac32BufferBipolar("in", "input"));
     o.inlets.add(new InletBool32("hold", "hold"));
     for (int i = 0; i < (n / 4); i++) {
     DisplayFrac4ByteVBar v = new DisplayFrac4ByteVBar("v" + i);
     v.noLabel = true;
     o.displays.add(v);
     }
     o.setRotatedParams(true);
     o.sLocalData = "int8_t t[" + n + "];\n"
     + "int index;\n"
     + "int32_t pval;\n";
     o.sInitCode = "int i;\n"
     + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
     + "index = 0;\n"
     + "pval = 0;\n";
     o.sKRateCode = "int i;\n"
     + "if (index<0) {\n" // search for trigger
     + "   for(i=0;i<16;i++){\n"
     + "      int32_t val = (%in%[i])>(1<<19);\n"
     + "      if (val&&(!pval)){\n" // start trigger
     + "         index=0; break;\n"
     + "      }\n"
     + "      pval = val;\n"
     + "   }\n"
     + "   if (index == 0){\n"
     + "      for(;i<16;i++){\n"
     + "         t[index++]=(uint8_t)(%in%[i]>>21);\n"
     + "      }\n"
     + "   }\n"
     + "} else { " // finish trigger
     + "   for(i=0;i<16;i++){\n"
     + "      if(index==" + n + ") {"
     + "         index=-1;\n"
     + "         break;\n"
     + "      }"
     + "      t[index++]=(uint8_t)(%in%[i]>>21);\n"
     + "   }"
     + "   if ((index==-1)&&(!%hold%)){\n";
     for (int i = 0; i < (n / 4); i++) {
     o.sKRateCode += "    %v" + i + "%=*((int32_t*)(&t[" + (i * 4) + "]));\n";
     }
     o.sKRateCode += "  pval = (%in%[BUFSIZE-1])>(1<<19);\n"
     + "   }\n"
     + "}\n";
     return o;
     }
     */

    static AxoObject createCScope_v2() {
        AxoObject o = new AxoObject("scope 128 b trig", "bipolar audio rate signal oscilloscope (time domain), showing 128 consecutive samples after the signal becomes positive.");
        int n = 128;
        o.inlets.add(new InletFrac32BufferBipolar("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        o.displays.add(new DisplayFrac8S128VBar("scope"));
        //        for (int i = 0; i < (n / 4); i++) {
//            DisplayFrac4ByteVBar v = new DisplayFrac4ByteVBar("v" + i);
//            v.noLabel = true;
        //           o.displays.add(v);
        //       }
        o.helpPatch = "scope.axh";
        o.setRotatedParams(true);
        o.sLocalData = "int8_t t[" + n + "];\n"
                + "int index;\n"
                + "int32_t pval;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
                + "index = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "int i;\n"
                + "if (index<0) {\n" // search for trigger
                + "   for(i=0;i<16;i++){\n"
                + "      int32_t val = (%in%[i])>(1<<19);\n"
                + "      if (val&&(!pval)){\n" // start trigger
                + "         index=0; break;\n"
                + "      }\n"
                + "      pval = val;\n"
                + "   }\n"
                + "   if (index == 0){\n"
                + "      for(;i<16;i++){\n"
                + "         t[index++]=(uint8_t)(%in%[i]>>21);\n"
                + "      }\n"
                + "   }\n"
                + "} else { " // finish trigger
                + "   for(i=0;i<16;i++){\n"
                + "      if(index==" + n + ") {\n"
                + "         index=-1;\n"
                + "         break;\n"
                + "      }\n"
                + "      t[index++]=(uint8_t)(%in%[i]>>21);\n"
                + "   }\n"
                + "   if ((index==-1)&&(!%hold%)){\n"
                + "     for(i=0;i<128;i++)\n"
                + "       %scope%[i]=t[i];\n"
                + "     pval = (%in%[BUFSIZE-1])>(1<<19);\n"
                + "   }\n"
                + "}\n";
        return o;
    }

    static AxoObject createFScope_v2() {
        AxoObject o = new AxoObject("kscope 128 p", "positive control rate signal oscilloscope (time domain), showing 128 consecutive samples");
        int n = 128;
        o.inlets.add(new InletFrac32Pos("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        o.displays.add(new DisplayFrac8U128VBar("scope"));
        o.helpPatch = "kscope.axh";
        o.setRotatedParams(true);
        o.sLocalData = "int8_t t[" + n + "];\n"
                + "int index;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
                + "index = 0;\n";
        o.sKRateCode = "int i;\n"
                + "t[index++] = (uint8_t)(%in%>>20);\n"
                + "if (index == " + n + "){\n"
                + "  index = 0;\n"
                + "  if (!%hold%){\n"
                + "    for(i=0;i<128;i++)\n"
                + "      %scope%[i]=t[i];\n"
                + "  }\n"
                + "}\n";
        return o;
    }

    static AxoObject createEScope_v2() {
        AxoObject o = new AxoObject("kscope 128 b trig", "bipolar control rate signal oscilloscope (time domain), showing 128 consecutive samples after the signal becomes positive.");
        int n = 128;
        o.inlets.add(new InletFrac32Bipolar("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        o.displays.add(new DisplayFrac8S128VBar("scope"));
        //        for (int i = 0; i < (n / 4); i++) {
//            DisplayFrac4ByteVBar v = new DisplayFrac4ByteVBar("v" + i);
//            v.noLabel = true;
        //           o.displays.add(v);
        //       }
        o.setRotatedParams(true);
        o.helpPatch = "kscope.axh";
        o.sLocalData = "int8_t t[" + n + "];\n"
                + "int index;\n"
                + "int32_t pval;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
                + "index = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "int i;\n"
                + "if (index<0) {\n" // search for trigger
                + "      int32_t val = (%in%)>(1<<19);\n"
                + "      if (val&&(!pval)){\n" // start trigger
                + "         index=0;\n"
                + "      }\n"
                + "      pval = val;\n"
                + "   if (index == 0){\n"
                + "         t[index++]=(uint8_t)(%in%>>21);\n"
                + "   }\n"
                + "} else { \n" // finish trigger
                + "   if(index==" + n + ") {\n"
                + "      index=-1;\n"
                + "   } else \n"
                + "      t[index++]=(uint8_t)(%in%>>21);\n"
                + "   if ((index==-1)&&(!%hold%)){\n"
                + "     int i;\n"
                + "     for(i=0;i<128;i++)\n"
                + "       %scope%[i]=t[i];\n"
                + "     pval = %in%>(1<<19);\n"
                + "   }\n"
                + "}\n";
        return o;
    }

    static AxoObject createGScope_v2() {
        AxoObject o = new AxoObject("kscope 128 p trig", "positive control rate signal oscilloscope (time domain), showing 128 consecutive samples after the signal becomes positive.");
        int n = 128;
        o.inlets.add(new InletFrac32Pos("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        o.displays.add(new DisplayFrac8U128VBar("scope"));
        //        for (int i = 0; i < (n / 4); i++) {
//            DisplayFrac4ByteVBar v = new DisplayFrac4ByteVBar("v" + i);
//            v.noLabel = true;
        //           o.displays.add(v);
        //       }
        o.helpPatch = "kscope.axh";
        o.setRotatedParams(true);
        o.sLocalData = "int8_t t[" + n + "];\n"
                + "int index;\n"
                + "int32_t pval;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) t[i]=0;\n"
                + "index = 0;\n"
                + "pval = 0;\n";
        o.sKRateCode = "int i;\n"
                + "if (index<0) {\n" // search for trigger
                + "      int32_t val = (%in%)>(1<<19);\n"
                + "      if (val&&(!pval)){\n" // start trigger
                + "         index=0;\n"
                + "      }\n"
                + "      pval = val;\n"
                + "   if (index == 0){\n"
                + "         t[index++]=(uint8_t)(%in%>>20);\n"
                + "   }\n"
                + "} else { \n" // finish trigger
                + "   if(index==" + n + ") {\n"
                + "      index=-1;\n"
                + "   } else \n"
                + "      t[index++]=(uint8_t)(%in%>>20);\n"
                + "   if ((index==-1)&&(!%hold%)){\n"
                + "     int i;\n"
                + "     for(i=0;i<128;i++)\n"
                + "       %scope%[i]=t[i];\n"
                + "     pval = %in%>(1<<19);\n"
                + "   }\n"
                + "}\n";
        return o;
    }

    static AxoObject createDisplayI() {
        AxoObject o = new AxoObject("i", "display integer");
        o.inlets.add(new InletInt32("in", "input"));
        o.displays.add(new DisplayInt32Label("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createDisplayIBar16() {
        AxoObject o = new AxoObject("ibar 16", "display integer bar");
        o.inlets.add(new InletInt32Pos("in", "input"));
        o.outlets.add(new OutletInt32Pos("chain_out", "chain output"));
        o.displays.add(new DisplayInt32Bar16("v"));
        o.sKRateCode = "%v%=%in%;\n"
                + "%chain_out% = %in%-16;\n";
        return o;
    }

    static AxoObject createDisplayIBar32() {
        AxoObject o = new AxoObject("ibar 32", "display integer bar");
        o.inlets.add(new InletInt32Pos("in", "input"));
        o.outlets.add(new OutletInt32Pos("chain_out", "chain output"));
        o.displays.add(new DisplayInt32Bar32("v"));
        o.sKRateCode = "%v%=%in%;\n"
                + "%chain_out% = %in%-32;\n";
        return o;
    }

    static AxoObject createDisplayIBar64() {
        AxoObject o = new AxoObject("ibar 64", "display integer bar");
        o.inlets.add(new InletInt32Pos("in", "input"));
        o.outlets.add(new OutletInt32Pos("chain_out", "chain output"));
        DisplayInt32Bar16 d = new DisplayInt32Bar16("v1");
        d.noLabel = true;
        o.displays.add(d);
        d = new DisplayInt32Bar16("v2");
        d.noLabel = true;
        o.displays.add(d);
        d = new DisplayInt32Bar16("v3");
        d.noLabel = true;
        o.displays.add(d);
        d = new DisplayInt32Bar16("v4");
        d.noLabel = true;
        o.displays.add(d);
        o.sKRateCode = "%v1%=%in%;\n"
                + "%v2%=%in%-16;\n"
                + "%v3%=%in%-32;\n"
                + "%v4%=%in%-48;\n"
                + "%chain_out% = %in%-64;\n";
        return o;
    }

    static AxoObject createDisplayBool32() {
        AxoObject o = new AxoObject("bool", "display boolean");
        o.inlets.add(new InletBool32("in", "input"));
        //o.displays.add(new DisplayBool32("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createDisplayNote() {
        AxoObject o = new AxoObject("note", "display note");
        o.setAuthor("Mark Harris");
        o.inlets.add(new InletFrac32Bipolar("in", "input"));
        o.displays.add(new DisplayNoteLabel("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createDisplayVU() {
        AxoObject o = new AxoObject("vu", "cheap vu meter display");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.displays.add(new DisplayFrac32VU("v"));
        o.sKRateCode = "%v%=%in%[0];\n";
        return o;
    }

    static AxoObject createHexDisplayFrac() {
        AxoObject o = new AxoObject("hex", "hexadecimal display (for developers)");
        o.inlets.add(new InletFrac32("in", "input"));
        o.displays.add(new DisplayInt32HexLabel("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createHexDisplayInt() {
        AxoObject o = new AxoObject("hex", "hexadecimal display (for developers)");
        o.inlets.add(new InletInt32("in", "input"));
        o.displays.add(new DisplayInt32HexLabel("v"));
        o.sKRateCode = "%v%=%in%;\n";
        return o;
    }

    static AxoObject createHexDisplayString() {
        AxoObject o = new AxoObject("hex", "hexadecimal display (for developers): char pointer");
        o.inlets.add(new InletCharPtr32("in", "input"));
        o.displays.add(new DisplayInt32HexLabel("v"));
        o.sKRateCode = "%v%=(int32_t)%in%;\n";
        return o;
    }

}
