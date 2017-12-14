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
package axoloti.atom;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.object.ObjectController;
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
abstract public class AtomDefinition extends AbstractModel {

    @Attribute
    String name;
    @Attribute(required = false)
    public String description;

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

    // FIXME: violating the MVC pattern for now and use a singleton controller for this model
    private AtomDefinitionController atomController = null;

    public AtomDefinitionController createController(AbstractDocumentRoot documentRoot, AbstractController parent) {
        if (atomController == null) {
            atomController = new AtomDefinitionController(this, documentRoot, (ObjectController) parent);
        }
        return atomController;
    }
}
