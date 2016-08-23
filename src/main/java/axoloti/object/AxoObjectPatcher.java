/**
 * Copyright (C) 2013, 2014 Johannes Taelman
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johannes Taelman
 */
public class AxoObjectPatcher extends AxoObject {

    public AxoObjectPatcher() {
    }

    public AxoObjectPatcher(String id, String sDescription) {
        super(id, sDescription);
    }

    @Override
    public AxoObjectInstancePatcher CreateInstance(Patch patch, String InstanceName1, Point location) {
        if ((sMidiCCCode != null)
                || (sMidiAllNotesOffCode != null)
                || (sMidiCCCode != null)
                || (sMidiChannelPressure != null)
                || (sMidiNoteOffCode != null)
                || (sMidiNoteOnCode != null)
                || (sMidiPBendCode != null)
                || (sMidiResetControllersCode != null)) {
            Logger.getLogger(AxoObject.class.getName()).log(Level.SEVERE, "Object {0} uses obsolete midi handling. If it is a subpatch-generated object, open and save the original patch again!", InstanceName1);
        }

        AxoObjectInstancePatcher o = new AxoObjectInstancePatcher(this, patch, InstanceName1, location);
        if (patch != null) {
            patch.objectinstances.add(o);
        }
        o.PostConstructor();
        return o;
    }
    
}
