/**
 * Copyright (C) 2015 Johannes Taelman
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

import axoloti.attributedefinition.AxoAttributeSpinner;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.outlets.OutletInt32;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author jtaelman
 */
public class Constant extends gentools {

    static void GenerateAll() {
        String catName = "const";
        WriteAxoObject(catName, CreateConstI());
    }

    private static AxoObjectAbstract CreateConstI() {
        AxoObject o = new AxoObject("i", "constant");
        o.outlets.add(new OutletInt32("out", "output"));
        o.attributes.add(new AxoAttributeSpinner("value", -(1 << 31), (1 << 31) - 1, 0));
        o.sKRateCode = "%out%= %value%;\n";
        return o;
    }
}
