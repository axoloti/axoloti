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

import axoloti.mvc.array.ArrayController;
import axoloti.object.AxoObject;
import axoloti.object.ObjectController;
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletTypes;

/**
 *
 * @author jtaelman
 */
public class OutletDefinitionsEditorPanel extends AtomDefinitionsEditor<Outlet> {

    public OutletDefinitionsEditorPanel(ObjectController controller) {
        super(controller, AxoObject.OBJ_OUTLETS, OutletTypes.getTypes());
    }

    @Override
    String getDefaultName() {
        return "o";
    }

    @Override
    String getAtomTypeName() {
        return "outlet";
    }

    @Override
    ArrayController getTController() {
        return getController().outlets;
    }

}
