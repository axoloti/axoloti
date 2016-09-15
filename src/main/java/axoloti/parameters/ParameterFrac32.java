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

import axoloti.datatypes.ValueFrac32;
import java.util.List;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterFrac32<T extends ParameterInstanceFrac32> extends Parameter<T> {

    @Element(required = false)
    public ValueFrac32 DefaultValue;

    public ParameterFrac32() {
    }

    public ParameterFrac32(String name) {
        super(name);
    }

    public ParameterFrac32(String name, ValueFrac32 DefaultValue) {
        super(name);
        this.DefaultValue = DefaultValue;
    }

    @Override
    public List<String> getEditableFields() {
        List l = super.getEditableFields();
        l.add("DefaultValue");
        return l;
    }
}
