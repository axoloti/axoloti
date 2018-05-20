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
import axoloti.object.inlet.InletBool32Rising;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Bipolar;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import axoloti.object.parameter.ParameterInt32Box;
import static generatedobjects.GenTools.writeAxoObject;

/**
 *
 * @author Johannes Taelman
 */
class Stochastics extends GenTools {

    static void generateAll() {
        String catName = "rand";
        writeAxoObject(catName, createRand());
        writeAxoObject(catName, createRandTrigger());
        writeAxoObject(catName, createRandTriggerI());

//        objs.add(CreatekNoisePoissonOsc1());
//        objs.add(CreatekNoisePoissonOsc2());
//        objs.add(CreatekNoisePoissonOsc3());
//        objs.add(CreatekNoisePoissonOsc4());
//        objs.add(CreateNoisePoissonOsc1());
//        objs.add(CreateNoisePoissonOsc2());
//        objs.add(CreateNoisePoissonOsc3());
//        objs.add(CreateNoisePoissonOsc4());
    }

    static AxoObject createRand() {
        AxoObject o = new AxoObject("uniform f", "uniform distributed (white) noise, k-rate generation. Range -64..64");
        o.outlets.add(new OutletFrac32("wave", "white noise"));
        o.sKRateCode = "outlet_wave = (int32_t)(GenerateRandomNumber())>>4;";
        return o;
    }

    static AxoObject createRandTrigger() {
        AxoObject o = new AxoObject("uniform f trig", "uniform distributed (white) noise, triggered generation. Range -64..64");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.outlets.add(new OutletFrac32Bipolar("rand", "random number"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = "val = (int32_t)(GenerateRandomNumber())>>4;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((inlet_trig>0) && !ntrig) { val = (int32_t)(GenerateRandomNumber())>>4; ntrig=1;}\n"
                + "   else if (!(inlet_trig>0)) ntrig=0;\n"
                + "   outlet_rand= val;\n";
        return o;
    }

    static AxoObject createRandTriggerI() {
        AxoObject o = new AxoObject("uniform i", "uniform distributed (white) noise, k-rate generation. Range 0..(n-1)");
        o.inlets.add(new InletBool32Rising("trig", "trigger"));
        o.params.add(new ParameterInt32Box("max", 0, 1 << 16));
        o.outlets.add(new OutletInt32("v", "random value"));
        o.sLocalData = "int32_t val;\n"
                + "int ntrig;\n";
        o.sInitCode = ""
                + "val = 0;\n"
                + "ntrig = 0;\n";
        o.sKRateCode = "   if ((inlet_trig>0) && !ntrig) {\n"
                + "      if (param_max) \n"
                + "         val = (int32_t)(GenerateRandomNumber()% (param_max));\n"
                + "      else val = 0;\n"
                + "      ntrig=1;\n"
                + "   }\n"
                + "   else if (!(inlet_trig>0)) ntrig=0;\n"
                + "   outlet_v = val;\n";
        return o;
    }

    static AxoObject createNoisePoissonOsc1() {
        AxoObject o = new AxoObject("poisson1~", "Poisson noise generator 1");
        o.outlets.add(new OutletFrac32Buffer("wave", "poisson noise"));
        o.sSRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave = ((!(tmp&0x7F000000))+ (!(tmp&0x007F0000)) + (!(tmp&0x00007F00)) + (!(tmp&0x0000007F)))<<25;}\n";
        return o;
    }

    static AxoObject createNoisePoissonOsc2() {
        AxoObject o = new AxoObject("poisson2~", "Poisson noise generator 2");
        o.outlets.add(new OutletFrac32Buffer("wave", "poisson noise"));
        o.sSRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave = ((!(tmp&0xFF000000))+ (!(tmp&0x00FF0000)) + (!(tmp&0x0000FF00)) + (!(tmp&0x000000FF)))<<25;}\n";
        return o;
    }

    static AxoObject createNoisePoissonOsc3() {
        AxoObject o = new AxoObject("poisson3~", "Poisson noise generator 3");
        o.outlets.add(new OutletFrac32Buffer("wave", "poisson noise"));
        o.sSRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave = ((!(tmp&0x0001FF))+ (!(tmp&0x03FE00)) + (!(tmp&0x7FC0000)))<<25;}\n";
        return o;
    }

    static AxoObject createNoisePoissonOsc4() {
        AxoObject o = new AxoObject("poisson4~", "Poisson noise generator 4");
        o.outlets.add(new OutletFrac32Buffer("wave", "poisson noise"));
        o.sSRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave= ((!(tmp&0x000003FF))+ (!(tmp&0x000FFC00)) + (!(tmp&0x3FF00000)))<<25;}\n";
        return o;
    }

    static AxoObject createkNoisePoissonOsc1() {
        AxoObject o = new AxoObject("poisson1", "Poisson noise generator 1");
        o.outlets.add(new OutletFrac32("wave", "poisson noise"));
        o.sKRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave= ((!(tmp&0x7F000000))+ (!(tmp&0x007F0000)) + (!(tmp&0x00007F00)) + (!(tmp&0x0000007F)))<<25;}\n";
        return o;
    }

    static AxoObject createkNoisePoissonOsc2() {
        AxoObject o = new AxoObject("poisson2", "Poisson noise generator 2");
        o.outlets.add(new OutletFrac32("wave", "poisson noise"));
        o.sKRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave= ((!(tmp&0xFF000000))+ (!(tmp&0x00FF0000)) + (!(tmp&0x0000FF00)) + (!(tmp&0x000000FF)))<<25;}\n";
        return o;
    }

    static AxoObject createkNoisePoissonOsc3() {
        AxoObject o = new AxoObject("poisson3", "Poisson noise generator 3");
        o.outlets.add(new OutletFrac32("wave", "poisson noise"));
        o.sKRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave= ((!(tmp&0x0001FF))+ (!(tmp&0x03FE00)) + (!(tmp&0x7FC0000)))<<25;}\n";
        return o;
    }

    static AxoObject createkNoisePoissonOsc4() {
        AxoObject o = new AxoObject("poisson4", "Poisson noise generator 4");
        o.outlets.add(new OutletFrac32("wave", "poisson noise"));
        o.sKRateCode = "{ int32_t x->tmp = GenerateRandomNumber();\n"
                + "outlet_wave= ((!(tmp&0x000003FF))+ (!(tmp&0x000FFC00)) + (!(tmp&0x3FF00000)))<<25;}\n";
        return o;
    }
}
