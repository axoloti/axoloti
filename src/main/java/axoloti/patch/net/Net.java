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
package axoloti.patch.net;

import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractModel;
import axoloti.patch.PatchModel;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.preferences.Theme;
import axoloti.property.ListProperty;
import axoloti.property.Property;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "net")
public class Net extends AbstractModel<NetController> {

    @ElementList(inline = true, required = false)
    private List<OutletInstance> source = new ArrayList<>();
    @ElementList(inline = true, required = false)
    private List<InletInstance> dest = new ArrayList<>();

    private PatchModel parent;

    public Net() {
    }

    /*
        for (OutletInstance o : sources) {
            o.setConnected(true);
        }
        for (InletInstance i : dests) {
            i.setConnected(true);
        }
     */

    public Net(PatchModel parent, OutletInstance[] sources, InletInstance[] dests) {
        this.parent = parent;
        setDocumentRoot(parent.getDocumentRoot());
        this.source = Arrays.asList(sources);
        this.dest = Arrays.asList(dests);
    }

    public void validate() {
        if (source == null) {
            throw new Error("source is null, empty array required");
        }
        if (dest == null) {
            throw new Error("dest is null, empty array required");
        }
        if (dest.size() + source.size() < 2) {
            throw new Error("less than 2 iolets connected, should not exist");
        }
        for (int j = 0; j < dest.size(); j++) {
            InletInstance i = dest.get(j);
            IAxoObjectInstance o = i.getParent();
            if (o == null) {
                continue;
            }
            if (!o.getInletInstances().contains(i)) {
                String inletName = i.getName();
                InletInstance i2 = o.findInletInstance(inletName);
                if (i2 == null) {
                    throw new Error("detached net");
                }
                dest.set(j, i2);
            }
        }
        for (int j = 0; j < source.size(); j++) {
            OutletInstance i = source.get(j);
            IAxoObjectInstance o = i.getParent();
            if (o == null) {
                continue;
            }
            if (!o.getOutletInstances().contains(i)) {
                String outletName = i.getName();
                OutletInstance i2 = o.findOutletInstance(outletName);
                if (i2 == null) {
                    throw new Error("detached net");
                }
                source.set(j, i2);
            }
        }
    }

    public boolean isValidNet() {
        if (source == null) {
            return false;
        }
        if (source.size() != 1) {
            return false;
        }
        if (dest == null) {
            return false;
        }
        if (dest.isEmpty()) {
            return false;
        }
        for (InletInstance s : dest) {
            if (!getDataType().isConvertableToType(s.getDataType())) {
                return false;
            }
        }
        return true;
    }

    Color getColor() {
        Color c = getDataType().getColor();
        if (c == null) {
            c = Theme.getCurrentTheme().Cable_Default;
        }
        return c;
    }

    public DataType getDataType() {
        if (source == null) {
            return null;
        }
        if (source.isEmpty()) {
            return null;
        }
        if (source.size() == 1) {
            return source.get(0).getDataType();
        }
        OutletInstance first_outlet = java.util.Collections.min(source); // TODO: verify
        DataType t = first_outlet.getDataType();
        return t;
    }

    public String CType() {
        DataType d = getDataType();
        if (d != null) {
            return d.CType();
        } else {
            return null;
        }
    }

    public String getCName() {
        int i = getParent().getNets().indexOf(this);
        return "net" + i;
    }

    public boolean needsLatch() {
        // reads before last write on net
        int lastSource = 0;
        for (OutletInstance s : getSources()) {
            int i = getParent().getObjectInstances().indexOf(s.getParent());
            if (i > lastSource) {
                lastSource = i;
            }
        }
        int firstDest = java.lang.Integer.MAX_VALUE;
        for (InletInstance d : getDestinations()) {
            int i = getParent().getObjectInstances().indexOf(d.getParent());
            if (i < firstDest) {
                firstDest = i;
            }
        }
        return (firstDest <= lastSource);
    }

    public boolean isFirstOutlet(OutletInstance oi) {
        if (getSources().size() == 1) {
            return true;
        }
        for (IAxoObjectInstance o : getParent().getObjectInstances()) {
            for (OutletInstance i : o.getOutletInstances()) {
                if (getSources().contains(i)) {
                    // o is first objectinstance connected to this net
                    return oi == i;
                }
            }
        }
        Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "IsFirstOutlet: shouldn't get here");
        return false;
    }

    public final static ListProperty NET_SOURCES = new ListProperty("Sources", Net.class);
    public final static ListProperty NET_DESTINATIONS = new ListProperty("Destinations", Net.class);

    public List<OutletInstance> getSources() {
        return Collections.unmodifiableList(source);
    }

    public void setSources(List<OutletInstance> source) {
        List<OutletInstance> prev_value = this.source;
        this.source = source;
        firePropertyChange(
                NET_SOURCES,
                prev_value, source);
    }

    public List<InletInstance> getDestinations() {
        return Collections.unmodifiableList(dest);
    }

    public void setDestinations(List<InletInstance> dest) {
        List<InletInstance> old_value = this.dest;
        this.dest = dest;
        firePropertyChange(
                NET_DESTINATIONS,
                old_value, dest);
    }

    @Override
    public List<Property> getProperties() {
        List<Property> l = new ArrayList<>();
        l.add(NET_SOURCES);
        l.add(NET_DESTINATIONS);
        return l;
    }

    @Override
    public NetController createController() {
        return new NetController(this);
    }

    @Override
    public PatchModel getParent() {
        return parent;
    }

    public void setParent(PatchModel patchModel) {
        parent = patchModel;
    }

}
