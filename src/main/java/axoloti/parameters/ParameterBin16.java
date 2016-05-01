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
package axoloti.parameters;

/**
 *
 * @author Johannes Taelman
 */
public class ParameterBin16 extends Parameter {

    public ParameterBin16() {
    }

    public ParameterBin16(String name) {
        super(name);
    }

    @Override
    public ParameterInstanceBin16 InstanceFactory() {
        return new ParameterInstanceBin16();
    }

    static public final String TypeName = "bin16";

    @Override
    public String getTypeName() {
        return TypeName;
    }
}
