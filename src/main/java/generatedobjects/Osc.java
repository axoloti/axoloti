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

import axoloti.object.AxoObject;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Bipolar;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletFrac32BufferBipolar;
import axoloti.object.inlet.InletFrac32Pos;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.OutletFrac32Bipolar;
import axoloti.object.outlet.OutletFrac32BufferBipolar;
import axoloti.object.outlet.OutletFrac32BufferPos;
import axoloti.object.parameter.ParameterFrac32SMapPitch;
import axoloti.object.parameter.ParameterFrac32UMap;
import axoloti.object.parameter.ParameterFrac32UMapFreq;
import axoloti.object.parameter.ParameterFrac32UMapGain;
import static generatedobjects.GenTools.writeAxoObject;
import java.util.LinkedList;

/**
 *
 * @author Johannes Taelman
 */
class Osc extends GenTools {

    static void generateAll() {
        String catName = "osc";
//        WriteAxoObject("osc", createSRateSineOsc());
//        WriteAxoObject("osc", createSRateSineOsc2());
//        WriteAxoObject("osc", createSRateSineOsc3());
        writeAxoObject(catName, createSRateSineOsc4());
//        WriteAxoObject(unstable + "osc", createSRateSineOsc5());
        writeAxoObject(catName, createSRateSineOsc5());
//        WriteAxoObject(catName, createSineAdd8());
//        WriteAxoObject("osc", createSawTilde());
//        WriteAxoObject("osc", createSaw2Tilde());
        writeAxoObject(catName, createSaw3Tilde());
        writeAxoObject(catName, createSawSyncTilde());
        writeAxoObject(catName, createSawTilde_cheap());
        writeAxoObject(catName, createSawTilde_medium());
        writeAxoObject(catName, createSaw7());
        writeAxoObject(catName, createSquare7());
        writeAxoObject(catName, createTriTilde());
        writeAxoObject(catName, createTriTilde_cheap());
        writeAxoObject(catName, createSquareTilde());
        writeAxoObject(catName, createSquareTilde_Cheap());
        writeAxoObject(catName, createSquareTilde_Medium());
        writeAxoObject(catName, createSquareSyncTilde());
//        WriteAxoObject(catName, createSquare2Tilde());
//        WriteAxoObject(catName, createSquare3Tilde());
        writeAxoObject(catName, createPWMTilde());
//        WriteAxoObject(catName, createPWM2Tilde());
        writeAxoObject(catName, createSRatePhasorOsc4());
        writeAxoObject(catName, createSRatePhasor0());
        writeAxoObject(catName, createSRatePhasor3q());

        catName = "noise";
        writeAxoObject(catName, createRandTilde());
        writeAxoObject(catName, createGaussTilde());
        writeAxoObject(catName, createPinkNoiseTilde());
        writeAxoObject(catName, createPinkNoise2Tilde());

        catName = "rand";
        writeAxoObject(catName, createPinkNoise());
        writeAxoObject(catName, createPinkNoise2());

        catName = "other";
        writeAxoObject(catName, create_lfsr());
        writeAxoObject(catName, create_bufindex());
    }

    static AxoObject createSRateSineOsc4() {
        AxoObject o = new AxoObject("sine", "sine wave oscillator");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "sine wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Buffer("freq", "frequency"));
        o.inlets.add(new InletFrac32Buffer("phase", "phase"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));

        o.sLocalData = "uint32_t Phase;";
        o.sInitCode = "Phase = 0;";
        o.sKRateCode = "   int32_t freq;\n"
                + "   MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n";
        o.sSRateCode = "Phase += freq + inlet_freq;\n"
                + "int32_t r;\n"
                + "int32_t p2 = Phase + (inlet_phase<<4);\n"
                + "SINE2TINTERP(p2,r)\n"
                + "outlet_wave= (r>>4);\n";
        return o;
    }

