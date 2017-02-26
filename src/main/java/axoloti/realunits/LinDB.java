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
public class LinDB implements NativeToReal {

    final double maxGain;

    public LinDB(double maxGain) {
        this.maxGain = maxGain;
    }

    @Override
    public String ToReal(Value v) {
        if (v.getDouble() != 0) {
            return (String.format("%.1fdB", maxGain + 20 * Math.log10(Math.abs(v.getDouble() / 64.0))));
        } else {
            return "-infdB";
        }
    }

    @Override
    public double FromReal(String s) throws ParseException {
        Pattern pattern = Pattern.compile("(?<num>[\\d\\.\\-\\+]*)\\p{Space}*[dD][bB]?");
        Matcher matcher = pattern.matcher(s);

        if (matcher.matches()) {
            double num;

            try {
                num = Float.parseFloat(matcher.group("num"));
            } catch (java.lang.NumberFormatException ex) {
                throw new ParseException("Not LinDB", 0);
            }

            return Math.pow(10.0, (num - maxGain) / 20) * 64.0;
        }

        throw new ParseException("Not LinDB", 0);
    }
}
