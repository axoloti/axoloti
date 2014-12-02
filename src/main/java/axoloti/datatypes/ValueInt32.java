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
package axoloti.datatypes;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class ValueInt32 extends Value<Int32> {

    @Attribute(name = "i")
    private int v;

    public ValueInt32() {
    }

    public ValueInt32(int v) {
        this.v = v;
    }

    public ValueInt32(Value<Int32> vv) {
        v = ((ValueInt32) vv).v;
    }

    @Override
    public void setInt(int val) {
        this.v = val;
    }

    @Override
    public int getInt() {
        return v;
    }

    @Override
    public int compareTo(Int32 o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getFrac() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setFrac(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getDouble() {
        return (double) v;
    }

    @Override
    public void setDouble(double d) {
        v = (int) d;
    }

    @Override
    public int getRaw() {
        return getInt();
    }

    @Override
    public void setRaw(int i) {
        setInt(i);
    }

}
