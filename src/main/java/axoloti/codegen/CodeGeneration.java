/**
 * Copyright (C) 2017 Johannes Taelman
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
package axoloti.codegen;

/**
 *
 * @author jtaelman
 */
public class CodeGeneration {

    private CodeGeneration() {
    }

    // PARAM_NAME_LENGTH equals MAX_PARAMETER_NAME_LENGTH in firmware/parameters.h
    static final public int PARAM_NAME_LENGTH = 8;

    // to generate a static initializer for a char array in c++
    // CPPCharArrayStaticInitializer("static",5)
    // returns {'s','t','a','t','i'}
    // not null terminated!
    static public String CPPCharArrayStaticInitializer(String n, int length) {
        StringBuilder s = new StringBuilder("{");
        int i;
        int ni = (n.length() > length ? length : n.length());
        for (i = 0; i < ni; i++) {
            s.append("'");
            s.append(n.charAt(i));
            s.append("',");
        }
        while (i < length) {
            s.append(" 0 ,");
            i++;
        }
        s.append("}");
        return s.toString();
    }
}
