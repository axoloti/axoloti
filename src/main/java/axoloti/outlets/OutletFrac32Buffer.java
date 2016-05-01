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
package axoloti.outlets;

import axoloti.datatypes.DataType;
import axoloti.datatypes.Frac32buffer;

/**
 *
 * @author Johannes Taelman
 */
public class OutletFrac32Buffer extends Outlet {

    public OutletFrac32Buffer() {
        super();
    }

    public OutletFrac32Buffer(String name, String description) {
        super(name, description);
    }

    @Override
    public DataType getDatatype() {
        return Frac32buffer.d;
    }

    static public final String TypeName = "frac32buffer";

    @Override
    public String getTypeName() {
        return TypeName;
    }
}
