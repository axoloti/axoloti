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
package axoloti;

import axoloti.datatypes.Value;
import axoloti.datatypes.ValueFrac32;
import axoloti.datatypes.ValueInt32;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;

/**
 *
 * @author Johannes Taelman
 */
public class Preset {

    @Attribute
    public int index;

    @ElementUnion({
        @Element(name = "i", type = ValueInt32.class),
        @Element(name = "f", type = ValueFrac32.class)
    })
    public Value value;

    public Preset() {
    }

    public Preset(int index, Value value) {
        this.index = index;
        this.value = value;
    }
}
