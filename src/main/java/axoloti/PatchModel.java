/**
 * Copyright (C) 2013 - 2016 Johannes Taelman
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

import axoloti.attribute.AttributeInstance;
import axoloti.attributedefinition.AxoAttributeComboBox;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.InletBool32;
import axoloti.inlets.InletCharPtr32;
import axoloti.inlets.InletFrac32;
import axoloti.inlets.InletFrac32Buffer;
import axoloti.inlets.InletInstance;
import axoloti.inlets.InletInt32;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectFile;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectInstanceComment;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.object.AxoObjectZombie;
import axoloti.object.AxoObjects;
import axoloti.outlets.OutletBool32;
import axoloti.outlets.OutletCharPtr32;
import axoloti.outlets.OutletFrac32;
import axoloti.outlets.OutletFrac32Buffer;
import axoloti.outlets.OutletInstance;
import axoloti.outlets.OutletInt32;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.Constants;
import axoloti.utils.Preferences;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Complete;
import org.simpleframework.xml.core.Persist;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.core.Validate;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author Johannes Taelman
 */
@Root
public class PatchModel {

    @Attribute(required = false)
    String appVersion;
    public @ElementListUnion({
        @ElementList(entry = "obj", type = AxoObjectInstance.class, inline = true, required = false),
        @ElementList(entry = "patcher", type = AxoObjectInstancePatcher.class, inline = true, required = false),
        @ElementList(entry = "patchobj", type = AxoObjectInstancePatcherObject.class, inline = true, required = false),
        @ElementList(entry = "comment", type = AxoObjectInstanceComment.class, inline = true, required = false),
        @ElementList(entry = "hyperlink", type = AxoObjectInstanceHyperlink.class, inline = true, required = false),
        @ElementList(entry = "zombie", type = AxoObjectInstanceZombie.class, inline = true, required = false)})
    ArrayList<AxoObjectInstanceAbstract> objectinstances = new ArrayList<AxoObjectInstanceAbstract>();
    @ElementList(name = "nets")
    public ArrayList<Net> nets = new ArrayList<Net>();
    @Element(required = false)
    PatchSettings settings;
    @Element(required = false, data = true)
    String notes = "";
    @Element(required = false)
    Rectangle windowPos;
    private String FileNamePath;
    ArrayList<ParameterInstance> ParameterInstances = new ArrayList<ParameterInstance>();
    ArrayList<DisplayInstance> DisplayInstances = new ArrayList<DisplayInstance>();
    private ArrayList<Modulator> Modulators = new ArrayList<Modulator>();
    private boolean dirty = false;
    @Element(required = false)
    private String helpPatch;

    private ArrayList<ModelChangedListener> modelChangedListeners = new ArrayList<ModelChangedListener>();

    private boolean loadingUndoState = false;

    // patch this patch is contained in
    private PatchModel container = null;
    private AxoObjectInstanceAbstract controllerinstance;

    public boolean presetUpdatePending = false;

    boolean locked = false;

    private List<String> undoStates = new ArrayList<String>();
    public int currentState = 0;

    static public class PatchVersionException
            extends RuntimeException {

        PatchVersionException(String msg) {
            super(msg);
        }
    }

    private static final int AVX = getVersionX(Version.AXOLOTI_SHORT_VERSION),
            AVY = getVersionY(Version.AXOLOTI_SHORT_VERSION),
            AVZ = getVersionZ(Version.AXOLOTI_SHORT_VERSION);

    private static int getVersionX(String vS) {
        if (vS != null) {
            int i = vS.indexOf('.');
            if (i > 0) {
                String v = vS.substring(0, i);
                try {
                    return Integer.valueOf(v);
                } catch (NumberFormatException e) {
                }
            }
        }
        return -1;
    }

