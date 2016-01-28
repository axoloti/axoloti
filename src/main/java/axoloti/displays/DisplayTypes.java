/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti.displays;

/**
 *
 * @author jtaelman
 */
public class DisplayTypes {

    final static Display types[] = {
        new DisplayBool32(),
        new DisplayFrac32SChart(),
        new DisplayFrac32SDial(),
        new DisplayFrac32UChart(),
        new DisplayFrac32UDial(),
        new DisplayFrac32VBar(),
        new DisplayFrac32VBarDB(),
        new DisplayFrac32VU(),
        new DisplayFrac4ByteVBar(),
        new DisplayFrac4UByteVBar(),
        new DisplayFrac8S128VBar(),
        new DisplayFrac8U128VBar(),
        new DisplayInt32Bar16(),
        new DisplayInt32Bar32(),
        new DisplayInt32HexLabel(),
        new DisplayInt32Label(),
        new DisplayNoteLabel(),
        new DisplayVScale()
    };

    static public Display[] getTypes() {
        return types;
    }
}
