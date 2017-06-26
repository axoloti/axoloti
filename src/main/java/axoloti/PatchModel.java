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

import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.array.ArrayModel;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectInstanceComment;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectInstanceZombie;
import axoloti.object.AxoObjects;
import axoloti.object.IAxoObject;
import axoloti.outlets.OutletInstance;
import axoloti.parameters.ParameterInstance;
import axoloti.utils.AxolotiLibrary;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
public class PatchModel extends AbstractModel /*implements IAxoObject*/ {

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

    ArrayList<Modulator> Modulators = new ArrayList<Modulator>();
    @Element(required = false)
    String helpPatch;

    Integer dspLoad;

    // a "controller object" is magically added to evey top-level patch
    // (configured in preferences)
    AxoObjectInstanceAbstract controllerObjectInstance;

    public boolean presetUpdatePending = false;

    boolean locked = false;

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

    public PatchModel() {
        super();
        settings = new PatchSettings();
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
    public void setDirty() {
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


    void refreshIndexes() {
        for (AxoObjectInstanceAbstract o : objectinstances) {
            o.refreshIndex();
        }

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

    List<IAxoObject> GetUsedAxoObjects() {
        ArrayList<IAxoObject> aos = new ArrayList<IAxoObject>();
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

    public ArrayModel<Net> getNets() {
        return nets;
    }

    // ------------- new MVC methods
    public String getFileNamePath() {
        return FileNamePath;
    }

    public void setFileNamePath(String FileNamePath) {
        String oldValue = this.FileNamePath;
        this.FileNamePath = FileNamePath;
        firePropertyChange(
                PatchController.PATCH_FILENAME,
                oldValue, FileNamePath);
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        Boolean oldvalue = this.locked;
        this.locked = locked;
        firePropertyChange(
                PatchController.PATCH_LOCKED,
                oldvalue, locked);
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
