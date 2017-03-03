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

import axoloti.MainFrame;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.SubPatchMode;
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.attributeviews.AttributeInstanceViewObjRef;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.attributeviews.PAttributeInstanceViewObjRef;
import axoloti.piccolo.objectviews.PAxoObjectInstanceView;
import axoloti.utils.CharEscape;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceObjRef extends AttributeInstanceString<AxoAttributeObjRef> {

    @Attribute(name = "obj")
    String objName = "";

    private AxoObjectInstance axoObj;

    public AttributeInstanceObjRef() {
    }

    public AttributeInstanceObjRef(AxoAttributeObjRef param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
        this.axoObj = axoObj1;
    }

    @Override
    public String CValue() {
        String o = objName;
        if (o == null) {
            o = "";
        }
        if (o.isEmpty()) {
            Logger.getLogger(AttributeInstanceObjRef.class.getName()).log(Level.SEVERE, "incomplete object reference attribute in {0}", getObjectInstance().getInstanceName());
        }
        String o2 = "parent->";

        if ((o.length() > 3) && (o.substring(0, 3).equals("../"))
                && ((getObjectInstance().getPatchModel().getSettings().subpatchmode == SubPatchMode.polyphonic)
                || (getObjectInstance().getPatchModel().getSettings().subpatchmode == SubPatchMode.polychannel)
                || (getObjectInstance().getPatchModel().getSettings().subpatchmode == SubPatchMode.polyexpression))) {
            o2 = o2 + "common->";
        }

        while ((o.length() > 3) && (o.substring(0, 3).equals("../"))) {
            o2 = o2 + "parent->";
            o = o.substring(3);
        }
        String ao[] = o.split("/");
        String o3 = "";
        for (int i = 1; i < ao.length; i++) {
            o3 = o3 + ".instance" + CharEscape.CharEscape(ao[i]) + "_i";
        }
        o2 = o2 + "instance" + CharEscape.CharEscape(ao[0]) + "_i" + o3;
        return o2;
    }

    @Override
    public String getString() {
        return objName;
    }

    @Override
    public void setString(String objName) {
        this.objName = objName;
    }

    @Override
    public IAttributeInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PAttributeInstanceViewObjRef(this, (PAxoObjectInstanceView) o);
        } else {
            return new AttributeInstanceViewObjRef(this, (AxoObjectInstanceView) o);
        }
    }

    @Persist
    public void Persist() {
        if (objName == null) {
            objName = "";
        }
    }
}
