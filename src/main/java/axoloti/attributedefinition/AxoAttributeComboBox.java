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
package axoloti.attributedefinition;

import axoloti.attribute.AttributeInstanceComboBox;
import axoloti.object.AxoObjectInstance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author Johannes Taelman
 */
public class AxoAttributeComboBox extends AxoAttribute {

    @ElementList(required = false)
    public ArrayList<String> MenuEntries;
    @ElementList(required = false)
    public ArrayList<String> CEntries;

    public AxoAttributeComboBox() {
        if (MenuEntries == null) {
            MenuEntries = new ArrayList<String>();
        }
        if (CEntries == null) {
            CEntries = new ArrayList<String>();
        }
    }

    public AxoAttributeComboBox(String name, String MenuEntries[], String CEntries[]) {
        super(name);
        this.MenuEntries = new ArrayList<String>();
        this.CEntries = new ArrayList<String>();
        this.MenuEntries.addAll(Arrays.asList(MenuEntries));
        this.CEntries.addAll(Arrays.asList(CEntries));
    }

    public ArrayList<String> getMenuEntries() {
        return MenuEntries;
    }

    public ArrayList<String> getCEntries() {
        return CEntries;
    }

    @Override
    public AttributeInstanceComboBox InstanceFactory(AxoObjectInstance o) {
        return new AttributeInstanceComboBox(this, o);
    }

    static public final String TypeName = "combo";

    @Override
    public String getTypeName() {
        return TypeName;
    }

    @Override
    public List<String> getEditableFields() {
        List l = super.getEditableFields();
        l.add("MenuEntries");
        l.add("CEntries");
        return l;
    }

}
