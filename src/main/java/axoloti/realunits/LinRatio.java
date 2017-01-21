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
public class LinRatio implements NativeToReal {

    final double range;

    public LinRatio(double range) {
        this.range = range;
    }

    @Override
    public String ToReal(Value v) {
        return (String.format("x%.3f", range * v.getDouble() / 64.0));
    }

    @Override
    public double FromReal(String s) throws ParseException {
        Pattern pattern = Pattern.compile("(?<unit1>[xX\\*]?)\\p{Space}*(?<num>[\\d\\.\\-\\+]*)\\p{Space}*(?<unit2>[xX\\*]?)");
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            double num;

            try {
                num = Float.parseFloat(matcher.group("num"));
            } catch (java.lang.NumberFormatException ex) {
                throw new ParseException("Not LinRatio", 0);
            }

            String units1 = matcher.group("unit1");
            String units2 = matcher.group("unit2");
            if (!(units1.contains("x") || units1.contains("X") || units1.contains("*") || units2.contains("x") || units2.contains("X") || units2.contains("*")))
                throw new ParseException("Not LinRatio", 0);

            return (num * 64) / range;
        }

        throw new ParseException("Not LinRatio", 0);
    }
}
