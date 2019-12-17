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
package axoloti.patch.object.display;

import axoloti.object.display.DisplayNoteLabel;
import axoloti.realunits.NativeToReal;
import axoloti.realunits.PitchToNote;

/**
 *
 * @author Johannes Taelman
 */
public class DisplayInstanceNoteLabel extends DisplayInstanceFrac32<DisplayNoteLabel> {

    private final NativeToReal conv = new PitchToNote();

    DisplayInstanceNoteLabel(DisplayNoteLabel display) {
        super(display);
    }

    public NativeToReal getConv() {
        return conv;
    }

}
