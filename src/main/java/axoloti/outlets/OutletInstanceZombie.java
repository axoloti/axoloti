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
package axoloti.outlets;

import axoloti.MainFrame;
import static axoloti.PatchViewType.PICCOLO;
import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.objectviews.AxoObjectInstanceViewZombie;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewZombie;
import axoloti.piccolo.outlets.POutletInstanceZombieView;

/**
 *
 * @author Johannes Taelman
 */
public class OutletInstanceZombie extends OutletInstance {

    public OutletInstanceZombie() {
    }

    public OutletInstanceZombie(AxoObjectInstanceZombie obj, String name) {
        this.axoObj = obj;
        this.outletname = name;
        this.objname = obj.getInstanceName();
    }

    @Override
    public DataType getDataType() {
        return new axoloti.datatypes.DTZombie();
    }

    @Override
    public String GetLabel() {
        return outletname;
    }

    @Override
    public IOutletInstanceView getViewInstance(IAxoObjectInstanceView o) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            return new POutletInstanceZombieView(this, (PAxoObjectInstanceViewZombie) o);
        } else {
            return new OutletInstanceZombieView(this, (AxoObjectInstanceViewZombie) o);
        }
    }

    @Override
    public IOutletInstanceView createView(IAxoObjectInstanceView o) {
        IOutletInstanceView outletInstanceView = getViewInstance(o);
        o.addOutletInstanceView(outletInstanceView);
        outletInstanceView.PostConstructor();
        return outletInstanceView;
    }
}
