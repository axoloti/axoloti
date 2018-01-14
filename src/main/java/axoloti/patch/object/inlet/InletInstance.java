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
package axoloti.patch.object.inlet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.inlet.Inlet;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.iolet.IoletInstance;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "dest")
public class InletInstance<T extends Inlet> extends IoletInstance<T> {

    @Attribute(name = "inlet", required = false)
    protected String inletname;

    public InletInstance() {
        super();
    }

    public InletInstance(String objname, String inletname) {
        super(objname);
        this.inletname = inletname;
    }

    public InletInstance(AtomDefinitionController inletController, final IAxoObjectInstance axoObj) {
        super(inletController, axoObj);
    }

    @Override
    public String getName() {
        return inletname;
    }

    @Override
    public void setName(String inletname) {
        String preVal = this.inletname;
        this.inletname = inletname;
        firePropertyChange(NAME, preVal, inletname);
    }

    public boolean isSource() {
        return false;
    }
}
