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
package axoloti.attribute;

import axoloti.attributedefinition.AxoAttributeInt32;
import axoloti.attributeviews.AttributeInstanceViewInt32;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceInt32 extends AttributeInstanceInt<AxoAttributeInt32> {



    public AttributeInstanceInt32() {
    }

    public AttributeInstanceInt32(AxoAttributeInt32 param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public String CValue() {
        return "" + value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public AttributeInstanceViewInt32 ViewFactory(AxoObjectInstanceView o) {
        return new AttributeInstanceViewInt32(this, o);
    }
}