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
package axoloti.patch.object.parameter.preset;

import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class Preset<DT> {

    @Attribute
    private int index;
/*
    @ElementUnion({
        @Element(name = "i", type = ValueInt32.class),
        @Element(name = "f", type = ValueFrac32.class)
    })
    public Value value;
*/

    private DT v;

    public Preset() {
    }

    public Preset(DT v) {
        this.v = v;
    }

    public Preset(int index, DT value) {
        this.index = index;
        this.v = value;
    }

    public DT getValue() {
        return v;
    }

    public int getIndex() {
        return index;
    }
}
