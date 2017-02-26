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
public class FilterQ implements NativeToReal {

    @Override
    public String ToReal(Value v) {
        double q = 32 / (64 - v.getDouble());
        return (String.format("Q=%.1f", q));
    }

    @Override
    public double FromReal(String s) throws ParseException {
        Pattern pattern = Pattern.compile("(?<unit1>[qQ]?)(?<num>[\\d\\.\\-\\+]*)\\p{Space}*(?<unit2>[qQ]?)");
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            double num;

            try {
                num = Float.parseFloat(matcher.group("num"));
            } catch (java.lang.NumberFormatException ex) {
                throw new ParseException("Not DecayTime", 0);
            }

            String units1 = matcher.group("unit1");
            String units2 = matcher.group("unit2");
            if (!(units1.contains("q") || units1.contains("Q") || units2.contains("q") || units2.contains("Q")))
                throw new ParseException("Not FilterQ", 0);

            double q = num;
            return -((32 / q) - 64);
        }

        throw new ParseException("Not FilterQ", 0);
    }
}
