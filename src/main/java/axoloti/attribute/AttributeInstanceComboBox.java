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

import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.attributeviews.AttributeInstanceViewComboBox;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceComboBox extends AttributeInstanceString<AxoAttributeComboBox> {

    @Attribute(name = "selection", required = false)
    String selection;

    int selectedIndex;

    public AttributeInstanceComboBox() {
    }

    public AttributeInstanceComboBox(AxoAttributeComboBox param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public String CValue() {
        if (getDefinition().getCEntries().isEmpty()) {
            return "";
        }
        String s = getDefinition().getCEntries().get(selectedIndex);
        if (s != null) {
            return s;
        } else {
            return "";
        }
    }

    @Override
    public String getString() {
        return selection;
    }

    @Override
    public void setString(String selection) {
        this.selection = selection;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    public AttributeInstanceViewComboBox ViewFactory(AxoObjectInstanceView o) {
        return new AttributeInstanceViewComboBox(this, o);
    }
}