    static AxoObject createSineAdd8() {
        AxoObject o = new AxoObject("sine 8", "sine wave oscillator");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "sine wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Buffer("freq", "frequency"));
        o.inlets.add(new InletFrac32Buffer("phase", "phase"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        int n = 8;
        for (int i = 0; i < n; i++) {
            o.params.add(new ParameterFrac32UMap("ratio" + (i + 1)));
            o.params.add(new ParameterFrac32UMapGain("gain" + (i + 1)));
        }
        o.sLocalData = "uint32_t Phase[" + n + "];";
        o.sInitCode = "int i;"
                + "for(i=0;i<" + n + ";i++) Phase[i] = 0;";
        o.sKRateCode = "   int32_t freq;\n"
                + "   MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n";
        o.sSRateCode = "int32_t acc = 0;";
        for (int i = 0; i < n; i++) {
            o.sSRateCode += "Phase[" + i + "] +=   ;\n"
                    + " "
                    + "acc = smmla(,,acc);\n";
        }
        /*
         +"Phase[0] = "
         + "Phase += freq + inlet_freq;\n"
         + "int32_t r;\n"
         + "int32_t p2 = Phase + (inlet_phase<<4);\n"
         + "SINE2TINTERP(p2,r)\n"
         + "outlet_wave= (r>>4);\n";*/
        return o;

    }

    static AxoObject createSRateSineOsc5() {
        AxoObject o = new AxoObject("sine lin", "sine wave oscillator\nlinear frequency input (goes all the way to 0)");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "sine wave"));
        o.inlets.add(new InletFrac32Bipolar("freq", "frequency"));
        o.inlets.add(new InletFrac32BufferBipolar("phase", "phase"));
        o.params.add(new ParameterFrac32UMapFreq("freq"));

        o.sLocalData = "uint32_t Phase; ";
        o.sInitCode = "Phase = 0;";
        o.sSRateCode = "Phase += (param_freq + inlet_freq)<<4;\n"
                + "int32_t r;\n"
                + "int32_t p2 = Phase + (inlet_phase<<4);\n"
                + "SINE2TINTERP(p2,r)\n"
                + "outlet_wave= (r>>4);\n";
        return o;
    }

