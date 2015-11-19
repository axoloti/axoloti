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

import axoloti.datatypes.ValueFrac32;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletFrac32BufferPos;
import axoloti.inlets.InletFrac32Pos;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.parameters.ParameterFrac32UMap;
import axoloti.parameters.ParameterFrac32UMapGain;
import axoloti.parameters.ParameterFrac32UMapGainSquare;
import static generatedobjects.gentools.WriteAxoObject;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
public class Mixer extends gentools {

    static void GenerateAll() {
        String catName = "mix";
        for (int i = 1; i < 9; i++) {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKMixer(i));
            c.add(CreateSMixer(i));
            WriteAxoObject(catName, c);
        }
        for (int i = 1; i < 9; i++) {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKMixerL(i));
            c.add(CreateSMixerL(i));
            WriteAxoObject(catName, c);
        }
        for (int i = 1; i < 9; i++) {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(CreateKMixerSQ(i));
            c.add(CreateSMixerSQ(i));
            WriteAxoObject(catName, c);
        }

        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_xfade());
            c.add(Create_xfade2());
            c.add(Create_xfadeTilde());
            WriteAxoObject(catName, c);
        }
    }

    static AxoObject CreateKMixer(int n) {
        AxoObject o = new AxoObject("mix " + n, "" + n + " input k-rate mixer");
        o.inlets.add(new InletFrac32("bus_in", "input with unity gain"));
        o.outlets.add(new OutletFrac32("out", "mix out"));
        for (int ii = 0; ii < n; ii++) {
            o.inlets.add(new InletFrac32("in" + (ii + 1), "input " + (ii + 1)));
            o.params.add(new ParameterFrac32UMap("gain" + (ii + 1), new ValueFrac32(32.0)));
        }
        o.sKRateCode = "{"
                + "   int32_t accum = ___SMMUL(%in1%,%gain1%);\n";
        for (int ii = 1; ii < n; ii++) {
            o.sKRateCode += "   accum = ___SMMLA(%in" + (ii + 1) + "%,%gain" + (ii + 1) + "%,accum);\n;";
        }
        o.sKRateCode += "   %out%=  __SSAT(%bus_in% + (accum<<5),28);\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateSMixer(int n) {
        AxoObject o = new AxoObject("mix " + n, "" + n + " input s-rate mixer");
        o.outlets.add(new OutletFrac32Buffer("out", "mix out"));
        o.inlets.add(new InletFrac32Buffer("bus_in", "input with unity gain"));
        for (int ii = 0; ii < n; ii++) {
            Inlet i = new InletFrac32Buffer("in" + (ii + 1), "input " + (ii + 1));
            o.inlets.add(i);
            o.params.add(new ParameterFrac32UMap("gain" + (ii + 1), new ValueFrac32(32.0)));
        }
        o.sSRateCode = "{"
                + "   int32_t accum = ___SMMUL(%in1%,%gain1%);\n";
        for (int ii = 1; ii < n; ii++) {
            o.sSRateCode += "   accum = ___SMMLA(%in" + (ii + 1) + "%,%gain" + (ii + 1) + "%,accum);\n;";
        }
        o.sSRateCode += "   %out%=  __SSAT(%bus_in% + (accum<<5),28);\n"
                + "}\n";
        return o;
    }

    static AxoObject CreateSMixerL(int n) {
        AxoObject o = new AxoObject("mix " + n + " g", "" + n + " input s-rate mixer, shows gain units");
        o.outlets.add(new OutletFrac32Buffer("out", "mix out"));
        o.inlets.add(new InletFrac32Buffer("bus_in", "input with unity gain"));
        for (int ii = 0; ii < n; ii++) {
            Inlet i = new InletFrac32Buffer("in" + (ii + 1), "input " + (ii + 1));
            o.inlets.add(i);
            o.params.add(new ParameterFrac32UMapGain("gain" + (ii + 1), new ValueFrac32(32.0)));
        }
        o.sSRateCode = "   int32_t accum = ___SMMUL(%in1%,%gain1%);\n";
        for (int ii = 1; ii < n; ii++) {
            o.sSRateCode += "   accum = ___SMMLA(%in" + (ii + 1) + "%,%gain" + (ii + 1) + "%,accum);\n;";
        }
        o.sSRateCode += "   %out%=  __SSAT(%bus_in% + (accum<<1),28);\n";
        return o;
    }

    static AxoObject CreateKMixerL(int n) {
        AxoObject o = new AxoObject("mix " + n + " g", "" + n + " input k-rate mixer, shows gain units");
        o.inlets.add(new InletFrac32("bus_in", "input with unity gain"));
        o.outlets.add(new OutletFrac32("out", "mix out"));
        for (int ii = 0; ii < n; ii++) {
            o.inlets.add(new InletFrac32("in" + (ii + 1), "input " + (ii + 1)));
            o.params.add(new ParameterFrac32UMapGain("gain" + (ii + 1), new ValueFrac32(32.0)));
        }
        o.sKRateCode = "   int32_t accum = ___SMMUL(%in1%,%gain1%);\n";
        for (int ii = 1; ii < n; ii++) {
            o.sKRateCode += "   accum = ___SMMLA(%in" + (ii + 1) + "%,%gain" + (ii + 1) + "%,accum);\n;";
        }
        o.sKRateCode += "   %out%=  __SSAT(%bus_in% + (accum<<1),28);\n";
        return o;
    }

    static AxoObject CreateSMixerSQ(int n) {
        AxoObject o = new AxoObject("mix " + n + " sq", "" + n + " input s-rate mixer, square gain scale");
        o.outlets.add(new OutletFrac32Buffer("out", "mix out"));
        o.inlets.add(new InletFrac32Buffer("bus_in", "input with unity gain"));
        for (int ii = 0; ii < n; ii++) {
            Inlet i = new InletFrac32Buffer("in" + (ii + 1), "input " + (ii + 1));
            o.inlets.add(i);
            o.params.add(new ParameterFrac32UMapGainSquare("gain" + (ii + 1), new ValueFrac32(32.0)));
        }
        o.sSRateCode = "   int32_t accum = ___SMMUL(%in1%,%gain1%);\n";
        for (int ii = 1; ii < n; ii++) {
            o.sSRateCode += "   accum = ___SMMLA(%in" + (ii + 1) + "%,%gain" + (ii + 1) + "%,accum);\n;";
        }
        o.sSRateCode += "   %out%=  __SSAT(%bus_in% + (accum<<1),28);\n";
        return o;
    }

    static AxoObject CreateKMixerSQ(int n) {
        AxoObject o = new AxoObject("mix " + n + " sq", "" + n + " input k-rate mixer, square gain scale");
        o.inlets.add(new InletFrac32("bus_in", "input with unity gain"));
        o.outlets.add(new OutletFrac32("out", "mix out"));
        for (int ii = 0; ii < n; ii++) {
            o.inlets.add(new InletFrac32("in" + (ii + 1), "input " + (ii + 1)));
            o.params.add(new ParameterFrac32UMapGainSquare("gain" + (ii + 1), new ValueFrac32(32.0)));
        }
        o.sKRateCode = "   int32_t accum = ___SMMUL(%in1%,%gain1%);\n";
        for (int ii = 1; ii < n; ii++) {
            o.sKRateCode += "   accum = ___SMMLA(%in" + (ii + 1) + "%,%gain" + (ii + 1) + "%,accum);\n;";
        }
        o.sKRateCode += "   %out%=  __SSAT(%bus_in% + (accum<<1),28);\n";
        return o;
    }

    static AxoObject Create_xfade() {
        AxoObject o = new AxoObject("xfade", "crossfade between two inputs");
        o.inlets.add(new InletFrac32("i1", "input"));
        o.inlets.add(new InletFrac32("i2", "input"));
        o.inlets.add(new InletFrac32Pos("c", "control"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "   {\n"
                + "      int64_t a = (int64_t)%i2% * %c%;\n"
                + "      a += (int64_t)%i1% * ((128<<20)-%c%);\n"
                + "      %o%= a>>27;\n"
                + "   }\n";
        return o;
    }

    static AxoObject Create_xfade2() {
        AxoObject o = new AxoObject("xfade", "crossfade between two inputs");
        o.inlets.add(new InletFrac32Buffer("i1", "input"));
        o.inlets.add(new InletFrac32Buffer("i2", "input"));
        o.inlets.add(new InletFrac32BufferPos("c", "control"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sSRateCode = "   {\n"
                + "      int64_t a = (int64_t)%i2% * %c%;\n"
                + "      a += (int64_t)%i1% * ((128<<20)-%c%);\n"
                + "      %o%= a>>27;\n"
                + "   }\n";
        return o;
    }

    static AxoObject Create_xfadeTilde() {
        AxoObject o = new AxoObject("xfade", "crossfade between two inputs");
        o.inlets.add(new InletFrac32Buffer("i1", "input"));
        o.inlets.add(new InletFrac32Buffer("i2", "input"));
        o.inlets.add(new InletFrac32Pos("c", "control"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sKRateCode = "   int32_t ccompl = ((128<<20)-%c%);\n";
        o.sSRateCode = "   {\n"
                + "      int64_t a = (int64_t)%i2% * %c%;\n"
                + "      a += (int64_t)%i1% * ccompl;\n"
                + "      %o%= a>>27;\n"
                + "   }\n";
        return o;
    }

}
