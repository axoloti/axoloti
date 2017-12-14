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

import axoloti.TextEditor;
import axoloti.atom.AtomDefinitionController;
import axoloti.attributedefinition.AxoAttributeTextEditor;
import axoloti.object.AxoObjectInstance;
import org.simpleframework.xml.Element;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceTextEditor extends AttributeInstanceString<AxoAttributeTextEditor> {

    public TextEditor editor;

    @Element(data = true, required = false)
    String sText;

    AttributeInstanceTextEditor() {
        super();
    }

    AttributeInstanceTextEditor(AtomDefinitionController controller, AxoObjectInstance axoObj1) {
        super(controller, axoObj1);
    }

    @Override
    public String CValue() {
        return sText;
    }

    @Override
    public String getValue() {
        return sText;
    }

    @Override
    public void setValue(String sText) {
        this.sText = sText;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (editor != null) {
            editor.Close();
        }
        editor = null;
    }

}
