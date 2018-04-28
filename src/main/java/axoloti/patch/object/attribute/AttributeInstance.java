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

import axoloti.mvc.AbstractController;
import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.attribute.AxoAttribute;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.atom.AtomInstance;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.target.fs.SDFileReference;
import static axoloti.utils.CharEscape.CharEscape;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class AttributeInstance<T extends AxoAttribute> extends AtomInstance<T> {

    @Attribute
    String attributeName;

    private final AtomDefinitionController controller;

    AttributeInstance() {
        this.controller = null;
    }

    AttributeInstance(AtomDefinitionController controller, AxoObjectInstance axoObj1) {
        this.controller = controller;
        setParent(axoObj1);
    }

    public static final ObjectProperty ATTR_VALUE = new ObjectProperty("Value", Object.class, AttributeInstance.class);

    @Override
    public List<Property> getProperties() {
        List<Property> l = super.getProperties();
        l.add(ATTR_VALUE);
        return l;
    }

    public abstract String CValue();

    public abstract void CopyValueFrom(AttributeInstance a1);

    public String GetCName() {
        return "attr_" + CharEscape(attributeName);
    }

    @Override
    public T getModel() {
        return (T) getController().getModel();
    }

    public ArrayList<SDFileReference> GetDependendSDFiles() {
        return null;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoAttribute.NAME.is(evt)) {
            setName((String) evt.getNewValue());
        }
    }

    @Override
    public AtomDefinitionController getController() {
        return controller;
    }

    public String getName() {
        return attributeName;
    }

    public void setName(String attributeName) {
        String preVal = this.attributeName;
        this.attributeName = attributeName;
        firePropertyChange(NAME, preVal, attributeName);
    }

    abstract public Object getValue();

    abstract public void setValue(Object value);


    @Override
    public AbstractController createController() {
        return new AttributeInstanceController(this);
    }

}
