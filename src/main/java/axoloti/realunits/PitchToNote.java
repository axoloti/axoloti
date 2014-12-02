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
public class PitchToNote implements NativeToReal {

    @Override
    public String ToReal(Value v) {
        String s;
        int n;
        double f;
        n = (int) Math.round(v.getDouble());
        f = v.getDouble() - n;
        switch ((n + 64) % 12) {
            case 0:
                s = "C";
                break;
            case 1:
                s = "C#";
                break;
            case 2:
                s = "D";
                break;
            case 3:
                s = "D#";
                break;
            case 4:
                s = "E";
                break;
            case 5:
                s = "F";
                break;
            case 6:
                s = "F#";
                break;
            case 7:
                s = "G";
                break;
            case 8:
                s = "G#";
                break;
            case 9:
                s = "A";
                break;
            case 10:
                s = "A#";
                break;
            case 11:
                s = "B";
                break;
            default:
                s = "error";
        }
        int i = (n + 52) / 12;
        s += Integer.toString(i);
        if (f > 0) {
            s += String.format("+%02d", Math.round(f * 100));
        } else if (f < 0) {
            s += String.format("-%02d", -Math.round(f * 100));
        } else {
            s += "   ";
        }
        return s;
    }

}
