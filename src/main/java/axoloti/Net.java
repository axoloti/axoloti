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

import static axoloti.PatchViewType.PICCOLO;
import axoloti.datatypes.DataType;
import axoloti.inlets.InletInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.outlets.OutletInstance;
import axoloti.piccolo.PNetView;
import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.*;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "net")
public class Net {

    @ElementList(inline = true, required = false)
    ArrayList<OutletInstance> source;
    @ElementList(inline = true, required = false)
    ArrayList<InletInstance> dest = new ArrayList<>();
    public PatchModel patchModel;
    boolean selected = false;

    public Net() {
        if (source == null) {
            source = new ArrayList<>();
        }
        if (dest == null) {
            dest = new ArrayList<>();
        }
    }

    public Net(PatchModel patchModel) {
        this();
        this.patchModel = patchModel;
    }

    public void PostConstructor() {
        // InletInstances and OutletInstances actually already exist, need to replace dummies with the real ones
        ArrayList<OutletInstance> source2 = new ArrayList<>();
        for (OutletInstance i : source) {
            String objname = i.getObjname();
            String outletname = i.getOutletname();

            AxoObjectInstanceAbstract o = patchModel.GetObjectInstance(objname);
            if (o == null) {
                Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "could not resolve net source obj : {0}::{1}", new Object[]{i.getObjname(), i.getOutletname()});
                patchModel.nets.remove(this);
                return;
            }
            OutletInstance r = o.GetOutletInstance(outletname);
            if (r == null) {
                Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "could not resolve net source outlet : {0}::{1}", new Object[]{i.getObjname(), i.getOutletname()});
                patchModel.nets.remove(this);
                return;
            }
            source2.add(r);
        }
        ArrayList<InletInstance> dest2 = new ArrayList<>();
        for (InletInstance i : dest) {
            String objname = i.getObjname();
            String inletname = i.getInletname();
            AxoObjectInstanceAbstract o = patchModel.GetObjectInstance(objname);
            if (o == null) {
                Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "could not resolve net dest obj :{0}::{1}", new Object[]{i.getObjname(), i.getInletname()});
                patchModel.nets.remove(this);
                return;
            }
            InletInstance r = o.GetInletInstance(inletname);
            if (r == null) {
                Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "could not resolve net dest inlet :{0}::{1}", new Object[]{i.getObjname(), i.getInletname()});
                patchModel.nets.remove(this);
                return;
            }
            dest2.add(r);
        }
        source = source2;
        dest = dest2;
    }

    public void connectInlet(InletInstance inlet) {
        if (inlet.getObjectInstance().patchModel != patchModel) {
            return;
        }
        dest.add(inlet);
    }

    public void connectOutlet(OutletInstance outlet) {
        if (outlet.getObjectInstance().patchModel == patchModel) {
            source.add(outlet);
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

    public boolean NeedsLatch() {
        // reads before last write on net
        int lastSource = 0;
        for (OutletInstance s : source) {
            int i = patchModel.objectinstances.indexOf(s.getObjectInstance());
            if (i > lastSource) {
                lastSource = i;
            }
        }
        int firstDest = java.lang.Integer.MAX_VALUE;
        for (InletInstance d : dest) {
            int i = patchModel.objectinstances.indexOf(d.getObjectInstance());
            if (i < firstDest) {
                firstDest = i;
            }
        }
        return (firstDest <= lastSource);
    }

    public boolean IsFirstOutlet(OutletInstance oi) {
        if (source.size() == 1) {
            return true;
        }
        for (AxoObjectInstanceAbstract o : patchModel.objectinstances) {
            for (OutletInstance i : o.getOutletInstances()) {
                if (source.contains(i)) {
                    // o is first objectinstance connected to this net
                    return oi == i;
                }
            }
        }
        Logger.getLogger(Net.class.getName()).log(Level.SEVERE, "IsFirstOutlet: shouldn't get here");
        return false;
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

    public String CName() {
        int i = patchModel.nets.indexOf(this);
        return "net" + i;
    }

    public INetView createView(PatchView patchView) {
        if (MainFrame.prefs.getPatchViewType() == PICCOLO) {
            INetView n = new PNetView(this, (PatchViewPiccolo) patchView);
            n.PostConstructor();
            return n;
        } else {
            INetView n = new NetView(this, (PatchViewSwing) patchView);
            n.PostConstructor();
            return n;
        }
    }

    public void setPatchModel(PatchModel patchModel) {
        this.patchModel = patchModel;
    }
}
