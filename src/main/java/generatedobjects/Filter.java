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
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.ParameterFrac32SMap;
import axoloti.object.parameter.ParameterFrac32SMapKPitch;
import axoloti.object.parameter.ParameterFrac32SMapLFOPitch;
import axoloti.object.parameter.ParameterFrac32SMapPitch;
import axoloti.object.parameter.ParameterFrac32UMap;
import axoloti.object.parameter.ParameterFrac32UMapFilterQ;
import axoloti.object.parameter.ParameterInt32Box;
import static generatedobjects.GenTools.writeAxoObject;
import java.util.LinkedList;

/**
 *
 * @author Johannes Taelman
 */
class Filter extends GenTools {

    static void generateAll() {
        String catName = "filter";
        writeAxoObject(catName, createVCF());
        writeAxoObject(catName, createVCF2());
        writeAxoObject(catName, createVCF3());

        writeAxoObject(catName, createLPF());
        writeAxoObject(catName, createHPF());
        writeAxoObject(catName, createBPF());

        writeAxoObject(catName, createLPFM());
        writeAxoObject(catName, createHPFM());
        writeAxoObject(catName, createBPFM());

//        WriteAxoObject(catName, createNF());
//        WriteAxoObject(catName, createAPF());
        writeAxoObject(catName, create_lowpassTilde());
        writeAxoObject(catName, create_hipassTilde());
        writeAxoObject(catName, create_lowpassMTilde());
        writeAxoObject(catName, create_hipassMTilde());

        writeAxoObject(catName, create_Fir16());
        writeAxoObject(catName, create_EQ5hq());
        writeAxoObject(catName, create_EQ4());

        writeAxoObject(catName, new AxoObject[]{createDelta(), createDeltaTilde(), createDeltaI()});
        writeAxoObject(catName, new AxoObject[]{createIntegrator(), createIntegratorTilde()});
        writeAxoObject(catName, new AxoObject[]{createIntegratorLeaky(), createIntegratorLeakyTilde()});

        writeAxoObject(catName, create_lpfsvf_tilde());
        writeAxoObject(catName, create_hpfsvf_tilde());
        writeAxoObject(catName, create_bpfsvf_tilde());
        writeAxoObject(catName, create_notchfsvf_tilde());
        writeAxoObject(catName, create_svf_multimode_tilde());

//UNRELEASED        WriteAxoObject(catName, create_lpfsvf_drive());
        writeAxoObject(catName, create_bp_svf_m());

//        WriteAxoObject(catName, create_lpfsvf2_tilde());
        catName = "kfilter";
        writeAxoObject(catName, create_K_lpfsvf());
        writeAxoObject(catName, create_K_hpfsvf());
        writeAxoObject(catName, create_K_bpfsvf());
        writeAxoObject(catName, create_k_bpfsvf_m());
        writeAxoObject(catName, create_K_lowpass());
    }

    static AxoObject createVCF() {
        AxoObject o = new AxoObject("vcf", "2-pole resonant low-pass filter (biquad), filter updated at k-rate");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("frequency", "cutoff frequency"));
        o.inlets.add(new InletFrac32("reso", "filter resonance"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));

