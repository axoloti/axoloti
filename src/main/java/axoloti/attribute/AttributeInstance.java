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
import axoloti.atom.AtomInstance;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.attributeviews.AttributeInstanceView;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import static axoloti.utils.CharEscape.CharEscape;
import components.LabelComponent;
import java.util.ArrayList;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class AttributeInstance<T extends AxoAttribute> implements AtomInstance<T> {

    @Attribute
    String attributeName;

    T attr;

    AxoObjectInstance axoObj;
    LabelComponent lbl;

    public AttributeInstance() {
    }

    public AttributeInstance(T attr, AxoObjectInstance axoObj1) {
        this.attr = attr;
        axoObj = axoObj1;
        attributeName = attr.getName();
    }

    public abstract String CValue();

    public abstract void CopyValueFrom(AttributeInstance a1);

    public String GetCName() {
        return "attr_" + CharEscape(attributeName);
    }

    @Override
    public AxoObjectInstance getObjectInstance() {
        return axoObj;
    }

    @Override
    public T getDefinition() {
        return attr;
    }

    public ArrayList<SDFileReference> GetDependendSDFiles() {
        return null;
    }

    public void Close() {
    }

    public abstract AttributeInstanceView ViewFactory(AxoObjectInstanceView o);

    public AttributeInstanceView CreateView(AxoObjectInstanceView o) {
        AttributeInstanceView pi = ViewFactory(o);
        o.add(pi);
        pi.PostConstructor();
        return pi;
    }

    public String getAttributeName() {
        return attributeName;
    }

    void SetDirty() {
        // propagate dirty flag to patch if there is one
        if (getObjectInstance().getPatchModel() != null) {
            getObjectInstance().getPatchModel().SetDirty();
        }
    }
}
