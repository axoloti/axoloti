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
package axoloti.inlets;

import axoloti.MainFrame;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.objectviews.AxoObjectInstanceViewZombie;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.inlets.PInletInstanceZombieView;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewZombie;

/**
 *
 * @author Johannes Taelman
 */
public class InletInstanceZombie extends InletInstance {

    public InletInstanceZombie() {
    }

    public InletInstanceZombie(AxoObjectInstanceZombie obj, String name) {
        this.axoObj = obj;
        this.inletname = name;
        this.objname = obj.getInstanceName();
    }

    @Override
    public DataType getDataType() {
        return new axoloti.datatypes.DTZombie();
    }

    @Override
    public String GetLabel() {
        return inletname;
    }

    @Override
    public IInletInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new PInletInstanceZombieView(this, (PAxoObjectInstanceViewZombie) o);
        } else {
            return new InletInstanceZombieView(this, (AxoObjectInstanceViewZombie) o);
        }
    }

    @Override
    public IInletInstanceView createView(IAxoObjectInstanceView o) {
        IInletInstanceView inletInstanceView = getViewInstance(o);
        o.addInletInstanceView(inletInstanceView);
        inletInstanceView.PostConstructor();
        o.resizeToGrid();
        return inletInstanceView;
    }
}
