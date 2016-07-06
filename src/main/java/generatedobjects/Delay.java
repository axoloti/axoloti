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
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.parameters.ParameterFrac32SMapPitch;
import axoloti.parameters.ParameterFrac32UMap;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Delay extends gentools {

    static void GenerateAll() {
        String catName = "delay";
        WriteAxoObject(catName, CreateDelwriteTilde());
        WriteAxoObject(catName, CreateDelwriteTilde_SDRAM());
        WriteAxoObject(catName, new AxoObject[]{CreateDelreadTilde(), CreateDelreadTildeTilde()});
        WriteAxoObject(catName, CreateDelread2TildeTilde());
        WriteAxoObject(catName, CreatePitchToDelaytime());
        WriteAxoObject(catName, CreateSDelay());
        WriteAxoObject(catName, CreateSDelayFdbk());
        WriteAxoObject(catName, CreateSDelayFdbkMix());
        WriteAxoObject(catName, CreateSDelay());
    }

    static AxoObject CreateSDelayFdbkMix() {
        AxoObject o = new AxoObject("echo fdbk mix", "Audio delay with feedback and mix, fixed delay time");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletFrac32("mix", "mix"));
        o.inlets.add(new InletFrac32("feedback", "feedback"));
        o.params.add(new ParameterFrac32UMap("mix"));
        o.params.add(new ParameterFrac32UMap("feedback"));
        o.attributes.add(new AxoAttributeSpinner("delaylength", 32, 20000, 5000));
        o.sLocalData = "uint32_t delaywindex;\n"
                + "uint32_t delayrindex;\n"
                + "int16_t delayline[%delaylength%];\n";
        o.sInitCode = "   int i;\n"
                + "   for(i=0;i<%delaylength%;i++) delayline[i] = 0;\n"
                + "   delaywindex = 0;\n"
                + "   delayrindex = 1;\n";
        o.sKRateCode = "  int32_t _mix = param_mix + inlet_mix;\n"
                + "   int32_t _fdbk = param_feedback + inlet_feedback;\n";
        o.sSRateCode = " int32_t rd = delayline[delayrindex++];\n"
                + "delayline[delaywindex++] = __SSAT((%in%>>15) + ___SMMUL(rd<<5,_fdbk),16);\n"
                + "if (delayrindex == %delaylength%) delayrindex = 0;\n"
                + "if (delaywindex == %delaylength%) delaywindex = 0;\n"
                + "%out% = (%in%>>1) + (___SMMUL(rd<<16,_mix<<2));\n";
        return o;
    }

    static AxoObject CreateSDelayFdbk() {
        AxoObject o = new AxoObject("echo fdbk", "Audio delay with feedback, fixed delay time");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletFrac32("feedback", "feedback"));
        o.attributes.add(new AxoAttributeSpinner("delaylength", 32, 20000, 5000));
        o.sLocalData = "uint32_t delaywindex;\n"
                + "uint32_t delayrindex;\n"
                + "int16_t delayline[%delaylength%];\n";
        o.sInitCode = "   int i;\n"
                + "   for(i=0;i<%delaylength%;i++) delayline[i] = 0;\n"
                + "   delaywindex = 0;\n"
                + "   delayrindex = 1;\n";
        o.sSRateCode = " int32_t rd = delayline[delayrindex++];\n"
                + "delayline[delaywindex++] = __SSAT((%in%>>14) + ___SMMUL(rd<<5,inlet_feedback),16);\n"
                + "if (delayrindex == %delaylength%) delayrindex = 0;\n"
                + "if (delaywindex == %delaylength%) delaywindex = 0;\n"
                + "%out% = rd<<14;\n";
        return o;
    }

    static AxoObject CreateSDelay() {
        AxoObject o = new AxoObject("echo", "Audio delay line, fixed delay time");
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.attributes.add(new AxoAttributeSpinner("delaylength", 32, 20000, 5000));
        o.sLocalData = "uint32_t delaywindex;\n"
                + "uint32_t delayrindex;\n"
                + "int16_t delayline[%delaylength%];\n";
        o.sInitCode = "   int i;\n"
                + "   for(i=0;i<%delaylength%;i++) delayline[i] = 0;\n"
                + "   delaywindex = 0;\n"
                + "   delayrindex = 1;\n";
        o.sSRateCode = " int32_t rd = delayline[delayrindex++];\n"
                + "delayline[delaywindex++] = __SSAT(%in%>>14,16);\n"
                + "if (delayrindex == %delaylength%) delayrindex = 0;\n"
                + "if (delaywindex == %delaylength%) delaywindex = 0;\n"
                + "%out% = rd<<14;";
        return o;
    }

    static AxoObject CreateDelwriteTilde() {
        AxoObject o = new AxoObject("write", "delayline definition, read with delread~");
        String mentries[] = {"256 (5.33ms)",
            "512 (10.66ms)",
            "1024 (21.33ms)",
            "2048 (42.66ms)",
            "4096 (85.33ms)",
            "8192 (170ms)",
            "16384 (341ms)",
            "32768 (682ms)"};
        String centries[] = {"8", "9", "10", "11", "12", "13", "14", "15"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.inlets.add(new InletFrac32Buffer("in", "wave input"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "int16_t array[1<<%size%];\n"
                + "uint32_t writepos;";
        o.sInitCode = "   int i;\n"
                + "   writepos = 0;\n"
                + "   for(i=0;i<LENGTH;i++) array[i] = 0;\n";
        o.sSRateCode = "  writepos = (writepos + 1)&LENGTHMASK;\n"
                + "   array[writepos] = __SSAT(%in%>>14,16);\n";
        return o;
    }

    static AxoObject CreateDelwriteTilde_SDRAM() {
        AxoObject o = new AxoObject("write sdram", "delayline definition, read it with \"delay/read\" objects referencing the instance name of this object");
        String mentries[] = {"256 (5.33ms)",
            "512 (10.66ms)",
            "1024 (21.33ms)",
            "2048 (42.66ms)",
            "4096 (85.33ms)",
            "8192 (170ms)",
            "16384 (341ms)",
            "32768 (682ms)",
            "65536 (1.36s)",
            "131072 (2.37s)",
            "262144 (5.46s)",
            "524288 (10.9s)",
            "1048576 (21.8s)",
            "2097152 (43.7s)",};
        String centries[] = {"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21"};
        o.attributes.add(new AxoAttributeComboBox("size", mentries, centries));
        o.inlets.add(new InletFrac32Buffer("in", "wave input"));
        o.sLocalData = "static const uint32_t LENGTHPOW = (%size%);\n"
                + "static const uint32_t LENGTH = (1<<%size%);\n"
                + "static const uint32_t LENGTHMASK = ((1<<%size%)-1);\n"
                + "int16_t *array;\n"
                + "uint32_t writepos;";
        o.sInitCode = "static int16_t _array[attr_poly][1<<attr_size]  __attribute__ ((section (\".sdram\")));\n"
                + "array = &_array[parent->polyIndex][0];\n"
                + "   int i;\n"
                + "   writepos = 0;\n"
                + "   for(i=0;i<LENGTH;i++) array[i] = 0;\n";
        o.sSRateCode = "  writepos = (writepos + 1)&LENGTHMASK;\n"
                + "   array[writepos] = __SSAT(%in%>>14,16);\n";
        return o;
    }

    static AxoObject CreateDelreadTilde() {
        AxoObject o = new AxoObject("read", "delay read, non-interpolated");
        o.inlets.add(new InletFrac32("time", "delay time (fraction of total delayline size)"));
        o.outlets.add(new OutletFrac32Buffer("out", "wave"));
        o.params.add(new ParameterFrac32UMap("time"));
        o.attributes.add(new AxoAttributeObjRef("delayname"));
        o.sKRateCode = "   uint32_t delay = %delayname%.writepos - (__USAT(param_time + inlet_time,27)>>(27-%delayname%.LENGTHPOW)) - BUFSIZE;\n";
        o.sSRateCode = "  %out%= %delayname%.array[(delay++) & %delayname%.LENGTHMASK]<<14;\n";
        return o;
    }

    static AxoObject CreateDelreadTildeTilde() {
        AxoObject o = new AxoObject("read", "delay read, non-interpolated");
        o.inlets.add(new InletFrac32Buffer("time", "delay time (fraction of total delayline size)"));
        o.outlets.add(new OutletFrac32Buffer("out", "wave"));
        o.params.add(new ParameterFrac32UMap("time"));
        o.attributes.add(new AxoAttributeObjRef("delayname"));
        o.sSRateCode = ""
                + "      uint32_t delay1 = %delayname%.writepos - (__USAT(param_time + inlet_time,27)>>(27-%delayname%.LENGTHPOW)) - BUFSIZE + buffer_index;\n"
                + "      %out%= %delayname%.array[delay1 & %delayname%.LENGTHMASK]<<14;\n";
        return o;
    }

    static AxoObject CreateDelread2TildeTilde() {
        AxoObject o = new AxoObject("read interp", "delay read, linear interpolated");
        o.inlets.add(new InletFrac32Buffer("time", "delay time (fraction of total delayline size)"));
        o.outlets.add(new OutletFrac32Buffer("out", "wave"));
        o.params.add(new ParameterFrac32UMap("time"));
        o.attributes.add(new AxoAttributeObjRef("delayname"));
        o.sSRateCode = ""
                + "      uint32_t tmp_d =  __USAT(param_time + inlet_time,27);\n"
                + "      uint32_t tmp_di = %delayname%.writepos - (tmp_d>>(27-%delayname%.LENGTHPOW)) - BUFSIZE + buffer_index -1;\n"
                + "      uint32_t tmp_w1 = (tmp_d<<(%delayname%.LENGTHPOW+3)) & 0x3FFFFFFF;\n"
                + "      uint32_t tmp_w2 = (1<<30) - tmp_w1;\n"
                + "      int32_t tmp_a1 = %delayname%.array[tmp_di&%delayname%.LENGTHMASK]<<16;\n"
                + "      int32_t tmp_a2 = %delayname%.array[(tmp_di+1)&%delayname%.LENGTHMASK]<<16;\n"
                + "      int32_t tmp_r = ___SMMUL(tmp_a1,tmp_w1);\n"
                + "      tmp_r = ___SMMLA(tmp_a2,tmp_w2,tmp_r);\n"
                + "      %out%= tmp_r;\n";
        return o;
    }

    static AxoObject CreatePitchToDelaytime() {
        AxoObject o = new AxoObject("mtod", "Pitch (note index) to period time");
        o.outlets.add(new OutletFrac32("delay", "delay time"));
        o.inlets.add(new InletFrac32("pitch", "phase increment"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sKRateCode = "   int32_t freq;\n"
                + "   MTOF(0-param_pitch - inlet_pitch,freq);\n"
                + "   %delay% = freq;\n";
        return o;
    }

}
