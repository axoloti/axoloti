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
package axoloti.object.parameter;

import java.security.MessageDigest;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterBin1Momentary extends ParameterBin {

    public ParameterBin1Momentary() {
    }

    public ParameterBin1Momentary(String name) {
        super(name);
    }

    @Override
    public void updateSHA(MessageDigest md) {
        super.updateSHA(md);
        md.update("bool32.b".getBytes());
    }

    static public final String TYPE_NAME = "bool32.mom";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public String getCType() {
        return "param_type_bin_1bit_momentary";
    }

    @Override public int getNBits() {
        return 1;
    }
}
