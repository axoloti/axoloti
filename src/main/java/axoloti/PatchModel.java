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
import axoloti.attributedefinition.AxoAttribute;
import axoloti.displays.Display;
import axoloti.displays.DisplayInstance;
import axoloti.inlets.Inlet;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.array.ArrayModel;
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
import axoloti.outlets.Outlet;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.Parameter;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.AxolotiLibrary;
import axoloti.utils.Constants;
import axoloti.utils.Preferences;
import java.awt.Point;
import java.awt.Rectangle;
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
public class PatchModel extends AbstractModel {

    //TODO - use execution order, rather than UI ordering
    static final boolean USE_EXECUTION_ORDER = false;

    @Attribute(required = false)
    String appVersion;
    public @ElementListUnion({
        @ElementList(entry = "obj", type = AxoObjectInstance.class, inline = true, required = false),
        @ElementList(entry = "patcher", type = AxoObjectInstancePatcher.class, inline = true, required = false),
        @ElementList(entry = "patchobj", type = AxoObjectInstancePatcherObject.class, inline = true, required = false),
        @ElementList(entry = "comment", type = AxoObjectInstanceComment.class, inline = true, required = false),
        @ElementList(entry = "hyperlink", type = AxoObjectInstanceHyperlink.class, inline = true, required = false),
        @ElementList(entry = "zombie", type = AxoObjectInstanceZombie.class, inline = true, required = false)})
    ArrayModel<AxoObjectInstanceAbstract> objectinstances = new ArrayModel<AxoObjectInstanceAbstract>();
    @ElementList(name = "nets")
    public ArrayModel<Net> nets = new ArrayModel<Net>();
    @Element(required = false)
    PatchSettings settings;
    @Element(required = false, data = true)
    String notes = "";
    @Element(required = false)
    Rectangle windowPos;
    String FileNamePath;
    ArrayList<ParameterInstance> ParameterInstances = new ArrayList<ParameterInstance>();
    ArrayList<DisplayInstance> DisplayInstances = new ArrayList<DisplayInstance>();
    ArrayList<Modulator> Modulators = new ArrayList<Modulator>();
    @Element(required = false)
    String helpPatch;

    Integer dspLoad;

    // patch this patch is contained in
    private PatchModel container = null;
    // controller object
    AxoObjectInstanceAbstract controllerObjectInstance;

    public boolean presetUpdatePending = false;

    boolean locked = false;

    @Override
    public PatchController createController(AbstractDocumentRoot documentRoot) {
        return new PatchController(this, documentRoot);
    }

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

