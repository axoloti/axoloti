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
package axoloti.realunits;

import axoloti.datatypes.Value;

/**
 *
 * @author Johannes Taelman
 */
public class PitchHz implements NativeToReal {

    @Override
    public String ToReal(Value v) {
        double hz = 440.0 * Math.pow(2.0, (v.getDouble() + 64 - 69) / 12.0);
        if (hz > 10000.0) {
            return (String.format("%.2f kHz", hz / 1000));
        }
        if (hz > 1000.0) {
            return (String.format("%.3f kHz", hz / 1000));
        } else if (hz > 100.0) {
            return (String.format("%.1f Hz", hz));
        } else if (hz > 10.0) {
            return (String.format("%.2f Hz", hz));
        } else {
            return (String.format("%.3f Hz", hz));
        }
    }

}
