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
package axoloti.utils;

/**
 *
 * @author Johannes Taelman
 */
public class CharEscape {

    static public String CharEscape(String s) {
        s = s.replaceAll("_", "__");
        s = s.replaceAll(" ", "_space_");
        s = s.replaceAll("\\*", "_star_");
        s = s.replaceAll("/", "_slash_");
        s = s.replaceAll("-", "_dash_");
        s = s.replaceAll("\\+", "_plus_");
        s = s.replaceAll("\\~", "_tilde_");
        s = s.replaceAll("%", "_pct_");
        s = s.replaceAll("@", "_at_");
        s = s.replaceAll("!", "_excl_");
        s = s.replaceAll("#", "_cross_");
        s = s.replaceAll("\\$", "_dollar_");
        s = s.replaceAll("&", "_amp_");
        s = s.replaceAll("\\(", "_bo_");
        s = s.replaceAll("\\)", "_bc_");
        s = s.replaceAll("\\>", "_gt_");
        s = s.replaceAll("\\<", "_lt_");
        s = s.replaceAll("=", "_eq_");
        s = s.replaceAll(":", "_colon_");
        s = s.replaceAll("\\.", "_dot_");
        return s;
    }
}
