/**
 * Copyright (C) 2013, 2014, 2015 Johannes Taelman
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
package axoloti.patch.object.outlet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import axoloti.object.atom.AtomDefinition;
import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.outlet.Outlet;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.iolet.IoletInstance;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "source")
public class OutletInstance<T extends Outlet> extends IoletInstance<T> {

    @Attribute(name = "outlet", required = false)
    String outletname;

    public OutletInstance() {
        super();
    }

    public OutletInstance(String objname, String outletname) {
        super(objname);
        this.outletname = outletname;
    }

    public OutletInstance(AtomDefinitionController outletController, IAxoObjectInstance axoObj) {
        super(outletController, axoObj);
    }

    @Override
    public String getName() {
        return outletname;
    }

    @Override
    public void setName(String outletname) {
        String preVal = this.outletname;
        this.outletname = outletname;
        firePropertyChange(AtomDefinition.NAME, preVal, outletname);
    }

    public boolean isSource() {
        return true;
    }
}
