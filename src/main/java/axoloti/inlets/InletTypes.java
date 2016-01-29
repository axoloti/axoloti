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

/**
 *
 * @author jtaelman
 */
public class InletTypes {

    final static Inlet types[] = {
        new InletBool32(),
        new InletBool32Rising(),
        new InletBool32RisingFalling(),
        new InletFrac32(),
        new InletFrac32Pos(),
        new InletFrac32Bipolar(),
        new InletFrac32Buffer(),
        new InletFrac32BufferBipolar(),
        new InletFrac32BufferPos(),
        new InletInt32(),
        new InletInt32Bipolar(),
        new InletInt32Pos(),
        new InletCharPtr32()
    };

    static public Inlet[] getTypes() {
        return types;
    }

}
