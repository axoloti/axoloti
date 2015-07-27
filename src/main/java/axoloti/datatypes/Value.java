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

/**
 *
 * @author Johannes Taelman
 * @param <dt> data type
 */
public abstract class Value<dt extends DataType> implements Comparable<dt> {

    public Value() {
    }

    public Value(Value<dt> v) {
    }

    public abstract int getInt();

    public abstract int getFrac();

    public abstract double getDouble();

    public abstract void setInt(int i);

    public abstract void setFrac(int frac);

    public abstract void setDouble(double d);

    public abstract int getRaw();

    public abstract void setRaw(int i);
}
