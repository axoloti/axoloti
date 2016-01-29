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
package axoloti.displays;

import components.displays.VLineComponentDB;

/**
 *
 * @author Johannes Taelman
 */
public class DisplayInstanceFrac4UByteVBarDB extends DisplayInstanceFrac32<DisplayFrac4UByteVBarDB> {

    private VLineComponentDB vbar[];

    public DisplayInstanceFrac4UByteVBarDB() {
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        vbar = new VLineComponentDB[4];
        for (int i = 0; i < 4; i++) {
            vbar[i] = new VLineComponentDB(0, -60, 10);
            vbar[i].setValue(0);
            add(vbar[i]);
        }
    }

    @Override
    public void updateV() {
        vbar[0].setValue((byte) ((value.getRaw() & 0x000000FF)));
        vbar[1].setValue((byte) ((value.getRaw() & 0x0000FF00) >> 8));
        vbar[2].setValue((byte) ((value.getRaw() & 0x00FF0000) >> 16));
        vbar[3].setValue((byte) ((value.getRaw() & 0xFF000000) >> 24));
    }
}
