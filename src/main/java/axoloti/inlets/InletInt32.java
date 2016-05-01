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
package axoloti.inlets;

import axoloti.datatypes.DataType;
import axoloti.datatypes.Int32;

/**
 *
 * @author Johannes Taelman
 */
public class InletInt32 extends Inlet {

    public InletInt32(String name, String description) {
        super(name, description);
    }

    public InletInt32() {
        super();
    }

    @Override
    public DataType getDatatype() {
        return Int32.d;
    }

    static public final String TypeName = "int32";

    @Override
    public String getTypeName() {
        return TypeName;
    }
    
}
