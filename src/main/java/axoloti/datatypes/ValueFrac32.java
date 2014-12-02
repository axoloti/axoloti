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

/**
 *
 * @author Johannes Taelman
 */
//@Root(name="value")
public class ValueFrac32 extends Value<Frac32> {

    @Attribute
    private double v;

    public ValueFrac32() {
    }

    public ValueFrac32(Value<Frac32> vv) {
        v = ((ValueFrac32) vv).v;
    }

    public ValueFrac32(double v) {
        this.v = v;
    }

    @Override
    public void setInt(int i) {
        this.v = i;
    }

    @Override
    public int getFrac() {
        return (int) (v * (1 << 21));
    }

    @Override
    public int compareTo(Frac32 o) {
        return 0;
    }

    @Override
    public int getInt() {
        return (int) v;
    }

    @Override
    public void setFrac(int i) {
        v = ((double) i) / (double) (1 << 21);
    }

    @Override
    public double getDouble() {
        return v;
    }

    @Override
    public void setDouble(double d) {
        v = d;
    }

    @Override
    public int getRaw() {
        return getFrac();
    }

    @Override
    public void setRaw(int i) {
        setFrac(i);
    }
}
