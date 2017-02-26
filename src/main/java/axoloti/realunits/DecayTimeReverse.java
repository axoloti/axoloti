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
public class DecayTimeReverse implements NativeToReal {

    @Override
    public String ToReal(Value v) {
        double t = Math.log(2.0) * (1.0 / v.getDouble()) * (16 / 48000.0) * 4096;
        if (t > 1.0) {
            return (String.format("%.2f s", t));
        } else if (t > 0.1) {
            return (String.format("%.0f ms", t * 1000));
        } else {
            return (String.format("%.1f ms", t * 1000));
        }
    }

    @Override
    public double FromReal(String s) throws ParseException {
        Pattern pattern = Pattern.compile("(?<num>[\\d\\.\\-\\+]*)\\p{Space}*(?<unit>[mM]?)[sS]");
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            double num, mul = 1.0;

            try {
                num = Float.parseFloat(matcher.group("num"));
            } catch (java.lang.NumberFormatException ex) {
                throw new ParseException("Not DecayTimeReverse", 0);
            }

            String units = matcher.group("unit");
            if (units.contains("m") || units.contains("M"))
                mul = 0.001;

            double t = num * mul;
            return 1 / (t / Math.log(2.0) * (16 / 48000.0) * 4096);
        }

        throw new ParseException("Not DecayTimeReverse", 0);
    }
}
