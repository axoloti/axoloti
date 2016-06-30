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

import java.awt.Color;

/**
 *
 * @author Johannes Taelman
 */
public interface DataType {

    abstract boolean IsConvertableToType(DataType dest);

    abstract boolean HasDefaultValue();

    abstract String GenerateSetDefaultValueCode();

    abstract String GenerateConversionToType(DataType dest, String in);

    abstract String CType();

    abstract Color GetColor();

    abstract String GenerateCopyCode(String dest, String source);

    abstract boolean isPointer();

    abstract String UnconnectedSink();
}
