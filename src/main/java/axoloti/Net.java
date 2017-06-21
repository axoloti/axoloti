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
package axoloti;

import axoloti.datatypes.DataType;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractModel;
import axoloti.outlets.OutletInstance;
import java.awt.Color;
import java.util.ArrayList;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "net")
public class Net extends AbstractModel {

    @ElementList(inline = true, required = false)
    ArrayList<OutletInstance> source;
    @ElementList(inline = true, required = false)
    ArrayList<InletInstance> dest = new ArrayList<>();
    boolean selected = false;

    public Net() {
        if (source == null) {
            source = new ArrayList<>();
        }
        if (dest == null) {
            dest = new ArrayList<>();
        }
    }

    public boolean isValidNet() {
        if (source.isEmpty()) {
            return false;
        }
        if (source.size() > 1) {
            return false;
        }
        if (dest.isEmpty()) {
            return false;
        }
        for (InletInstance s : dest) {
            if (!getDataType().IsConvertableToType(s.getDataType())) {
                return false;
            }
        }
        return true;
    }

    Color GetColor() {
        Color c = getDataType().GetColor();
        if (c == null) {
            c = Theme.getCurrentTheme().Cable_Default;
        }
        return c;
    }

    public DataType getDataType() {
        if (source.isEmpty()) {
            return null;
        }
        if (source.size() == 1) {
            return source.get(0).getDataType();
        }
        java.util.Collections.sort(source);
        DataType t = source.get(0).getDataType();
        return t;
    }

    public ArrayList<OutletInstance> GetSource() {
        return source;
    }

    public String CType() {
        DataType d = getDataType();
        if (d != null) {
            return d.CType();
        } else {
            return null;
        }
    }

    public ArrayList<OutletInstance> getSources() {
        return source;
    }

    public void setSources(ArrayList<OutletInstance> source) {
        ArrayList<OutletInstance> old_value = this.source;
        this.source = source;
        firePropertyChange(
                NetController.NET_SOURCES,
                old_value, source);
    }

    public ArrayList<InletInstance> getDestinations() {
        return dest;
    }

    public void setDestinations(ArrayList<InletInstance> dest) {
        ArrayList<InletInstance> old_value = this.dest;
        this.dest = dest;
        firePropertyChange(
                NetController.NET_DESTINATIONS,
                old_value, dest);
    }
}
