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

/**
 *
 * @author Johannes Taelman
 */
import axoloti.atom.AtomDefinition;
import axoloti.attribute.AttributeInstance;
import axoloti.object.AxoObjectInstance;
import axoloti.utils.CharEscape;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;

public abstract class AxoAttribute implements AtomDefinition, Cloneable {

    @Attribute
    String name;
    @Attribute(required = false)
    public String description;

    public AxoAttribute() {
    }

    public AxoAttribute(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public AttributeInstance CreateInstance(AxoObjectInstance o) {
        AttributeInstance pi = InstanceFactory(o);
        o.add(pi);
        pi.PostConstructor();
        return pi;
    }

    public AttributeInstance CreateInstance(AxoObjectInstance o, AttributeInstance a) {
        AttributeInstance pi = InstanceFactory(o);
        if (a != null) {
            pi.CopyValueFrom(a);
        }
        o.add(pi);
        pi.PostConstructor();
        return pi;
    }

    public abstract AttributeInstance InstanceFactory(AxoObjectInstance o);

    public void updateSHA(MessageDigest md) {
        md.update(name.getBytes());
    }

    public String GetCName() {
        return "attr_" + CharEscape.CharEscape(name);
    }

    @Override
    public AxoAttribute clone() throws CloneNotSupportedException {
        return (AxoAttribute) super.clone();
    }

    @Override
    public List<String> getEditableFields() {
        return new ArrayList<String>();
    }
}
