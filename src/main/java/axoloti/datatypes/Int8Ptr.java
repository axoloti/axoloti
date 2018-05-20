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

import axoloti.preferences.Theme;
import java.awt.Color;

/**
 *
 * @author jtaelman
 */
public class Int8Ptr implements DataType {

    public static final Int8Ptr d = new Int8Ptr();

    @Override
    public boolean isConvertableToType(DataType dest) {
        return false;
    }

    @Override
    public String CType() {
        return "int8_t *";
    }

    @Override
    public String generateConversionToType(DataType dest, String in) {
        throw new Error("no conversion for " + dest);
    }

    @Override
    public Color getColor() {
        return Theme.getCurrentTheme().Cable_Int8Pointer;
    }

    @Override
    public String generateCopyCode(String dest, String source) {
        return null;
    }

    @Override
    public boolean hasDefaultValue() {
        return false;
    }

    @Override
    public String generateSetDefaultValueCode() {
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
    public String unconnectedSink() {
        return "";
    }
}
