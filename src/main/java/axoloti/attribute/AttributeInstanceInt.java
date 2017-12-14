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

import axoloti.atom.AtomDefinitionController;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.object.AxoObjectInstance;
import axoloti.property.IntegerProperty;
import axoloti.property.Property;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class AttributeInstanceInt<T extends AxoAttribute> extends AttributeInstance<T> {

    @Attribute
    int value;
    public static final Property ATTR_VALUE = new IntegerProperty("Value", AttributeInstanceInt.class);

    AttributeInstanceInt() {
    }

    public AttributeInstanceInt(AtomDefinitionController controller, AxoObjectInstance axoObj1) {
        super(controller, axoObj1);
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        l.add(ATTR_VALUE);
        return l;
    }

    @Override
    public void CopyValueFrom(AttributeInstance a) {
        if (a instanceof AttributeInstanceInt) {
            AttributeInstanceInt a1 = (AttributeInstanceInt) a;
            value = a1.value;
        }
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        Integer oldvalue = this.value;
        this.value = value;
        firePropertyChange(
                ATTR_VALUE,
                oldvalue, value);
    }

}
