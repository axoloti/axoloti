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
import axoloti.StringRef;
import axoloti.TextEditor;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.attributeviews.AttributeInstanceViewTextEditor;
import axoloti.attributeviews.IAttributeInstanceView;
import axoloti.object.AxoObjectInstance;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.attributeviews.PAttributeInstanceViewTextEditor;
import axoloti.piccolo.objectviews.PAxoObjectInstanceView;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTextEditor extends AttributeInstanceString<AxoAttributeTextEditor> {

    public TextEditor editor;

    final StringRef sRef = new StringRef();

    @Element(data = true, name = "sText", required = false)
    String getSText() {
        return sRef.s;
    }

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

    @Override
    public String CValue() {
        return sRef.s;
    }

    @Override
    public String getString() {
        return sRef.s;
    }

    @Override
    public void setString(String sText) {
        sRef.s = sText;
    }

    @Override
    public IAttributeInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PAttributeInstanceViewTextEditor(this, (PAxoObjectInstanceView) o);
        } else {
            return new AttributeInstanceViewTextEditor(this, (AxoObjectInstanceView) o);
        }
    }

    public StringRef getStringRef() {
        return sRef;
    }

    @Override
    public void Close() {
        if (editor != null) {
            editor.Close();
        }
        editor = null;
    }
}
