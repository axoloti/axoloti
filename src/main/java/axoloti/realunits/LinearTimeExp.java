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
public class LinearTimeExp implements NativeToReal {

    @Override
    public String ToReal(Value v) {
        double hz = 440.0 * Math.pow(2.0, (-v.getDouble() + 64 - 69) / 12.0) / 32;
        double t = 1.0 / hz;
        if (t > 1) {
            return (String.format("%.2f s", t));
        } else if (t > 0.1) {
            return (String.format("%.1f ms", t * 1000));
        } else {
            return (String.format("%.2f ms", t * 1000));
        }
    }
}
