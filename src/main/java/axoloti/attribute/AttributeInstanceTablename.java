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

import axoloti.MainFrame;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.attributedefinition.AxoAttributeTablename;
import axoloti.attributeviews.AttributeInstanceViewTablename;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.attributeviews.PAttributeInstanceViewTablename;
import axoloti.piccolo.objectviews.PAxoObjectInstanceView;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTablename extends AttributeInstanceString<AxoAttributeTablename> {

    @Attribute(name = "table")
    String tableName = "";

    private AxoObjectInstance axoObj;

    public AttributeInstanceTablename() {
    }

    public AttributeInstanceTablename(AxoAttributeTablename param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        this.axoObj = axoObj1;
    }

    @Override
    public String CValue() {
        return tableName;
    }

    @Override
    public String getString() {
        return tableName;
    }

    @Override
    public void setString(String tableName) {
        this.tableName = tableName;
    }

    @Persist
    public void Persist() {
        if (tableName == null) {
            tableName = "";
        }
    }

    @Override
    public IAttributeInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PAttributeInstanceViewTablename(this, (PAxoObjectInstanceView) o);
        } else {
            return new AttributeInstanceViewTablename(this, (AxoObjectInstanceView) o);
        }
    }
}
