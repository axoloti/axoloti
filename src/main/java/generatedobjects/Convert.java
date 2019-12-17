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
import axoloti.object.inlet.InletBool32;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.inlet.InletInt32;
import axoloti.object.outlet.OutletBool32;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletInt32;
import static generatedobjects.GenTools.writeAxoObject;

/**
 *
 * @author Johannes Taelman
 */
class Convert extends GenTools {

    static void generateAll() {
        String catName = "conv";
        writeAxoObject(catName, create_interp());
        writeAxoObject(catName, create_nointerp());
        writeAxoObject(catName, createNoteToPitch());
        writeAxoObject(catName, new AxoObjectAbstract[]{create_unipolar2bipolar(), create_unipolar2bipolarTilde()});
        writeAxoObject(catName, new AxoObjectAbstract[]{create_bipolar2unipolar(), create_bipolar2unipolarTilde()});
        writeAxoObject(catName, toFrac32());
        writeAxoObject(catName, toBool32());
        writeAxoObject(catName, toInt32());
    }

    static AxoObject create_interp() {
        AxoObject o = new AxoObject("interp", "linear interpolation from k- to s-rate");
        o.inlets.add(new InletFrac32("i", "input"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sLocalData = "   int32_t _prev;\n "
                + "   int32_t _step;\n";
        o.sKRateCode = "   _step = (%i% - _prev)>>4;\n"
                + "   int32_t _i = _prev;\n"
                + "   _prev = %i%;\n";
        o.sSRateCode = "   %o% = _i;\n"
                + "   _i += _step;\n";
        return o;
    }

    static AxoObject create_nointerp() {
        AxoObject o = new AxoObject("nointerp", "k- to s-rate without interpolation");
        o.inlets.add(new InletFrac32("i", "input"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sSRateCode = "   %o% = %i%;\n";
        return o;
    }

    static AxoObject createNoteToPitch() {
        AxoObject o = new AxoObject("mtof", "Midi note number to frequency, equal tempered tuning, A=440Hz");
        o.outlets.add(new OutletFrac32("frequency", "frequency"));
        o.inlets.add(new InletFrac32("pitch", "chromatic note"));
        o.sKRateCode = "int f1;\n"
                + "MTOF(inlet_pitch,f1);\n"
                + "outlet_frequency = f1>>4;\n";
        return o;
    }

    static AxoObject create_unipolar2bipolar() {
        AxoObject o = new AxoObject("unipolar2bipolar", "unipolar to bipolar");
        o.inlets.add(new InletFrac32("i", "input"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "   %o%= (%i%-(1<<26))<<1;\n";
        return o;
    }

    static AxoObject create_unipolar2bipolarTilde() {
        AxoObject o = new AxoObject("unipolar2bipolar", "unipolar to bipolar");
        o.inlets.add(new InletFrac32Buffer("i", "input"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sSRateCode = "   %o%= (%i%-(1<<26))<<1;\n";
        return o;
    }

    static AxoObject create_bipolar2unipolar() {
        AxoObject o = new AxoObject("bipolar2unipolar", "bipolar to unipolar");
        o.inlets.add(new InletFrac32("i", "input"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "   %o%= (%i%>>1)+(1<<26);\n";
        return o;
    }

    static AxoObject create_bipolar2unipolarTilde() {
        AxoObject o = new AxoObject("bipolar2unipolar", "bipolar to unipolar");
        o.inlets.add(new InletFrac32Buffer("i", "input"));
        o.outlets.add(new OutletFrac32Buffer("o", "output"));
        o.sSRateCode = "   %o%= (%i%>>1)+(1<<26);\n";
        return o;
    }

    static AxoObject toFrac32() {
        AxoObject o = new AxoObject("to f", "convert to fractional output");
        o.inlets.add(new InletFrac32("i", "input"));
        o.outlets.add(new OutletFrac32("o", "output"));
        o.sKRateCode = "   %o%= %i%;\n";
        return o;
    }

    static AxoObject toInt32() {
        AxoObject o = new AxoObject("to i", "convert to integer output");
        o.inlets.add(new InletInt32("i", "input"));
        o.outlets.add(new OutletInt32("o", "output"));
        o.sKRateCode = "   %o%= %i%;\n";
        return o;
    }

    static AxoObject toBool32() {
        AxoObject o = new AxoObject("to b", "convert to boolean output");
        o.inlets.add(new InletBool32("i", "input"));
        o.outlets.add(new OutletBool32("o", "output"));
        o.sKRateCode = "   %o%= %i%;\n";
        return o;
    }
}
