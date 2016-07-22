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

import axoloti.displays.DisplayFrac32VBar;
import axoloti.displays.DisplayFrac32VBarDB;
import axoloti.displays.DisplayFrac4UByteVBar;
import axoloti.displays.DisplayVScale;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.parameters.ParameterFrac32UMap;

/**
 *
 * @author Johannes Taelman
 */
public class Spectral extends gentools {

    static void GenerateAll() {
        String catName = "spectral";
        WriteAxoObject(catName, CreateRFFT128B());
//        WriteAxoObject(dirname, CreateRFFT256());
//        WriteAxoObject(catName, CreateAnalyzer24());
//        WriteAxoObject(catName, CreateAnalyzer24DB());
        WriteAxoObject(catName, CreateAnalyzer24DB2());
//        WriteAxoObject(unstable + "/" + catName, CreateVocoder());
    }

    static AxoObject CreateRFFT128() {
        AxoObject o = new AxoObject("rfft 128", "spectral analyzer display using 128 input points fft");
        int n = 128;
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        for (int i = 0; i < (n / 4); i++) {
            DisplayFrac4UByteVBar v = new DisplayFrac4UByteVBar("v" + i);
            v.noLabel = true;
            o.displays.add(v);
        }
        o.setRotatedParams(true);
        o.sLocalData = "int32_t inbuf[" + n + "];\n"
                + "int32_t outbuf[" + n + "];\n"
                + "    int32_t fftbuf[256];\n"
                + "    int32_t hanning_q31[128];\n"
                + "    arm_rfft_instance_q31 rfft;\n"
                + "    arm_cfft_radix4_instance_q31 cfft;\n"
                + "int32_t state;\n"
                + "msg_t ThreadX2(){\n"
                + "      int i;\n"
                + "      int n = 128;\n"
                + "      arm_rfft_init_q31(&rfft, &cfft, 128,0,1);\n"
                + "      for(i=0;i<n;i++){\n"
                + "        hanning_q31[i] = (int32_t)(0.5f*2147483647.0f*(1.0f-cosf(2.0f*PI*i/n)));\n"
                + "      }\n"
                + "      while (!chThdShouldTerminate()) {\n"
                + "        chThdSleepMilliseconds(20);\n"
                + "        if (state == 128) {\n"
                + "          arm_mult_q31(hanning_q31, &inbuf[0],&inbuf[0], n);\n"
                + "          arm_rfft_q31(&rfft, &inbuf[0], &fftbuf[0]);\n"
                + "          arm_cmplx_mag_q31(&fftbuf[0], outbuf, n);\n"
                + "          // reduce to packed 8bit\n"
                + "          for(i=0;i<n/4;i++){\n"
                + "            int32_t ni;\n"
                + "            uint8_t *nc;\n"
                + "            nc = (uint8_t *)&ni;\n"
                + "            nc[0] = 0xFF & (outbuf[i*4]>>19);\n"
                + "            nc[1] = 0xFF & (outbuf[i*4+1]>>19);\n"
                + "            nc[2] = 0xFF & (outbuf[i*4+2]>>19);\n"
                + "            nc[3] = 0xFF & (outbuf[i*4+3]>>19);\n"
                + "            outbuf[i] = ni;\n"
                + "          }\n"
                + "          state = 129;\n"
                + "        }\n"
                + "      }\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 4096);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) inbuf[i]=0;\n"
                + "for(i=0;i<" + n + ";i++) outbuf[i]=0;\n"
                + "state = 0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    NORMALPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n";
        o.sKRateCode = "if (state<" + n + "){\n"
                + "   int i;\n"
                + "   for(i=0;i<16;i++)\n"
                + "      inbuf[state++] = %in%[i];\n"
                + "}\n"
                + "else if (state == " + (n + 1) + "){\n";
        o.sKRateCode += "   state = 0;\n"
                + "if (!%hold%){";
        for (int i = 0; i < (n / 4); i++) {
            o.sKRateCode += "    %v" + i + "%=outbuf[" + i + "];\n";
        }
        o.sKRateCode += "}\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateRFFT128B() {
        AxoObject o = new AxoObject("rfft 128", "spectral analyzer display using 128 input points fft");
        int n = 128;
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        for (int i = 0; i < (n / 8); i++) {
            DisplayFrac4UByteVBar v = new DisplayFrac4UByteVBar("v" + i);
            v.noLabel = true;
            o.displays.add(v);
        }
        o.setRotatedParams(true);
        o.sLocalData = "int32_t inbuf[" + n + "];\n"
                + "int32_t outbuf[" + n + "];\n"
                + "    int32_t fftbuf[" + (n * 2) + "];\n"
                + "    int32_t hanning_q31[" + n + "];\n"
                + "    arm_rfft_instance_q31 rfft;\n"
                + "    arm_cfft_radix4_instance_q31 cfft;\n"
                + "int32_t state;\n"
                + "msg_t ThreadX2(){\n"
                + "      int i;\n"
                + "      int n = " + n + ";\n"
                + "      arm_rfft_init_q31(&rfft, &cfft, " + n + ",0,1);\n"
                + "      for(i=0;i<n;i++){\n"
                + "        hanning_q31[i] = (int32_t)(0.5f*2147483647.0f*(1.0f-cosf(2.0f*PI*i/n)));\n"
                + "      }\n"
                + "      while (!chThdShouldTerminate()) {\n"
                + "        chThdSleepMilliseconds(20);\n"
                + "        if (state == " + n + ") {\n"
                + "          arm_mult_q31(hanning_q31, &inbuf[0],&inbuf[0], n);\n"
                + "          arm_rfft_q31(&rfft, &inbuf[0], &fftbuf[0]);\n"
                + "          arm_cmplx_mag_q31(&fftbuf[0], outbuf, n/2);\n"
                + "          // reduce to packed 8bit\n"
                + "          for(i=0;i<n/8;i++){\n"
                + "            int32_t ni;\n"
                + "            uint8_t *nc;\n"
                + "            nc = (uint8_t *)&ni;\n"
                + "            nc[0] = 0xFF & (outbuf[i*4]>>19);\n"
                + "            nc[1] = 0xFF & (outbuf[i*4+1]>>19);\n"
                + "            nc[2] = 0xFF & (outbuf[i*4+2]>>19);\n"
                + "            nc[3] = 0xFF & (outbuf[i*4+3]>>19);\n"
                + "            outbuf[i] = ni;\n"
                + "          }\n"
                + "          state = " + (n + 1) + ";\n"
                + "        }\n"
                + "      }\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 4096);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) inbuf[i]=0;\n"
                + "for(i=0;i<" + n + ";i++) outbuf[i]=0;\n"
                + "state = 0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    NORMALPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n";
        o.sKRateCode = "if (state<" + n + "){\n"
                + "   int i;\n"
                + "   for(i=0;i<16;i++)\n"
                + "      inbuf[state++] = %in%[i];\n"
                + "}\n"
                + "else if (state == " + (n + 1) + "){\n";
        o.sKRateCode += "   state = 0;\n"
                + "if (!%hold%){";
        for (int i = 0; i < (n / 8); i++) {
            o.sKRateCode += "    %v" + i + "%=outbuf[" + i + "];\n";
        }
        o.sKRateCode += "}\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateRFFT256() {
        AxoObject o = new AxoObject("rfft 256", "spectral analyzer display using 128 input points fft");
        int n = 256;
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletBool32("hold", "hold"));
        for (int i = 0; i < (n / 8); i++) {
            DisplayFrac4UByteVBar v = new DisplayFrac4UByteVBar("v" + i);
            v.noLabel = true;
            o.displays.add(v);
        }
        o.setRotatedParams(true);
        o.sLocalData = "int32_t inbuf[" + n + "];\n"
                + "int32_t outbuf[" + n + "];\n"
                + "    int32_t fftbuf[1024];\n"
                + "    int32_t hanning_q31[256];\n"
                + "    arm_rfft_instance_q31 rfft;\n"
                + "    arm_cfft_radix4_instance_q31 cfft;\n"
                + "int32_t state;\n"
                + "msg_t ThreadX2(){\n"
                + "      int i;\n"
                + "      int n = 256;\n"
                + "      arm_rfft_init_q31(&rfft, &cfft, 256,0,1);\n"
                + "      for(i=0;i<n;i++){\n"
                + "        hanning_q31[i] = (int32_t)(0.5f*2147483647.0f*(1.0f-cosf(2.0f*PI*i/n)));\n"
                + "      }\n"
                + "      while (!chThdShouldTerminate()) {\n"
                + "        chThdSleepMilliseconds(20);\n"
                + "        if (state == 256) {\n"
                + "          arm_mult_q31(hanning_q31, &inbuf[0],&inbuf[0], n);\n"
                + "          arm_rfft_q31(&rfft, &inbuf[0], &fftbuf[0]);\n"
                + "          arm_cmplx_mag_q31(&fftbuf[0], outbuf, n);\n"
                + "          // reduce to packed 8bit\n"
                + "          for(i=0;i<n/8;i++){\n"
                + "            int32_t ni;\n"
                + "            uint8_t *nc;\n"
                + "            nc = (uint8_t *)&ni;\n"
                + "            nc[0] = 0xFF & (outbuf[i*4]>>19);\n"
                + "            nc[1] = 0xFF & (outbuf[i*4+1]>>19);\n"
                + "            nc[2] = 0xFF & (outbuf[i*4+2]>>19);\n"
                + "            nc[3] = 0xFF & (outbuf[i*4+3]>>19);\n"
                + "            outbuf[i] = ni;\n"
                + "          }\n"
                + "          state = 257;\n"
                + "        }\n"
                + "      }\n"
                + "}\n"
                + "static msg_t ThreadX(void *arg) {\n"
                + "((attr_parent *)arg)->ThreadX2();\n"
                + "}\n";
        o.sLocalData += "WORKING_AREA(waThreadX, 16384);\n"
                + "Thread *Thd;\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + n + ";i++) inbuf[i]=0;\n"
                + "for(i=0;i<" + n + ";i++) outbuf[i]=0;\n"
                + "state = 0;\n"
                + "  Thd = chThdCreateStatic(waThreadX, sizeof(waThreadX),\n"
                + "                    LOWPRIO, ThreadX, (void *)this);\n";
        o.sDisposeCode = "chThdTerminate(Thd);\n";
        o.sKRateCode = "if (state<" + n + "){\n"
                + "   int i;\n"
                + "   for(i=0;i<16;i++)\n"
                + "      inbuf[state++] = %in%[i];\n"
                + "}\n"
                + "else if ((state == " + (n + 1) + ")&&(!%hold%)){\n"
                + "   state = 0;\n";
        for (int i = 0; i < (n / 8); i++) {
            o.sKRateCode += "    %v" + i + "%=outbuf[" + i + "];\n";
        }
        o.sKRateCode += "}\n";
        return o;
    }

    static AxoObject CreateAnalyzer24() {
        int channels = 20;
        AxoObject o = new AxoObject("analyzer24DB", "spectral analyzer made out of 2nd order bandpass filters, linear scale");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterFrac32UMap("reso"));
        for (int i = 0; i < channels; i++) {
            DisplayFrac32VBar d = new DisplayFrac32VBar("v" + i);
            d.noLabel = true;
            o.displays.add(d);
        }
        o.sLocalData = "biquad_state bs[" + channels + "];\n"
                + "biquad_coefficients bc[" + channels + "];\n"
                + "int32_t val[" + channels + "];\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + channels + ";i++){\n"
                + "   int32_t pitch = (-32 + (i*4))<<21;\n" // major thirds intervals
                + "   int32_t freq;\n"
                + "   MTOF(pitch,freq);\n"
                + "   biquad_bp_coefs(&bc[i],freq,INT_MAX - (__USAT(%reso%,27)<<4));\n"
                + "   biquad_clearstate(&bs[i]);"
                + "}\n";
        o.sKRateCode = "";
        for (int i = 0; i < channels; i++) {
            o.sKRateCode += "{\n";
            o.sKRateCode += "int32buffer t;\n";
            o.sKRateCode += "biquad_dsp(&bs[" + i + "],&bc[" + i + "],%in%,t);\n";
            o.sKRateCode += "long int accu = 0;\n";
            o.sKRateCode += "int i;\n";
            o.sKRateCode += "for(i=0;i<BUFSIZE;i++) accu += (t[i]>0?t[i]:-t[i])>>2;\n";
            o.sKRateCode += "val[" + i + "] -= val[" + i + "]>>6;\n";
            o.sKRateCode += "val[" + i + "] += accu>>8;\n";
            o.sKRateCode += "%v" + i + "% = val[" + i + "];\n";
            o.sKRateCode += "}\n";
        }
        // hide labels.....
        o.setRotatedParams(true);
        return o;
    }

    static AxoObject CreateAnalyzer24DB() {
        int channels = 20;
        AxoObject o = new AxoObject("analyzer24db", "spectral analyzer made out of 2nd order bandpass filters, decibel scale");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterFrac32UMap("reso"));
        for (int i = 0; i < channels; i++) {
            DisplayFrac32VBarDB d = new DisplayFrac32VBarDB("v" + i);
            d.noLabel = true;
            o.displays.add(d);
        }
        o.sLocalData = "biquad_state bs[" + channels + "];\n"
                + "biquad_coefficients bc[" + channels + "];\n"
                + "int32_t val[" + channels + "];\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + channels + ";i++){\n"
                + "   int32_t pitch = (-32 + (i*4))<<21;\n" // major thirds intervals
                + "   int32_t freq;\n"
                + "   MTOF(pitch,freq);\n"
                + "   biquad_bp_coefs(&bc[i],freq,INT_MAX - (__USAT(%reso%,27)<<4));\n"
                + "   biquad_clearstate(&bs[i]);"
                + "}\n";
        o.sKRateCode = "";
        for (int i = 0; i < channels; i++) {
            o.sKRateCode += "{\n";
            o.sKRateCode += "int32buffer t;\n";
            o.sKRateCode += "biquad_dsp(&bs[" + i + "],&bc[" + i + "],%in%,t);\n";
            o.sKRateCode += "long int accu = 0;\n";
            o.sKRateCode += "int i;\n";
            o.sKRateCode += "for(i=0;i<BUFSIZE;i++) accu += (t[i]>0?t[i]:-t[i])>>2;\n";
            o.sKRateCode += "val[" + i + "] -= val[" + i + "]>>6;\n";
            o.sKRateCode += "val[" + i + "] += accu>>8;\n";
            o.sKRateCode += "%v" + i + "% = val[" + i + "];\n";
            o.sKRateCode += "}\n";
        }
        // hide labels.....
        o.setRotatedParams(true);
        return o;
    }

    static AxoObject CreateAnalyzer24DB2() {
        int channels = 20;
        AxoObject o = new AxoObject("analyzer 24", "spectral analyzer made out of two 2nd order bandpass filters per band, decibel scale (vertical), steeper filters");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
//        o.params.add(new ParameterFrac32UMap("reso"));
//        o.params.add(new ParameterFrac32UMap("spread"));
        o.displays.add(new DisplayVScale());
        for (int i = 0; i < channels; i++) {
            DisplayFrac32VBarDB d = new DisplayFrac32VBarDB("v" + i);
            d.noLabel = true;
            o.displays.add(d);
        }
        o.sLocalData = "biquad_state bs1[" + channels + "];\n"
                + "biquad_state bs2[" + channels + "];\n"
                + "biquad_coefficients bc1[" + channels + "];\n"
                + "biquad_coefficients bc2[" + channels + "];\n"
                + "int32_t val[" + channels + "];\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + channels + ";i++){\n"
                + "   int32_t pitch = (-20 + (i*4))<<21;\n" // major thirds intervals
                + "   int32_t freq;\n"
                + "   MTOF(pitch,freq);\n"
                + "   biquad_bp_coefs(&bc1[i],freq,INT_MAX - ((56<<21)<<4));\n"
                + "   biquad_clearstate(&bs1[i]);\n"
                + "   int32_t pitch2 = pitch + (2<<21);\n"
                + "   int32_t freq2;\n"
                + "   MTOF(pitch2,freq2);\n"
                + "   biquad_bp_coefs(&bc2[i],freq2,INT_MAX - ((56<<21)<<4));\n"
                + "   biquad_clearstate(&bs2[i]);\n"
                + "}\n";
        o.sKRateCode = "";
        for (int i = 0; i < channels; i++) {
            o.sKRateCode += "{\n";
            o.sKRateCode += "int32buffer t1;\n";
            o.sKRateCode += "biquad_dsp(&bs1[" + i + "],&bc1[" + i + "],%in%,t1);\n";
            o.sKRateCode += "int32buffer t2;\n";
            o.sKRateCode += "biquad_dsp(&bs2[" + i + "],&bc2[" + i + "],t1,t2);\n";
            o.sKRateCode += "long int accu = 0;\n";
            o.sKRateCode += "int i;\n";
            o.sKRateCode += "for(i=0;i<BUFSIZE;i++) accu += (t2[i]>0?t2[i]:-t2[i])>>2;\n";
            o.sKRateCode += "val[" + i + "] -= val[" + i + "]>>6;\n";
            o.sKRateCode += "val[" + i + "] += accu>>8;\n";
            o.sKRateCode += "%v" + i + "% = val[" + i + "];\n";
            o.sKRateCode += "}\n";
        }
        // hide labels.....
        o.setRotatedParams(true);
        return o;
    }

    static AxoObject CreateVocoder() {
        AxoObject o = new AxoObject("vocoder 24", "channel vocoder v1");
        o.inlets.add(new InletFrac32Buffer("car", "carrier"));
        o.inlets.add(new InletFrac32Buffer("mod", "modulator"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32UMap("reso"));
        int channels = 20;
        o.sLocalData = "biquad_state bsmod[" + channels + "];\n"
                + "biquad_state bscar[" + channels + "];\n"
                + "biquad_coefficients bc[" + channels + "];\n"
                + "int32_t val[" + channels + "];\n";
        o.sInitCode = "int i;\n"
                + "for(i=0;i<" + channels + ";i++){\n"
                + "   int32_t pitch = (-32 + (i*4))<<21;\n" // major thirds intervals
                + "   int32_t freq;\n"
                + "   MTOF(pitch,freq);\n"
                + "   biquad_bp_coefs(&bc[i],freq,INT_MAX - (__USAT(%reso%,27)<<4));\n"
                + "   biquad_clearstate(&bscar[i]);"
                + "   biquad_clearstate(&bsmod[i]);"
                + "}\n";
        o.sKRateCode = "int32buffer o;\n";
        o.sKRateCode += "int i;\n";
        o.sKRateCode += "for(i=0;i<BUFSIZE;i++) o[i]=0;\n";
        o.sKRateCode += "for(i=0;i<" + channels + ";i++){\n";
        o.sKRateCode += "int32buffer t;\n";
        o.sKRateCode += "biquad_dsp(&bsmod[i],&bc[i],%mod%,t);\n";
        o.sKRateCode += "long int accu = 0;\n";
        o.sKRateCode += "int j;\n";
        o.sKRateCode += "for(j=0;j<BUFSIZE;j++) accu += (t[j]>0?t[j]:-t[j]);\n";
        o.sKRateCode += "val[i] -= val[i]>>6;\n";
        o.sKRateCode += "val[i] += accu>>6;\n";
        o.sKRateCode += "biquad_dsp(&bscar[i],&bc[i],%car%,t);\n";
        o.sKRateCode += "for(j=0;j<BUFSIZE;j++) o[j] += ___SMMUL(t[j],val[i]);\n";
        o.sKRateCode += "}\n";
        o.sKRateCode += "int j;\n for(j=0;j<BUFSIZE;j++) %out%[j] = o[j];\n";

        return o;
    }
}
