/**
 * Copyright (C) 2016 Johannes Taelman
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
package axoloti.object.atom;

import axoloti.mvc.AbstractModel;
import axoloti.object.AxoObject;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.property.StringPropertyNull;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author jtaelman
 */
/**
 * An Axoloti Object Definition is composed out of AtomDefinition
 *
 */
abstract public class AtomDefinition extends AbstractModel<AtomDefinitionController> {

    @Attribute
    private String name;
    @Attribute(required = false)
    private String description;

    private AxoObject parent;

    public AtomDefinition() {
    }

    public AtomDefinition(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static final Property NAME = new StringProperty("Name", AtomDefinition.class);
    public static final Property DESCRIPTION = new StringPropertyNull("Description", AtomDefinition.class);

    @Override
    public List<Property> getProperties() {
        ArrayList<Property> l = new ArrayList<>();
        l.add(NAME);
        l.add(DESCRIPTION);
        return l;
    }

    final public String getName() {
        return name;
    }

    final public void setName(String name) {
        String old_value = this.name;
        this.name = name;
        firePropertyChange(
                NAME,
                old_value, name);
    }

    final public String getDescription() {
        if (description == null) {
            return "";
        }
        return description;
    }

    final public void setDescription(String description) {
        String old_value = this.description;
        this.description = description;
        firePropertyChange(
                DESCRIPTION,
                old_value, description);
    }

    abstract public String getTypeName();

    abstract public List<Property> getEditableFields();

    @Override
    protected AtomDefinitionController createController() {
        return new AtomDefinitionController(this);
    }

    @Override
    public AxoObject getParent() {
        if (parent == null) {
            throw new Error("AtomDefinition: no parent? " + this.getClass().toString() + " " + toString());
        }
        return parent;
    }

    public void setParent(AxoObject p) {
        parent = p;
    }
}
