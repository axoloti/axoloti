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
public class DTZombie implements DataType {

    @Override
    public boolean IsConvertableToType(DataType dest) {
        return false;
    }

    @Override
    public boolean HasDefaultValue() {
        return false;
    }

    @Override
    public String GenerateSetDefaultValueCode() {
        return "";
    }

    @Override
    public String GenerateConversionToType(DataType dest, String in) {
        return "";
    }

    @Override
    public String CType() {
        return "";
    }

    @Override
    public Color GetColor() {
        return Theme.getCurrentTheme().Cable_Zombie;
    }

    @Override
    public String GenerateCopyCode(String dest, String source) {
        return "";
    }

    @Override
    public boolean isPointer() {
        return false;
    }

    @Override
    public String UnconnectedSink() {
        return "";
    }
}