    private void applyType(AxoObjectInstance obji, AxoObjectAbstract objtype) {
        // WARNING: only call after deserialization, before nets are reconstructed, or controllers are created
        ArrayList<AttributeInstance> pAttributeInstances = (ArrayList<AttributeInstance>) obji.getAttributeInstances().getArray().clone();
        ArrayList<ParameterInstance> pParameterInstances = (ArrayList<ParameterInstance>) obji.getParameterInstances().getArray().clone();

        if (objtype instanceof AxoObject) {
            AxoObject objtype1 = (AxoObject) objtype;

            obji.inletInstances.clear();
            for (Inlet inl : objtype1.inlets) {
                InletInstance inlin = new InletInstance(inl, obji);
                obji.inletInstances.add(inlin);
            }

            obji.outletInstances.clear();
            for (Outlet o : objtype1.outlets) {
                OutletInstance oin = new OutletInstance(o, obji);
                obji.outletInstances.add(oin);
            }

            obji.attributeInstances.clear();
            for (AxoAttribute p : objtype1.attributes) {
                AttributeInstance attrp1 = null;
                for (AttributeInstance attrp : pAttributeInstances) {
                    if (attrp.getAttributeName().equals(p.getName())) {
                        attrp1 = attrp;
                        break;
                    }
                }
                AttributeInstance attri = p.CreateInstance(obji, attrp1);
                obji.attributeInstances.add(attri);
            }

            obji.parameterInstances.clear();
            for (Parameter p : objtype1.params) {
                ParameterInstance pin = p.CreateInstance(obji);
                for (ParameterInstance pinp : pParameterInstances) {
                    if (pinp.getName().equals(pin.getName())) {
                        pin.CopyValueFrom(pinp);
                        break;
                    }
                }
                obji.parameterInstances.add(pin);
            }

            obji.displayInstances.clear();
            for (Display p : objtype1.displays) {
                DisplayInstance pin = p.CreateInstance(obji);
                obji.displayInstances.add(pin);
            }
        }
    }

/*
    public void applyType(AxoObjectInstance obji, AxoObjectAbstract objtype) {

        AttributeInstance pAttributeInstances[] = (AttributeInstance[])obji.getAttributeInstances().toArray();
        InletInstance pInletInstances[] = (InletInstance[])obji.getInletInstances().toArray();
        OutletInstance pOutletInstances[] = (OutletInstance[])obji.getOutletInstances().toArray();
        ParameterInstance pParameterInstances[] = (ParameterInstance[])obji.getParameterInstances().toArray();

        ArrayList<AttributeInstance> nAttributeInstances = new ArrayList<>();
        ArrayList<InletInstance> nInletInstances = new ArrayList<>();
        ArrayList<OutletInstance> nOutletInstances = new ArrayList<>();
        ArrayList<ParameterInstance> nParameterInstances = new ArrayList<>();
        ArrayList<DisplayInstance> nDisplayInstances = new ArrayList<>();

        if (objtype instanceof AxoObject) {
            AxoObject objtype1  = (AxoObject)objtype;

            for (Inlet inl : objtype1.inlets) {
                InletInstance inlinp = null;
                for (InletInstance inlin1 : pInletInstances) {
                    if (inlin1.GetLabel().equals(inl.getName())) {
                        inlinp = inlin1;
                    }
                }
                InletInstance inlin = new InletInstance(inl, obji);
                if (inlinp != null) {
                    Net n = getNet(inlinp);
                    if (n != null) {
                        n.dest.add(inlin);
                    }
                }
                nInletInstances.add(inlin);
            }

            // disconnect stale inlets from nets
//            for (InletInstance inlin1 : pInletInstances) {
//                getPatch().disconnect(inlin1);
//            }

            for (Outlet o : objtype1.outlets) {
                OutletInstance oinp = null;
                for (OutletInstance oinp1 : pOutletInstances) {
                    if (oinp1.GetLabel().equals(o.getName())) {
                        oinp = oinp1;
                    }
                }
                OutletInstance oin = new OutletInstance(o, obji);
                if (oinp != null) {
                    Net n = getNet(oinp);
                    if (n != null) {
                        n.source.add(oin);
                    }
                }
                nOutletInstances.add(oin);
            }

            // disconnect stale outlets from nets
//            for (OutletInstance oinp1 : pOutletInstances) {
//                getPatch().disconnect(oinp1);
//            }

            for (AxoAttribute p : objtype1.attributes) {
                AttributeInstance attrp1 = null;
                for (AttributeInstance attrp : pAttributeInstances) {
                    if (attrp.getName().equals(p.getName())) {
                        attrp1 = attrp;
                    }
                }
                AttributeInstance attri = p.CreateInstance(obji, attrp1);
                nAttributeInstances.add(attri);
            }

            for (Parameter p : objtype1.params) {
                ParameterInstance pin = p.CreateInstance(obji);
                for (ParameterInstance pinp : pParameterInstances) {
                    if (pinp.getName().equals(pin.getName())) {
                        pin.CopyValueFrom(pinp);
                    }
                }
                nParameterInstances.add(pin);
            }

            for (Display p : objtype1.displays) {
                DisplayInstance pin = p.CreateInstance(obji);
                nDisplayInstances.add(pin);
            }
        }
    }
    */
    public void PostContructor() {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            o.patchModel = this;
            AxoObjectAbstract t = o.resolveType();
            if ((t != null) && (t.providesModulationSource())) {
                if (t instanceof AxoObject) {
                    applyType((AxoObjectInstance) o, (AxoObject) t);
                }
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

        ArrayList<AxoObjectInstanceAbstract> obj2 = (ArrayList<AxoObjectInstanceAbstract>) objectinstances.getArray().clone();
        for (AxoObjectInstanceAbstract o : obj2) {
            AxoObjectAbstract t = o.getType();
            if ((t != null) && (!t.providesModulationSource())) {
                o.patchModel = this;
                if (t instanceof AxoObject) {
                    applyType((AxoObjectInstance) o, (AxoObject) t);
                }
                //System.out.println("Obj added " + o.getInstanceName());
            } else if (t == null) {
                objectinstances.remove(o);
                AxoObjectInstanceZombie zombie = new AxoObjectInstanceZombie(new AxoObjectZombie(), this, o.getInstanceName(), new Point(o.getX(), o.getY()));
                zombie.patchModel = this;
                zombie.typeName = o.typeName;
                objectinstances.add(zombie);
            }
        }

        System.out.println("object:(1)");
        for (AxoObjectInstanceAbstract o : objectinstances) {
            System.out.println("  " + o.getType().id + ":" + o.getInstanceName());
        }

        for (Net n : nets) {
            n.patchModel = this;
            n.PostConstructor();
        }
        if (settings == null) {
            settings = new PatchSettings();
        }
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

    @Deprecated
    public void ClearDirty() {
    }

    @Deprecated
    public void setDirty() {
    }

    @Deprecated
    public boolean isDirty() {
        return false;
    }

    public PatchModel container() {
        return container;
    }

    public void setContainer(PatchModel c) {
        container = c;
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

    boolean save(File f) {
        SortByPosition();
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            serializer.write(this, f);
            MainFrame.prefs.addRecentFile(f.getAbsolutePath());
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

        i = 0;
        DisplayInstances = new ArrayList<DisplayInstance>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            for (DisplayInstance p : o.getDisplayInstances()) {
                p.setOffset(offset);
                p.setIndex(i);
                int l = p.getLength();
                offset += l;
                i++;
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
        /*
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
            if (result.contains(c)) {
                result.remove(c);
            }
            result.addFirst(c);
        }
        // prepend their parents
        for (AxoObjectInstanceAbstract c : parents) {
            if (!before.contains(c)) {
                SortParentsByExecution(c, result);
            }
        }
        */
    }

    void SortByExecution() {
        /*
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
/////////        objectinstances = new ArrayModel<AxoObjectInstanceAbstract>(result);
        refreshIndexes();
        */
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
        if (controllerObjectInstance != null) {
            Set<String> i = controllerObjectInstance.getType().GetIncludes();
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

    public HashSet<String> getModules() {
        HashSet<String> modules = new HashSet<>();
        for (AxoObjectInstanceAbstract o : objectinstances) {
            Set<String> i = o.getType().GetModules();
            if (i != null) {
                modules.addAll(i);
            }
        }
        return modules;
    }

    public String getModuleDir(String module) {
        for (AxolotiLibrary lib : MainFrame.prefs.getLibraries()) {
            File f = new File(lib.getLocalLocation() + "modules/" + module);
            if (f.exists() && f.isDirectory()) {
                return lib.getLocalLocation() + "modules/" + module;
            }
        }
        return null;
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
        controllerObjectInstance = null;
        String cobjstr = prefs.getControllerObject();
        if (prefs.isControllerEnabled() && cobjstr != null && !cobjstr.isEmpty()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Using controller object: {0}", cobjstr);
            AxoObjectAbstract x = null;
            ArrayList<AxoObjectAbstract> objs = MainFrame.axoObjects.GetAxoObjectFromName(cobjstr, GetCurrentWorkingDirectory());
            if ((objs != null) && (!objs.isEmpty())) {
                x = objs.get(0);
            }
            if (x != null) {
                controllerObjectInstance = x.CreateInstance(null, "ctrl0x123", new Point(0, 0));
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Unable to created controller for : {0}", cobjstr);
            }
        }

        CreateIID();

        System.out.println("object:(2)");
        for (AxoObjectInstanceAbstract o : objectinstances) {
            System.out.println("  "+o.getType().id+":"+o.getInstanceName());
        }

        //TODO - use execution order, rather than UI ordering
        if (USE_EXECUTION_ORDER) {
            SortByExecution();
        } else {
            SortByPosition();
        }

        System.out.println("object:(3)");
        for (AxoObjectInstanceAbstract o : objectinstances) {
            System.out.println("  "+o.getType().id+":"+o.getInstanceName());
        }
        
        // cheating here by creating a new controller...
        PatchController controller = new PatchController(this, null);
        PatchViewCodegen codegen = new PatchViewCodegen(this, controller);               
        String c = codegen.GenerateCode4();
        return c;
    }

    void ExportAxoObj(File f1) {
        String fnNoExtension = f1.getName().substring(0, f1.getName().lastIndexOf(".axo"));
        
        SortByPosition();
        // cheating here by creating a new controller...
        PatchController controller = new PatchController(this, null);
        PatchViewCodegen codegen = new PatchViewCodegen(this, controller);
        AxoObject ao = codegen.GenerateAxoObj(new AxoObject());
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
    
/*
    public void transferObjectConnections(AxoObjectInstanceZombie oldObject, AxoObjectInstance newObject) {
        transferObjectConnections(oldObject.getInletInstances().getArray(),
                oldObject.getOutletInstances().getArray(),
                newObject);
    }

    public void transferObjectConnections(AxoObjectInstance oldObject, AxoObjectInstance newObject) {
        transferObjectConnections(oldObject.inletInstances.getArray(),
                oldObject.outletInstances.getArray(),
                newObject);
    }
*/
    
    public void transferObjectConnections(ArrayList<InletInstance> inletInstances,
            ArrayList<OutletInstance> outletInstances,
            AxoObjectInstance newObject) { /*
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
        }*/
    }

    public void transferInputValues(AxoObjectInstance oldObject, AxoObjectInstance newObject) {
        // was broken: transfer by name, not by index...
    }

    public void transferState(AxoObjectInstance oldObject, AxoObjectInstance newObject) {
        // was broken: transfer by name, not by index...
    }

    // broken: should move to controller...
    /*
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
//            delete(oldObject);
            setDirty();
        }
        return newObject;
    }
    */
    
    
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

    @Deprecated
    public File getBinFile() {
        String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";
        return new File(buildDir + "/xpatch.bin");
    }

    public ArrayModel<AxoObjectInstanceAbstract> getObjectInstances() {
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

    public ArrayModel<Net> getNets() {
        return nets;
    }

    public void addNet(Net n) {
        nets.add(n);
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
            ArrayList<AxoObjectInstanceAbstract> obj2 = (ArrayList<AxoObjectInstanceAbstract>) p.objectinstances.getArray().clone();
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
                /*
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
                */
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

    public void setLocked(Boolean locked) {
        Boolean oldvalue = this.locked;
        this.locked = locked;
        firePropertyChange(
                PatchController.PATCH_LOCKED,
                oldvalue, locked);
    }

    public Boolean getLocked() {
        return locked;
    }

    public Integer getDspLoad() {
        return dspLoad;
    }

    public void setDspLoad(Integer dspLoad) {
        Integer oldvalue = this.dspLoad;
        this.dspLoad = dspLoad;
        firePropertyChange(
                PatchController.PATCH_DSPLOAD,
                oldvalue, dspLoad);
    }

    // ------------- new objectinstances MVC stuff
    public ArrayModel<AxoObjectInstanceAbstract> getObjectinstances() {
        return objectinstances;
    }

    public void setObjectinstances(ArrayModel<AxoObjectInstanceAbstract> objectinstances) {
        ArrayModel<AxoObjectInstanceAbstract> old_value = this.objectinstances;
        this.objectinstances = objectinstances;
        firePropertyChange(
                PatchController.PATCH_OBJECTINSTANCES,
                old_value, objectinstances);
    }
}