        o.sLocalData = "data_filter_biquad_A fd;\n";
        o.sInitCode = "  init_filter_biquad_A(&fd);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(inlet_frequency,freq);\n"
                + " f_filter_biquad_A(&fd,inlet_in,outlet_out,freq,INT_MAX - (__USAT(inlet_reso,27)<<4));\n";
        return o;
    }

    static AxoObject createVCF2() {
        AxoObject o = new AxoObject("vcf2", "2-pole resonant low-pass filter (biquad), filter updated at k-rate");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "filter resonance"));
        o.params.add(new ParameterFrac32SMap("pitch"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "data_filter_biquad_A fd;\n";
        o.sInitCode = "  init_filter_biquad_A(&fd);\n";
        o.sKRateCode = "  {\n"
                + "      int32_t freq;\n"
                + "      MTOF(param_pitch + inlet_pitch,freq);\n"
                + "      f_filter_biquad_A(&fd,%in%,%out%,freq,INT_MAX - (__USAT(inlet_reso,27)<<4));\n"
                + "   }\n";
        return o;
    }

    static AxoObject createVCF3() {
        AxoObject o = new AxoObject("vcf3", "2-pole resonant low-pass filter (biquad), filter updated at k-rate");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "filter resonance"));
        o.params.add(new ParameterFrac32SMap("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "data_filter_biquad_A fd;\n";
        o.sInitCode = "  init_filter_biquad_A(&fd);\n";
        o.sKRateCode = "  {\n"
                + "      int32_t freq;\n"
                + "      MTOF(param_pitch + inlet_pitch,freq);\n"
                + "      f_filter_biquad_A(&fd,%in%,%out%,freq,INT_MAX - (__USAT(inlet_reso + param_reso,27)<<4));\n"
                + "   }\n";
        return o;
    }

    static AxoObject createLPF() {
        AxoObject o = new AxoObject("lp", "2-pole resonant low-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "biquad_state bs;"
                + "biquad_coefficients bc;\n";
        o.sInitCode = "biquad_clearstate(&bs);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(param_pitch,freq);\n"
                + "      biquad_lp_coefs(&bc,freq,INT_MAX - (__USAT(param_reso,27)<<4));\n"
                + "      biquad_dsp(&bs,&bc,%in%,%out%);\n";
        return o;
    }

    static AxoObject createLPFM() {
        AxoObject o = new AxoObject("lp m", "2-pole resonant low-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "filter resonance"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "biquad_state bs;"
                + "biquad_coefficients bc;\n";
        o.sInitCode = "biquad_clearstate(&bs);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(param_pitch + inlet_pitch,freq);\n"
                + "      biquad_lp_coefs(&bc,freq,INT_MAX - (__USAT(inlet_reso + param_reso,27)<<4));\n"
                + "      biquad_dsp(&bs,&bc,%in%,%out%);\n";
        return o;
    }

    static AxoObject createBPF() {
        AxoObject o = new AxoObject("bp", "2-pole resonant band-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "biquad_state bs;"
                + "biquad_coefficients bc;\n";
        o.sInitCode = "biquad_clearstate(&bs);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(param_pitch,freq);\n"
                + "      biquad_bp_coefs(&bc,freq,INT_MAX - (__USAT(param_reso,27)<<4));\n"
                + "      biquad_dsp(&bs,&bc,%in%,%out%);\n";
        return o;
    }

    static AxoObject createBPFM() {
        AxoObject o = new AxoObject("bp m", "2-pole resonant band-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "filter resonance"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "biquad_state bs;"
                + "biquad_coefficients bc;\n";
        o.sInitCode = "biquad_clearstate(&bs);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(param_pitch + inlet_pitch,freq);\n"
                + "      biquad_bp_coefs(&bc,freq,INT_MAX - (__USAT(inlet_reso + param_reso,27)<<4));\n"
                + "      biquad_dsp(&bs,&bc,%in%,%out%);\n";
        return o;
    }

    static AxoObject createHPF() {
        AxoObject o = new AxoObject("hp", "2-pole resonant band-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "biquad_state bs;"
                + "biquad_coefficients bc;\n";
        o.sInitCode = "biquad_clearstate(&bs);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(param_pitch,freq);\n"
                + "      biquad_hp_coefs(&bc,freq,INT_MAX - (__USAT(param_reso,27)<<4));\n"
                + "      biquad_dsp(&bs,&bc,%in%,%out%);\n";
        return o;
    }

    static AxoObject createHPFM() {
        AxoObject o = new AxoObject("hp m", "2-pole resonant high-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "filter resonance"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "biquad_state bs;"
                + "biquad_coefficients bc;\n";
        o.sInitCode = "biquad_clearstate(&bs);\n";
        o.sKRateCode = "      int32_t freq;\n"
                + "      MTOF(param_pitch + inlet_pitch,freq);\n"
                + "      biquad_hp_coefs(&bc,freq,INT_MAX - (__USAT(inlet_reso + param_reso,27)<<4));\n"
                + "      biquad_dsp(&bs,&bc,%in%,%out%);\n";
        return o;
    }

    static AxoObject createAPF() {
        AxoObject o = new AxoObject("ap", "2-pole resonant all-pass filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sKRateCode = "#error ALLPASS FILTER NOT IMPLEMENTED YET\n";
        return o;
    }

    static AxoObject createNF() {
        AxoObject o = new AxoObject("notch", "2-pole resonant notch filter (biquad)");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sKRateCode = "#error NOTCH FILTER NOT IMPLEMENTED YET\n";
        return o;
    }

    static AxoObject create_lowpassTilde() {
        AxoObject o = new AxoObject("lp1", "1st order lowpass filter");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32SMapPitch("freq"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "int32_t f;\n"
                + "   MTOF(param_freq,f);\n";
        o.sSRateCode = "   val = ___SMMLA((%in%-val)<<1,f,val);\n"
                + "   %out%= val;\n";
        return o;
    }

    static AxoObject create_K_lowpass() {
        AxoObject o = new AxoObject("lowpass", "1st order lowpass filter, control rate");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.params.add(new ParameterFrac32SMapLFOPitch("freq"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "int32_t f;\n"
                + "   MTOF(param_freq,f);\n"
                + "   val = ___SMMLA((%in%-val)<<1,f,val);\n"
                + "   %out%= val;\n";
        return o;
    }

    static AxoObject create_hipassTilde() {
        AxoObject o = new AxoObject("hp1", "1st order hipass filter");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32SMapPitch("freq"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "int32_t f;\n"
                + "   MTOF(param_freq,f);\n";
        o.sSRateCode = "   val = ___SMMLA((%in%-val)<<1,f,val);\n"
                + "   %out%= %in%-val;\n";
        return o;
    }

    static AxoObject create_lowpassMTilde() {
        AxoObject o = new AxoObject("lp1 m", "1st order lowpass filter, modulation input");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletFrac32("freq", "cutoff frequency"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32UMap("freq"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "int32_t f;\n"
                + "   MTOF(param_freq+inlet_freq,f);\n";
        o.sSRateCode = "   val = ___SMMLA((%in%-val)<<1,f,val);\n"
                + "   %out%= val;\n";
        return o;
    }

    static AxoObject create_hipassMTilde() {
        AxoObject o = new AxoObject("hp1 m", "1st order hipass filter, modulation input");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletFrac32("freq", "cutoff frequency"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.params.add(new ParameterFrac32UMap("freq"));
        o.sLocalData = "int32_t val;\n";
        o.sInitCode = "val = 0;\n";
        o.sKRateCode = "int32_t f;\n"
                + "   MTOF(param_freq+inlet_freq,f);\n";
        o.sSRateCode = "   val = ___SMMLA((%in%-val)<<1,f,val);\n"
                + "   %out%= %in%-val;\n";
        return o;
    }

    static AxoObject createDelta() {
        AxoObject o = new AxoObject("delta", "pseudo derivative, difference between previous and current value");
        o.outlets.add(new OutletFrac32("d", "a(t) - a(t-1)"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.sLocalData = "int32_t _ap;\n";
        o.sInitCode = "   _ap = 0;\n";
        o.sKRateCode = "   %d%= %a% - _ap;\n"
                + "   _ap = %a%;\n";
        return o;
    }

    static AxoObject createDeltaTilde() {
        AxoObject o = new AxoObject("delta", "pseudo derivative, difference between previous and current value");
        o.outlets.add(new OutletFrac32Buffer("d", "a(t) - a(t-1)"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.sLocalData = "int32_t _ap;\n";
        o.sInitCode = "   _ap = 0;\n";
        o.sSRateCode = "   %d%= %a% - _ap;\n"
                + "   _ap = %a%;\n";
        return o;
    }

    static AxoObject createDeltaI() {
        AxoObject o = new AxoObject("delta", "pseudo derivative, difference between previous and current value");
        o.outlets.add(new OutletInt32("d", "a(t) - a(t-1)"));
        o.inlets.add(new InletInt32("a", "a"));
        o.sLocalData = "int32_t _ap;\n";
        o.sInitCode = "   _ap = 0;\n";
        o.sKRateCode = "   %d%= %a% - _ap;\n"
                + "   _ap = %a%;\n";
        return o;
    }

    static AxoObject createIntegrator() {
        AxoObject o = new AxoObject("integrator", "cumulative sum, saturating to +-64 units");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sKRateCode = "acc += %in%;\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject createIntegratorTilde() {
        AxoObject o = new AxoObject("integrator", "cumulative sum, saturating to +-64 units");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sSRateCode = "acc += %in%;\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject createIntegratorLeaky() {
        AxoObject o = new AxoObject("integrator leaky", "cumulative sum, saturating to +-64 units");
        o.inlets.add(new InletFrac32("in", "input"));
        o.outlets.add(new OutletFrac32("out", "output"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sKRateCode = "acc += %in%;\n"
                + "acc -= acc>>14;\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject createIntegratorLeakyTilde() {
        AxoObject o = new AxoObject("integrator leaky", "cumulative sum, saturating to +-64 units");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.sLocalData = "int32_t acc;\n";
        o.sInitCode = "acc = 0;\n";
        o.sSRateCode = "acc += %in%;\n"
                + "acc -= acc>>14;\n"
                + "acc = __SSAT(acc,28);\n"
                + "%out% = acc;\n";
        return o;
    }

    static AxoObject create_Fir16() {
        AxoObject o = new AxoObject("fir16", "finite impulse response filter, with 16 coefficients");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.inlets.add(new InletFrac32Buffer("coefs", "input"));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.sLocalData = "arm_fir_instance_q31 f;\n"
                + "q31_t state[BUFSIZE + BUFSIZE -1];\n";
        o.sInitCode = "arm_fir_init_q31(&f,\n"
                + "	BUFSIZE,\n"
                + "	0,\n"
                + "	&state[0],\n"
                + "	BUFSIZE);\n";
        o.sKRateCode = "f.pCoeffs = (q31_t*)%coefs%;\n"
                + "\n"
                + "arm_fir_fast_q31(\n"
                + "  &f,\n"
                + "  (q31_t*)%in%,\n"
                + "  %out%,\n"
                + "  BUFSIZE);\n";
        return o;
    }

    static AxoObject create_EQ5hq() {
        AxoObject o = new AxoObject("eq5hq", "Five-band equalizer. The transition frequencies are 100, 500, 2000, and 6000 Hz. High quality version. Unstable behavior when changing the low gain live.");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterInt32Box("low", -9, 9));
        o.params.add(new ParameterInt32Box("lowmid", -9, 9));
        o.params.add(new ParameterInt32Box("mid", -9, 9));
        o.params.add(new ParameterInt32Box("highmid", -9, 9));
        o.params.add(new ParameterInt32Box("high", -9, 9));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.includes = new LinkedList<>();
        o.includes.add("./eq5coefs.h");
        o.sLocalData = "q63_t biquadStateBand1Q31[4 * 2];\n"
                + "q63_t biquadStateBand2Q31[4 * 2];\n"
                + "q31_t biquadStateBand3Q31[4 * 2];\n"
                + "q31_t biquadStateBand4Q31[4 * 2];\n"
                + "q31_t biquadStateBand5Q31[4 * 2];\n"
                + "  arm_biquad_cas_df1_32x64_ins_q31 S1;\n"
                + "  arm_biquad_cas_df1_32x64_ins_q31 S2;\n"
                + "  arm_biquad_casd_df1_inst_q31 S3;\n"
                + "  arm_biquad_casd_df1_inst_q31 S4;\n"
                + "  arm_biquad_casd_df1_inst_q31 S5;\n"
                + "static const int NUMSTAGES=2;\n";
        o.sInitCode = "  arm_biquad_cas_df1_32x64_init_q31(&S1, NUMSTAGES,\n"
                + "            (q31_t *) &eq5coefs[190*0 + 10*9],\n"
                + "            &biquadStateBand1Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cas_df1_32x64_init_q31(&S2, NUMSTAGES,\n"
                + "            (q31_t *) &eq5coefs[190*1 + 10*9],\n"
                + "            &biquadStateBand2Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cascade_df1_init_q31(&S3, NUMSTAGES,\n"
                + "          (q31_t *) &eq5coefs[190*2 + 10*9],\n"
                + "          &biquadStateBand3Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cascade_df1_init_q31(&S4, NUMSTAGES,\n"
                + "          (q31_t *) &eq5coefs[190*3 + 10*9],\n"
                + "          &biquadStateBand4Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cascade_df1_init_q31(&S5, NUMSTAGES,\n"
                + "          (q31_t *) &eq5coefs[190*4 + 10*9],\n"
                + "          &biquadStateBand5Q31[0], 2);\n";
        o.sKRateCode = "S1.pCoeffs = (q31_t*)&eq5coefs[190*0 + 10*(%low% + 9)];\n"
                + "S2.pCoeffs = (q31_t*)&eq5coefs[190*1 + 10*(%lowmid% + 9)];\n"
                + "S3.pCoeffs = (q31_t*)&eq5coefs[190*2 + 10*(%mid% + 9)];\n"
                + "S4.pCoeffs = (q31_t*)&eq5coefs[190*3 + 10*(%highmid% + 9)];\n"
                + "S5.pCoeffs = (q31_t*)&eq5coefs[190*4 + 10*(%high% + 9)];\n"
                + "\n"
                + "arm_biquad_cas_df1_32x64_q31(&S1, (q31_t *)%in%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cas_df1_32x64_q31(&S2, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cascade_df1_q31(&S3, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cascade_df1_q31(&S4, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cascade_df1_q31(&S5, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n";
        return o;
    }

    static AxoObject create_EQ4() {
        AxoObject o = new AxoObject("eq4", "Four-band equalizer. The transition frequencies are 100, 500, 2000, and 6000 Hz. Lower quality version. The low band is ommitted.");
        o.inlets.add(new InletFrac32Buffer("in", "input"));
        o.params.add(new ParameterInt32Box("lowmid", -9, 9));
        o.params.add(new ParameterInt32Box("mid", -9, 9));
        o.params.add(new ParameterInt32Box("highmid", -9, 9));
        o.params.add(new ParameterInt32Box("high", -9, 9));
        o.outlets.add(new OutletFrac32Buffer("out", "output"));
        o.includes = new LinkedList<>();
        o.includes.add("./eq5coefs.h");
        o.sLocalData = "q31_t biquadStateBand2Q31[4 * 2];\n"
                + "q31_t biquadStateBand3Q31[4 * 2];\n"
                + "q31_t biquadStateBand4Q31[4 * 2];\n"
                + "q31_t biquadStateBand5Q31[4 * 2];\n"
                + "  arm_biquad_casd_df1_inst_q31 S2;\n"
                + "  arm_biquad_casd_df1_inst_q31 S3;\n"
                + "  arm_biquad_casd_df1_inst_q31 S4;\n"
                + "  arm_biquad_casd_df1_inst_q31 S5;\n"
                + "static const int NUMSTAGES=2;\n";
        o.sInitCode = "  arm_biquad_cascade_df1_init_q31(&S2, NUMSTAGES,\n"
                + "            (q31_t *) &eq5coefs[190*1 + 10*9],\n"
                + "            &biquadStateBand2Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cascade_df1_init_q31(&S3, NUMSTAGES,\n"
                + "          (q31_t *) &eq5coefs[190*2 + 10*9],\n"
                + "          &biquadStateBand3Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cascade_df1_init_q31(&S4, NUMSTAGES,\n"
                + "          (q31_t *) &eq5coefs[190*3 + 10*9],\n"
                + "          &biquadStateBand4Q31[0], 2);\n"
                + "\n"
                + "  arm_biquad_cascade_df1_init_q31(&S5, NUMSTAGES,\n"
                + "          (q31_t *) &eq5coefs[190*4 + 10*9],\n"
                + "          &biquadStateBand5Q31[0], 2);\n";
        o.sKRateCode = "S2.pCoeffs = (q31_t*)&eq5coefs[190*1 + 10*(%lowmid% + 9)];\n"
                + "S3.pCoeffs = (q31_t*)&eq5coefs[190*2 + 10*(%mid% + 9)];\n"
                + "S4.pCoeffs = (q31_t*)&eq5coefs[190*3 + 10*(%highmid% + 9)];\n"
                + "S5.pCoeffs = (q31_t*)&eq5coefs[190*4 + 10*(%high% + 9)];\n"
                + "\n"
                + "arm_biquad_cascade_df1_q31(&S2, (q31_t *)%in%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cascade_df1_q31(&S3, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cascade_df1_q31(&S4, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n"
                + "arm_biquad_cascade_df1_q31(&S5, (q31_t *)%out%, (q31_t *)%out%, BUFSIZE);\n";
        return o;
    }

    static AxoObject create_lpfsvf_tilde() {
        AxoObject o = new AxoObject("lp svf", "Low pass filter, state-variable type");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = low;\n"
                + "%out% = out1;\n";
        /*
         o.sSRateCode = "int32_t in1 = %in%;\n"
         + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
         + "low = low + (___SMMUL(freq,band)<<1);\n"
         + "int32_t high  = notch - low;\n"
         + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
         + "int32_t out1 = low;\n"
         + "notch = notch = %in% - (___SMMUL(damp,band)<<1);\n"
         + "low = low + (___SMMUL(freq,band)<<1);\n"
         + "high = notch - low;\n"
         + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
         + "out1 += low;\n"
         + "%out% = out1;\n";
         */
        return o;
    }

    static AxoObject create_hpfsvf_tilde() {
        AxoObject o = new AxoObject("hp svf", "Highpass filter, state-variable type");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = high;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_bpfsvf_tilde() {
        AxoObject o = new AxoObject("bp svf", "Bandpass filter, state-variable type");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = band;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_notchfsvf_tilde() {
        AxoObject o = new AxoObject("notch svf", "Notch (band reject) filter, state-variable type");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "outlet_out = notch;\n";
        return o;
    }

    static AxoObject create_svf_multimode_tilde() {
        AxoObject o = new AxoObject("multimode svf m", "multimode filter, state-variable type, modulation inputs");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "resonance"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("hp", "highpass filter output"));
        o.outlets.add(new OutletFrac32Buffer("bp", "bandpass filter output"));
        o.outlets.add(new OutletFrac32Buffer("lp", "lowpass filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (__USAT(inlet_reso + param_reso,27)<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "int32_t pitch = __SSAT(param_pitch+inlet_pitch,28);\n"
                + "MTOFEXTENDED(pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "%lp% = low;\n"
                + "%hp% = high;\n"
                + "%bp% = band;\n";
        return o;
    }

    static AxoObject create_bp_svf_m() {
        AxoObject o = new AxoObject("bp svf m", "Bandpass filter, state-variable type, modulation inputs");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch"));
        o.inlets.add(new InletFrac32("reso", "resonance"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (__USAT(inlet_reso + param_reso,27)<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch + inlet_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = band;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_lpfsvf2_tilde() {
        AxoObject o = new AxoObject("lp svf2", "Low pass filter, state-variable type, double pumped");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = low;\n"
                + "%out% = out1;\n";
        /*
         o.sSRateCode = "int32_t in1 = %in%;\n"
         + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
         + "low = low + (___SMMUL(freq,band)<<1);\n"
         + "int32_t high  = notch - low;\n"
         + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
         + "int32_t out1 = low;\n"
         + "notch = notch = %in% - (___SMMUL(damp,band)<<1);\n"
         + "low = low + (___SMMUL(freq,band)<<1);\n"
         + "high = notch - low;\n"
         + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
         + "out1 += low;\n"
         + "%out% = out1;\n";
         */
        return o;
    }

    static AxoObject create_bpfsvf2_tilde() {
        AxoObject o = new AxoObject("bp svf2", "Band pass filter, state-variable type, double pumped");
        return o;
    }

    static AxoObject create_hpfsvf2_tilde() {
        AxoObject o = new AxoObject("hp svf2", "High pass filter, state-variable type, double pumped");
        return o;
    }

    static AxoObject create_K_lpfsvf() {
        AxoObject o = new AxoObject("lp svf", "Low pass filter, state-variable type, control rate");
        o.inlets.add(new InletFrac32("in", "filter input"));
        o.params.add(new ParameterFrac32SMapKPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n"
                + "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = low;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_lpfsvf_drive() {
        AxoObject o = new AxoObject("lp svf drive", "Low pass filter, state-variable type");
        o.inlets.add(new InletFrac32Buffer("in", "filter input"));
        o.params.add(new ParameterFrac32SMapPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.params.add(new ParameterFrac32UMap("drive"));
        o.outlets.add(new OutletFrac32Buffer("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n";
        o.sSRateCode = "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band - (___SMMUL(___SMMUL(___SMMUL(band,band)<<1,band)<<2,%drive%<<4)<<8);\n"
                + "int32_t out1 = low;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_K_hpfsvf() {
        AxoObject o = new AxoObject("hp svf", "Highpass filter, state-variable type, control rate");
        o.inlets.add(new InletFrac32("in", "filter input"));
        o.params.add(new ParameterFrac32SMapKPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n"
                + "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = high;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_K_bpfsvf() {
        AxoObject o = new AxoObject("bp svf", "Bandpass filter, state-variable type, control rate");
        o.inlets.add(new InletFrac32("in", "filter input"));
        o.params.add(new ParameterFrac32SMapKPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (param_reso<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n"
                + "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = band;\n"
                + "%out% = out1;\n";
        return o;
    }

    static AxoObject create_k_bpfsvf_m() {
        AxoObject o = new AxoObject("bp svf m", "Bandpass filter, state-variable type, control rate");
        o.inlets.add(new InletFrac32("in", "filter input"));
        o.inlets.add(new InletFrac32("pitch", "pitch input"));
        o.inlets.add(new InletFrac32("reso", "resonance input"));
        o.params.add(new ParameterFrac32SMapKPitch("pitch"));
        o.params.add(new ParameterFrac32UMapFilterQ("reso"));
        o.outlets.add(new OutletFrac32("out", "filter output"));
        o.sLocalData = "int32_t low;\n"
                + "int32_t band;\n";
        o.sInitCode = "low = 0;\n"
                + "band = 0;\n";
        o.sKRateCode = "int32_t damp = (0x80<<24) - (__USAT(inlet_reso + param_reso,27)<<4);\n"
                + "damp = ___SMMUL(damp,damp);\n"
                + "int32_t alpha;\n"
                + "int32_t freq;\n"
                + "MTOFEXTENDED(param_pitch + inlet_pitch,alpha);\n"
                + "SINE2TINTERP(alpha,freq);\n"
                + "int32_t in1 = %in%;\n"
                + "int32_t notch = %in% - (___SMMUL(damp,band)<<1);\n"
                + "low = low + (___SMMUL(freq,band)<<1);\n"
                + "int32_t high  = notch - low;\n"
                + "band = (___SMMUL(freq,high)<<1) + band;// - drive*band*band*band;\n"
                + "int32_t out1 = band;\n"
                + "%out% = out1;\n";
        return o;
    }

}