    static AxoObject createSaw3Tilde() {
        AxoObject o = new AxoObject("saw", "saw wave oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "saw wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 4;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+freq;\n"
                + "    if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>6);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    int32_t sum=0;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      sum+=*t;\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum = (16384*blepvoices)-sum - 8192;\n"
                + "    uint32_t g = osc_p;\n"
                + "    sum=(g>>5) + (sum<<13);\n"
                + "    outlet_wave[j]=sum;\n"
                + "  }";
        return o;
    }

    static AxoObject createSawSyncTilde() {
        AxoObject o = new AxoObject("saw sync", "Saw wave oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "saw wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Buffer("sync", "sync, resets oscillator phase on rising zero-crossing"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 4;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  int32_t vgain[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;"
                + "  i0 = 0;\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+freq;\n"
                + "    int i1 = %sync%[j]>>2;\n" // prevent overflow
                + "    if ((i1>0)&&!(i0>0)){   // phase reset\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                + "      oscp[nextvoice] = &blept[x];\n"
                + "      vgain[nextvoice] = vgain[nextvoice] = (((x * (freq>>7)) + (((uint32_t)p)>>1)))>>18;\n"
                + "      osc_p = x * (freq>>6);\n"
                + "    } else if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>6);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "      vgain[nextvoice] = 1<<13;\n"
                + "    }\n"
                + "    i0 = i1;\n"
                + "    int32_t sum=0;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      sum+=(16384-(*t))*vgain[i];\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    //sum = -sum;\n"
                + "    uint32_t g = osc_p;\n"
                + "    outlet_wave[j]=(g>>5)+sum-(1<<26);\n"
                + "  }";
        return o;
    }

    static AxoObject createSawTilde_cheap() {
        AxoObject o = new AxoObject("saw cheap", "saw wave oscillator\nNon-bandwith limited, cheap sound");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "saw wave, non-anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p;\n";
        o.sInitCode = "    osc_p=0;\n";
        o.sKRateCode = "  uint32_t freq;\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    osc_p+=freq;\n"
                + "    outlet_wave[j] = (osc_p)>>5;\n"
                + "  }\n";
        return o;
    }

    static AxoObject createSawTilde_medium() {
        AxoObject o = new AxoObject("saw medium", "saw wave oscillator\nNon-bandwith limited, medium quality");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "saw wave, non-anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p;\n";
        o.sInitCode = "    osc_p=0;\n";
        o.sKRateCode = "  uint32_t freq;\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int32_t f0i = 0x7fffffff/(1+((int)freq)>>11);\n"
                + "  int j;\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int32_t p1 = osc_p;\n"
                + "    int32_t p2 = p1 + freq;\n"
                + "    osc_p = p2;\n"
                + "    if ((p2<0)&&(p1>0))\n"
                //                + "      outlet_wave[j] = ___SMMLS(f0i, p2<<1, 0x400)<<14;\n"
                + "outlet_wave[j] = ___SMMLS(f0i, p2&~(1<<31), 0x200)<<15;\n"
                + "    else\n"
                + "      outlet_wave[j] = p2>>7;\n"
                + "  }\n";
        return o;
    }

    static AxoObject createSaw7() {
        AxoObject o = new AxoObject("supersaw", "seven detuned saw wave oscillators\nNon-bandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "supersaw wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Bipolar("detune", "detune"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMap("detune"));
        //o.params.add(new ParameterFrac32UMap("amt"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p[7];\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<7;i++)\n"
                + "    osc_p[i]=i<<28;\n";
        o.sKRateCode = "  uint32_t f0;\n"
                + "  uint32_t f[6];\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,f0);\n"
                + "  uint32_t det1 = __USAT(param_detune + inlet_detune,27);\n"
                + "  uint32_t det = ___SMMUL(det1,det1);\n"
                + "  uint32_t f0d = ___SMMUL(det<<8,f0);\n"
                + "  int i,j;\n"
                + "  f[0] = ___SMMLA(f0d,-0x54321230,f0);\n"
                + "  f[1] = ___SMMLA(f0d,-0x31111110,f0);\n"
                + "  f[2] = ___SMMLA(f0d,-0x10203040,f0);\n"
                + "  f[3] = ___SMMLA(f0d,0x10304500,f0);\n"
                + "  f[4] = ___SMMLA(f0d,0x32121210,f0);\n"
                + "  f[5] = ___SMMLA(f0d,0x55422110,f0);\n"
                + "  int32_t f0i = 0x7fffffff/(1+((int)f0)>>11);\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int32_t p1 = osc_p[6];\n"
                + "    int32_t p2 = p1 + f0;\n"
                + "    osc_p[6] = p2;\n"
                + "    if ((p2<0)&&(p1>0))\n"
                + "      outlet_wave[j] = ___SMMLS(f0i, p2&~(1<<31), 0x200)<<15;\n"
                + "    else\n"
                + "      outlet_wave[j] = p2>>7;\n"
                + "\n"
                + "    for(i=0;i<6;i++){\n"
                + "      int32_t p1 = osc_p[i];\n"
                + "      int32_t p2 = p1 + f[i];\n"
                + "      osc_p[i] = p2;\n"
                + "      if ((p2<0)&&(p1>0))\n"
                + "        outlet_wave[j] += ___SMMLS(f0i, p2&~(1<<31), 0x200)<<15;\n"
                + "      else\n"
                + "        outlet_wave[j] += p2>>7;\n"
                + "    }\n"
                + "}";
        /*
         "  uint32_t f0;\n"
         + "  uint32_t f[6];\n"
         + "  MTOFEXTENDED(param_pitch + inlet_pitch,f0);\n"
         + "  int i,j;\n"
         + "  f[0] = ___SMMLA(f0,-0x05432123,f0);\n"
         + "  f[1] = ___SMMLA(f0,-0x03111111,f0);\n"
         + "  f[2] = ___SMMLA(f0,-0x01020304,f0);\n"
         + "  f[3] = ___SMMLA(f0,0x01030450,f0);\n"
         + "  f[4] = ___SMMLA(f0,0x03212121,f0);\n"
         + "  f[5] = ___SMMLA(f0,0x05542211,f0);\n"
         + "\n"
         + "  for(j=0;j<BUFSIZE;j++){\n"
         + "    osc_p[6]+=f0;\n"
         + "    outlet_wave[j] = (osc_p[6])>>7;\n"
         + "    for(i=0;i<6;i++){\n"
         + "      osc_p[i]+=f[i];\n"
         + "      outlet_wave[j] += (osc_p[i])>>7;\n"
         + "    }\n"
         + "}\n";*/
        return o;
    }

    static AxoObject createSquare7() {
        AxoObject o = new AxoObject("supersquare", "seven detuned square wave oscillators\nNon-bandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "supersaw wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p[7];\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<7;i++)\n"
                + "    osc_p[i]=0;\n";
        o.sKRateCode = "  uint32_t f0;\n"
                + "  uint32_t f[6];\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,f0);\n"
                + "  int i,j;\n"
                + "  f[0] = ___SMMLA(f0,-0x05432123,f0);\n"
                + "  f[1] = ___SMMLA(f0,-0x03111111,f0);\n"
                + "  f[2] = ___SMMLA(f0,-0x01020304,f0);\n"
                + "  f[3] = ___SMMLA(f0,0x01030450,f0);\n"
                + "  f[4] = ___SMMLA(f0,0x03212121,f0);\n"
                + "  f[5] = ___SMMLA(f0,0x05542211,f0);\n"
                + "\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    osc_p[6]+=f0;\n"
                + "    outlet_wave[j] = (osc_p[6]>0)<<24;\n"
                + "    for(i=0;i<6;i++){\n"
                + "      osc_p[i]+=f[i];\n"
                + "      outlet_wave[j] += (osc_p[i]>0)<<24;\n"
                + "    }\n"
                + "}\n";
        return o;
    }

    static AxoObject createTriTilde() {
        AxoObject o = new AxoObject("tri", "triangle oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "triangle wave, anti-aliased"));
//        o.outlets.add(new OutletFrac32BufferBipolar("test", "triangle wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.includes.add("./bltable.h");
        o.sLocalData = "  uint32_t osc_p;\n"
                + "  static const int blepvoices = 4;\n"
                + "  const int16_t *oscp[blepvoices];\n"
                + "  int16_t amp[blepvoices];\n"
                + "  uint32_t nextvoice;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++){\n"
                + "      oscp[j] = &blt[BLEPSIZE-1];\n"
                + "      amp[j]=0;\n"
                + "    }\n"
                + "   nextvoice = 0;\n";
        o.sKRateCode = "  uint32_t freq;\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  const int16_t *lastblep = &blt[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    uint32_t p;\n"
                + "    p = osc_p;\n"
                + "    int32_t p3 = p-2*freq;\n"
                + "    int32_t tri;\n"
                + "    if (p3>0){\n"
                + "       tri = ((1<<30)-(p3))>>4;\n"
                + "    } else {\n"
                + "       tri = (p3+(1<<30))>>4;\n"
                + "    }\n"
                + "    osc_p = p+freq;\n"
                + "    if ((((int32_t)osc_p)>0)^(((int32_t)p)>0)){   // dispatch\n"
                + "      if ((freq>>6)>0) {\n"
                + "         nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "         int32_t x = (osc_p&0x7FFFFFFF)/(((uint32_t)freq)>>6);\n"
                + "         oscp[nextvoice] = &blt[x];\n"
                + "         amp[nextvoice] = (((int32_t)osc_p)<0)?freq>>16:-(freq>>16);\n"
                + "      }\n"
                + "    }\n"
                + "    int32_t sum=0;\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      const int16_t *t = oscp[i];\n"
                + "      sum += (*t)*amp[i];\n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    outlet_wave[j]=tri + (sum>>3);\n"
                + "  }\n";
        return o;
    }

    static AxoObject createTriTilde_cheap() {
        AxoObject o = new AxoObject("tri cheap", "triangle wave oscillator\nNon-bandwith limited, cheap sound");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "triangle wave, non-anti-aliased"));
//        o.outlets.add(new OutletFrac32BufferBipolar("test", "triangle wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p;\n";
        o.sInitCode = "    osc_p=0;\n";
        o.sKRateCode = "  uint32_t freq;\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    osc_p+=freq;\n"
                + "    if (osc_p>0){\n"
                + "       outlet_wave[j] = ((1<<30)-(osc_p))>>4;\n"
                + "    } else {\n"
                + "       outlet_wave[j] = (osc_p+(1<<30))>>4;\n"
                + "    }\n"
                + "}\n";
        return o;
    }

    static AxoObject createSquareTilde() {
        AxoObject o = new AxoObject("square", "square wave oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "square wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+(freq<<1);\n"
                + "    int32_t sum=0;\n"
                + "    if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>5);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    outlet_wave[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject createSquareTilde_Cheap() {
        AxoObject o = new AxoObject("square cheap", "square wave oscillator\nNon-bandwith limited, cheap sound");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "square wave, non-anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p;\n";
        o.sInitCode = "    osc_p=0;\n";
        o.sKRateCode = "  uint32_t freq;\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    osc_p+=freq;\n"
                + "    if (osc_p>0){\n"
                + "       outlet_wave[j] = (1<<26);\n"
                + "    } else {\n"
                + "       outlet_wave[j] = -(1<<26);\n"
                + "    }\n"
                + "}\n";
        return o;
    }

    static AxoObject createSquareTilde_Medium() {
        AxoObject o = new AxoObject("square medium", "square wave oscillator\nNon-bandwith limited, medium quality");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "square wave"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.includes = new LinkedList<>();
        o.sLocalData = "  int32_t osc_p;\n";
        o.sInitCode = "    osc_p=0;\n";
        o.sKRateCode = "  uint32_t freq;\n"
                + "  MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int32_t f0i = 0x7fffffff/(1+(freq)>>11);\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int32_t p1 = osc_p;\n"
                + "    int32_t p2 = p1 + freq;\n"
                + "    osc_p = p2;\n"
                + "    if ((p2<0)&&(p1>0))\n"
                + "      outlet_wave[j] = ___SMMLS(f0i, (-p1)<<1, 0x400)<<16;\n"
                + "    else if ((p1<0)&&(p2>0))\n"
                + "      outlet_wave[j] = ___SMMLS(f0i, (p1)<<1, -0x400)<<16;\n"
                + "    else\n"
                + "      outlet_wave[j] = (p2>0)?-(1<<26):((1<<26));\n"
                + "  }\n";
        return o;
    }

    static AxoObject createSquareSyncTilde() {
        AxoObject o = new AxoObject("square sync", "square wave oscillator\nBandwith limited with sync input.\nSync resets oscillator phase on rising zero-crossing");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "square wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Buffer("sync", "sync, resets phase on rising zero-crossing"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t i0;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];\n"
                + "   nextvoice = 0;\n"
                + "   i0 = 0;\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+(freq<<1);\n"
                + "    int32_t sum=0;\n"
                + "    int i1 = %sync%[j]>>2;\n" // prevent overflow
                + "    if ((i1>0)&&!(i0>0)){   // phase reset\n"
                + "      int32_t x = 64-((-i0<<6)/(i1-i0));\n" // f(0)/(f(0)-f(1))
                + "      osc_p = x * (freq>>6);\n"
                + "      if (nextvoice&1) {\n"
                + "        nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "        oscp[nextvoice] = &blept[x];\n"
                + "      }\n"
                + "    } else if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>5);\n"
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
                + "    outlet_wave[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject createSquare2Tilde() {
        AxoObject o = new AxoObject("square2", "square wave oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "square wave, anti-aliased"));
        o.outlets.add(new OutletFrac32BufferBipolar("wave2", "square wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletInt32("shift", "shift"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+(freq<<1);\n"
                + "    int32_t sum=0;\n"
                + "    if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>%shift%);\n" // 6
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=(*t)*2-16384; else sum-=(*t)*2-16384; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    outlet_wave[j]=sum<<12;"
                + "    %wave2%[j] = (-((((nextvoice+1)&1)<<1)-1)<<14)<<12;\n"
                + "  }";
        return o;
    }

    static AxoObject createSquare3Tilde() {
        AxoObject o = new AxoObject("square3", "square wave oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "square wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+(freq<<1);\n"
                + "    int32_t sum=0;\n"
                + "    if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>5);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    outlet_wave[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject createPWMTilde() {
        AxoObject o = new AxoObject("pwm", "pulse width modulation oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "pwm wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Bipolar("pw", "pulse width. Zero corresponds to 50% duty cycle."));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t pwmp;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;";
        o.sKRateCode = "      uint32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+freq;\n"
                + "    int32_t sum=0;\n"
                + "    if (((int32_t)osc_p)>=((int32_t)(osc_p-pwmp))){\n"
                + "      if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "        nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "        int32_t x = 0;\n"
                + "        if (freq>>24)\n"
                + "           x = osc_p/(freq>>6);\n"
                + "        else if (freq)\n"
                + "           x = (osc_p<<6)/freq;\n"
                + "        oscp[nextvoice] = &blept[x];\n"
                + "        pwmp = ((1<<27)+inlet_pw)<<4;\n"
                + "      }\n"
                + "      if (((osc_p-pwmp)>0)&&!((p-pwmp)>0)){   // dispatch\n"
                + "        nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "        uint32_t x = 0;\n"
                + "        if (freq>>24)\n"
                + "          x = (osc_p-pwmp)/(freq>>6);\n"
                + "        else if (freq)\n"
                + "          x = ((osc_p-pwmp)<<6)/(freq);\n"
                + "        oscp[nextvoice] = &blept[x];\n"
                + "      }\n"
                + "    } else {\n"
                + "      if (((osc_p-pwmp)>0)&&!((p-pwmp)>0)){   // dispatch\n"
                + "        nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "        uint32_t x = 0;\n"
                + "        if (freq>>24)\n"
                + "          x = (osc_p-pwmp)/(freq>>6);\n"
                + "        else if (freq)\n"
                + "          x = ((osc_p-pwmp)<<6)/(freq);\n"
                + "        oscp[nextvoice] = &blept[x];\n"
                + "      }\n"
                + "      if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "        nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "        int32_t x = 0;\n"
                + "        if (freq>>24)\n"
                + "           x = osc_p/(freq>>6);\n"
                + "        else if (freq)\n"
                + "           x = (osc_p<<6)/freq;\n"
                + "        oscp[nextvoice] = &blept[x];\n"
                + "        pwmp = ((1<<27)+inlet_pw)<<4;\n"
                + "      }\n"
                + "    }\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<13;\n"
                + "    outlet_wave[j]=sum<<13;\n"
                + "  }";
        return o;
    }

    static AxoObject createPWM2Tilde() {
        AxoObject o = new AxoObject("pwm2", "pulse width modulation oscillator\nBandwith limited");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "pwm wave, anti-aliased"));
        o.inlets.add(new InletFrac32Bipolar("pitch", "pitch"));
        o.inlets.add(new InletFrac32Pos("pw", "pulse width"));
        o.inlets.add(new InletInt32("shift", "shift"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.sLocalData = "  int32_t osc_p;\n"
                + "  static const int blepvoices = 8;\n"
                + "  int16_t *oscp[blepvoices];\n"
                + "  uint32_t nextvoice;\n"
                + "  int32_t pwmp;\n";
        o.sInitCode = "    int j;\n"
                + "    for(j=0;j<blepvoices;j++)\n"
                + "      oscp[j] = &blept[BLEPSIZE-1];"
                + "   nextvoice = 0;";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n"
                + "  int j;\n"
                + "  int16_t *lastblep = &blept[BLEPSIZE-1];\n"
                + "  for(j=0;j<BUFSIZE;j++){\n"
                + "    int i;\n"
                + "    int p;\n"
                + "    p = osc_p;\n"
                + "    osc_p = p+freq;\n"
                + "    int32_t sum=0;\n"
                + "    if ((osc_p>0)&&!(p>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = osc_p/(freq>>6);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "      pwmp = inlet_pw;\n"
                + "    }\n"
                + "    if ((osc_p-(pwmp<<4)>0)&&!(p-(pwmp<<4)>0)){   // dispatch\n"
                + "      nextvoice = (nextvoice+1)&(blepvoices-1);\n"
                + "      int32_t x = (osc_p-(inlet_pw<<4))/(freq>>6);\n"
                + "      oscp[nextvoice] = &blept[x];\n"
                + "    }\n"
                + "    for(i=0;i<blepvoices;i++){ // sample\n"
                + "      int16_t *t = oscp[i];\n"
                + "      if (i&1) sum+=*t; else sum-=*t; \n"
                + "      t+=64;\n"
                + "      if (t>=lastblep) t=lastblep;\n"
                + "      oscp[i]=t;\n"
                + "    }\n"
                + "    sum = sum*2;\n"
                + "    sum -= ((((nextvoice+1)&1)<<1)-1)<<14;\n"
                + "    outlet_wave[j]=sum<<12;\n"
                + "  }";
        return o;
    }

    static AxoObject createSRatePhasorOsc4() {
        AxoObject o = new AxoObject("phasor", "phasor\nsaw wave like oscillator");
        o.outlets.add(new OutletFrac32BufferPos("phasor", "phasor wave"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32Buffer("freq", "phase increment"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));

        o.sLocalData = "uint32_t Phase;";
        o.sInitCode = "Phase = 0;";
        o.sKRateCode = "   uint32_t freq;\n"
                + "   MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n";
        o.sSRateCode = "Phase += (freq>>0) + inlet_freq;\n"
                + "   %phasor% = Phase>>5;\n";
        return o;
    }

    static AxoObject createSRatePhasor0() {
        AxoObject o = new AxoObject("phasor lin", "phasor\nsaw like wave with linear frequency input(goes all the way to 0)");
        o.outlets.add(new OutletFrac32BufferPos("phasor", "phasor wave"));
        o.inlets.add(new InletFrac32Bipolar("freq", "frequency"));
        o.inlets.add(new InletFrac32BufferBipolar("phase", "phase"));
        o.params.add(new ParameterFrac32UMapFreq("freq"));

        o.sLocalData = "uint32_t Phase; ";
        o.sInitCode = "Phase = 0;";
        o.sSRateCode = "Phase += (param_freq + inlet_freq)<<4;\n"
                + "int32_t r;\n"
                + "int32_t p2 = Phase + (inlet_phase<<4);\n"
                + "%phasor%= (p2>>4);\n";
        return o;
    }

    static AxoObject createSRatePhasor3q() {
        AxoObject o = new AxoObject("phasor compl", "FM phasor\n 0 and 180 degree outputs");
        o.outlets.add(new OutletFrac32BufferPos("phasor0", "phasor wave"));
        o.outlets.add(new OutletFrac32BufferPos("phasor180", "phasor wave, 180 degrees shifted"));
        o.inlets.add(new InletFrac32("pitch", "phase increment"));
        o.inlets.add(new InletFrac32Buffer("freq", "phase increment"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));

        o.sLocalData = "uint32_t Phase;";
        o.sInitCode = "Phase = 0;";
        o.sKRateCode = "   uint32_t freq;\n"
                + "   MTOFEXTENDED(param_pitch + inlet_pitch,freq);\n";
        o.sSRateCode = "Phase += (freq>>0) + inlet_freq;\n"
                + "   %phasor0% = Phase>>5;\n"
                + "   %phasor180% = (Phase+(1<<31))>>5;\n";
        return o;
    }

    static AxoObject createRandTilde() {
        AxoObject o = new AxoObject("uniform", "uniform distributed (white) noise\nRange -64..64");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "white noise"));
        o.sSRateCode = "outlet_wave= (int32_t)(GenerateRandomNumber())>>4;";
        return o;
    }

    static AxoObject createGaussTilde() {
        AxoObject o = new AxoObject("gaussian", "pseudo gaussian distributed (white) noise\nRange -64..64");
        o.outlets.add(new OutletFrac32BufferBipolar("wave", "white noise"));
        o.sLocalData = "uint32_t seeds[8];\n";
// seeds from http://www.random.org/cgi-bin/randbyte?nbytes=32&format=h
        o.sInitCode = "seeds[0] = 0x21c32332 + GenerateRandomNumber();\n"
                + "seeds[1] = 0xfbc57f7a + GenerateRandomNumber();\n"
                + "seeds[2] = 0x7dd1ef4a + GenerateRandomNumber();\n"
                + "seeds[3] = 0xe4ec34ad + GenerateRandomNumber();\n"
                + "seeds[4] = 0x72007b2f + GenerateRandomNumber();\n"
                + "seeds[5] = 0x3d1e9783 + GenerateRandomNumber();\n"
                + "seeds[6] = 0xa4a8f892 + GenerateRandomNumber();\n"
                + "seeds[7] = 0xc82c5e28 + GenerateRandomNumber();\n";
        o.sSRateCode = "int i;\n"
                + "int32_t n=0;\n"
                + "for(i=0;i<8;i++){\n"
                + "   seeds[i] = (seeds[i] * 196314165) + 907633515;"
                + "   n += ((int32_t)(seeds[i]))>>7;\n"
                + "}\n"
                + "outlet_wave= n;";
        return o;
    }

    /*
     0102010301020104010201030102010501020103010201040102010301020106010201030102010401020103010201050102010301020104010201030102010x
     0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
     1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1   1
     2       2       2       2       2       2       2       2       2       2       2       2       2       2       2       2
     3               3               3               3               3               3               3               3
     4                               4                               4                               4
     5                                                               5
     6
     */
    static AxoObject createPinkNoiseTilde() {
        AxoObject o = new AxoObject("pink", "Cheap almost pink noise\nover 7 octaves. Range -64..64");
        o.outlets.add(new OutletFrac32BufferBipolar("out", "pink noise"));
        o.sLocalData = "static const int noct = 7;\n"
                + "int32_t obuf[noct];\n"
                + "int32_t sum;\n"
                + "uint32_t seed;\n"
                + "int index;\n"
                + "const int8_t * dyadictree(void){\n"
                + "   static const int8_t dtree[] = {\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,6,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,-1};\n"
                + "   return dtree;\n"
                + "}\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<noct;i++)\n"
                + "   obuf[i]=0;\n"
                + "index = 0;\n"
                + "sum = 0;\n"
                + "seed = 0x830af41e + GenerateRandomNumber();\n";
        o.sSRateCode = "int o = dyadictree()[index++];\n"
                + "if (o==-1){\n"
                + "   index=0;\n"
                + "} else {\n"
                + "   sum -= obuf[o];\n"
                + "   seed = (seed * 196314165) + 907633515;\n"
                + "   obuf[o] = ((int32_t)seed)>>7;\n"
                + "   sum += obuf[o];\n"
                + "}\n"
                + "seed = (seed * 196314165) + 907633515;\n"
                + "%out% = sum + (((int32_t)seed)>>7);\n";
        return o;
    }

    static AxoObject createPinkNoise2Tilde() {
        AxoObject o = new AxoObject("pink oct", "Cheap almost pink noise\nRange -64..64. Configureable number of octaves.");
        String mentries[] = {
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7"};
        o.attributes.add(new AxoAttributeComboBox("octaves", mentries, mentries));
        o.outlets.add(new OutletFrac32BufferBipolar("out", "pink noise"));
        o.sLocalData = "static const int noct = %octaves%;\n"
                + "int32_t obuf[noct];\n"
                + "int32_t sum;\n"
                + "uint32_t seed;\n"
                + "int index;\n"
                + "const int8_t * dyadictree(void){\n"
                + "   static const int8_t dtree[] = {\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,6,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,7};\n"
                + "   return dtree;\n"
                + "}\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<noct;i++)\n"
                + "   obuf[i]=0;\n"
                + "index = 0;\n"
                + "sum = 0;\n"
                + "seed = 0x830af41e + GenerateRandomNumber();\n";
        o.sSRateCode = "int o = dyadictree()[index++];\n"
                + "if (o==%octaves%){\n"
                + "   index=0;\n"
                + "} else {\n"
                + "   sum -= obuf[o];\n"
                + "   seed = (seed * 196314165) + 907633515;\n"
                + "   obuf[o] = ((int32_t)seed)>>7;\n"
                + "   sum += obuf[o];\n"
                + "}\n"
                + "seed = (seed * 196314165) + 907633515;\n"
                + "%out%= sum + (((int32_t)seed)>>%octaves%);\n";
        return o;
    }

    static AxoObject createPinkNoise() {
        AxoObject o = new AxoObject("pink", "Cheap almost pink noise\nover 7 octaves. Range -64..64");
        o.outlets.add(new OutletFrac32Bipolar("out", "pink noise"));
        o.sLocalData = "static const int noct = 7;\n"
                + "int32_t obuf[noct];\n"
                + "int32_t sum;\n"
                + "uint32_t seed;\n"
                + "int index;\n"
                + "const int8_t * dyadictree(void){\n"
                + "   static const int8_t dtree[] = {\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,6,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,-1};\n"
                + "   return dtree;\n"
                + "}\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<noct;i++)\n"
                + "   obuf[i]=0;\n"
                + "index = 0;\n"
                + "sum = 0;\n"
                + "seed = 0x830af41e + GenerateRandomNumber();\n";
        o.sKRateCode = "int o = dyadictree()[index++];\n"
                + "if (o==-1){\n"
                + "   index=0;\n"
                + "} else {\n"
                + "   sum -= obuf[o];\n"
                + "   seed = (seed * 196314165) + 907633515;\n"
                + "   obuf[o] = ((int32_t)seed)>>7;\n"
                + "   sum += obuf[o];\n"
                + "}\n"
                + "seed = (seed * 196314165) + 907633515;\n"
                + "%out% = sum + (((int32_t)seed)>>7);\n";
        return o;
    }

    static AxoObject createPinkNoise2() {
        AxoObject o = new AxoObject("pink oct", "Cheap almost pink noise\nRange -64..64. Configureable number of octaves.");
        String mentries[] = {
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7"};
        o.attributes.add(new AxoAttributeComboBox("octaves", mentries, mentries));
        o.outlets.add(new OutletFrac32Bipolar("out", "pink noise"));
        o.sLocalData = "static const int noct = %octaves%;\n"
                + "int32_t obuf[noct];\n"
                + "int32_t sum;\n"
                + "uint32_t seed;\n"
                + "int index;\n"
                + "const int8_t * dyadictree(void){\n"
                + "   static const int8_t dtree[] = {\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,6,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,5,\n"
                + "   0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,4,0,1,0,2,0,1,0,3,0,1,0,2,0,1,0,7};\n"
                + "   return dtree;\n"
                + "}\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<noct;i++)\n"
                + "   obuf[i]=0;\n"
                + "index = 0;\n"
                + "sum = 0;\n"
                + "seed = 0x830af41e + GenerateRandomNumber();\n";
        o.sKRateCode = "int o = dyadictree()[index++];\n"
                + "if (o==%octaves%){\n"
                + "   index=0;\n"
                + "} else {\n"
                + "   sum -= obuf[o];\n"
                + "   seed = (seed * 196314165) + 907633515;\n"
                + "   obuf[o] = ((int32_t)seed)>>7;\n"
                + "   sum += obuf[o];\n"
                + "}\n"
                + "seed = (seed * 196314165) + 907633515;\n"
                + "%out% = sum + (((int32_t)seed)>>%octaves%);\n";
        return o;
    }

    static AxoObject create_lfsr() {
        AxoObject o = new AxoObject("lfsr", "linear feedback shift register cyclic pattern\naudio rate");
        o.outlets.add(new OutletFrac32BufferBipolar("out", "lfs pattern"));
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
        o.sLocalData = "uint32_t state;\n";
        o.sInitCode = "state = 1;\n";
        o.sSRateCode = "  if (state & 1)  {\n"
                + "     state = (state >> 1) ^ %polynomial%;\n"
                + "     %out% = 1<<27;\n"
                + "  } else {\n"
                + "    state = (state >> 1);\n"
                + "     %out% = -1<<27;\n"
                + "  }\n";
        return o;
    }

    static AxoObject create_bufindex() {
        AxoObject o = new AxoObject("bufferindex", "outputs a constant buffer containing [0,4,8,12,...].");
        o.outlets.add(new OutletFrac32BufferPos("out", "lfs pattern"));
        o.sKRateCode = "int i;\n"
                + "for(i=0;i<BUFSIZE;i++)\n"
                + "  %out%[i] = i<<23;\n";
        return o;
    }
}
