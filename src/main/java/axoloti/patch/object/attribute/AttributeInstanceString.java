/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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
package axoloti.patch.object.attribute;

import axoloti.object.attribute.AxoAttribute;
import axoloti.patch.object.AxoObjectInstance;

/**
 *
 * @author Johannes Taelman
 */
public abstract class AttributeInstanceString<T extends AxoAttribute> extends AttributeInstance<T> {

    AttributeInstanceString() {
        super();
    }

    AttributeInstanceString(T attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
    }

    @Override
    public abstract String getValue();

    protected abstract void setValueString(String value);

    @Override
    public void setValue(Object value) {
        setValueString((String) value);
    }

    @Override
    public void copyValueFrom(AttributeInstance a) {
        if (a instanceof AttributeInstanceString) {
            AttributeInstanceString a1 = (AttributeInstanceString) a;
            setValue(a1.getValue());
        }
    }

}
