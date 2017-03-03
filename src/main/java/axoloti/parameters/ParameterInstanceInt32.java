/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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

import axoloti.datatypes.Value;
import axoloti.datatypes.ValueInt32;
import axoloti.object.AxoObjectInstance;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterInstanceInt32<T extends Parameter> extends ParameterInstance<T> {

    final ValueInt32 value = new ValueInt32();

    @Attribute(name = "value", required = false)
    public int getValuex() {
        return value.getInt();
    }

    public ParameterInstanceInt32() {
    }

    public ParameterInstanceInt32(@Attribute(name = "value") int v) {
        value.setInt(v);
    }

    public ParameterInstanceInt32(T param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public ValueInt32 getValue() {
        return value;
    }

    @Override
    public void setValue(Value value) {
        this.value.setInt(value.getInt());
    }

    @Override
    public void CopyValueFrom(ParameterInstance p) {
        super.CopyValueFrom(p);
        if (p instanceof ParameterInstanceInt32) {
            ParameterInstanceInt32 p1 = (ParameterInstanceInt32) p;
            presets = p1.presets;
            value.setRaw(p1.value.getRaw());
        }
    }
}