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
import axoloti.object.attribute.AxoAttributeSpinner;
import axoloti.object.inlet.InletFrac32;
import axoloti.object.inlet.InletFrac32Buffer;
import axoloti.object.outlet.OutletFrac32;
import axoloti.object.outlet.OutletFrac32Buffer;
import axoloti.object.outlet.OutletFrac32BufferPos;
import axoloti.object.outlet.OutletFrac32Pos;
import static generatedobjects.GenTools.writeAxoObject;

/**
 *
 * @author Johannes Taelman
 */
class Math extends GenTools {

    static void generateAll() {
        writeAxoObject("math", new AxoObject[]{createQuantize(), createQuantizeTilde()});
        writeAxoObject("math", new AxoObject[]{createWrap(), createWrapTilde()});
    }

    static AxoObject createQuantize() {
        AxoObject o = new AxoObject("quantize", "quantize to n bits");
        o.outlets.add(new OutletFrac32("b", "quant(a)"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("bits", 1, 28, 7));
        o.sKRateCode = "   %b%= %a% & (~((1<<(28-%bits%))-1));\n";
        return o;
    }

    static AxoObject createQuantizeTilde() {
        AxoObject o = new AxoObject("quantize", "quantize to n bits");
        o.outlets.add(new OutletFrac32Buffer("b", "quant(a)"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("bits", 1, 28, 7));
        o.sSRateCode = "   %b%= %a% & (~((1<<(28-%bits%))-1));\n";
        return o;
    }

    static AxoObject createWrap() {
        AxoObject o = new AxoObject("wrap", "wrap to 0..64 range after multiplying with 2^bits");
        o.outlets.add(new OutletFrac32Pos("b", "quant(a)"));
        o.inlets.add(new InletFrac32("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("bits", 0, 27, 7));
        o.sKRateCode = "   %b%= (%a%<<%bits%) & ((1<<27)-1);\n";
        return o;
    }

    static AxoObject createWrapTilde() {
        AxoObject o = new AxoObject("wrap", "wrap to 0..64 range after multiplying with 2^bits");
        o.outlets.add(new OutletFrac32BufferPos("b", "quant(a)"));
        o.inlets.add(new InletFrac32Buffer("a", "a"));
        o.attributes.add(new AxoAttributeSpinner("bits", 0, 27, 7));
        o.sSRateCode = "   %b%= (%a%<<%bits%) & ((1<<27)-1);\n";
        return o;
    }

}
