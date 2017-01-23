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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.ParseException;

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

    @Override
    public double FromReal(String s) throws ParseException {
        Pattern pattern = Pattern.compile("(?<note>[a-gA-G])\\p{Space}*(?<sharp>[#bB]?)\\p{Space}*(?<oct>\\d+)\\p{Space}*(?<sign>[-\\+]?)\\p{Space}*(?<delta>\\d*)");
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            double n;
            char note, sharp = 0, sign = 0;
            int incidental = 0, oct, delta = 0;

            note = matcher.group("note").toLowerCase().charAt(0);
            if (matcher.group("sharp").length() != 0)
                sharp = matcher.group("sharp").toLowerCase().charAt(0);
            if (matcher.group("sign").length() != 0)
                sign = matcher.group("sign").toLowerCase().charAt(0);
            try {
                oct = Integer.parseInt(matcher.group("oct"));
                if (matcher.group("delta").length() != 0)
                    delta = Integer.parseInt(matcher.group("delta"));
            } catch (java.lang.NumberFormatException ex) {
                throw new ParseException("Not PitchToNote", 0);
            }

            if (sharp == '#')
            {
                incidental = 1;
            }
            else if (sharp == 'b')
            {
                if (note == 'a')
                    note = 'g';
                else
                    note--;
                incidental = 1;
            }

            switch (note) {
                case 'a':
                    n = 9 + incidental;
                    break;
                case 'b':
                    n = 11;
                    break;
                case 'c':
                    n = 0 + incidental;
                    break;
                case 'd':
                    n = 2 + incidental;
                    break;
                case 'e':
                    n = 4;
                    break;
                case 'f':
                    n = 5 + incidental;
                    break;
                case 'g':
                    n = 7 + incidental;
                    break;
                default:
                    throw new ParseException("Not PitchToNote", 0);
            }
            n += (oct * 12) - 52;
            if (sign == '+')
            {
                n += delta / 100.0;
            }
            else if (sign == '-')
            {
                n -= delta / 100.0;
            }
            return n;
        }

        throw new ParseException("Not PitchToNote", 0);
    }

}
