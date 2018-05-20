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
package axoloti.object.parameter;

import axoloti.datatypes.ValueFrac32;
import axoloti.property.DoubleProperty;
import axoloti.property.Property;
import java.util.List;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public abstract class ParameterFrac32 extends Parameter {

    @Element(required = false)
    public ValueFrac32 DefaultValue = new ValueFrac32(0);

    static final Property DEFAULTVALUE = new DoubleProperty("DefaultValue", ParameterFrac32.class, "Default");

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
    public List<Property> getEditableFields() {
        List l = super.getEditableFields();
        l.add(DEFAULTVALUE);
        return l;
    }

    @Override
    public Double getDefaultValue() {
        return DefaultValue.getDouble();
    }

    public void setDefaultValue(Double DefaultValue) {
        Double prev = getDefaultValue();
        this.DefaultValue = new ValueFrac32(DefaultValue);
        firePropertyChange(DEFAULTVALUE, prev, DefaultValue);
    }
}
