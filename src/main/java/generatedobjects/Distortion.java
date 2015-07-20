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

import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Bipolar;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletFrac32BufferBipolar;
import axoloti.parameters.ParameterFrac32UMap;
import static generatedobjects.gentools.WriteAxoObject;
import java.util.HashSet;

/**
 *
 * @author Johannes Taelman
 */
public class Distortion extends gentools {

    static void GenerateAll() {
        String catName = "dist";
        WriteAxoObject(catName, new AxoObject[]{Create_Softsat(), Create_SoftsatTilde()});
        WriteAxoObject(catName, Create_InfGain());
        WriteAxoObject(catName, Create_SchmittTriggerTilde());
        WriteAxoObject(catName, Create_SampleHoldBL());
        WriteAxoObject(catName, Create_SampleHold_Cheap());
        WriteAxoObject(catName, new AxoObject[]{Create_Slew(), Create_SlewTilde()});
        WriteAxoObject(catName, Create_Rectify());
        WriteAxoObject(catName, Create_Rectify_full());
//UNRELEASED        WriteAxoObject(catName, Create_Rectify_lab());

    }

    static AxoObject Create_Softsat() {
        AxoObject o = new AxoObject("soft", "symetrical soft saturation distortion: y=1.5*x-0.5*x^3 for -1&lt;x&lt;1, y=-1 for x&lt;-1, y=1 for x&gt;1, no oversampling or anti-aliasing");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32Bipolar("out", "output"));
        o.sKRateCode = "int32_t ts = __SSAT(%in%,28);\n"
                + "int32_t tsq31 = ts<<3;\n"
                + "int32_t tsq31p3 = ___SMMUL(tsq31,___SMMUL(tsq31,tsq31));\n"
                + "%out% = ts + (ts>>1) - (tsq31p3);\n";
        return o;
    }

    static AxoObject Create_SoftsatTilde() {
        AxoObject o = new AxoObject("soft", "symetrical soft saturation distortion: y=1.5*x-0.5*x^3 for -1&lt;x&lt;1, y=-1 for x&lt;-1, y=1 for x&gt;1, no oversampling or anti-aliasing");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sSRateCode = "int32_t ts = __SSAT(%in%,28);\n"
                + "int32_t tsq31 = ts<<3;\n"
                + "int32_t tsq31p3 = ___SMMUL(tsq31,___SMMUL(tsq31,tsq31));\n"
                + "%out% = ts + (ts>>1) - (tsq31p3);\n";
        return o;
    }

    static AxoObject Create_InfGain() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("inf", "Infinite gain, hard clipping. Algorithm: linear interpolated zero-crossing detector and blit synthesis");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   i0 = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %in%[j]>>2;\n" // prevent overflow
                + "    int32_t sum=0;\n"
                + "    if ((i1>0)&&!(i0>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    } else if ((i1<0)&&!(i0<0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((i0<<6)/(i0-i1));\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    i0 = i1;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    %out%[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject Create_SchmittTriggerTilde() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("schmitttrigger", "Schmitt trigger: hard clipping with hysteresis. Algorithm: linear interpolated zero-crossing detector and blit synthesis");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.params.add(new ParameterFrac32UMap("hysteresis"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   i0 = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %in%[j]>>2;\n" // prevent overflow
                + "    i1 += (nextvoice&1)?%hysteresis%:-%hysteresis%;\n"
                + "    int32_t sum=0;\n"
                + "    if ((i1>0)&&!(i0>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    } else if ((i1<0)&&!(i0<0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((i0<<6)/(i0-i1));\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    i0 = i1;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    %out%[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject Create_SampleHoldBL() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("samplehold", "high quality audio rate sample and hold using blit synthesis (bandwidth limited)");
        o.inlets.add(new InletFrac32Buffer("in", "level input"));
        o.inlets.add(new InletFrac32Buffer("trig", "trigger input, triggers on rising zero-crossing"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
//        o.outlets.add(new OutletFrac32BufferBipolar("out2", "audio output"));
        //o.outlets.add(new OutletInt32("xxx", "x debug"));
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  int32_t vgain[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n"
                + "  int32_t in0;\n"
                + "  int32_t acc;\n";
        o.sInitCode = "  int j;\n"
                + "  for(j=0;j<blepvoices;j++)\n"
                + "    oscp[j] = &blept[BLEPSIZE-1];\n"
                + "  nextvoice = 0;\n"
                + "  i0 = 0;\n"
                + "  in0 = 0;\n"
                + "  acc = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %trig%[j]>>2;\n" // prevent overflow
                + "    if ((i1>0)&&!(i0>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = (i1<<6)/(i1-i0);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "      int32_t val = (((64-x)*(%in%[j]>>2)) + (x*(in0>>2)))>>6;\n"
                + "      vgain[nextvoice] = (acc-val)<<2;\n"
                + "      acc = val;\n"
                + "    }\n"
                + "    int32_t sum=0;\n"
                + "    i0 = i1;\n"
                + "    in0 = %in%[j];\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      sum =___SMMLA((16384-(*t))<<16,vgain[i],sum);\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) {"
                + "        t=lastblep;\n"
                + "        vgain[i] = 0;\n"
                + "      }\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    %out%[j]=(sum+acc)<<1;\n"
                + "  }";
        return o;
    }

    static AxoObject Create_SampleHold_Cheap() {
        AxoObject o = new AxoObject("samplehold cheap", "low-quality audio rate sample and hold using blit synthesis (not bandwidth limited)");
        o.inlets.add(new InletFrac32Buffer("in", "level input"));
        o.inlets.add(new InletFrac32Buffer("trig", "trigger input, triggers on rising zero-crossing"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.sLocalData
                = "  int32_t in0;\n"
                + "  int32_t hold;\n";
        o.sInitCode = "  in0 = 0;\n"
                + "  hold = 0;\n";
        o.sSRateCode = "  if ((%trig%>0)&&!(in0>0)){\n"
                + "    hold = %in%>>1;\n"
                + "  }\n"
                + "  in0 = %trig%;\n"
                + "  %out% = hold;\n";
        return o;
    }

    static AxoObject Create_Slew() {
        AxoObject o = new AxoObject("slew", "symetric slew rate limiter (not bandwidth limited)");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.params.add(new ParameterFrac32UMap("slew"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sKRateCode = "if (%in%>acc){\n"
                + "  if ((%in%-acc)>%slew%)\n"
                + "    acc += %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "} else {\n"
                + "  if ((acc-%in%)>%slew%)\n"
                + "    acc -= %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "}\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject Create_SlewTilde() {
        AxoObject o = new AxoObject("slew", "symetric slew rate limiter (not bandwidth limited)");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32UMap("slew"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sSRateCode = "if (%in%>acc){\n"
                + "  if ((%in%-acc)>%slew%)\n"
                + "    acc += %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "} else {\n"
                + "  if ((acc-%in%)>%slew%)\n"
                + "    acc -= %slew%;\n"
                + "  else\n"
                + "    acc = %in%;\n"
                + "}\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject Create_Rectify_full() {
        AxoObject o = new AxoObject("rectifier full", "full-wave rectifier distortion, bandlimited");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.includes = new HashSet<String>();
        o.includes.add("./bltable.h");
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  const int16_t *oscp[blepvoices];\n"
                + "  int16_t amp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t in1;\n"
                + "  int32_t in2;\n"
                + "  int32_t in3;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blt[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   in1 = 0;\n"
                + "   in2 = 0;\n"
                + "   in3 = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  const int16_t *lastblep = &blt[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int in0 = %in%[j]>>2;\n"
                + "    int32_t sum = 0;\n"
                + "    if ((in0>0)&&(in1<0)){   // dispatch\n"
                + "	// rising\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = ((in0<<6)/(in0-in1));\n"
                + "      oscp[nextvoice] = &blt[x];\n"
                + "      amp[nextvoice] = (in0-in1)>>16;\n"
                + "    } else if ((in0<0)&&(in1>0)){   // dispatch\n"
                + "	//falling\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = ((in0<<6)/(in0-in1));\n"
                + "      oscp[nextvoice] = &blt[x];\n"
                + "      amp[nextvoice] = (in1-in0)>>16;\n"
                + "    }\n"
                + "    sum = (in3>0)?in3>>1:-in3>>1;\n"
                + "    in3 = in2;\n"
                + "    in2 = in1;\n"
                + "    in1 = in0;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      const int16_t *t = oscp[i];\n"
                + "      sum += (*t)*amp[i];\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    %out%[j]=sum<<2;\n"
                + "  }\n";
        return o;
    }

    static AxoObject Create_Rectify() {
        AxoObject o = new AxoObject("rectifier", "half-wave rectifier distortion, bandlimited");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.includes = new HashSet<String>();
        o.includes.add("./bltable.h");
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  const int16_t *oscp[blepvoices];\n"
                + "  int16_t amp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t in1;\n"
                + "  int32_t in2;\n"
                + "  int32_t in3;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blt[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   in1 = 0;\n"
                + "   in2 = 0;\n"
                + "   in3 = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  const int16_t *lastblep = &blt[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int in0 = %in%[j]>>2;\n"
                + "    int32_t sum = 0;\n"
                + "    if ((in0>0)&&(in1<0)){   // dispatch\n"
                + "	// rising\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = ((in0<<6)/(in0-in1));\n"
                + "      oscp[nextvoice] = &blt[x];\n"
                + "      amp[nextvoice] = (in0-in1)>>16;\n"
                + "    } else if ((in0<0)&&(in1>0)){   // dispatch\n"
                + "	//falling\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = ((in0<<6)/(in0-in1));\n"
                + "      oscp[nextvoice] = &blt[x];\n"
                + "      amp[nextvoice] = (in1-in0)>>16;\n"
                + "    }\n"
                + "    sum = (in3>0)?in3:0;\n"
                + "    in3 = in2;\n"
                + "    in2 = in1;\n"
                + "    in1 = in0;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      const int16_t *t = oscp[i];\n"
                + "      sum += (*t)*amp[i];\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    %out%[j]=sum<<2;\n"
                + "  }\n";
        return o;
    }

    static AxoObject Create_Rectify_lab() {
        /*
         zero-crossing interpolation
         f(x) = f(0) + x*(f(1)-f(0))
         assert f(x) = 0
         0 = f(0) + x*(f(1)-f(0))
         eliminate x
         x = f(0)/(f(0)-f(1))

         test
         f(0) = -5
         f(1) = 15
         x = -5/(-5-15)
         = 0.25
        
         f(0) = 15
         f(1) = -5
         x = 15/(15+5)
         = 0.75
         */

        AxoObject o = new AxoObject("rectifierlab", "half-wave rectifier distortion, bandlimited");
        o.inlets.add(new InletFrac32Buffer("in", "audio input"));
        o.inlets.add(new InletBool32("enable1", ""));
        o.inlets.add(new InletBool32("enable2", ""));
        o.inlets.add(new InletBool32("inv1", ""));
        o.inlets.add(new InletBool32("inv2", ""));
        o.inlets.add(new InletBool32("p1", ""));
        o.inlets.add(new InletBool32("p2", ""));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "audio output"));
        o.outlets.add(new OutletFrac32BufferBipolar("test1", "audio output"));
        o.outlets.add(new OutletFrac32BufferBipolar("test2", "audio output"));
        o.includes = new HashSet<String>();
        o.includes.add("./bltable.h");
        o.sLocalData
                = "  static const int blepvoices = 8;\n"
                + "  const int16_t *oscp[blepvoices];\n"
                + "  int16_t amp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n"
                + "  int32_t i0d;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blt[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   i0 = 0;\n"
                + "   i0d = 0;\n";
        o.sKRateCode = "  int j;\n"
                + "  const int16_t *lastblep = &blt[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int i1 = %in%[j]>>2;\n"
                + "//    %test1%[j] = ((i1>0)&&(i0>0))?i0:0;\n"
                + "//    %test1%[j] = (i0>0)?i0:0;\n"
                + "    %test1%[j] = (i1>0)?i1:0;\n"
                + "//    %test1%[j] = (i0d>0)?i0d:0;\n"
                + "    %test2%[j]=0;\n"
                + "    int32_t sum = 0;\n"
                + "    if (%enable1%&&(i1>0)&&(i0<0)){   // dispatch\n"
                + "	// rising\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = ((-i0<<6)/(i1-i0));\n"
                + "      if (%p1%) x = 64-x;\n"
                + "      oscp[nextvoice] = &blt[x];\n"
                + "      if (!%inv1%) \n"
                + "         amp[nextvoice] = (i1-i0)>>16;\n"
                + "      else \n"
                + "         amp[nextvoice] = -(i1-i0)>>16;\n"
                + "\n"
                + "    } else if (%enable2%&&(i1<0)&&(i0>0)){   // dispatch\n"
                + "	//falling\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = ((i0<<6)/(i0-i1));\n"
                + "      if (%p2%) x = 64-x;\n"
                + "      oscp[nextvoice] = &blt[x];\n"
                + "\n"
                + "      if (!%inv2%) \n"
                + "         amp[nextvoice] = (i1-i0)>>16;\n"
                + "      else \n"
                + "         amp[nextvoice] = -(i1-i0)>>16;\n"
                + "\n"
                + "    }\n"
                + "    i0d = i0;\n"
                + "    i0 = i1;\n"
                + "\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      const int16_t *t = oscp[i];\n"
                + "      sum += (*t)*amp[i];\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    //sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    %out%[j]=sum;\n"
                + "  }\n";
        return o;
    }

}

/*
 loadmatfile('minblep.mat')
 p=matrix(minblep, 64,32);
 q=-cumsum(p'-1,1);
 r=q-repmat(q(32,:),32,1);
 s=r/-r(1,1);
 t=s./repmat(s(1,:),32,1);
 //mprintf("%d, ", matrix(r'/-r(1,1)*16384,64*32,1))

 */
/*
 int j;
 const int16_t *lastblep = &bltri[BLEPSIZE-1];
 for(j=0;j<BUFSIZE;j++){
 int i;
 int i1 = %in%[j]>>2;
 %test1%[j] = ((i1>0)&&(i0>0))?i0:0;
 //    %test1%[j] = (i0>0)?i0:0;
 //    %test1%[j] = (i1>0)?i0:0;
 %test2%[j]=0;
 int32_t sum = 0;
 if (%enable1%&&(i1>0)&&!(i0>0)){   // dispatch

 nextvoice = (nextvoice+1)&(blepvoices-1);
 int32_t x = ((-i0<<6)/(i1-i0));
 if (%p1%) x = 64-x;
 oscp[nextvoice] = &bltri[x];
 if (!%inv1%) 
 amp[nextvoice] = i1>>16;
 else 
 amp[nextvoice] = -i1>>16;

 } else if (%enable2%&&(i1<0)&&!(i0<0)){   // dispatch

 nextvoice = (nextvoice+1)&(blepvoices-1);
 int32_t x = ((i0<<6)/(i0-i1));
 if (%p2%) x = 64-x;
 oscp[nextvoice] = &bltri[x];

 if (!%inv2%) 
 amp[nextvoice] = i1>>16;
 else 
 amp[nextvoice] = -i1>>16;

 }
 i0 = i1;
 for(i=0;i<blepvoices;i++){ // sample
 const int16_t *t = oscp[i];
 sum += (*t)*amp[i];
 t+=64;
 if (t>=lastblep) t=lastblep;
 oscp[i]=t;
 }
 //sum -= ((((nextvoice+1)&1)<<1)-1)<<13;
 %out%[j]=sum;
 }

 */
/*
 int j;
 const int16_t *lastblep = &bltri[BLEPSIZE-1];
 for(j=0;j<BUFSIZE;j++){
 int i;
 int i1 = %in%[j]>>2;
 %test2%[j]=0;


 //    %test1%[j] = ((i1>0)&&(i0>0))?i0:0;
 //    %test1%[j] = (i0d>0)?i0d:0;
 %test1%[j] = (i0>0)?i0:0;
 //    %test1%[j] = (i1>0)?i1:0;

 int32_t sum = 0;
 if (%enable1%&&(i1>0)&&(i0<0)){   // dispatch
 nextvoice = (nextvoice+1)&(blepvoices-1);
 int32_t x = ((-i0<<6)/(i1-i0));
 if (%p1%) x = 64-x;
 oscp[nextvoice] = &bltri[x];
 if (!%inv1%) 
 amp[nextvoice] = (i0-i1)>>16;
 else 
 amp[nextvoice] = -(i0-i1)>>16;
 } else if (%enable2%&&(i1<0)&&(i0>0)){   // dispatch

 nextvoice = (nextvoice+1)&(blepvoices-1);
 int32_t x = ((i0<<6)/(i0-i1));
 if (%p2%) x = 64-x;
 oscp[nextvoice] = &bltri[x];
 //	i1 = 0;
 if (!%inv2%) 
 amp[nextvoice] = (i0-i1)>>16;
 else 
 amp[nextvoice] = -(i0-i1)>>16;

 }

 i0d = i0;
 i0 = i1;
 for(i=0;i<blepvoices;i++){ // sample
 const int16_t *t = oscp[i];
 sum += (*t)*amp[i];
 t+=64;
 if (t>=lastblep) t=lastblep;
 oscp[i]=t;
 }
 //sum -= ((((nextvoice+1)&1)<<1)-1)<<13;
 %out%[j]=sum<<1;
 }
 */
