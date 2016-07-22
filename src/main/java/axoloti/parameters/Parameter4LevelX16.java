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
public class Parameter4LevelX16 extends Parameter<ParameterInstance4LevelX16> {

    public Parameter4LevelX16() {
    }

    public Parameter4LevelX16(String name) {
        super(name);
    }

    @Override
    public ParameterInstance4LevelX16 InstanceFactory() {
        return new ParameterInstance4LevelX16();
    }

    static public final String TypeName = "int2x16";

    @Override
    public String getTypeName() {
        return TypeName;
    }
}
