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
 */package axoloti.datatypes;

import axoloti.Theme;
import java.awt.Color;

/**
 *
 * @author jtaelman
 */
public class Int8Array implements DataType {

    public static final Int8Array d = new Int8Array();

    @Override
    public boolean IsConvertableToType(DataType dest) {
        return false;
    }

    @Override
    public String CType() {
        return "int8_t[128]";
    }

    @Override
    public String GenerateConversionToType(DataType dest, String in) {
        throw new Error("no conversion for " + dest);
    }

    @Override
    public Color GetColor() {
        return Theme.getCurrentTheme().Cable_Int8Array;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Int32Ptr);
    }

    @Override
    public String GenerateCopyCode(String dest, String source) {
        return null;
    }

    @Override
    public boolean HasDefaultValue() {
        return false;
    }

    @Override
    public String GenerateSetDefaultValueCode() {
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 10;
        return hash;
    }

    @Override
    public boolean isPointer() {
        return true;
    }

    @Override
    public String UnconnectedSink() {
        return "";
    }
}
