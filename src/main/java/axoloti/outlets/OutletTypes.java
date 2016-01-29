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

/**
 *
 * @author jtaelman
 */
public class OutletTypes {

    final static Outlet types[] = {
        new OutletBool32(),
        new OutletBool32Pulse(),
        new OutletCharPtr32(),
        new OutletFrac32(),
        new OutletFrac32Bipolar(),
        new OutletFrac32Pos(),
        new OutletFrac32Buffer(),
        new OutletFrac32BufferBipolar(),
        new OutletFrac32BufferPos(),
        new OutletInt32(),
        new OutletInt32Bipolar(),
        new OutletInt32Pos()
    };

    static public Outlet[] getTypes() {
        return types;
    }

}
