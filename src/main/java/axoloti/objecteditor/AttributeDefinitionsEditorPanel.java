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
package axoloti.objecteditor;

import axoloti.attributedefinition.AttributeTypes;
import axoloti.attributedefinition.AxoAttribute;
import axoloti.mvc.array.ArrayController;
import axoloti.mvc.array.ArrayModel;

/**
 *
 * @author jtaelman
 */
public class AttributeDefinitionsEditorPanel extends AtomDefinitionsEditor<AxoAttribute> {

    public AttributeDefinitionsEditorPanel(ArrayController controller) {
        super(controller, AttributeTypes.getTypes());
    }

    @Override
    ArrayModel<AxoAttribute> GetAtomDefinitions() {
        return obj.attributes;
    }

    @Override
    String getDefaultName() {
        return "a";
    }
}
