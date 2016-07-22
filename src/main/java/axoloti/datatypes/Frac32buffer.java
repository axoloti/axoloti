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
public class Frac32buffer extends DataTypeBuffer {

    public static final Frac32buffer d = new Frac32buffer();

    @Override
    public boolean IsConvertableToType(DataType dest) {
        if (equals(dest)) {
            return true;
        }
        if (dest.equals(Bool32.d)) {
            return true;
        }
        if (dest.equals(Frac32.d)) {
            return true;
        }
        if (dest.equals(Int32.d)) {
            return true;
        }
        return false;
    }

    @Override
    public String GenerateConversionToType(DataType dest, String in) {
        if (equals(dest)) {
            return in;
        }
        if (Bool32.d.equals(dest)) {
            return "(" + in + "[0]>0)";
        }
        if (Frac32.d.equals(dest)) {
            return "(" + in + "[0])";
        }
        if (Int32.d.equals(dest)) {
            return "(" + in + "[0]>>21)";
        }
        throw new Error("no conversion for " + dest);
    }

    @Override
    public String CType() {
        return "int32buffer ";
    }

    @Override
    public Color GetColor() {
        return Theme.getCurrentTheme().Cable_Frac32Buffer;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Frac32buffer);
    }

    @Override
    public String GenerateCopyCode(String dest, String source) {
        return "   for(i=0;i<BUFSIZE;i++)\n"
                + "      " + dest + "[i]=" + source + "[i];\n";
    }

    @Override
    public boolean HasDefaultValue() {
        return false;
    }

    @Override
    public String GenerateSetDefaultValueCode() {
        return "zerobuffer";
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean isPointer() {
        return false;
    }

    @Override
    public String UnconnectedSink() {
        return "UNCONNECTED_OUTPUT_BUFFER";
    }
}
