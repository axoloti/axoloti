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

import axoloti.datatypes.DataType;
import axoloti.object.AxoObjectInstanceZombie;
import components.LabelComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

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
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        jack = new components.JackInputComponent(this);
        add(jack);
        add(Box.createHorizontalStrut(2));
        add(new LabelComponent(this.inletname));
        add(Box.createHorizontalGlue());
    }

    @Override
    public DataType GetDataType() {
        return new axoloti.datatypes.DTZombie();
    }

    @Override
    public String GetLabel() {
        return inletname;
    }
}
