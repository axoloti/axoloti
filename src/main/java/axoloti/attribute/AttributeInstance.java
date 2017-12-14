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

import axoloti.SDFileReference;
import axoloti.atom.AtomDefinitionController;
import axoloti.atom.AtomInstance;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.object.AxoObjectInstance;
import static axoloti.utils.CharEscape.CharEscape;
import components.LabelComponent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class AttributeInstance<T extends AxoAttribute> extends AtomInstance<T> {

    @Attribute
    String attributeName;

    final AtomDefinitionController controller;

    AxoObjectInstance axoObj;
    LabelComponent lbl;

    AttributeInstance() {
        this.controller = null;
    }

    AttributeInstance(AtomDefinitionController controller, AxoObjectInstance axoObj1) {
        this.controller = controller;
        axoObj = axoObj1;
    }

    public abstract String CValue();

    public abstract void CopyValueFrom(AttributeInstance a1);

    public String GetCName() {
        return "attr_" + CharEscape(attributeName);
    }

    public AxoObjectInstance getObjectInstance() {
        return axoObj;
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

}
