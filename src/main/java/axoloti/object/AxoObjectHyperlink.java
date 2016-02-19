/**
 * Copyright (C) 2015 Johannes Taelman
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
package axoloti.object;

import axoloti.Patch;
import java.awt.Point;

/**
 *
 * @author jtaelman
 */
public class AxoObjectHyperlink extends AxoObjectAbstract {

    public AxoObjectHyperlink() {
        super();
    }

    public AxoObjectHyperlink(String id, String sDescription) {
        super(id, sDescription);
    }

    @Override
    public AxoObjectInstanceHyperlink CreateInstance(Patch patch, String InstanceName1, Point location) {
        AxoObjectInstanceHyperlink o = new AxoObjectInstanceHyperlink(this, patch, InstanceName1, location);
        if (patch != null) {
            patch.objectinstances.add(o);
        }
        o.PostConstructor();
        return o;
    }
 
    @Override
    public String GenerateUUID() {
        return null;
    }
    
}
