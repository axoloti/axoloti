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
package axoloti.object.attribute;

import axoloti.property.Property;
import axoloti.property.StringListProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.simpleframework.xml.ElementList;

/**
 *
 * @author Johannes Taelman
 */
public class AxoAttributeComboBox extends AxoAttribute {

    @ElementList(required = false)
    public List<String> MenuEntries = new ArrayList<>();
    @ElementList(required = false)
    public List<String> CEntries = new ArrayList<>();

    public static final Property ATOM_MENUENTRIES = new StringListProperty("MenuEntries", AxoAttributeComboBox.class, "Menu entries");
    public static final Property ATOM_CENTRIES = new StringListProperty("CEntries", AxoAttributeComboBox.class, "C++ entries");

    public AxoAttributeComboBox() {
    }

    public AxoAttributeComboBox(String name, String MenuEntries[], String CEntries[]) {
        super(name);
        this.MenuEntries.addAll(Arrays.asList(MenuEntries));
        this.CEntries.addAll(Arrays.asList(CEntries));
    }

    public List<String> getMenuEntries() {
        return Collections.unmodifiableList(MenuEntries);
    }

    public void setMenuEntries(List<String> MenuEntries) {
        List<String> oldVal = this.MenuEntries;
        this.MenuEntries = MenuEntries;
        firePropertyChange(ATOM_MENUENTRIES, oldVal, MenuEntries);
    }

    public List<String> getCEntries() {
        return Collections.unmodifiableList(CEntries);
    }

    public void setCEntries(List<String> CEntries) {
        List<String> oldVal = this.CEntries;
        this.CEntries = CEntries;
        firePropertyChange(ATOM_CENTRIES, oldVal, CEntries);
    }

    static public final String TYPE_NAME = "combo";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public List<Property> getEditableFields() {
        List l = super.getEditableFields();
        l.add(ATOM_MENUENTRIES);
        l.add(ATOM_CENTRIES);
        return l;
    }

}
