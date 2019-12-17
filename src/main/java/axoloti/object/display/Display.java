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
package axoloti.object.display;

import axoloti.datatypes.DataType;
import axoloti.object.atom.AtomDefinition;
import axoloti.property.BooleanProperty;
import axoloti.property.Property;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class Display extends AtomDefinition {

    @Attribute(required = false)
    public Boolean noLabel;

    public final static Property NOLABEL = new BooleanProperty("NoLabel", Display.class, "Hide label");

    public Display() {
    }

    public Display(String name) {
        super(name, null);
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    /**
     * Get the data size of this display
     *
     * Only to be used in axoloti.patch.object.display.*
     *
     * @return Display size in number of 32bit words
     */
    public int getLength() {
        return 1;
    }

    public String getCName() {
        return "disp_" + getName();
    }

    public abstract DataType getDataType();

    public String getCMetaType() {
        return "display_meta_type_undefined";
    }

    public void updateSHA(MessageDigest md) {
        md.update(getName().getBytes());
//        md.update((byte)getDatatype().hashCode());
    }

    @Override
    public Display clone() throws CloneNotSupportedException {
        return (Display) super.clone();
    }

    @Override
    public List<Property> getEditableFields() {
        List<Property> l = new ArrayList<>();
        l.add(NOLABEL);
        return l;
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        l.add(NOLABEL);
        return l;
    }

    public Boolean getNoLabel() {
        return noLabel;
    }

    public void setNoLabel(Boolean noLabel) {
        this.noLabel = noLabel;
        firePropertyChange(NOLABEL, null, noLabel);
    }

}
