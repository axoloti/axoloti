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

import axoloti.SubPatchMode;
import axoloti.attributedefinition.AxoAttributeObjRef;
import axoloti.object.AxoObjectInstance;
import axoloti.utils.CharEscape;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceObjRef extends AttributeInstanceString<AxoAttributeObjRef> {

    @Attribute(name = "obj")
    String objName = "";
    JTextField TFObjName;
    JLabel vlabel;

    public AttributeInstanceObjRef() {
    }

    public AttributeInstanceObjRef(AxoAttributeObjRef param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFObjName = new JTextField(objName);
        Dimension d = TFObjName.getSize();
        d.width = 92;
        d.height = 22;
        TFObjName.setFont(Constants.font);
        TFObjName.setMaximumSize(d);
        TFObjName.setMinimumSize(d);
        TFObjName.setPreferredSize(d);
        TFObjName.setSize(d);
        add(TFObjName);
        TFObjName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                objName = TFObjName.getText();
                System.out.println("objref change " + objName);
            }
        });
        TFObjName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                objName = TFObjName.getText();
                System.out.println("objref change " + objName);
            }
        });
    }

    @Override
    public String CValue() {
        String o = objName;
        String o2 = "parent->";

        if ((o.length() > 3) && (o.substring(0, 3).equals("../"))
                && ((GetObjectInstance().patch.getSettings().subpatchmode == SubPatchMode.polyphonic)
                || (GetObjectInstance().patch.getSettings().subpatchmode == SubPatchMode.polychannel)
                || (GetObjectInstance().patch.getSettings().subpatchmode == SubPatchMode.polyexpression))) {
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
    public void Lock() {
        if (TFObjName != null) {
            TFObjName.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFObjName != null) {
            TFObjName.setEnabled(true);
        }
    }

    @Override
    public String getString() {
        return objName;
    }

    @Override
    public void setString(String objName) {
        this.objName = objName;
        if (TFObjName != null) {
            TFObjName.setText(objName);
        }
    }
}
