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

import components.LabelComponent;

/**
 *
 * @author Johannes Taelman
 */
public class DisplayInstanceInt32HexLabel extends DisplayInstanceInt32<DisplayInt32HexLabel> {

    private LabelComponent readout;

    public DisplayInstanceInt32HexLabel() {
        super();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        readout = new LabelComponent("0xxxxxxxxx");
        add(readout);
        readout.setSize(80, 18);
    }

    @Override
    public void updateV() {
        readout.setText(String.format("0x%08X", value.getInt()));
    }
}
