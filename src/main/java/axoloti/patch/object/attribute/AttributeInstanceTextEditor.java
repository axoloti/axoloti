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

import axoloti.abstractui.IAbstractEditor;
import axoloti.object.attribute.AxoAttributeTextEditor;
import axoloti.patch.object.AxoObjectInstance;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTextEditor extends AttributeInstanceString<AxoAttributeTextEditor> {

    private IAbstractEditor editor;

    @Element(data = true, required = false)
    private String sText;

    AttributeInstanceTextEditor() {
        super();
    }

    AttributeInstanceTextEditor(AxoAttributeTextEditor attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
    }

    @Override
    public String CValue() {
        if (sText == null) {
            return "";
        } else {
            return sText;
        }
    }

    @Override
    public String getValue() {
        return sText;
    }

    @Override
    protected void setValueString(String sText) {
        String oldvalue = this.sText;
        this.sText = sText;
        firePropertyChange(
                ATTR_VALUE,
                oldvalue, sText);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (editor != null) {
            editor.close();
        }
        editor = null;
    }

    public IAbstractEditor getEditor() {
        return editor;
    }

    public void setEditor(IAbstractEditor editor) {
        this.editor = editor;
    }

}
