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

import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.outlets.OutletCharPtr32;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class Strings extends gentools {

    static void GenerateAll() {
        String catName = "string";
        WriteAxoObject(catName, Generate_ConstString());
        WriteAxoObject(catName, Generate_ConstStringi());
    }

    static AxoObject Generate_ConstString() {
        AxoObject o = new AxoObject("c", "constant string");
        o.attributes.add(new AxoAttributeTablename("str"));
        o.outlets.add(new OutletCharPtr32("out", "string"));
        o.sLocalData = "char c[64];\n";
        o.sInitCode = "strcpy(&c[0],\"%str%\");\n";
        o.sKRateCode = "%out% = &c[0];\n";
        return o;
    }

    static AxoObject Generate_ConstStringi() {
        AxoObject o = new AxoObject("indexed", "generates string: prefix000suffix");
        o.inlets.add(new InletInt32("index", "index"));
        o.attributes.add(new AxoAttributeTablename("prefix"));
        o.attributes.add(new AxoAttributeTablename("suffix"));
        o.outlets.add(new OutletCharPtr32("out", "string"));
        o.sLocalData = "char c[64];\n"
                + "int offset;\n"
                + "int pval;";
        o.sInitCode = "strcpy(&c[0],\"%prefix%000%suffix%\");\n"
                + "offset = strlen(\"%prefix%\");\n"
                + "pval = 0;\n";
        o.sKRateCode = "if (%index% != pval){"
                + "   pval = %index%;\n"
                + "   int i = %index%;"
                + "   int i0 = i/10;\n"
                + "   c[offset+2] = '0'+i-10*i0;\n"
                + "   i = i0; i0 = i/10;\n"
                + "   c[offset+1] = '0'+i-10*i0;\n"
                + "   i = i0; i0 = i/10;\n"
                + "   c[offset+0] = '0'+i-10*i0;\n"
                + "}\n"
                + "%out% = &c[0];\n";
        return o;
    }
}
