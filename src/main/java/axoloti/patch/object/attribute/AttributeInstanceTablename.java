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

import axoloti.object.attribute.AxoAttributeTablename;
import axoloti.patch.object.AxoObjectInstance;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTablename extends AttributeInstanceString<AxoAttributeTablename> {

    @Attribute(name = "table")
    private String tableName = "";

    AttributeInstanceTablename() {
        super();
    }

    AttributeInstanceTablename(AxoAttributeTablename attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
    }

    @Override
    public String CValue() {
        return tableName;
    }

    @Override
    public String getValue() {
        return tableName;
    }

    @Override
    protected void setValueString(String tableName) {
        String oldvalue = this.tableName;
        this.tableName = tableName;
        firePropertyChange(
                ATTR_VALUE,
                oldvalue, this.tableName);
    }

    @Persist
    public void persist() {
        if (tableName == null) {
            tableName = "";
        }
    }

}
