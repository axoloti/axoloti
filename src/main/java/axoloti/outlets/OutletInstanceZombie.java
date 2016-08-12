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

import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.objectviews.AxoObjectInstanceViewAbstract;

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
    public DataType GetDataType() {
        return new axoloti.datatypes.DTZombie();
    }

    @Override
    public String GetLabel() {
        return outletname;
    }

    @Override
    public OutletInstanceView ViewFactory(AxoObjectInstanceViewAbstract o) {
        return new OutletInstanceZombieView(this, o);
    }

    @Override
    public OutletInstanceView CreateView(AxoObjectInstanceViewAbstract o) {
        OutletInstanceView outletInstanceView = ViewFactory(o);
        o.add(outletInstanceView);
        o.resizeToGrid();
        return outletInstanceView;
    }
}
