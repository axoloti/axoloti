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
package axoloti.datatypes;

import axoloti.Theme;
import java.awt.Color;

/**
 *
 * @author Johannes Taelman
 */
public class Bool32 implements DataType {

    public static final Bool32 d = new Bool32();

    @Override
    public boolean IsConvertableToType(DataType dest) {
        if (equals(dest)) {
            return true;
        }
        if (Int32.d.equals(dest)) {
            return true;
        }
        return Frac32.d.equals(dest);
    }

    @Override
    public String GenerateConversionToType(DataType dest, String in) {
        if (equals(dest)) {
            return in;
        }
        if (Int32.d.equals(dest)) {
            return "(" + in + "?1:0)";
        }
        if (Frac32.d.equals(dest)) {
            return "(" + in + "?(1<<27)-1:0)";
        }
        throw new Error("no conversion for " + dest);
    }

    @Override
    public String CType() {
        return "bool ";
    }

    @Override
    public Color GetColor() {
        return Theme.getCurrentTheme().Cable_Bool32;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Bool32);
    }

    @Override
    public String GenerateCopyCode(String dest, String source) {
        return dest + " = " + source + ";\n";
    }

    @Override
    public boolean HasDefaultValue() {
        return true;
    }

    @Override
    public String GenerateSetDefaultValueCode() {
        return "0";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean isPointer() {
        return false;
    }

    @Override
    public String UnconnectedSink() {
        return "(bool &)UNCONNECTED_OUTPUT";
    }
}