    private static int getVersionY(String vS) {
        if (vS != null) {
            int i = vS.indexOf('.');
            if (i > 0) {
                int j = vS.indexOf('.', i + 1);
                if (j > 0) {
                    String v = vS.substring(i + 1, j);
                    try {
                        return Integer.valueOf(v);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return -1;
    }

    private static int getVersionZ(String vS) {
        if (vS != null) {
            int i = vS.indexOf('.');
            if (i > 0) {
                int j = vS.indexOf('.', i + 1);
                if (j > 0) {
                    String v = vS.substring(j + 1);
                    try {
                        return Integer.valueOf(v);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return -1;
    }

    @Validate
    public void Validate() {
        // called after deserialializtion, stops validation
        if (appVersion != null
                && !appVersion.equals(Version.AXOLOTI_SHORT_VERSION)) {
            int vX = getVersionX(appVersion);
            int vY = getVersionY(appVersion);
            int vZ = getVersionZ(appVersion);

            if (AVX > vX) {
                return;
            }
            if (AVX == vX) {
                if (AVY > vY) {
                    return;
                }
                if (AVY == vY) {
                    if (AVZ > vZ) {
                        return;
                    }
                    if (AVZ == vZ) {
                        return;
                    }
                }
            }

            throw new PatchVersionException(appVersion);
        }
    }

    @Complete
    public void Complete() {
        // called after deserialializtion
    }

    @Persist
    public void Persist() {
        // called prior to serialization
        appVersion = Version.AXOLOTI_SHORT_VERSION;
    }

    public PatchSettings getSettings() {
        return settings;
    }

    public void setFileNamePath(String FileNamePath) {
        this.FileNamePath = FileNamePath;
    }

    public String getFileNamePath() {
        return FileNamePath;
    }

    public PatchModel() {
        super();
    }

    public void PostContructor() {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            o.patchModel = this;
            AxoObjectAbstract t = o.resolveType();
            if ((t != null) && (t.providesModulationSource())) {

                Modulator[] m = t.getModulators();
                if (Modulators == null) {
                    Modulators = new ArrayList<Modulator>();
                }
                for (Modulator mm : m) {
                    mm.objinst = o;
                    Modulators.add(mm);
                }
            }
        }

        ArrayList<AxoObjectInstanceAbstract> obj2 = (ArrayList<AxoObjectInstanceAbstract>) objectinstances.clone();
        for (AxoObjectInstanceAbstract o : obj2) {
            AxoObjectAbstract t = o.getType();
            if ((t != null) && (!t.providesModulationSource())) {
                o.patchModel = this;
                //System.out.println("Obj added " + o.getInstanceName());
            } else if (t == null) {
                objectinstances.remove(o);
                AxoObjectInstanceZombie zombie = new AxoObjectInstanceZombie(new AxoObjectZombie(), this, o.getInstanceName(), new Point(o.getX(), o.getY()));
                zombie.patchModel = this;
                zombie.typeName = o.typeName;
                objectinstances.add(zombie);
            }
        }
        ArrayList<Net> nets2 = (ArrayList<Net>) nets.clone();
        for (Net n : nets2) {
            n.patchModel = this;
        }
        if (settings == null) {
            settings = new PatchSettings();
        }
        ClearDirty();
    }

    public ArrayList<ParameterInstance> getParameterInstances() {
        return ParameterInstances;
    }

    public AxoObjectInstanceAbstract GetObjectInstance(String n) {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (n.equals(o.getInstanceName())) {
                return o;
            }
        }
        return null;
    }

    public void ClearDirty() {
        dirty = false;
    }

    public void setDirty() {
        notifyModelChangedListeners();
        dirty = true;

        if (container != null) {
            container.setDirty();
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public PatchModel container() {
        return container;
    }

    public void setContainer(PatchModel c) {
        container = c;
    }

    public AxoObjectInstanceAbstract AddObjectInstance(AxoObjectAbstract obj, Point loc) {
        if (obj == null) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "AddObjectInstance NULL");
            return null;
        }
        int i = 1;
        String n = obj.getDefaultInstanceName() + "_";
        while (GetObjectInstance(n + i) != null) {
            i++;
        }
        AxoObjectInstanceAbstract objinst = obj.CreateInstance(this, n + i, loc);
        setDirty();

        Modulator[] m = obj.getModulators();
        if (m != null) {
            if (Modulators == null) {
                Modulators = new ArrayList<Modulator>();
            }
            for (Modulator mm : m) {
                mm.objinst = objinst;
                Modulators.add(mm);
            }
        }

        return objinst;
    }

    public Net GetNet(InletInstance io) {
        for (Net net : nets) {
            for (InletInstance d : net.dest) {
                if (d == io) {
                    return net;
                }
            }
        }
        return null;
    }

    public Net GetNet(OutletInstance io) {
        for (Net net : nets) {
            for (OutletInstance d : net.source) {
                if (d == io) {
                    return net;
                }
            }
        }
        return null;
    }

    /*
     private boolean CompatType(DataType source, DataType d2){
     if (d1 == d2) return true;
     if ((d1 == DataType.bool32)&&(d2 == DataType.frac32)) return true;
     if ((d1 == DataType.frac32)&&(d2 == DataType.bool32)) return true;
     return false;
     }*/
    public Net AddConnection(InletInstance il, OutletInstance ol) {
        if (il.getObjectInstance().getPatchModel() != this) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getObjectInstance().getPatchModel() != this) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        Net n1, n2;
        n1 = GetNet(il);
        n2 = GetNet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(this);
            nets.add(n);
            n.connectInlet(il);
            n.connectOutlet(ol);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: already connected");
            return null;
        } else if ((n1 != null) && (n2 == null)) {
            if (n1.source.isEmpty()) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: adding outlet to inlet net");
                n1.connectOutlet(ol);
                return n1;
            } else {
                disconnect(il);
                Net n = new Net(this);
                nets.add(n);
                n.connectInlet(il);
                n.connectOutlet(ol);
                Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
                return n;
            }
        } else if ((n1 == null) && (n2 != null)) {
            n2.connectInlet(il);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: add additional outlet");
            return n2;
        } else if ((n1 != null) && (n2 != null)) {
            // inlet already has connect, and outlet has another
            // replace
            disconnect(il);
            n2.connectInlet(il);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: replace inlet with existing net");
            return n2;
        }
        return null;
    }

    public Net AddConnection(InletInstance il, InletInstance ol) {
        if (il == ol) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: same inlet");
            return null;
        }
        if (il.getObjectInstance().patchModel != this) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getObjectInstance().patchModel != this) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        Net n1, n2;
        n1 = GetNet(il);
        n2 = GetNet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(this);
            nets.add(n);
            n.connectInlet(il);
            n.connectInlet(ol);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: already connected");
        } else if ((n1 != null) && (n2 == null)) {
            n1.connectInlet(ol);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: inlet added");
            return n1;
        } else if ((n1 == null) && (n2 != null)) {
            n2.connectInlet(il);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: inlet added");
            return n2;
        } else if ((n1 != null) && (n2 != null)) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: both inlets included in net");
            return null;
        }
        return null;
    }

    public Net disconnect(InletInstance io) {
        Net n = GetNet(io);
        if (n != null) {
            n.dest.remove(io);
            if (n.source.size() + n.dest.size() <= 1) {
                delete(n);
            }
            return n;
        }

        return null;
    }

    public Net disconnect(OutletInstance io) {
        Net n = GetNet(io);
        if (n != null) {
            n.source.remove((OutletInstance) io);
            if (n.source.size() + n.dest.size() <= 1) {
                delete(n);
            }
            return n;
        }
        return null;
    }

    public Net delete(Net n) {
        nets.remove(n);
        return n;
    }

    public boolean delete(AxoObjectInstanceAbstract o) {
        boolean deletionSucceeded = false;
        if (o == null) {
            return deletionSucceeded;
        }
        for (InletInstance ii : o.getInletInstances()) {
            disconnect(ii);
        }
        for (OutletInstance oi : o.getOutletInstances()) {
            disconnect(oi);
        }
        int i;
        for (i = Modulators.size() - 1; i >= 0; i--) {
            Modulator m1 = Modulators.get(i);
            if (m1.objinst == o) {
                Modulators.remove(m1);
                for (Modulation mt : m1.Modulations) {
                    mt.destination.removeModulation(mt);
                }
            }
        }
        deletionSucceeded = objectinstances.remove(o);
        AxoObjectAbstract t = o.getType();
        if (o != null) {
            //            o.Close();
            t.DeleteInstance(o);
        }
        return deletionSucceeded;
    }

    public void updateModulation(Modulation n) {
        // find modulator
        Modulator m = null;
        for (Modulator m1 : Modulators) {
            if (m1.objinst == n.source) {
                if ((m1.name == null) || (m1.name.isEmpty())) {
                    m = m1;
                    break;
                } else if (m1.name.equals(n.modName)) {
                    m = m1;
                    break;
                }
            }
        }
        if (m == null) {
            throw new UnsupportedOperationException("Modulator not found");
        }
        if (!m.Modulations.contains(n)) {
            m.Modulations.add(n);
            System.out.println("modulation added to Modulator " + Modulators.indexOf(m));
        }
    }

    public void pushUndoState() {
        pushUndoState(true);
    }

    public void pushUndoState(boolean advanceCurrentState) {
        if (advanceCurrentState) {
            currentState += 1;
        }
        saveState();
        cleanUpDanglingUndoStates();
    }

    public void popUndoState() {
        currentState -= 1;
    }

    public void cleanUpDanglingUndoStates() {
        try {
            undoStates.subList(currentState + 1, undoStates.size()).clear();
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }
    }

    void saveState() {
        SortByPosition();
        Serializer serializer = new Persister();
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        try {
            serializer.write(this, b);
            try {
                undoStates.set(currentState, b.toString());
            } catch (IndexOutOfBoundsException e) {
                undoStates.add(b.toString());
            }
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isLoadingUndoState() {
        return loadingUndoState;
    }

    public void setLoadingUndoState(boolean loadingUndoState) {
        this.loadingUndoState = loadingUndoState;
    }

    void loadState() {
        Serializer serializer = new Persister();
        ByteArrayInputStream b = new ByteArrayInputStream(undoStates.get(currentState).getBytes());
        try {
            PatchModel p = serializer.read(PatchModel.class, b);
            objectinstances = p.objectinstances;
            for (AxoObjectInstanceAbstract o : objectinstances) {
                o.setPatchModel(this);
            }
            nets = p.nets;
            Modulators = p.Modulators;
            for (Net n : nets) {
                n.setPatchModel(this);
            }
            PostContructor();
            setLoadingUndoState(true);
            notifyModelChangedListeners();
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean save(File f) {
        SortByPosition();
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            serializer.write(this, f);
            MainFrame.prefs.addRecentFile(f.getAbsolutePath());
            dirty = false;
        } catch (Exception ex) {
            Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
//        if (settings == null) {
//            return;
//        }
//        if (settings.subpatchmode == SubPatchMode.no) {
//            return;
//        }
        /*
         String axoObjPath = getFileNamePath();
         int i = axoObjPath.lastIndexOf(".axp");
         axoObjPath = axoObjPath.substring(0, i) + ".axo";
         Logger.getLogger(Patch.class.getName()).log(Level.INFO, "exporting axo to " + axoObjPath);
         File f2 = new File(axoObjPath);
         ExportAxoObj(f2);
         MainFrame.axoObjects.LoadAxoObjects();
         */
    }

    int displayDataLength = 0;

    void refreshIndexes() {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            o.refreshIndex();
        }
        int i = 0;
        ParameterInstances = new ArrayList<ParameterInstance>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            for (ParameterInstance p : o.getParameterInstances()) {
                p.setIndex(i);
                i++;
                ParameterInstances.add(p);
            }
        }
        int offset = 0;
        // 0 : header
        // 1 : patchref
        // 2 : length

        DisplayInstances = new ArrayList<DisplayInstance>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            for (DisplayInstance p : o.getDisplayInstances()) {
                p.setOffset(offset + 3);
                int l = p.getLength();
                offset += l;
                DisplayInstances.add(p);
            }
        }
        displayDataLength = offset;
    }

    void SortByPosition() {
        Collections.sort(this.objectinstances);
        refreshIndexes();
    }

    void SortParentsByExecution(AxoObjectInstanceAbstract o, LinkedList<AxoObjectInstanceAbstract> result) {
        LinkedList<AxoObjectInstanceAbstract> before = new LinkedList<AxoObjectInstanceAbstract>(result);
        LinkedList<AxoObjectInstanceAbstract> parents = new LinkedList<AxoObjectInstanceAbstract>();
        // get the parents
        for (InletInstance il : o.getInletInstances()) {
            Net n = GetNet(il);
            if (n != null) {
                for (OutletInstance ol : n.GetSource()) {
                    AxoObjectInstanceAbstract i = ol.getObjectInstance();
                    if (!parents.contains(i)) {
                        parents.add(i);
                    }
                }
            }
        }
        // sort the parents
        Collections.sort(parents);
        // prepend any we haven't seen before
        for (AxoObjectInstanceAbstract c : parents) {
            if (!result.contains(c)) {
                result.addFirst(c);
            }
        }
        // prepend their parents
        for (AxoObjectInstanceAbstract c : parents) {
            if (!before.contains(c)) {
                SortParentsByExecution(c, result);
            }
        }
    }

    void SortByExecution() {
        LinkedList<AxoObjectInstanceAbstract> endpoints = new LinkedList<AxoObjectInstanceAbstract>();
        LinkedList<AxoObjectInstanceAbstract> result = new LinkedList<AxoObjectInstanceAbstract>();
        // start with all objects without outlets (end points)
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.getOutletInstances().isEmpty()) {
                endpoints.add(o);
            } else {
                int count = 0;
                for (OutletInstance ol : o.getOutletInstances()) {
                    if (GetNet(ol) != null) {
                        count++;
                    }
                }
                if (count == 0) {
                    endpoints.add(o);
                }
            }
        }
        // sort them by position
        Collections.sort(endpoints);
        // walk their inlets
        for (AxoObjectInstanceAbstract o : endpoints) {
            SortParentsByExecution(o, result);
        }
        // add the end points
        result.addAll(endpoints);
        // turn it back into a freshly sorted array
        objectinstances = new ArrayList<AxoObjectInstanceAbstract>(result);
        refreshIndexes();
    }

    public Modulator GetModulatorOfModulation(Modulation modulation) {
        if (Modulators == null) {
            return null;
        }
        for (Modulator m : Modulators) {
            if (m.Modulations.contains(modulation)) {
                return m;
            }
        }
        return null;
    }

    public int GetModulatorIndexOfModulation(Modulation modulation) {
        if (Modulators == null) {
            return -1;
        }
        for (Modulator m : Modulators) {
            int i = m.Modulations.indexOf(modulation);
            if (i >= 0) {
                return i;
            }
        }
        return -1;
    }

    List<AxoObjectAbstract> GetUsedAxoObjects() {
        ArrayList<AxoObjectAbstract> aos = new ArrayList<AxoObjectAbstract>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (!aos.contains(o.getType())) {
                aos.add(o.getType());
            }
        }
        return aos;
    }

    public HashSet<String> getIncludes() {
        HashSet<String> includes = new HashSet<String>();
        if (controllerinstance != null) {
            Set<String> i = controllerinstance.getType().GetIncludes();
            if (i != null) {
                includes.addAll(i);
            }
        }
        for (AxoObjectInstanceAbstract o : objectinstances) {
            Set<String> i = o.getType().GetIncludes();
            if (i != null) {
                includes.addAll(i);
            }
        }

        return includes;
    }

    public HashSet<String> getDepends() {
        HashSet<String> depends = new HashSet<String>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            Set<String> i = o.getType().GetDepends();
            if (i != null) {
                depends.addAll(i);
            }
        }
        return depends;
    }

    public String generateIncludes() {
        String inc = "";
        Set<String> includes = getIncludes();
        for (String s : includes) {
            if (s.startsWith("\"")) {
                inc += "#include " + s + "\n";
            } else {
                inc += "#include \"" + s + "\"\n";
            }
        }
        return inc;
    }

    /* the c++ code generator */
    String GeneratePexchAndDisplayCode() {
        String c = GeneratePexchAndDisplayCodeV();
        c += "    int32_t PExModulationPrevVal[attr_poly][NMODULATIONSOURCES];\n";
        return c;
    }

    String GeneratePexchAndDisplayCodeV() {
        String c = "";
        c += "    static const uint32_t NPEXCH = " + +ParameterInstances.size() + ";\n";
        c += "    ParameterExchange_t PExch[NPEXCH];\n";
        c += "    int32_t displayVector[" + (displayDataLength + 3) + "];\n";
        c += "    static const uint32_t NPRESETS = " + settings.GetNPresets() + ";\n";
        c += "    static const uint32_t NPRESET_ENTRIES = " + settings.GetNPresetEntries() + ";\n";
        c += "    static const uint32_t NMODULATIONSOURCES = " + settings.GetNModulationSources() + ";\n";
        c += "    static const uint32_t NMODULATIONTARGETS = " + settings.GetNModulationTargetsPerSource() + ";\n";
        return c;
    }

    String GenerateObjectCode(String classname, boolean enableOnParent, String OnParentAccess) {
        String c = "";
        {
            c += "/* modsource defines */\n";
            int k = 0;
            for (Modulator m : Modulators) {
                c += "static const int " + m.getCName() + " = " + k + ";\n";
                k++;
            }
        }
        {
            c += "/* parameter instance indices */\n";
            int k = 0;
            for (ParameterInstance p : ParameterInstances) {
                c += "static const int PARAM_INDEX_" + p.getObjectInstance().getLegalName() + "_" + p.getLegalName() + " = " + k + ";\n";
                k++;
            }
        }
        c += "/* controller classes */\n";
        if (controllerinstance != null) {
            c += controllerinstance.GenerateClass(classname, OnParentAccess, enableOnParent);
        }
        c += "/* object classes */\n";
        for (AxoObjectInstanceAbstract o : objectinstances) {
            c += o.GenerateClass(classname, OnParentAccess, enableOnParent);
        }
        c += "/* controller instances */\n";
        if (controllerinstance != null) {
            String s = controllerinstance.getCInstanceName();
            if (!s.isEmpty()) {
                c += "     " + s + " " + s + "_i;\n";
            }
        }

        c += "/* object instances */\n";
        for (AxoObjectInstanceAbstract o : objectinstances) {
            String s = o.getCInstanceName();
            if (!s.isEmpty()) {
                c += "     " + s + " " + s + "_i;\n";
            }
        }
        c += "/* net latches */\n";
        for (Net n : nets) {
            // check if net has multiple sources
            if ((n.CType() != null) && n.NeedsLatch()) {
                c += "    " + n.CType() + " " + n.CName() + "Latch" + ";\n";
            }
        }
        return c;
    }

    String GenerateStructCodePlusPlusSub(String classname, boolean enableOnParent) {
        String c = "";
        c += GeneratePexchAndDisplayCode();
        c += GenerateObjectCode(classname, enableOnParent, "parent->");
        return c;
    }

    String GenerateStructCodePlusPlus(String classname, boolean enableOnParent, String parentclassname) {
        String c = "";
        c += "class " + classname + "{\n";
        c += "   public:\n";
        c += GenerateStructCodePlusPlusSub(parentclassname, enableOnParent);
        return c;
    }

    String GeneratePresetCode3(String ClassName) {
        String c = "   static const int32_t * GetPresets(void){\n";
        c += "      static const int32_t p[NPRESETS][NPRESET_ENTRIES][2] = {\n";
        for (int i = 0; i < settings.GetNPresets(); i++) {
//            c += "// preset " + i + "\n";
//            c += "pp = (int*)(&Presets[" + i + "]);\n";
            int[] dp = DistillPreset(i + 1);
            c += "         {\n";
            for (int j = 0; j < settings.GetNPresetEntries(); j++) {
                c += "           {" + dp[j * 2] + "," + dp[j * 2 + 1] + "}";
                if (j != settings.GetNPresetEntries() - 1) {
                    c += ",\n";
                } else {
                    c += "\n";
                }
            }
            if (i != settings.GetNPresets() - 1) {
                c += "         },\n";
            } else {
                c += "         }\n";
            }
        }
        c += "      };\n";
        c += "   return &p[0][0][0];\n";
        c += "   };\n";

        c += "void ApplyPreset(int index){\n"
                + "   if (!index) {\n"
                + "     int i;\n"
                + "     int32_t *p = GetInitParams();\n"
                + "     for(i=0;i<NPEXCH;i++){\n"
                + "        PExParameterChange(&PExch[i],p[i],0xFFEF);\n"
                + "     }\n"
                + "   }\n"
                + "   index--;\n"
                + "   if (index < NPRESETS) {\n"
                + "     PresetParamChange_t *pa = (PresetParamChange_t *)(GetPresets());\n"
                + "     PresetParamChange_t *p = &pa[index*NPRESET_ENTRIES];\n"
                + "       int i;\n"
                + "       for(i=0;i<NPRESET_ENTRIES;i++){\n"
                + "         PresetParamChange_t *pp = &p[i];\n"
                + "         if ((pp->pexIndex>=0)&&(pp->pexIndex<NPEXCH)) {\n"
                + "           PExParameterChange(&PExch[pp->pexIndex],pp->value,0xFFEF);"
                + "         }\n"
                + "         else break;\n"
                + "       }\n"
                + "   }\n"
                + "}\n";
        return c;
    }

    String GenerateModulationCode3() {
        String s = "   static PExModulationTarget_t * GetModulationTable(void){\n";
        s += "    static const PExModulationTarget_t PExModulationSources[NMODULATIONSOURCES][NMODULATIONTARGETS] = \n";
        s += "{";
        for (int i = 0; i < settings.GetNModulationSources(); i++) {
            s += "{";
            if (i < Modulators.size()) {
                Modulator m = Modulators.get(i);
                for (int j = 0; j < settings.GetNModulationTargetsPerSource(); j++) {
                    if (j < m.Modulations.size()) {
                        Modulation n = m.Modulations.get(j);
                        s += "{" + n.destination.indexName() + ", " + n.value.getRaw() + "}";
                    } else {
                        s += "{-1,0}";
                    }
                    if (j != settings.GetNModulationTargetsPerSource() - 1) {
                        s += ",";
                    } else {
                        s += "}";
                    }
                }
            } else {
                for (int j = 0; j < settings.GetNModulationTargetsPerSource() - 1; j++) {
                    s += "{-1,0},";
                }
                s += "{-1,0}}";
            }
            if (i != settings.GetNModulationSources() - 1) {
                s += ",\n";
            }
        }
        s += "};\n";
        s += "   return (PExModulationTarget_t *)&PExModulationSources[0][0];\n";
        s += "   };\n";

        return s;
    }

    String GenerateParamInitCode3(String ClassName) {
        int s = ParameterInstances.size();
        String c = "   static int32_t * GetInitParams(void){\n"
                + "      static const int32_t p[" + s + "]= {\n";
        for (int i = 0; i < s; i++) {
            c += "      " + ParameterInstances.get(i).GetValueRaw();
            if (i != s - 1) {
                c += ",\n";
            } else {
                c += "\n";
            }
        }
        c += "      };\n"
                + "      return (int32_t *)&p[0];\n"
                + "   }";
        return c;
    }

    String GenerateObjInitCodePlusPlusSub(String className, String parentReference) {
        String c = "";
        if (controllerinstance != null) {
            String s = controllerinstance.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + s + "_i.Init(" + parentReference;
                for (DisplayInstance i : controllerinstance.getDisplayInstances()) {
                    if (i.display.getLength() > 0) {
                        c += ", ";
                        c += i.valueName("");
                    }
                }
                c += " );\n";
            }
        }

        for (AxoObjectInstanceAbstract o : objectinstances) {
            String s = o.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + o.getCInstanceName() + "_i.Init(" + parentReference;
                for (DisplayInstance i : o.getDisplayInstances()) {
                    if (i.display.getLength() > 0) {
                        c += ", ";
                        c += i.valueName("");
                    }
                }
                c += " );\n";
            }
        }
        c += "      int k;\n"
                + "      for (k = 0; k < NPEXCH; k++) {\n"
                + "        if (PExch[k].pfunction){\n"
                + "          (PExch[k].pfunction)(&PExch[k]);\n"
                + "        } else {\n"
                + "          PExch[k].finalvalue = PExch[k].value;\n"
                + "        }\n"
                + "      }\n";
        return c;
    }

    String GenerateParamInitCodePlusPlusSub(String className, String parentReference) {
        String c = "";
        c += "   int i;\n";
        c += "   int j;\n";
        c += "   const int32_t *p;\n";
        c += "   p = GetInitParams();\n";
        c += "   for(j=0;j<" + ParameterInstances.size() + ";j++){\n";
        c += "      PExch[j].value = p[j];\n";
        c += "      PExch[j].modvalue = p[j];\n";
        c += "      PExch[j].signals = 0;\n";
        c += "      PExch[j].pfunction = 0;\n";
//        c += "      PExch[j].finalvalue = p[j];\n"; /*TBC*/
        c += "   }\n";
        c += "   int32_t *pp = &PExModulationPrevVal[0][0];\n";
        c += "   for(j=0;j<attr_poly*NMODULATIONSOURCES;j++){\n";
        c += "      *pp = 0; pp++;\n";
        c += "   }\n";
        c += "     displayVector[0] = 0x446F7841;\n"; // "AxoD"
        c += "     displayVector[1] = 0;\n";
        c += "     displayVector[2] = " + displayDataLength + ";\n";
        return c;
    }

    String GenerateInitCodePlusPlus(String className) {
        String c = "";
        c += "/* init */\n";
        c += "void Init() {\n";
        c += GenerateParamInitCodePlusPlusSub("", "this");
        c += GenerateObjInitCodePlusPlusSub("", "this");
        c += "}\n\n";
        return c;
    }

    String GenerateDisposeCodePlusPlusSub(String className) {
        // reverse order
        String c = "";
        int l = objectinstances.size();
        for (int i = l - 1; i >= 0; i--) {
            AxoObjectInstanceAbstract o = objectinstances.get(i);
            String s = o.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + o.getCInstanceName() + "_i.Dispose();\n";
            }
        }
        if (controllerinstance != null) {
            String s = controllerinstance.getCInstanceName();
            if (!s.isEmpty()) {
                c += "   " + controllerinstance.getCInstanceName() + "_i.Dispose();\n";
            }
        }

        return c;
    }

    String GenerateDisposeCodePlusPlus(String className) {
        String c = "";
        c += "/* dispose */\n";
        c += "void Dispose() {\n";
        c += GenerateDisposeCodePlusPlusSub(className);
        c += "}\n\n";
        return c;
    }

    String GenerateDSPCodePlusPlusSub(String ClassName, boolean enableOnParent) {
        String c = "";
        c += "//--------- <nets> -----------//\n";
        for (Net n : nets) {
            if (n.CType() != null) {
                c += "    " + n.CType() + " " + n.CName() + ";\n";
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Net has no data type!");
            }
        }
        c += "//--------- </nets> ----------//\n";
        c += "//--------- <zero> ----------//\n";
        c += "  int32_t UNCONNECTED_OUTPUT;\n";
        c += "  static const int32_t UNCONNECTED_INPUT=0;\n";
        c += "  static const int32buffer zerobuffer = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};\n";
        c += "  int32buffer UNCONNECTED_OUTPUT_BUFFER;\n";
        c += "//--------- </zero> ----------//\n";

        c += "//--------- <controller calls> ----------//\n";
        if (controllerinstance != null) {
            c += GenerateDSPCodePlusPlusSubObj(controllerinstance, ClassName, enableOnParent);
        }
        c += "//--------- <object calls> ----------//\n";
        for (AxoObjectInstanceAbstract o : objectinstances) {
            c += GenerateDSPCodePlusPlusSubObj(o, ClassName, enableOnParent);
        }
        c += "//--------- </object calls> ----------//\n";

        c += "//--------- <net latch copy> ----------//\n";
        for (Net n : nets) {
            // check if net has multiple sources
            if (n.NeedsLatch()) {
                if (n.getDataType() != null) {
                    c += n.getDataType().GenerateCopyCode(n.CName() + "Latch", n.CName());
                } else {
                    Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Only inlets connected on net!");
                }
            }
        }
        c += "//--------- </net latch copy> ----------//\n";
        return c;
    }

    String GenerateDSPCodePlusPlusSubObj(AxoObjectInstanceAbstract o, String ClassName, boolean enableOnParent) {
        String c = "";
        String s = o.getCInstanceName();
        if (s.isEmpty()) {
            return c;
        }
        c += "  " + o.getCInstanceName() + "_i.dsp(";
//            c += "  " + o.GenerateDoFunctionName() + "(this";
        boolean needsComma = false;
        for (InletInstance i : o.getInletInstances()) {
            if (needsComma) {
                c += ", ";
            }
            Net n = GetNet(i);
            if ((n != null) && (n.isValidNet())) {
                if (i.getDataType().equals(n.getDataType())) {
                    if (n.NeedsLatch()
                            && (objectinstances.indexOf(n.source.get(0).getObjectInstance()) >= objectinstances.indexOf(o))) {
                        c += n.CName() + "Latch";
                    } else {
                        c += n.CName();
                    }
                } else if (n.NeedsLatch()
                        && (objectinstances.indexOf(n.source.get(0).getObjectInstance()) >= objectinstances.indexOf(o))) {
                    c += n.getDataType().GenerateConversionToType(i.getDataType(), n.CName() + "Latch");
                } else {
                    c += n.getDataType().GenerateConversionToType(i.getDataType(), n.CName());
                }
            } else if (n == null) { // unconnected input
                c += i.getDataType().GenerateSetDefaultValueCode();
            } else if (!n.isValidNet()) {
                c += i.getDataType().GenerateSetDefaultValueCode();
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Patch contains invalid net! {0}", i.getObjectInstance().getInstanceName() + ":" + i.getInletname());
            }
            needsComma = true;
        }
        for (OutletInstance i : o.getOutletInstances()) {
            if (needsComma) {
                c += ", ";
            }
            Net n = GetNet(i);
            if ((n != null) && n.isValidNet()) {
                if (n.IsFirstOutlet(i)) {
                    c += n.CName();
                } else {
                    c += n.CName() + "+";
                }
            } else {
                c += i.getDataType().UnconnectedSink();
            }
            needsComma = true;
        }
        for (ParameterInstance i : o.getParameterInstances()) {
            if (i.parameter.PropagateToChild == null) {
                if (needsComma) {
                    c += ", ";
                }
                c += i.variableName("", false);
                needsComma = true;
            }
        }
        for (DisplayInstance i : o.getDisplayInstances()) {
            if (i.display.getLength() > 0) {
                if (needsComma) {
                    c += ", ";
                }
                c += i.valueName("");
                needsComma = true;
            }
        }
        c += ");\n";
        return c;
    }

    String GenerateMidiInCodePlusPlus() {
        String c = "";
        if (controllerinstance != null) {
            c += controllerinstance.GenerateCallMidiHandler();
        }
        for (AxoObjectInstanceAbstract o : objectinstances) {
            c += o.GenerateCallMidiHandler();
        }
        return c;
    }

    String GenerateDSPCodePlusPlus(String ClassName, boolean enableOnParent) {
        String c;
        c = "/* krate */\n";
        c += "void dsp (void) {\n";
        c += "  int i;\n";
        c += "  for(i=0;i<BUFSIZE;i++) AudioOutputLeft[i]=0;\n";
        c += "  for(i=0;i<BUFSIZE;i++) AudioOutputRight[i]=0;\n";
        c += GenerateDSPCodePlusPlusSub(ClassName, enableOnParent);
        c += "}\n\n";
        return c;
    }

    String GenerateMidiCodePlusPlus(String ClassName) {
        String c = "";
        c += "void MidiInHandler(midi_device_t dev, uint8_t port,uint8_t status, uint8_t data1, uint8_t data2){\n";
        c += GenerateMidiInCodePlusPlus();
        c += "}\n\n";
        return c;
    }

    String GeneratePatchCodePlusPlus(String ClassName) {
        String c = "";
        c += "};\n\n";
        c += "static rootc root;\n";

        c += "void PatchProcess( int32_t * inbuf, int32_t * outbuf) {\n"
                + "  int i;\n"
                + "  for(i=0;i<BUFSIZE;i++){\n"
                + "    AudioInputLeft[i] = inbuf[i*2]>>4;\n"
                + "    switch(AudioInputMode) {\n"
                + "       case A_MONO:\n"
                + "             AudioInputRight[i] = AudioInputLeft[i];break;\n"
                + "       case A_BALANCED:\n"
                + "             AudioInputLeft[i] = (AudioInputLeft[i] - (inbuf[i*2+1]>>4) ) >> 1;\n"
                + "             AudioInputRight[i] = AudioInputLeft[i];"
                + "             break;\n"
                + "       case A_STEREO:\n"
                + "       default:\n"
                + "             AudioInputRight[i] = inbuf[i*2+1]>>4;\n"
                + "     }\n"
                + "  }\n"
                + "  root.dsp();\n";
        if (settings.getSaturate()) {
            c += "  for(i=0;i<BUFSIZE;i++){\n"
                    + "    outbuf[i*2] = __SSAT(AudioOutputLeft[i],28)<<4;\n"
                    + "    switch(AudioOutputMode) {\n"
                    + "       case A_MONO:\n"
                    + "             outbuf[i*2+1] = 0;break;\n"
                    + "       case A_BALANCED:\n"
                    + "             outbuf[i*2+1] = ~ outbuf[i*2];break;\n"
                    + "       case A_STEREO:\n"
                    + "       default:\n"
                    + "             outbuf[i*2+1] = __SSAT(AudioOutputRight[i],28)<<4;\n"
                    + "     }\n"
                    + "  }\n";
        } else {
            c += "  for(i=0;i<BUFSIZE;i++){\n"
                    + "    outbuf[i*2] = AudioOutputLeft[i];\n"
                    + "    switch(AudioOutputMode) {\n"
                    + "       case A_MONO:\n"
                    + "             outbuf[i*2+1] = 0;break;\n"
                    + "       case A_BALANCED:\n"
                    + "             outbuf[i*2+1] = ~ outbuf[i*2];break;\n"
                    + "       case A_STEREO:\n"
                    + "       default:\n"
                    + "             outbuf[i*2+1] = AudioOutputRight[i];\n"
                    + "     }\n"
                    + "  }\n";
        }
        c += "}\n\n";

        c += "void ApplyPreset(int32_t i) {\n"
                + "   root.ApplyPreset(i);\n"
                + "}\n\n";

        c += "void PatchMidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2){\n"
                + "  root.MidiInHandler(dev, port, status, data1, data2);\n"
                + "}\n\n";

        c += "typedef void (*funcp_t)(void);\n"
                + "typedef funcp_t * funcpp_t;\n"
                + "extern funcp_t __ctor_array_start;\n"
                + "extern funcp_t __ctor_array_end;"
                + "extern funcp_t __dtor_array_start;\n"
                + "extern funcp_t __dtor_array_end;";

        c += "void PatchDispose( ) {\n"
                + "  root.Dispose();\n"
                + "  {\n"
                + "    funcpp_t fpp = &__dtor_array_start;\n"
                + "    while (fpp < &__dtor_array_end) {\n"
                + "      (*fpp)();\n"
                + "      fpp++;\n"
                + "    }\n"
                + "  }\n"
                + "}\n\n";

        c += "void xpatch_init2(int fwid)\n"
                + "{\n"
                + "  if (fwid != 0x" + MainFrame.mainframe.LinkFirmwareID + ") {\n"
                + "    return;"
                + "  }\n"
                + "  extern uint32_t _pbss_start;\n"
                + "  extern uint32_t _pbss_end;\n"
                + "  volatile uint32_t *p;\n"
                + "  for(p=&_pbss_start;p<&_pbss_end;p++) *p=0;\n"
                + "  {\n"
                + "    funcpp_t fpp = &__ctor_array_start;\n"
                + "    while (fpp < &__ctor_array_end) {\n"
                + "      (*fpp)();\n"
                + "      fpp++;\n"
                + "    }\n"
                + "  }\n"
                + "  patchMeta.npresets = " + settings.GetNPresets() + ";\n"
                + "  patchMeta.npreset_entries = " + settings.GetNPresetEntries() + ";\n"
                + "  patchMeta.pPresets = (PresetParamChange_t*) root.GetPresets();\n"
                + "  patchMeta.pPExch = &root.PExch[0];\n"
                + "  patchMeta.pDisplayVector = &root.displayVector[0];\n"
                + "  patchMeta.numPEx = " + ParameterInstances.size() + ";\n"
                + "  patchMeta.patchID = " + GetIID() + ";\n"
                + "  extern char _sdram_dyn_start;\n"
                + "  extern char _sdram_dyn_end;\n"
                + "  sdram_init(&_sdram_dyn_start,&_sdram_dyn_end);\n"
                + "  root.Init();\n"
                + "  patchMeta.fptr_applyPreset = ApplyPreset;\n"
                + "  patchMeta.fptr_patch_dispose = PatchDispose;\n"
                + "  patchMeta.fptr_MidiInHandler = PatchMidiInHandler;\n"
                + "  patchMeta.fptr_dsp_process = PatchProcess;\n"
                + "}\n";
        return c;
    }

    int IID = -1; // iid identifies the patch

    public int GetIID() {
        return IID;
    }

    void CreateIID() {
        java.util.Random r = new java.util.Random();
        IID = r.nextInt();
    }

    String GenerateCode3() {
        Preferences prefs = MainFrame.prefs;
        controllerinstance = null;
        String cobjstr = prefs.getControllerObject();
        if (prefs.isControllerEnabled() && cobjstr != null && !cobjstr.isEmpty()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Using controller object: {0}", cobjstr);
            AxoObjectAbstract x = null;
            ArrayList<AxoObjectAbstract> objs = MainFrame.axoObjects.GetAxoObjectFromName(cobjstr, GetCurrentWorkingDirectory());
            if ((objs != null) && (!objs.isEmpty())) {
                x = objs.get(0);
            }
            if (x != null) {
                controllerinstance = x.CreateInstance(null, "ctrl0x123", new Point(0, 0));
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Unable to created controller for : {0}", cobjstr);
            }
        }

        CreateIID();
        SortByPosition();
        String c = generateIncludes();
        c += "\n"
                + "#pragma GCC diagnostic ignored \"-Wunused-variable\"\n"
                + "#pragma GCC diagnostic ignored \"-Wunused-parameter\"\n";
        if (settings == null) {
            c += "#define MIDICHANNEL 0 // DEPRECATED!\n";
        } else {
            c += "#define MIDICHANNEL " + (settings.GetMidiChannel() - 1) + " // DEPRECATED!\n";
        }
        c += "void xpatch_init2(int fwid);\n"
                + "extern \"C\" __attribute__ ((section(\".boot\"))) void xpatch_init(int fwid){\n"
                + "  xpatch_init2(fwid);\n"
                + "}\n\n";

        c += "void PatchMidiInHandler(midi_device_t dev, uint8_t port, uint8_t status, uint8_t data1, uint8_t data2);\n\n";

        c += "     int32buffer AudioInputLeft;\n";
        c += "     int32buffer AudioInputRight;\n";
        c += "     int32buffer AudioOutputLeft;\n";
        c += "     int32buffer AudioOutputRight;\n";
        c += "     typedef enum { A_STEREO, A_MONO, A_BALANCED } AudioModeType;\n";
        c += "     AudioModeType AudioOutputMode = A_STEREO;\n";
        c += "     AudioModeType AudioInputMode = A_STEREO;\n";

        c += "static void PropagateToSub(ParameterExchange_t *origin) {\n"
                + "      ParameterExchange_t *pex = (ParameterExchange_t *)origin->finalvalue;\n"
                + "      PExParameterChange(pex,origin->modvalue,0xFFFFFFEE);\n"
                + "}\n";

        c += GenerateStructCodePlusPlus("rootc", false, "rootc")
                + "static const int polyIndex = 0;\n"
                + GenerateParamInitCode3("rootc")
                + GeneratePresetCode3("rootc")
                + GenerateModulationCode3()
                + GenerateInitCodePlusPlus("rootc")
                + GenerateDisposeCodePlusPlus("rootc")
                + GenerateDSPCodePlusPlus("rootc", false)
                + GenerateMidiCodePlusPlus("rootc")
                + GeneratePatchCodePlusPlus("rootc");

        c = c.replace("attr_poly", "1");

        if (settings == null) {
            c = c.replace("attr_midichannel", "0");
        } else {
            c = c.replace("attr_midichannel", Integer.toString(settings.GetMidiChannel() - 1));
        }
        if (settings == null || !settings.GetMidiSelector()) {
            c = c.replace("attr_mididevice", "0");
            c = c.replace("attr_midiport", "0");
        }
        return c;
    }

    public AxoObject GenerateAxoObjNormal(AxoObject template) {
        SortByPosition();
        AxoObject ao = template;
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("patch/inlet f")) {
                ao.inlets.add(new InletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet i")) {
                ao.inlets.add(new InletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet b")) {
                ao.inlets.add(new InletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet a")) {
                ao.inlets.add(new InletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet string")) {
                ao.inlets.add(new InletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet f")) {
                ao.outlets.add(new OutletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet i")) {
                ao.outlets.add(new OutletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet b")) {
                ao.outlets.add(new OutletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet a")) {
                ao.outlets.add(new OutletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet string")) {
                ao.outlets.add(new OutletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            }
            for (ParameterInstance p : o.getParameterInstances()) {
                if (p.isOnParent()) {
                    ao.params.add(p.getParameterForParent());
                }
            }
        }
        /* object structures */
//         ao.sCName = fnNoExtension;
        ao.sLocalData = GenerateStructCodePlusPlusSub("attr_parent", true)
                + "static const int polyIndex = 0;\n";
        ao.sLocalData += GenerateParamInitCode3("");
        ao.sLocalData += GeneratePresetCode3("");
        ao.sLocalData += GenerateModulationCode3();
        ao.sLocalData = ao.sLocalData.replaceAll("attr_poly", "1");
        ao.sInitCode = GenerateParamInitCodePlusPlusSub("attr_parent", "this");
        ao.sInitCode += GenerateObjInitCodePlusPlusSub("attr_parent", "this");
        ao.sDisposeCode = GenerateDisposeCodePlusPlusSub("attr_parent");
        ao.includes = getIncludes();
        ao.depends = getDepends();
        if ((notes != null) && (!notes.isEmpty())) {
            ao.sDescription = notes;
        } else {
            ao.sDescription = "no description";
        }
        ao.sKRateCode = "int i; /*...*/\n";
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("patch/inlet f") || o.typeName.equals("patch/inlet i") || o.typeName.equals("patch/inlet b")) {
                ao.sKRateCode += "   " + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n";
            } else if (o.typeName.equals("patch/inlet string")) {
                ao.sKRateCode += "   " + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n";
            } else if (o.typeName.equals("patch/inlet a")) {
                ao.sKRateCode += "   for(i=0;i<BUFSIZE;i++) " + o.getCInstanceName() + "_i._inlet[i] = inlet_" + o.getLegalName() + "[i];\n";
            }

        }
        ao.sKRateCode += GenerateDSPCodePlusPlusSub("attr_parent", true);
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("patch/outlet f") || o.typeName.equals("patch/outlet i") || o.typeName.equals("patch/outlet b")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = " + o.getCInstanceName() + "_i._outlet;\n";
            } else if (o.typeName.equals("patch/outlet string")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = (char *)" + o.getCInstanceName() + "_i._outlet;\n";
            } else if (o.typeName.equals("patch/outlet a")) {
                ao.sKRateCode += "      for(i=0;i<BUFSIZE;i++) outlet_" + o.getLegalName() + "[i] = " + o.getCInstanceName() + "_i._outlet[i];\n";
            }
        }

        ao.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
                + GenerateMidiInCodePlusPlus();

        if ((settings != null) && (settings.GetMidiSelector())) {
            String cch[] = {"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
            String uch[] = {"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midichannel", uch, cch));
            // use a cut down list of those currently supported
            String cdev[] = {"0", "1", "2", "3", "15"};
            String udev[] = {"omni", "din", "usb device", "usb host", "internal"};
            ao.attributes.add(new AxoAttributeComboBox("mididevice", udev, cdev));
            String cport[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            String uport[] = {"omni", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midiport", uport, cport));
        }
        return ao;
    }

    public AxoObject GenerateAxoObj(AxoObject template) {
        AxoObject ao;
        if (settings == null) {
            ao = GenerateAxoObjNormal(template);
        } else {
            switch (settings.subpatchmode) {
                case no:
                case normal:
                    ao = GenerateAxoObjNormal(template);
                    break;
                case polyphonic:
                    ao = GenerateAxoObjPoly(template);
                    break;
                case polychannel:
                    ao = GenerateAxoObjPolyChannel(template);
                    break;
                case polyexpression:
                    ao = GenerateAxoObjPolyExpression(template);
                    break;
                default:
                    return null;
            }
        }
        if (settings != null) {
            ao.sAuthor = settings.getAuthor();
            ao.sLicense = settings.getLicense();
            ao.sDescription = notes;
            ao.helpPatch = helpPatch;
        }
        return ao;
    }

    void ExportAxoObj(File f1) {
        String fnNoExtension = f1.getName().substring(0, f1.getName().lastIndexOf(".axo"));
        AxoObject ao = GenerateAxoObj(new AxoObject());
        ao.sDescription = FileNamePath;
        ao.id = fnNoExtension;

        AxoObjectFile aof = new AxoObjectFile();
        aof.objs.add(ao);
        Serializer serializer = new Persister();
        try {
            serializer.write(aof, f1);
        } catch (Exception ex) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Export obj complete");
    }

//    void ExportAxoObjPoly2(File f1) {
//        String fnNoExtension = f1.getName().substring(0, f1.getName().lastIndexOf(".axo"));
//    }
    // Poly voices from one (or omni) midi channel
    AxoObject GenerateAxoObjPoly(AxoObject template) {
        SortByPosition();
        AxoObject ao = template;
        ao.id = "unnamedobject";
        ao.sDescription = FileNamePath;
        ao.includes = getIncludes();
        ao.depends = getDepends();
        if ((notes != null) && (!notes.isEmpty())) {
            ao.sDescription = notes;
        } else {
            ao.sDescription = "no description";
        }
        String centries[] = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
        ao.attributes.add(new AxoAttributeComboBox("poly", centries, centries));
        if ((settings != null) && (settings.GetMidiSelector())) {
            String cch[] = {"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"};
            String uch[] = {"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midichannel", uch, cch));
            // use a cut down list of those currently supported
            String cdev[] = {"0", "1", "2", "3", "15"};
            String udev[] = {"omni", "din", "usb device", "usb host", "internal"};
            ao.attributes.add(new AxoAttributeComboBox("mididevice", udev, cdev));
            String cport[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            String uport[] = {"omni", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"};
            ao.attributes.add(new AxoAttributeComboBox("midiport", uport, cport));
        }

        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("patch/inlet f")) {
                ao.inlets.add(new InletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet i")) {
                ao.inlets.add(new InletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet b")) {
                ao.inlets.add(new InletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet a")) {
                ao.inlets.add(new InletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/inlet string")) {
                ao.inlets.add(new InletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet f")) {
                ao.outlets.add(new OutletFrac32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet i")) {
                ao.outlets.add(new OutletInt32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet b")) {
                ao.outlets.add(new OutletBool32(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet a")) {
                ao.outlets.add(new OutletFrac32Buffer(o.getInstanceName(), o.getInstanceName()));
            } else if (o.typeName.equals("patch/outlet string")) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "string outlet impossible in poly subpatches!");
                // ao.outlets.add(new OutletCharPtr32(o.getInstanceName(), o.getInstanceName()));
            }
            for (ParameterInstance p : o.getParameterInstances()) {
                if (p.isOnParent()) {
                    ao.params.add(p.getParameterForParent());
                }
            }
        }

        ao.sLocalData = GenerateParamInitCode3("");
        ao.sLocalData += GeneratePexchAndDisplayCode();
        ao.sLocalData += "/* parameter instance indices */\n";
        int k = 0;
        for (ParameterInstance p : ParameterInstances) {
            ao.sLocalData += "static const int PARAM_INDEX_" + p.getObjectInstance().getLegalName() + "_" + p.getLegalName() + " = " + k + ";\n";
            k++;
        }

        ao.sLocalData += GeneratePresetCode3("");
        ao.sLocalData += GenerateModulationCode3();
        ao.sLocalData += "class voice {\n";
        ao.sLocalData += "   public:\n";
        ao.sLocalData += "   int polyIndex;\n";
        ao.sLocalData += GeneratePexchAndDisplayCodeV();
        ao.sLocalData += GenerateObjectCode("voice", true, "parent->common->");
        ao.sLocalData += "attr_parent *common;\n";
        ao.sLocalData += "void Init(voice *parent) {\n";
        ao.sLocalData += "        int i;\n"
                + "        for(i=0;i<NPEXCH;i++){\n"
                + "          PExch[i].pfunction = 0;\n"
                + "        }\n";
        ao.sLocalData += GenerateObjInitCodePlusPlusSub("voice", "parent");
        ao.sLocalData += "}\n\n";
        ao.sLocalData += "void dsp(void) {\n int i;\n";
        ao.sLocalData += GenerateDSPCodePlusPlusSub("", true);
        ao.sLocalData += "}\n";
        ao.sLocalData += "void dispose(void) {\n int i;\n";
        ao.sLocalData += GenerateDisposeCodePlusPlusSub("");
        ao.sLocalData += "}\n";
        ao.sLocalData += GenerateMidiCodePlusPlus("attr_parent");
        ao.sLocalData += "};\n";
        ao.sLocalData += "static voice * getVoices(void){\n"
                + "     static voice v[attr_poly];\n"
                + "    return v;\n"
                + "}\n";

        ao.sLocalData += "static void PropagateToVoices(ParameterExchange_t *origin) {\n"
                + "      ParameterExchange_t *pex = (ParameterExchange_t *)origin->finalvalue;\n"
                + "      int vi;\n"
                + "      for (vi = 0; vi < attr_poly; vi++) {\n"
                + "        PExParameterChange(pex,origin->modvalue,0xFFFFFFEE);\n"
                + "          pex = (ParameterExchange_t *)((int)pex + sizeof(voice)); // dirty trick...\n"
                + "      }"
                + "}\n";

        ao.sLocalData += "int8_t notePlaying[attr_poly];\n";
        ao.sLocalData += "int32_t voicePriority[attr_poly];\n";
        ao.sLocalData += "int32_t priority;\n";
        ao.sLocalData += "int32_t sustain;\n";
        ao.sLocalData += "int8_t pressed[attr_poly];\n";

        ao.sLocalData = ao.sLocalData.replaceAll("parent->PExModulationSources", "parent->common->PExModulationSources");
        ao.sLocalData = ao.sLocalData.replaceAll("parent->PExModulationPrevVal", "parent->common->PExModulationPrevVal");
        ao.sLocalData = ao.sLocalData.replaceAll("parent->GetModulationTable", "parent->common->GetModulationTable");

        ao.sInitCode = GenerateParamInitCodePlusPlusSub("", "parent");
        ao.sInitCode += "int k;\n"
                + "   for(k=0;k<NPEXCH;k++){\n"
                + "      PExch[k].pfunction = PropagateToVoices;\n"
                + "      PExch[k].finalvalue = (int32_t) (&(getVoices()[0].PExch[k]));\n"
                + "   }\n";
        ao.sInitCode += "int vi; for(vi=0;vi<attr_poly;vi++) {\n"
                + "   voice *v = &getVoices()[vi];\n"
                + "   v->polyIndex = vi;\n"
                + "   v->common = this;\n"
                + "   v->Init(&getVoices()[vi]);\n"
                + "   notePlaying[vi]=0;\n"
                + "   voicePriority[vi]=0;\n"
                + "   for (j = 0; j < v->NPEXCH; j++) {\n"
                + "      v->PExch[j].value = 0;\n"
                + "      v->PExch[j].modvalue = 0;\n"
                + "   }\n"
                + "}\n"
                + "      for (k = 0; k < NPEXCH; k++) {\n"
                + "        if (PExch[k].pfunction){\n"
                + "          (PExch[k].pfunction)(&PExch[k]);\n"
                + "        } else {\n"
                + "          PExch[k].finalvalue = PExch[k].value;\n"
                + "        }\n"
                + "      }\n"
                + "priority=0;\n"
                + "sustain=0;\n";
        ao.sDisposeCode = "int vi; for(vi=0;vi<attr_poly;vi++) {\n"
                + "  voice *v = &getVoices()[vi];\n"
                + "  v->dispose();\n"
                + "}\n";
        ao.sKRateCode = "";
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("patch/outlet f") || o.typeName.equals("patch/outlet i")
                    || o.typeName.equals("patch/outlet b") || o.typeName.equals("patch/outlet string")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = 0;\n";
            } else if (o.typeName.equals("patch/outlet a")) {
                ao.sKRateCode += "{\n"
                        + "      int j;\n"
                        + "      for(j=0;j<BUFSIZE;j++) outlet_" + o.getLegalName() + "[j] = 0;\n"
                        + "}\n";
            }
        }
        ao.sKRateCode += "int vi; for(vi=0;vi<attr_poly;vi++) {";

        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("inlet") || o.typeName.equals("inlet_i") || o.typeName.equals("inlet_b") || o.typeName.equals("inlet_")
                    || o.typeName.equals("patch/inlet f") || o.typeName.equals("patch/inlet i") || o.typeName.equals("patch/inlet b")) {
                ao.sKRateCode += "   getVoices()[vi]." + o.getCInstanceName() + "_i._inlet = inlet_" + o.getLegalName() + ";\n";
            } else if (o.typeName.equals("inlet_string") || o.typeName.equals("patch/inlet string")) {
                ao.sKRateCode += "   getVoices()[vi]." + o.getCInstanceName() + "_i._inlet = (char *)inlet_" + o.getLegalName() + ";\n";
            } else if (o.typeName.equals("inlet~") || o.typeName.equals("patch/inlet a")) {
                ao.sKRateCode += "{int j; for(j=0;j<BUFSIZE;j++) getVoices()[vi]." + o.getCInstanceName() + "_i._inlet[j] = inlet_" + o.getLegalName() + "[j];}\n";
            }
        }
        ao.sKRateCode += "getVoices()[vi].dsp();\n";
        for (AxoObjectInstanceAbstract o : objectinstances) {
            if (o.typeName.equals("outlet") || o.typeName.equals("patch/outlet f")
                    || o.typeName.equals("patch/outlet i")
                    || o.typeName.equals("patch/outlet b")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " += getVoices()[vi]." + o.getCInstanceName() + "_i._outlet;\n";
            } else if (o.typeName.equals("patch/outlet string")) {
                ao.sKRateCode += "   outlet_" + o.getLegalName() + " = (char *)getVoices()[vi]." + o.getCInstanceName() + "_i._outlet;\n";
            } else if (o.typeName.equals("patch/outlet a")) {
                ao.sKRateCode += "{\n"
                        + "      int j;\n"
                        + "      for(j=0;j<BUFSIZE;j++) outlet_" + o.getLegalName() + "[j] += getVoices()[vi]." + o.getCInstanceName() + "_i._outlet[j];\n"
                        + "}\n";
            }
        }
        ao.sKRateCode += "}\n";
        ao.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
                + "if ((status == MIDI_NOTE_ON + attr_midichannel) && (data2)) {\n"
                + "  int min = 1<<30;\n"
                + "  int mini = 0;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voicePriority[i] < min){\n"
                + "      min = voicePriority[i];\n"
                + "      mini = i;\n"
                + "    }\n"
                + "  }\n"
                + "  voicePriority[mini] = 100000+priority++;\n"
                + "  notePlaying[mini] = data1;\n"
                + "  pressed[mini] = 1;\n"
                + "  getVoices()[mini].MidiInHandler(dev, port, status, data1, data2);\n"
                + "} else if (((status == MIDI_NOTE_ON + attr_midichannel) && (!data2))||\n"
                + "          (status == MIDI_NOTE_OFF + attr_midichannel)) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if ((notePlaying[i] == data1) && pressed[i]){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "        getVoices()[i].MidiInHandler(dev, port, status, data1, data2);\n"
                + "      }\n"
                + "  }\n"
                + "} else if (status == attr_midichannel + MIDI_CONTROL_CHANGE) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) getVoices()[i].MidiInHandler(dev, port, status, data1, data2);\n"
                + "  if (data1 == 64) {\n"
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].MidiInHandler(dev, port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0);\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "} else {"
                + "  int i;   for(i=0;i<attr_poly;i++) getVoices()[i].MidiInHandler(dev, port, status, data1, data2);\n"
                + "}\n";
        return ao;
    }

    // Poly (Multi) Channel supports per Channel CC/Touch
    // all channels are independent
    AxoObject GenerateAxoObjPolyChannel(AxoObject template) {
        AxoObject o = GenerateAxoObjPoly(template);
        o.sLocalData
                += "int8_t voiceChannel[attr_poly];\n";
        o.sInitCode
                += "int vc;\n"
                + "for (vc=0;vc<attr_poly;vc++) {\n"
                + "   voiceChannel[vc]=0xFF;\n"
                + "}\n";
        o.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
                + "int msg = (status & 0xF0);\n"
                + "int channel = (status & 0x0F);\n"
                + "if ((msg == MIDI_NOTE_ON) && (data2)) {\n"
                + "  int min = 1<<30;\n"
                + "  int mini = 0;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voicePriority[i] < min){\n"
                + "      min = voicePriority[i];\n"
                + "      mini = i;\n"
                + "    }\n"
                + "  }\n"
                + "  voicePriority[mini] = 100000 + priority++;\n"
                + "  notePlaying[mini] = data1;\n"
                + "  pressed[mini] = 1;\n"
                + "  voiceChannel[mini] = status & 0x0F;\n"
                + "  getVoices()[mini].MidiInHandler(dev, port, status & 0xF0, data1, data2);\n"
                + "} else if (((msg == MIDI_NOTE_ON) && (!data2))||\n"
                + "            (msg == MIDI_NOTE_OFF)) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (notePlaying[i] == data1){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      voiceChannel[i] = 0xFF;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "         getVoices()[i].MidiInHandler(dev, port, msg + attr_midichannel, data1, data2);\n"
                + "      }\n"
                + "  }\n"
                + "} else if (msg == MIDI_CONTROL_CHANGE) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "      getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "  if (data1 == 64) {\n"
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].MidiInHandler(dev, port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0);\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "} else if (msg == MIDI_PITCH_BEND) {\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "      getVoices()[i].MidiInHandler(dev, port, MIDI_PITCH_BEND + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "} else {"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel) {\n"
                + "         getVoices()[i].MidiInHandler(dev, port,msg + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "}\n";
        return o;
    }

    // Poly Expression supports the Midi Polyphonic Expression (MPE) Spec
    // Can be used with (or without) the MPE objects
    // the midi channel of the patch is the 'main/global channel'
    AxoObject GenerateAxoObjPolyExpression(AxoObject template) {
        AxoObject o = GenerateAxoObjPoly(template);
        o.sLocalData
                += "int8_t voiceChannel[attr_poly];\n"
                + "int8_t pitchbendRange;\n"
                + "int8_t lowChannel;\n"
                + "int8_t highChannel;\n"
                + "int8_t lastRPN_LSB;\n"
                + "int8_t lastRPN_MSB;\n";
        o.sInitCode
                += "int vc;\n"
                + "for (vc=0;vc<attr_poly;vc++) {\n"
                + "   voiceChannel[vc]=0xFF;\n"
                + "}\n"
                + "lowChannel = attr_midichannel + 1;\n"
                + "highChannel = attr_midichannel + ( 15 - attr_midichannel);\n"
                + "pitchbendRange = 48;\n"
                + "lastRPN_LSB=0xFF;\n"
                + "lastRPN_MSB=0xFF;\n";
        o.sMidiCode = ""
                + "if ( attr_mididevice > 0 && dev > 0 && attr_mididevice != dev) return;\n"
                + "if ( attr_midiport > 0 && port > 0 && attr_midiport != port) return;\n"
                + "int msg = (status & 0xF0);\n"
                + "int channel = (status & 0x0F);\n"
                + "if ((msg == MIDI_NOTE_ON) && (data2)) {\n"
                + "  if (channel == attr_midichannel \n"
                + "   || channel < lowChannel || channel > highChannel)\n"
                + "    return;\n"
                + "  int min = 1<<30;\n"
                + "  int mini = 0;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (voicePriority[i] < min){\n"
                + "      min = voicePriority[i];\n"
                + "      mini = i;\n"
                + "    }\n"
                + "  }\n"
                + "  voicePriority[mini] = 100000 + priority++;\n"
                + "  notePlaying[mini] = data1;\n"
                + "  pressed[mini] = 1;\n"
                + "  voiceChannel[mini] = status & 0x0F;\n"
                + "  getVoices()[mini].MidiInHandler(dev, port, status & 0xF0, data1, data2);\n"
                + "} else if (((msg == MIDI_NOTE_ON) && (!data2))||\n"
                + "            (msg == MIDI_NOTE_OFF)) {\n"
                + "  if (channel == attr_midichannel\n "
                + "   || channel < lowChannel || channel > highChannel)\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++){\n"
                + "    if (notePlaying[i] == data1 && voiceChannel[i] == channel){\n"
                + "      voicePriority[i] = priority++;\n"
                + "      voiceChannel[i] = 0xFF;\n"
                + "      pressed[i] = 0;\n"
                + "      if (!sustain)\n"
                + "         getVoices()[i].MidiInHandler(dev, port, msg + attr_midichannel, data1, data2);\n"
                + "      }\n"
                + "  }\n"
                + "} else if (msg == MIDI_CONTROL_CHANGE) {\n"
                + "  if (data1 == MIDI_C_POLY) {\n" // MPE enable mode
                + "     if (data2 > 0) {\n "
                + "       if (channel == attr_midichannel) {\n"
                + "         if (channel != 15) {\n" // e.g ch 1 (g), we use 2-N notes
                + "           lowChannel = channel + 1;\n"
                + "           highChannel = lowChannel + data2 - 1;\n"
                + "         } else {\n" // ch 16, we use 16(g) 15-N notes
                + "           highChannel = channel - 1;\n"
                + "           lowChannel = highChannel + 1 - data2;\n"
                + "         }\n"
                + "         for(int i=0;i<attr_poly;i++) {\n"
                + "           getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 100, lastRPN_LSB);\n"
                + "           getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 101, lastRPN_MSB);\n"
                + "           getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 6, pitchbendRange);\n"
                + "         }\n" //for
                + "      }\n" //if mainchannel
                + "    } else {\n" // enable/disable
                + "      lowChannel = 0;\n" //disable, we may in the future want to turn this in to normal poly mode
                + "      highChannel = 0;\n"
                + "    }\n"
                + "  }\n"// cc127
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "      getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "  if (data1 == MIDI_C_RPN_MSB || data1 == MIDI_C_RPN_LSB || data1 == MIDI_C_DATA_ENTRY) {\n"
                + "     switch(data1) {\n"
                + "         case MIDI_C_RPN_LSB: lastRPN_LSB = data2; break;\n"
                + "         case MIDI_C_RPN_MSB: lastRPN_MSB = data2; break;\n"
                + "         case MIDI_C_DATA_ENTRY: {\n"
                + "             if (lastRPN_LSB == 0 && lastRPN_MSB == 0) {\n"
                + "               for(i=0;i<attr_poly;i++) {\n"
                + "                 if (voiceChannel[i] != channel) {\n" // because already sent above
                + "                   pitchbendRange = data2;\n"
                + "                   getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 100, lastRPN_LSB);\n"
                + "                   getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 101, lastRPN_MSB);\n"
                + "                   getVoices()[i].MidiInHandler(dev, port, MIDI_CONTROL_CHANGE + attr_midichannel, 6, pitchbendRange);\n"
                + "                 }\n" // if
                + "               }\n" //for
                + "             }\n" // if lsb/msb=0
                + "           }\n" // case 6
                + "           break;\n"
                + "         default: break;\n"
                + "     }\n" //switch
                + "  } else if (data1 == 64) {\n" //end //cc 101,100,6, cc64
                + "    if (data2>0) {\n"
                + "      sustain = 1;\n"
                + "    } else if (sustain == 1) {\n"
                + "      sustain = 0;\n"
                + "      for(i=0;i<attr_poly;i++){\n"
                + "        if (pressed[i] == 0) {\n"
                + "          getVoices()[i].MidiInHandler(dev, port, MIDI_NOTE_ON + attr_midichannel, notePlaying[i], 0);\n"
                + "        }\n"
                + "      }\n"
                + "    }\n" //sus=1
                + "  }\n" //cc64
                + "} else if (msg == MIDI_PITCH_BEND) {\n"
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "      getVoices()[i].MidiInHandler(dev, port, MIDI_PITCH_BEND + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "} else {" // end pb, other midi
                + "  if (channel != attr_midichannel\n"
                + "    && (channel < lowChannel || channel > highChannel))\n"
                + "    return;\n"
                + "  int i;\n"
                + "  for(i=0;i<attr_poly;i++) {\n"
                + "    if (voiceChannel[i] == channel || channel == attr_midichannel) {\n"
                + "         getVoices()[i].MidiInHandler(dev, port, msg + attr_midichannel, data1, data2);\n"
                + "    }\n"
                + "  }\n"
                + "}\n"; // other midi
        return o;
    }

    public void WriteCode() {
        String c = GenerateCode3();

        try {
            String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";
            FileOutputStream f = new FileOutputStream(buildDir + "/xpatch.cpp");
            f.write(c.getBytes());
            f.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, ex.toString());
        }
        Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Generate code complete");
    }

    /*
     void ApplyPreset(int i) { // OBSOLETE
     presetNo = i;
     if (presets == null) {
     presets = new ArrayList<>();
     }
     while (presets.size()<8) presets.add(new PresetObsolete());
     if (i>0) {
     PresetObsolete p = presets.get(i-1);
     for(AxoObjectInstance o:objectinstances){
     for(ParameterInstance a:o.parameterInstances){
     a.SetPresetState(false);
     a.ppc = null;
     }
     }
     for(PresetParameterChange ppc:p.paramchanges){
     ppc.ref.ppc = ppc;
     ppc.ref.SetValueRaw(ppc.newValue);
     ppc.ref.SetPresetState(true);
     }
     } else if (i==0) {
     if (initPreset == null){
     initPreset = new InitPreset();
     initPreset.patch = this;
     }
     for(AxoObjectInstance o:objectinstances){
     for(ParameterInstance a:o.parameterInstances){
     a.SetPresetState(false);
     a.ppc = null;
     }
     }
     for(PresetParameterChange ppc:initPreset.paramchanges){
     ppc.ref.ppc = ppc;
     ppc.ref.SetValueRaw(ppc.newValue);
     ppc.ref.SetPresetState(true);
     }
     SaveToInitPreset();
     } else {
     for(AxoObjectInstance o:objectinstances){
     for(ParameterInstance a:o.parameterInstances){
     a.SetPresetState(false);
     a.ppc = null;
     }
     }
     }
     }
     */
    void ClearCurrentPreset() {
    }

    void CopyCurrentToInit() {
    }

    void DifferenceToPreset() {
        /*
         for(AxoObjectInstance o:objectinstances){
         for(ParameterInstance param:o.parameterInstances){
         // find corresponding in init
         PresetParameterChange ppc = null;
         for (PresetParameterChange ppc1:initPreset.paramchanges){
         if (ppc1.ref == param) {
         ppc=ppc1;
         break;
         }
         }
         if (ppc!=null) { // ppc = param in preset
         if (ppc.newValue != param.GetValueRaw()) {
         IncludeParameterInPreset(param);
         }
         }
         }
         }*/
    }

    /*
     PresetParameterChange IncludeParameterInPreset(ParameterInstance param) {
     if (presetNo>0){
     for (PresetParameterChange ppc:presets.get(presetNo-1).paramchanges){
     if (ppc.ref == param) return ppc;
     }
     PresetParameterChange ppc = new PresetParameterChange();
     ppc.newValue = param.GetValueRaw();
     ppc.paramName = param.getName();
     ppc.ref = param;
     presets.get(presetNo-1).paramchanges.add(ppc);
     param.SetPresetState(true);
     return ppc;
     }
     return null;
     }

     void ExcludeParameterFromPreset(ParameterInstance param) {
     if (presetNo>0)
     presets.get(presetNo-1).ExcludeParameter(param);
     }

     void SaveToInitPreset(){
     for(AxoObjectInstance o:objectinstances){
     for(ParameterInstance param:o.parameterInstances){
     PresetParameterChange ppc = null;
     for (PresetParameterChange ppc1:initPreset.paramchanges){
     if (ppc1.ref == param) {
     ppc=ppc1;
     break;
     }
     }
     if (ppc == null) {
     ppc = new PresetParameterChange();
     ppc.paramName = param.getName();
     ppc.ref = param;
     initPreset.paramchanges.add(ppc);
     }
     ppc.newValue = param.GetValueRaw();
     }
     }
     }
     */
    //final int NPRESETS = 8;
    //final int NPRESET_ENTRIES = 32;
    public int[] DistillPreset(int i) {
        int[] pdata;
        pdata = new int[settings.GetNPresetEntries() * 2];
        for (int j = 0; j < settings.GetNPresetEntries(); j++) {
            pdata[j * 2] = -1;
        }
        int index = 0;
        for (AxoObjectInstanceAbstract o : objectinstances) {
            for (ParameterInstance param : o.getParameterInstances()) {
                ParameterInstance p7 = (ParameterInstance) param;
                Preset p = p7.GetPreset(i);
                if (p != null) {
                    pdata[index * 2] = p7.getIndex();
                    pdata[index * 2 + 1] = p.value.getRaw();
                    index++;
                    if (index == settings.GetNPresetEntries()) {
                        Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "more than {0}entries in preset, skipping...", settings.GetNPresetEntries());
                        return pdata;
                    }
                }
            }
        }
        /*
         System.out.format("preset data : %d\n",i);
         for(int j=0;j<pdata.length/2;j++){
         System.out.format("  %d : %d\n",pdata[j*2],pdata[j*2+1] );
         }
         */
        return pdata;
    }

    public void transferObjectConnections(AxoObjectInstanceZombie oldObject, AxoObjectInstance newObject) {
        transferObjectConnections(oldObject.getInletInstances(),
                oldObject.getOutletInstances(),
                newObject);
    }

    public void transferObjectConnections(AxoObjectInstance oldObject, AxoObjectInstance newObject) {
        transferObjectConnections(oldObject.inletInstances,
                oldObject.outletInstances,
                newObject);
    }

    public void transferObjectConnections(ArrayList<InletInstance> inletInstances,
            ArrayList<OutletInstance> outletInstances,
            AxoObjectInstance newObject) {
        for (int i = 0; i < outletInstances.size(); i++) {
            OutletInstance oldOutletInstance = outletInstances.get(i);
            try {
                OutletInstance newOutletInstance = newObject.outletInstances.get(i);
                Net net = GetNet(oldOutletInstance);

                if (net != null) {
                    ArrayList<InletInstance> dest = (ArrayList< InletInstance>) net.dest.clone();
                    disconnect(oldOutletInstance);
                    for (InletInstance ii : dest) {
                        AddConnection(ii, newOutletInstance);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        for (int i = 0; i < inletInstances.size(); i++) {
            InletInstance oldInletInstance = inletInstances.get(i);
            try {
                InletInstance newInletInstance = newObject.inletInstances.get(i);
                Net net = GetNet(oldInletInstance);
                if (net != null) {
                    ArrayList<OutletInstance> source = (ArrayList< OutletInstance>) net.source.clone();
                    disconnect(oldInletInstance);
                    for (OutletInstance oi : source) {
                        AddConnection(newInletInstance, oi);
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public void transferInputValues(AxoObjectInstance oldObject, AxoObjectInstance newObject) {
        for (int i = 0; i < oldObject.getParameterInstances().size(); i++) {
            ParameterInstance oldParameterInstance = oldObject.getParameterInstances().get(i);
            try {
                ParameterInstance newParameterInstance = newObject.getParameterInstances().get(i);
                newParameterInstance.CopyValueFrom(oldParameterInstance);
                newObject.setDirty(true);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        for (int i = 0; i < oldObject.getAttributeInstances().size(); i++) {
            AttributeInstance oldAttributeInstance = oldObject.getAttributeInstances().get(i);
            try {
                AttributeInstance newAttributeInstance = newObject.getAttributeInstances().get(i);
                newAttributeInstance.CopyValueFrom(oldAttributeInstance);
                newObject.setDirty(true);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
    }

    public void transferState(AxoObjectInstance oldObject, AxoObjectInstance newObject) {
        transferObjectConnections(oldObject, newObject);
        transferInputValues(oldObject, newObject);
    }

    public AxoObjectInstanceAbstract ChangeObjectInstanceType1(AxoObjectInstanceAbstract oldObject, AxoObjectAbstract newObjectType) {
        if ((oldObject instanceof AxoObjectInstancePatcher) && (newObjectType instanceof AxoObjectPatcher)) {
            return oldObject;
        } else if ((oldObject instanceof AxoObjectInstancePatcherObject) && (newObjectType instanceof AxoObjectPatcherObject)) {
            return oldObject;
        } else if (oldObject instanceof AxoObjectInstance) {
            String n = oldObject.getInstanceName();
            oldObject.setInstanceName(n + Constants.TEMP_OBJECT_SUFFIX);
            AxoObjectInstanceAbstract newObject = AddObjectInstance(newObjectType, new Point(oldObject.getX(), oldObject.getY()));

            if (newObject instanceof AxoObjectInstance) {
                transferState((AxoObjectInstance) oldObject, (AxoObjectInstance) newObject);
            }
            return newObject;
        } else if (oldObject instanceof AxoObjectInstanceZombie) {
            AxoObjectInstanceAbstract newObject = AddObjectInstance(newObjectType, new Point(oldObject.getX(), oldObject.getY()));
            if ((newObject instanceof AxoObjectInstance)) {
                transferObjectConnections((AxoObjectInstanceZombie) oldObject, (AxoObjectInstance) newObject);
            }
            return newObject;
        }
        return oldObject;
    }

    public AxoObjectInstanceAbstract ChangeObjectInstanceType(AxoObjectInstanceAbstract oldObject, AxoObjectAbstract newObjectType) {
        AxoObjectInstanceAbstract newObject = ChangeObjectInstanceType1(oldObject, newObjectType);
        if (newObject != oldObject) {
            delete(oldObject);
            setDirty();
        }
        return newObject;
    }

    /**
     *
     * @param initial If true, only objects restored from object name reference
     * (not UUID) will promote to a variant with the same name.
     */
    public boolean PromoteOverloading(boolean initial) {
        refreshIndexes();
        Set<String> ProcessedInstances = new HashSet<String>();
        boolean p = true;
        boolean promotionOccured = false;
        while (p && !(ProcessedInstances.size() == objectinstances.size())) {
            p = false;
            for (AxoObjectInstanceAbstract o : objectinstances) {
                if (!ProcessedInstances.contains(o.getInstanceName())) {
                    ProcessedInstances.add(o.getInstanceName());
                    if (!initial || o.isTypeWasAmbiguous()) {
                        promotionOccured |= o.PromoteToOverloadedObj();
                    }
                    p = true;
                    break;
                }
            }
        }
        if (!(ProcessedInstances.size() == objectinstances.size())) {
            for (AxoObjectInstanceAbstract o : objectinstances) {
                if (!ProcessedInstances.contains(o.getInstanceName())) {
                    Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "PromoteOverloading : fault in {0}", o.getInstanceName());
                }
            }
        }
        return promotionOccured;
    }

    public InletInstance getInletByReference(String objname, String inletname) {
        if (objname == null) {
            return null;
        }
        if (inletname == null) {
            return null;
        }
        AxoObjectInstanceAbstract o = GetObjectInstance(objname);
        if (o == null) {
            return null;
        }
        return o.GetInletInstance(inletname);
    }

    public OutletInstance getOutletByReference(String objname, String outletname) {
        if (objname == null) {
            return null;
        }
        if (outletname == null) {
            return null;
        }
        AxoObjectInstanceAbstract o = GetObjectInstance(objname);
        if (o == null) {
            return null;
        }
        return o.GetOutletInstance(outletname);
    }

    public String GetCurrentWorkingDirectory() {
        if (FileNamePath == null) {
            return null;
        }
        int i = FileNamePath.lastIndexOf(File.separatorChar);
        if (i < 0) {
            return null;
        }
        return FileNamePath.substring(0, i);
    }

    public Rectangle getWindowPos() {
        return windowPos;
    }

    public String getNotes() {
        return notes;
    }

    public ArrayList<SDFileReference> GetDependendSDFiles() {
        ArrayList<SDFileReference> files = new ArrayList<SDFileReference>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            ArrayList<SDFileReference> f2 = o.GetDependendSDFiles();
            if (f2 != null) {
                files.addAll(f2);
            }
        }
        return files;
    }

    public File getBinFile() {
        String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";;
        return new File(buildDir + "/xpatch.bin");
    }

    public boolean canUndo() {
        return currentState > 0;
    }

    public boolean canRedo() {
        return currentState < undoStates.size() - 1;
    }

    public void undo() {
        if (canUndo()) {
            currentState -= 1;
            loadState();
            setDirty();
        }
    }

    public void redo() {
        if (canRedo()) {
            currentState += 1;
            loadState();
            setDirty();
        }
    }

    public ArrayList<AxoObjectInstanceAbstract> getObjectInstances() {
        return objectinstances;
    }

    public ArrayList<Modulator> getModulators() {
        return Modulators;
    }

    public void addModulator(Modulator m) {
        if (Modulators == null) {
            Modulators = new ArrayList<Modulator>();
        }
        Modulators.add(m);
    }

    public void addObjectInstance(AxoObjectInstanceAbstract o) {
        objectinstances.add(o);
    }

    public ArrayList<Net> getNets() {
        return nets;
    }

    public void addNet(Net n) {
        nets.add(n);
    }

    public void addModelChangedListener(ModelChangedListener listener) {
        modelChangedListeners.add(listener);
    }

    public void notifyModelChangedListeners() {
        for (ModelChangedListener m : modelChangedListeners) {
            m.modelChanged();
        }
    }

    void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        if (v.isEmpty()) {
            return;
        }
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchModel p = serializer.read(PatchModel.class, v);
            Map<String, String> dict = new HashMap<String, String>();
            ArrayList<AxoObjectInstanceAbstract> obj2 = (ArrayList<AxoObjectInstanceAbstract>) p.objectinstances.clone();
            for (AxoObjectInstanceAbstract o : obj2) {
                o.patchModel = this;
                AxoObjectAbstract obj = o.resolveType();
                if (obj != null) {
                    Modulator[] m = obj.getModulators();
                    if (m != null) {
                        if (Modulators == null) {
                            Modulators = new ArrayList<Modulator>();
                        }
                        for (Modulator mm : m) {
                            mm.objinst = o;
                            Modulators.add(mm);
                        }
                    }
                } else {
                    //o.patch = this;
                    p.objectinstances.remove(o);
                    AxoObjectInstanceZombie zombie = new AxoObjectInstanceZombie(new AxoObjectZombie(), this, o.getInstanceName(), new Point(o.getX(), o.getY()));
                    zombie.patchModel = this;
                    zombie.typeName = o.typeName;
                    p.objectinstances.add(zombie);
                }
            }
            int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
            for (AxoObjectInstanceAbstract o : p.objectinstances) {
                String original_name = o.getInstanceName();
                if (original_name != null) {
                    String new_name = original_name;
                    String ss[] = new_name.split("_");
                    boolean hasNumeralSuffix = false;
                    try {
                        if ((ss.length > 1) && (Integer.toString(Integer.parseInt(ss[ss.length - 1]))).equals(ss[ss.length - 1])) {
                            hasNumeralSuffix = true;
                        }
                    } catch (NumberFormatException e) {
                    }
                    if (hasNumeralSuffix) {
                        int n = Integer.parseInt(ss[ss.length - 1]) + 1;
                        String bs = original_name.substring(0, original_name.length() - ss[ss.length - 1].length());
                        while (GetObjectInstance(new_name) != null) {
                            new_name = bs + n++;
                        }
                        while (dict.containsKey(new_name)) {
                            new_name = bs + n++;
                        }
                    } else {
                        while (GetObjectInstance(new_name) != null) {
                            new_name = new_name + "_";
                        }
                        while (dict.containsKey(new_name)) {
                            new_name = new_name + "_";
                        }
                    }
                    if (!new_name.equals(original_name)) {
                        o.setInstanceName(new_name);
                    }
                    dict.put(original_name, new_name);
                }
                if (o.getX() < minX) {
                    minX = o.getX();
                }
                if (o.getY() < minY) {
                    minY = o.getY();
                }
                o.patchModel = this;
                objectinstances.add(o);
                int newposx = o.getX();
                int newposy = o.getY();

                if (pos != null) {
                    // paste at cursor position, with delta snapped to grid
                    newposx += Constants.X_GRID * ((pos.x - minX + Constants.X_GRID / 2) / Constants.X_GRID);
                    newposy += Constants.Y_GRID * ((pos.y - minY + Constants.Y_GRID / 2) / Constants.Y_GRID);
                }
                while (getObjectAtLocation(newposx, newposy) != null) {
                    newposx += Constants.X_GRID;
                    newposy += Constants.Y_GRID;
                }
                o.setLocation(newposx, newposy);
            }
            for (Net n : p.nets) {
                InletInstance connectedInlet = null;
                OutletInstance connectedOutlet = null;
                if (n.source != null) {
                    ArrayList<OutletInstance> source2 = new ArrayList<OutletInstance>();
                    for (OutletInstance o : n.source) {
                        String objname = o.getObjname();
                        String outletname = o.getOutletname();
                        if ((objname != null) && (outletname != null)) {
                            String on2 = dict.get(objname);
                            if (on2 != null) {
//                                o.name = on2 + " " + r[1];
                                OutletInstance i = new OutletInstance();
                                i.outletname = outletname;
                                i.objname = on2;
                                source2.add(i);
                            } else if (restoreConnectionsToExternalOutlets) {
                                AxoObjectInstanceAbstract obj = GetObjectInstance(objname);
                                if ((obj != null) && (connectedOutlet == null)) {
                                    OutletInstance oi = obj.GetOutletInstance(outletname);
                                    if (oi != null) {
                                        connectedOutlet = oi;
                                    }
                                }
                            }
                        }
                    }
                    n.source = source2;
                }
                if (n.dest != null) {
                    ArrayList<InletInstance> dest2 = new ArrayList<InletInstance>();
                    for (InletInstance o : n.dest) {
                        String objname = o.getObjname();
                        String inletname = o.getInletname();
                        if ((objname != null) && (inletname != null)) {
                            String on2 = dict.get(objname);
                            if (on2 != null) {
                                InletInstance i = new InletInstance();
                                i.inletname = inletname;
                                i.objname = on2;
                                dest2.add(i);
                            }
                        }
                    }
                    n.dest = dest2;
                }
                if (n.source.size() + n.dest.size() > 1) {
                    if ((connectedInlet == null) && (connectedOutlet == null)) {
                        n.patchModel = this;
                        nets.add(n);
                    } else if (connectedInlet != null) {
                        for (InletInstance o : n.dest) {
                            InletInstance o2 = getInletByReference(o.getObjname(), o.getInletname());
                            if ((o2 != null) && (o2 != connectedInlet)) {
                                AddConnection(connectedInlet, o2);
                            }
                        }
                        for (OutletInstance o : n.source) {
                            OutletInstance o2 = getOutletByReference(o.getObjname(), o.getOutletname());
                            if (o2 != null) {
                                AddConnection(connectedInlet, o2);
                            }
                        }
                    } else if (connectedOutlet != null) {
                        for (InletInstance o : n.dest) {
                            InletInstance o2 = getInletByReference(o.getObjname(), o.getInletname());
                            if (o2 != null) {
                                AddConnection(o2, connectedOutlet);
                            }
                        }
                    }
                }
            }
            setDirty();
        } catch (javax.xml.stream.XMLStreamException ex) {
            // silence
        } catch (Exception ex) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    AxoObjectInstanceAbstract getObjectAtLocation(int x, int y) {
        for (AxoObjectInstanceAbstract o : getObjectInstances()) {
            if ((o.getX() == x) && (o.getY() == y)) {
                return o;
            }
        }
        return null;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}
