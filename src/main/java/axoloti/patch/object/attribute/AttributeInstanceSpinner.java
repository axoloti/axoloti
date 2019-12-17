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

import axoloti.object.attribute.AxoAttributeSpinner;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.property.IntegerProperty;
import axoloti.property.Property;
import java.beans.PropertyChangeEvent;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceSpinner extends AttributeInstanceInt<AxoAttributeSpinner> {

    private Integer minValue;
    private Integer maxValue;

    public static final Property MINVALUE = new IntegerProperty("MinValue", AttributeInstanceSpinner.class, "Minimum");
    public static final Property MAXVALUE = new IntegerProperty("MaxValue", AttributeInstanceSpinner.class, "Maximum");
//    public static final Property DEFAULTVALUE = new IntegerProperty("DefaultValue",AttributeInstanceSpinner.class, "Default");

    AttributeInstanceSpinner() {
        super();
    }

    public AttributeInstanceSpinner(AxoAttributeSpinner attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
        value = attribute.getDefaultValue();
    }

    @Override
    public String CValue() {
        return "" + value;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoAttributeSpinner.ATOM_MAXVALUE.is(evt)) {
            setMaxValue((Integer) evt.getNewValue());
        } else if (AxoAttributeSpinner.ATOM_MINVALUE.is(evt)) {
            setMinValue((Integer) evt.getNewValue());
        }
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer MinValue) {
        Integer prevVal = this.minValue;
        this.minValue = MinValue;
        firePropertyChange(MINVALUE, prevVal, MinValue);
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer MaxValue) {
        Integer prevVal = this.maxValue;
        this.maxValue = MaxValue;
        firePropertyChange(MAXVALUE, prevVal, MaxValue);
    }

}
