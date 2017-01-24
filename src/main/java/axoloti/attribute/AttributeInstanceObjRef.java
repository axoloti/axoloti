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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persist;

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
        this.axoObj = axoObj1;
    }

    String valueBeforeAdjustment = "";

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFObjName = new JTextField(objName);
        Dimension d = TFObjName.getSize();
        d.width = 92;
        d.height = 22;
        TFObjName.setFont(Constants.FONT);
        TFObjName.setMaximumSize(d);
        TFObjName.setMinimumSize(d);
        TFObjName.setPreferredSize(d);
        TFObjName.setSize(d);
        add(TFObjName);
        TFObjName.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
                if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
                    transferFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }
        });
        TFObjName.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                objName = TFObjName.getText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        TFObjName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                valueBeforeAdjustment = TFObjName.getText();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFObjName.getText().equals(valueBeforeAdjustment)) {
                    SetDirty();
                }
            }
        });
    }

    @Override
    public String CValue() {
        String o = objName;
        if (o == null) {
            o = "";
        }
        if (o.isEmpty()) {
            Logger.getLogger(AttributeInstanceObjRef.class.getName()).log(Level.SEVERE, "incomplete object reference attribute in {0}", GetObjectInstance().getInstanceName());
        }
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

    @Persist
    public void Persist() {
        if (objName == null) {
            objName = "";
        }
    }
}
