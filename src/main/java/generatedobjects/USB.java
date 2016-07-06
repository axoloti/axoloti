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

import axoloti.object.AxoObject;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletInt32;
import static generatedobjects.gentools.WriteAxoObject;

/**
 *
 * @author Johannes Taelman
 */
public class USB extends gentools {

    static void GenerateAll() {
        String catName = "usb";
        WriteAxoObject(catName, Generate_mouse());
    }

    static AxoObject Generate_mouse() {
        AxoObject o = new AxoObject("mouse", "USB Mouse (draft)");
        o.outlets.add(new OutletInt32("x", "middle mouse button"));
        o.outlets.add(new OutletInt32("y", "middle mouse button"));
        o.outlets.add(new OutletBool32("left", "left mouse button"));
        o.outlets.add(new OutletBool32("right", "right mouse button"));
        o.outlets.add(new OutletBool32("middle", "middle mouse button"));
        o.sKRateCode = "%left% = hid_buttons[0];\n"
                + "%right% = hid_buttons[1];\n"
                + "%middle% = hid_buttons[2];\n"
                + "%x% = hid_mouse_x;\n"
                + "%y% = hid_mouse_y;\n";
        return o;
    }

    static AxoObject Generate_keyb() {
        AxoObject o = new AxoObject("keyb", "USB Keyboard (placeholder draft)");
        o.outlets.add(new OutletBool32("lshift", "left shift"));
        o.outlets.add(new OutletBool32("rshift", "right shift"));
        o.sKRateCode = "\n";
        return o;
    }

}
