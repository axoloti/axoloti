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
public class DecayTime implements NativeToReal {
    /*
     v1 = v0 * (v)
     */

    @Override
    public String ToReal(Value v) {
        double t = Math.log(2.0) * (1.0 / (64 - v.getDouble())) * (16 / 48000.0) * 4096;
        if (t > 1.0) {
            return (String.format("%.2f s", t));
        } else if (t > 0.1) {
            return (String.format("%.0f ms", t * 1000));
        } else {
            return (String.format("%.1f ms", t * 1000));
        }
    }
}
