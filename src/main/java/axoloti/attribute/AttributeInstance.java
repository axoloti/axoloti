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
import axoloti.Theme;
import axoloti.atom.AtomInstance;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.object.AxoObjectInstance;
import static axoloti.utils.CharEscape.CharEscape;
import components.LabelComponent;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public abstract class AttributeInstance<T extends AxoAttribute> extends JPanel implements AtomInstance<T> {

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

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Theme.getCurrentTheme().Object_Default_Background);
        add(new LabelComponent(GetDefinition().getName()));
        setSize(getPreferredSize());
        if (attr.getDescription() != null) {
            setToolTipText(attr.getDescription());
        }
    }

    @Override
    public String getName() {
        return attributeName;
    }

    public abstract void Lock();

    public abstract void UnLock();

    public abstract String CValue();

    public abstract void CopyValueFrom(AttributeInstance a1);

    public String GetCName() {
        return "attr_" + CharEscape(attributeName);
    }

    @Override
    public AxoObjectInstance GetObjectInstance() {
        return axoObj;
    }

    @Override
    public T GetDefinition() {
        return attr;
    }

    public ArrayList<SDFileReference> GetDependendSDFiles() {
        return null;
    }

    public void Close() {
    }

    void SetDirty() {
        // propagate dirty flag to patch if there is one
        if (axoObj.getPatch() != null) {
            axoObj.getPatch().SetDirty();
        }
    }
}
