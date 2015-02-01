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

import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.parameters.ParameterFrac32SMapRatio;
import java.util.ArrayList;

/**
 *
 * @author Johannes Taelman
 */
public class Spat extends gentools {

    static void GenerateAll() {
        String catName = "spat";
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_pan());
            c.add(Create_pantilde());
            WriteAxoObject(catName, c);
        }
        {
            ArrayList<AxoObjectAbstract> c = new ArrayList<AxoObjectAbstract>();
            c.add(Create_panm());
            c.add(Create_panmtilde());
            WriteAxoObject(catName, c);
        }
    }

    static AxoObject Create_pan() {
        AxoObject o = new AxoObject("pan", "stereo balance (panorama)");
        o.inlets.add(new InletFrac32("i1", "input"));
        o.params.add(new ParameterFrac32SMapRatio("c"));
        o.outlets.add(new OutletFrac32("left", "output"));
        o.outlets.add(new OutletFrac32("right", "output"));
        o.sKRateCode = "%left% = ___SMMUL((1<<29)-(%c%<<2),%i1%)<<2;"
                + "%right% = ___SMMUL((1<<29)+(%c%<<2),%i1%)<<2;";
        return o;
    }

    static AxoObject Create_pantilde() {
        AxoObject o = new AxoObject("pan", "stereo balance (panorama)");
        o.inlets.add(new InletFrac32Buffer("i1", "input"));
        o.params.add(new ParameterFrac32SMapRatio("c"));
        o.outlets.add(new OutletFrac32Buffer("left", "output"));
        o.outlets.add(new OutletFrac32Buffer("right", "output"));
        o.sSRateCode = "%left% = ___SMMUL((1<<29)-(%c%<<2),%i1%)<<2;"
                + "%right% = ___SMMUL((1<<29)+(%c%<<2),%i1%)<<2;";
        return o;
    }

    static AxoObject Create_panm() {
        AxoObject o = new AxoObject("pan m", "stereo balance (panorama) with modulation input");
        o.inlets.add(new InletFrac32("i1", "input"));
        o.inlets.add(new InletFrac32("c", "pan control"));
        o.outlets.add(new OutletFrac32("left", "output"));
        o.outlets.add(new OutletFrac32("right", "output"));
        o.sKRateCode = "%left% = ___SMMUL((1<<29)-(%c%<<2),%i1%)<<2;"
                + "%right% = ___SMMUL((1<<29)+(%c%<<2),%i1%)<<2;";
        return o;
    }

    static AxoObject Create_panmtilde() {
        AxoObject o = new AxoObject("pan m", "stereo balance (panorama) with modulation input");
        o.inlets.add(new InletFrac32Buffer("i1", "input"));
        o.inlets.add(new InletFrac32("c", "pan control"));
        o.outlets.add(new OutletFrac32Buffer("left", "output"));
        o.outlets.add(new OutletFrac32Buffer("right", "output"));
        o.sSRateCode = "%left% = ___SMMUL((1<<29)-(%c%<<2),%i1%)<<2;"
                + "%right% = ___SMMUL((1<<29)+(%c%<<2),%i1%)<<2;";
        return o;
    }
}
