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

import axoloti.StringRef;
import axoloti.TextEditor;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.object.AxoObjectInstance;
import components.ButtonComponent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JLabel;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTextEditor extends AttributeInstanceString<AxoAttributeTextEditor> {

    final StringRef sRef = new StringRef();

    @Element(data = true, name = "sText", required = false)
    String getSText() {
        return sRef.s;
    }
    ButtonComponent bEdit;
    JLabel vlabel;
    TextEditor editor;

    public AttributeInstanceTextEditor() {
    }

    public AttributeInstanceTextEditor(@Element(name = "sText", required = false) String s) {
        if (s == null) {
            sRef.s = "";
        } else {
            sRef.s = s;
        }
    }

    public AttributeInstanceTextEditor(AxoAttributeTextEditor param, AxoObjectInstance axoObj1) {
        super(param, axoObj1);
    }

    String valueBeforeAdjustment = "";

    void showEditor() {
        if (editor == null) {
            editor = new TextEditor(sRef, GetObjectInstance().getPatch().getPatchframe());
            editor.setTitle(GetObjectInstance().getInstanceName() + "/" + attr.getName());
            editor.addWindowFocusListener(new WindowFocusListener() {

                @Override
                public void windowGainedFocus(WindowEvent e) {
                    valueBeforeAdjustment = sRef.s;
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    if (!valueBeforeAdjustment.equals(sRef.s)) {
                        SetDirty();
                    }
                }
            });
        }
        editor.setState(java.awt.Frame.NORMAL);
        editor.setVisible(true);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        bEdit = new ButtonComponent("Edit");
        add(bEdit);
        bEdit.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                showEditor();
            }
        });
    }

    @Override
    public String CValue() {
        return sRef.s;
    }

    @Override
    public void Lock() {
        if (bEdit != null) {
            bEdit.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (bEdit != null) {
            bEdit.setEnabled(true);
        }
    }

    @Override
    public String getString() {
        return sRef.s;
    }

    @Override
    public void setString(String sText) {
        sRef.s = sText;
        if (editor != null) {
            editor.SetText(sText);
        }
    }

    @Override
    public void Close() {
        if (editor != null) {
            editor.Close();
        }
        editor = null;
    }
}
