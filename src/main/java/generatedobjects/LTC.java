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
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInt32;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class LTC extends gentools {

    static void GenerateAll() {
        String catName = "ltc";
        WriteAxoObject(catName, Create_Generator());
        WriteAxoObject(catName, Create_Decoder());
//UNRELEASED        WriteAxoObject(catName, Create_FSync());        
//UNRELEASED        WriteAxoObject(catName, Create_FSyncCoded());
    }

    static AxoObject Create_Generator() {
        AxoObject o = new AxoObject("gen", "LTC (linear timecode) generator, in sync with the audio sample clock. Does not support drop-frame format. This implementation only works when the system clock is 48kHz or 96kHz, not at 44.1kHz!");
        String FpsM[] = {
            "25",
            "30"
        };
        String FpsC[] = {
            "25",
            "30"
        };
        o.attributes.add(new AxoAttributeComboBox("fps", FpsM, FpsC));
        o.outlets.add(new OutletInt32("h", "hours"));
        o.outlets.add(new OutletInt32("m", "minutes"));
        o.outlets.add(new OutletInt32("s", "seconds"));
        o.outlets.add(new OutletInt32("f", "frames"));
        o.outlets.add(new OutletFrac32Buffer("out", "ltc output"));

        o.sLocalData = "uint8_t h; // hour counter\n"
                + "uint8_t m; // minute counter\n"
                + "uint8_t s; // seconds counter\n"
                + "uint8_t f; // frame counter\n"
                + "uint8_t b; // bit counter\n"
                + "uint8_t ss; // sample counter\n"
                + "int32_t bo; // bit output\n"
                + "\n"
                + "void inc(){\n"
                + "   f++;\n"
                + "   if (f == %fps%) {\n"
                + "      f = 0;\n"
                + "      s++;\n"
                + "      if (s == 60){\n"
                + "         s = 0;\n"
                + "         m++;\n"
                + "         if (m == 60){\n"
                + "            m=0;\n"
                + "            h++;\n"
                + "         }\n"
                + "      }\n"
                + "   }\n"
                + "}\n"
                + "void getbit(){\n"
                + "   b++;\n"
                + "   if (b==160) {\n"
                + "      b = 0;\n"
                + "      inc();\n"
                + "   }\n"
                + "   if ((b&1) == 0){\n"
                + "      bo = -bo;\n"
                + "   } else {\n"
                + "      switch(b>>1){\n"
                + "		case 0:\n"
                + "			bo = ((f%10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 1:\n"
                + "			bo = ((f%10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 2:\n"
                + "			bo = ((f%10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 3:\n"
                + "			bo = ((f%10)&0x8)?-bo:bo;\n"
                + "			break;\n"
                + "		case 4: // user bits field 1\n"
                + "		case 5:\n"
                + "		case 6:\n"
                + "		case 7:\n"
                + "			break;\n"
                + "		case 8:\n"
                + "			bo = ((f/10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 9:\n"
                + "			bo = ((f/10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 10: // drop frame\n"
                + "		case 11: // color frame\n"
                + "		case 12: // user bits field 2\n"
                + "		case 13: \n"
                + "		case 14: \n"
                + "		case 15: \n"
                + "			break;\n"
                + "		case 16:\n"
                + "			bo = ((s%10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 17:\n"
                + "			bo = ((s%10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 18:\n"
                + "			bo = ((s%10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 19:\n"
                + "			bo = ((s%10)&0x8)?-bo:bo;\n"
                + "			break;\n"
                + "		case 20: // user bits field 3\n"
                + "		case 21:\n"
                + "		case 22:\n"
                + "		case 23:\n"
                + "			break;\n"
                + "		case 24:\n"
                + "			bo = ((s/10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 25:\n"
                + "			bo = ((s/10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 26: \n"
                + "			bo = ((s/10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 27: // even parity (unimplemented)\n"
                + "		case 28: // user bits field 4\n"
                + "		case 29: \n"
                + "		case 30: \n"
                + "		case 31: \n"
                + "			break;\n"
                + "		case 32:\n"
                + "			bo = ((m%10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 33:\n"
                + "			bo = ((m%10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 34:\n"
                + "			bo = ((m%10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 35:\n"
                + "			bo = ((m%10)&0x8)?-bo:bo;\n"
                + "			break;\n"
                + "		case 36: // user bits field 5\n"
                + "		case 37:\n"
                + "		case 38:\n"
                + "		case 39:\n"
                + "			break;\n"
                + "		case 40:\n"
                + "			bo = ((m/10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 41:\n"
                + "			bo = ((m/10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 42: \n"
                + "			bo = ((m/10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 43: // binary group flag\n"
                + "		case 44: // user bits field 6\n"
                + "		case 45: \n"
                + "		case 46: \n"
                + "		case 47: \n"
                + "			break;\n"
                + "		case 48:\n"
                + "			bo = ((h%10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 49:\n"
                + "			bo = ((h%10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 50:\n"
                + "			bo = ((h%10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 51:\n"
                + "			bo = ((h%10)&0x8)?-bo:bo;\n"
                + "			break;\n"
                + "		case 52: // user bits field 7\n"
                + "		case 53:\n"
                + "		case 54:\n"
                + "		case 55:\n"
                + "			break;\n"
                + "		case 56:\n"
                + "			bo = ((h/10)&0x1)?-bo:bo;\n"
                + "			break;\n"
                + "		case 57:\n"
                + "			bo = ((h/10)&0x2)?-bo:bo;\n"
                + "			break;\n"
                + "		case 58: \n"
                + "			bo = ((h/10)&0x4)?-bo:bo;\n"
                + "			break;\n"
                + "		case 59: // binary group flag\n"
                + "		case 60: // user bits field 8\n"
                + "		case 61: \n"
                + "		case 62: \n"
                + "		case 63: \n"
                + "			break;\n"
                + "\n"
                + "		case 64:\n"
                + "		case 65:\n"
                + "			break;\n"
                + "		case 66:\n"
                + "		case 67:\n"
                + "		case 68:\n"
                + "		case 69:\n"
                + "		case 70:\n"
                + "		case 71:\n"
                + "		case 72:\n"
                + "		case 73:\n"
                + "		case 74:\n"
                + "		case 75:\n"
                + "		case 76:\n"
                + "		case 77:\n"
                + "			bo = -bo;\n"
                + "			break;\n"
                + "		case 78:\n"
                + "			break;\n"
                + "		case 79:\n"
                + "			bo = -bo;\n"
                + "			break;\n"
                + "		default: break;\n"
                + "	  }\n"
                + "   }\n"
                + "}\n";
        o.sInitCode = "h = 0;\n"
                + "m = 0;\n"
                + "s = 0;\n"
                + "f = 0;\n"
                + "b = 0;\n"
                + "ss = 0;\n"
                + "bo = 1<<26;\n";
        // 160 half-bits per smpte frame
        // 48kHz 24fps -> 12.5 samples/halfbit - no go!
        // 48kHz 25fps -> 12 samples/halfbit
        // 48kHz 30fps -> 10 samples/halfbit

        o.sKRateCode = "%h% = h;\n"
                + "%m% = m;\n"
                + "%s% = s;\n"
                + "%f% = f;\n";
        o.sSRateCode = "%out% = bo;\n"
                + "ss++;\n"
                + "if (ss==((SAMPLERATE/160)/%fps%)) {\n"
                + "   ss=0;\n"
                + "   getbit();\n"
                + "}\n";
        return o;
    }

    static AxoObject Create_Decoder() {
        AxoObject o = new AxoObject("decode", "LTC (linear timecode) decoder.  Does not support drop-frame format. This implementation only works when the system clock is 48kHz or 96kHz, not at 44.1kHz!");
        String FpsM[] = {
            "25",
            "30"
        };
        String FpsC[] = {
            "25",
            "30"
        };
        o.attributes.add(new AxoAttributeComboBox("fps", FpsM, FpsC));
        o.inlets.add(new InletFrac32Buffer("in", "ltc input"));
        o.outlets.add(new OutletInt32("h", "hours"));
        o.outlets.add(new OutletInt32("m", "minutes"));
        o.outlets.add(new OutletInt32("s", "seconds"));
        o.outlets.add(new OutletInt32("f", "frames"));
        o.outlets.add(new OutletInt32("smp", "sample offset to frame"));
        o.outlets.add(new OutletBool32("sync", "sync detect"));
        o.sLocalData = "uint8_t h; // hour counter\n"
                + "uint8_t m; // minute counter\n"
                + "uint8_t s; // seconds counter\n"
                + "uint8_t f; // frame counter\n"
                + "int8_t b; // bit counter\n"
                + "// <0 : syncing\n"
                + "// >=0 : reading\n"
                + "int32_t subsample;\n"
                + "uint8_t ss; // sample counter\n"
                + "int32_t bo; // bit output\n"
                + "int32_t psample; // previous sample\n"
                + "int32_t sexp; // expected sample period in audio samples\n"
                + "uint32_t data[2];\n"
                + "uint32_t data2[2];\n";
        o.sInitCode = "h=0;\n"
                + "m=0;\n"
                + "s=0;\n"
                + "f=0;\n"
                + "b=-1;\n"
                + "ss=0;\n"
                + "bo=0;\n"
                + "sexp =(SAMPLERATE/160)/%fps%;\n"
                + "subsample = 0;\n";
        o.sKRateCode = "%h% = h;\n"
                + "%m% = m;\n"
                + "%s% = s;\n"
                + "%f% = f;\n"
                + "%smp% = subsample;\n"
                + "%sync%=0;\n";
        o.sSRateCode = "int32_t i1 = %in%>>2;\n"
                + "int transition = ((psample>0)!=(i1>0));\n"
                + "\n"
                + "if (b == -1) {\n"
                + "	if (transition){\n"
                + "		b = -2;\n"
                + "		ss = 0; // beginning of sync word\n"
                + "	}\n"
                + "} else if (((b < -1)&&(b > -26))||(b == -27)||(b==-28)) {\n"
                + "	if ((ss > (sexp-2))&&(ss <= (sexp+2))) {\n"
                + "		if (transition) {\n"
                + "			b--;\n"
                + "			ss = 0;\n"
                + "			if (b == -29) {\n"
                + "				//f++;\n"
                + "				b = 0;\n"
                + "				data[0] = 0;\n"
                + "				data[1] = 0;\n"
                + "			} else if (b == -10) {\n"
                + "				// use this edge for subsample sync\n"
                + "				int32_t _i1 = i1;\n"
                + "				int32_t _i0 = psample;\n"
                + "				if (i1<0) {\n"
                + "					_i1 = -i1;\n"
                + "					_i0 = -psample;\n"
                + "				}\n"
                + "				subsample = ((_i0<<6)/(_i0-_i1)) + 64*buffer_index;\n"
                + "                             if (f == 0) {\n"
                + "                                 %sync% = 1;\n"
                + "                             }\n"
                + "			}\n"
                + "		}\n"
                + "	} else if (ss > (sexp+2)) {\n"
                + "	    b = -1; // start searching again...\n"
                + "	}\n"
                + "} else if (b == -26){\n"
                + "	if ((ss > (2*sexp-2))&&(ss <= (2*sexp+2))) {\n"
                + "		ss = 0;\n"
                + "		b--;\n"
                + "	} else if (ss > (2*sexp+2)) {\n"
                + "		// busted\n"
                + "		b = -1; // start searching again...\n"
                + "	}\n"
                + "} else if (b >= 0) {\n"
                + "	if ((ss > (sexp-2))&&(ss <= (sexp+2))) {\n"
                + "		if (transition){\n"
                + "			if (b<32) {\n"
                + "				data[0] |= 1<<b;\n"
                + "			} else {\n"
                + "				data[1] |= 1<<(b-32);\n"
                + "			}\n"
                + "		}\n"
                + "	}\n"
                + "	if ((ss > (2*sexp-2))&&(ss <= (2*sexp+2))) {\n"
                + "		if (transition){\n"
                + "			ss = 0;\n"
                + "			b++;\n"
                + "			if (b == 64) {\n"
                + "				f = (data[0]&0x0F) + 10*((data[0]>>8)&0x03);\n"
                + "				s = ((data[0]>>16)&0x0F) + 10*((data[0]>>24)&0x07);\n"
                + "				m = (data[1]&0x0F) + 10*((data[1]>>8)&0x07);\n"
                + "				h = ((data[1]>>16)&0x0F) + 10*((data[1]>>24)&0x03);\n"
                + "				data2[0] = data[0];\n"
                + "				data2[1] = data[1];\n"
                + "	//			f++;\n"
                + "				b = -1;\n"
                + "			}\n"
                + "		}\n"
                + "	} else if (ss > (2*sexp+2)) {\n"
                + "		// busted\n"
                + "		b = -1; // start searching again...\n"
                + "	}	\n"
                + "}\n"
                + "\n"
                + "psample = i1;\n"
                + "ss++;\n";
        return o;
    }

    static AxoObject Create_FSync() {
        AxoObject o = new AxoObject("fsync", "delivers frame sync pulse on the I2S data output (needs patch wire). Conflicts with normal audio output! Audio saturation in patch settings MUST BE OFF!");
        String FpsM[] = {
            "25",
            "30",
            "50",
            "60",
            "100",
            "120",
            "1200",
            "8000"};
        o.attributes.add(new AxoAttributeComboBox("fps", FpsM, FpsM));
        o.inlets.add(new InletBool32Rising("trig", "frame offset trigger"));
        o.inlets.add(new InletInt32("offset", "frame offset (64 units/sample)"));
        o.sLocalData = "int32_t sexp; // frame delay in 64*samples\n"
                + "uint32_t scount;\n"
                + "int32_t ptrig;\n";
        o.sInitCode = "sexp =(64*SAMPLERATE)/%fps%;\n"
                + "scount = 0;\n";
        o.sKRateCode = "int smp;\n"
                + "if (%trig% && !ptrig) {\n"
                + "	scount= 100 + %offset%;\n"
                + "}\n"
                + "ptrig = %trig%;\n"
                + "for(smp=0;smp<BUFSIZE;smp++){\n"
                + "  AudioOutputLeft[smp] = 0;\n"
                + "  AudioOutputRight[smp] = 0;\n"
                + "}\n"
                + "int subsmp;\n"
                + "for(smp=0;smp<BUFSIZE;smp++){\n"
                + "	for (subsmp = 0;subsmp<64;subsmp++){\n"
                + "		scount+=1;\n"
                + "		if (scount >= sexp){\n"
                + "			scount = 0;\n"
                + "		}\n"
                + "		if (scount < 19) {\n"
                + "			if (subsmp<32){\n"
                + "				AudioOutputLeft[smp] |= 1<<(31-subsmp);\n"
                + "			} else {\n"
                + "				AudioOutputRight[smp] |= 1<<(63-subsmp);\n"
                + "			}\n"
                + "		}\n"
                + "	}\n"
                + "}";
        return o;
    }

    static AxoObject Create_FSyncCoded() {
        AxoObject o = new AxoObject("fsync coded", "delivers frame sync pulse on the I2S data output (needs patch wire). Conflicts with normal audio output! Audio saturation in patch settings MUST BE OFF! Modulates timecode into sync jitter...");
        String FpsM[] = {
            "25",
            "30",
            "50",
            "60",
            "100",
            "120",
            "1200",
            "8000"};
        o.attributes.add(new AxoAttributeComboBox("fps", FpsM, FpsM));
        o.inlets.add(new InletInt32("h", "hours"));
        o.inlets.add(new InletInt32("m", "minutes"));
        o.inlets.add(new InletInt32("s", "seconds"));
        o.inlets.add(new InletBool32Rising("trig", "frame offset trigger"));
        o.inlets.add(new InletInt32("offset", "frame offset (64 units/sample)"));
        o.sLocalData = "int32_t sexp; // frame delay in 64*samples\n"
                + "uint32_t scount;\n"
                + "int32_t ptrig;\n"
                + "\n"
                + "uint8_t time_hours;\n"
                + "uint8_t time_minutes;\n"
                + "uint8_t time_seconds;\n"
                + "uint32_t time_frames;\n"
                + "uint8_t bitindex;\n"
                + "bool evenodd;\n"
                + "bool bitbuf;\n"
                + "\n"
                + "bool Get1BitTC2 (void) {\n"
                + "	if ((time_frames == 0)&&(bitindex==29)){\n"
                + "		bitindex = 1;\n"
                + "	} else if (bitindex < 29) {\n"
                + "		bitindex++;\n"
                + "	} else bitindex = 29;\n"
                + "	switch (bitindex) {\n"
                + "		case 0 : return 0;\n"
                + "		case 1 : return 1;\n"
                + "		case 2 : if (time_seconds&0x01) return 1; else return 0;\n"
                + "		case 3 : if (time_seconds&0x02) return 1; else return 0;\n"
                + "		case 4 : if (time_seconds&0x04) return 1; else return 0;\n"
                + "		case 5 : if (time_seconds&0x08) return 1; else return 0;\n"
                + "		case 6 : if (time_seconds&0x10) return 1; else return 0;\n"
                + "		case 7 : if (time_seconds&0x20) return 1; else return 0;\n"
                + "		case 8 : return 1;\n"
                + "		case 9 : if (time_minutes&0x01) return 1; else return 0;\n"
                + "		case 10 : if (time_minutes&0x02) return 1; else return 0;\n"
                + "		case 11 : if (time_minutes&0x04) return 1; else return 0;\n"
                + "		case 12 : if (time_minutes&0x08) return 1; else return 0;\n"
                + "		case 13 : if (time_minutes&0x10) return 1; else return 0;\n"
                + "		case 14 : if (time_minutes&0x20) return 1; else return 0;\n"
                + "		case 15 : return 1;\n"
                + "		case 16 : if (time_hours&0x01) return 1; else return 0;\n"
                + "		case 17 : if (time_hours&0x02) return 1; else return 0;\n"
                + "		case 18 : if (time_hours&0x04) return 1; else return 0;\n"
                + "		case 19 : if (time_hours&0x08) return 1; else return 0;\n"
                + "		case 20 : if (time_hours&0x10) return 1; else return 0;\n"
                + "		case 21 : if (time_hours&0x20) return 1; else return 0;\n"
                + "		case 22 : return 1;\n"
                + "		default: return 0;\n"
                + "	}\n"
                + "}\n";
        o.sInitCode = "sexp =(64*SAMPLERATE)/%fps%;\n"
                + "scount = 0;\n"
                + "bitindex = 0;\n";
        o.sKRateCode = "int smp;\n"
                + "if (%trig% && !ptrig) {\n"
                + "	if (bitindex >= 29)\n"
                + "		scount= %offset%; // 64*16 max\n"
                + "\n"
                + "	time_hours = %h%;\n"
                + "	time_minutes = %m%;\n"
                + "	time_seconds = %s%;\n"
                + "	time_frames = 0;\n"
                + "//	if ((bitindex == 29)||(bitindex == 0)) {\n"
                + "//		bitindex = 1;\n"
                + "//		evenodd = 1;\n"
                + "//	}\n"
                + "}\n"
                + "\n"
                + "ptrig = %trig%;\n"
                + "for(smp=0;smp<BUFSIZE;smp++){\n"
                + "  AudioOutputLeft[smp] = 0;\n"
                + "  AudioOutputRight[smp] = 0;\n"
                + "}\n"
                + "\n"
                + "int subsmp;\n"
                + "\n"
                + "for(smp=0;smp<BUFSIZE;smp++){\n"
                + "	for (subsmp = 0;subsmp<64;subsmp++){\n"
                + "		scount++;\n"
                + "		if (scount >= sexp){\n"
                + "			scount = 0;\n"
                + "			if (!evenodd) {\n"
                + "				bitbuf = Get1BitTC2();\n"
                + "//				bitbuf = 1;\n"
                + "				evenodd = 1;\n"
                + "			} else {\n"
                + "				evenodd = 0;\n"
                + "			}\n"
                + "			time_frames++;\n"
                + "		}\n"
                + "		int lo = 10000;\n"
                + "		int hi = 20000;\n"
                + "		int df = 1000; // 1000 -> 500us jitter\n"
                + "		if (evenodd) {\n"
                + "			if (bitbuf) {\n"
                + "				lo += df;\n"
                + "				hi += df;\n"
                + "			} else {\n"
                + "				lo -= df;\n"
                + "				hi -= df;\n"
                + "			}\n"
                + "		}\n"
                + "		if ((scount > lo) &&(scount < hi)) {\n"
                + "			if (subsmp<32){\n"
                + "				AudioOutputLeft[smp] |= 1<<(31-subsmp);\n"
                + "			} else {\n"
                + "				AudioOutputRight[smp] |= 1<<(63-subsmp);\n"
                + "			}\n"
                + "		}\n"
                + "	}\n"
                + "}\n";
        return o;
    }

}
