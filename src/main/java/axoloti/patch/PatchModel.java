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
package axoloti.patch;

import axoloti.Axoloti;
import axoloti.Version;
import axoloti.abstractui.PatchView;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractModel;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.IAxoObject;
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.objectlibrary.AxoObjects;
import axoloti.objectlibrary.AxolotiLibrary;
import axoloti.patch.net.Net;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceComment;
import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.AxoObjectInstanceZombie;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.preferences.Preferences;
import axoloti.property.BooleanProperty;
import axoloti.property.IntegerProperty;
import axoloti.property.ListProperty;
import axoloti.property.ObjectProperty;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import axoloti.property.StringPropertyNull;
import axoloti.target.fs.SDFileReference;
import axoloti.utils.ListUtils;
import axoloti.utils.StringUtils;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Complete;
import org.simpleframework.xml.core.Persist;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.core.Validate;
import org.simpleframework.xml.strategy.Strategy;

/**
 *
 * @author Johannes Taelman
 */
@Root(name = "patch")
public class PatchModel extends AbstractModel<PatchController> {

    @Attribute(required = false)
    String appVersion;
    @ElementListUnion({
        @ElementList(entry = "obj", type = AxoObjectInstance.class, inline = true, required = false),
        @ElementList(entry = "patcher", type = AxoObjectInstancePatcher.class, inline = true, required = false),
        @ElementList(entry = "patchobj", type = AxoObjectInstancePatcherObject.class, inline = true, required = false),
        @ElementList(entry = "comment", type = AxoObjectInstanceComment.class, inline = true, required = false),
        @ElementList(entry = "hyperlink", type = AxoObjectInstanceHyperlink.class, inline = true, required = false),
        @ElementList(entry = "zombie", type = AxoObjectInstanceZombie.class, inline = true, required = false)})
    List<IAxoObjectInstance> objectinstances;
    @Path("nets")
    @ElementList(inline=true, required=false)
    public List<Net> nets;
    @Element(required = false)
    PatchSettings settings;
    @Element(required = false, data = true)
    private String notes = "";
    @Element(required = false)
    Rectangle windowPos;
    public String FileNamePath; // TODO: move to DocumentRoot property

    @Element(required = false)
    private String helpPatch;

    public boolean presetUpdatePending = false;

    boolean locked = false;

    AxoObjectInstancePatcher container = null;

    @Override
    protected PatchController createController() {
        return new PatchController(this);
    }

    @Override
    public AxoObjectInstancePatcher getParent() {
        return container;
    }

    @Commit
    void commit() {
        for (Net o : getNets()) {
            o.setParent(this);
        }
        for (IAxoObjectInstance o : getObjectInstances()) {
            o.setParent(this);
        }
    }
    
    static public class PatchVersionException
            extends RuntimeException {

        PatchVersionException(String msg) {
            super(msg);
        }
    }

    public static PatchModel open(String patchName, InputStream stream) throws FileNotFoundException {
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchModel patchModel = serializer.read(PatchModel.class, stream);
            patchModel.setFileNamePath(patchName);
            AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
            patchModel.setDocumentRoot(documentRoot);
            PatchController patchController = patchModel.getController();
            documentRoot.getUndoManager().discardAllEdits();
            return patchModel;
        } catch (java.lang.reflect.InvocationTargetException ite) {
            Throwable targetException = ite.getTargetException();
            if (targetException instanceof PatchModel.PatchVersionException) {
                PatchModel.PatchVersionException pve = (PatchModel.PatchVersionException) targetException;
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, "Patch produced with newer version of Axoloti {0} {1}",
                        new Object[]{patchName, pve.getMessage()});
            } else {
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ite);
            }
            return null;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static PatchModel open(File f) throws FileNotFoundException {
        if (!f.canRead()) {
            throw new FileNotFoundException();
        }
        return open(f.getPath(), new FileInputStream(f));
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
                    return Integer.parseInt(v);
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
                        return Integer.parseInt(v);
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
                        return Integer.parseInt(v);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return -1;
    }

    @Validate
    public void validate() {
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
    public void complete() {
        // called after deserialializtion
    }

    @Persist
    public void persist() {
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

    public IAxoObjectInstance findObjectInstance(String n) {
        for (IAxoObjectInstance o : getObjectInstances()) {
            if (n.equals(o.getInstanceName())) {
                return o;
            }
        }
        return null;
    }

    public IAxoObjectInstance findObjectInstance(Point p) {
        for (IAxoObjectInstance o : getObjectInstances()) {
            if (p.equals(o.getLocation())) {
                return o;
            }
        }
        return null;
    }

    public boolean save(File f) {
        sortByPosition();
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            serializer.write(this, f);
            Preferences.getPreferences().addRecentFile(f.getAbsolutePath());
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
        /* // Why would we need an index in objectinstances?
        if (objectinstances == null) {
            return;
        }
        for (int i = 0; i < objectinstances.size(); i++) {
            IAxoObjectInstance o = objectinstances.get(i);
            o.
        }
         */
    }

    public void sortByPosition() {
        ArrayList<IAxoObjectInstance> clone = new ArrayList<>();
        clone.addAll(getObjectInstances());
        Collections.sort(clone);
        setObjectInstances(clone);
        refreshIndexes();
    }

    void sortParentsByExecution(IAxoObjectInstance o, LinkedList<IAxoObjectInstance> result) {
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

    public void sortByExecution() {
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

    private List<IAxoObject> getUsedAxoObjects() {
        ArrayList<IAxoObject> aos = new ArrayList<>();
        for (IAxoObjectInstance o : getObjectInstances()) {
            if (!aos.contains(o.getDModel())) {
                aos.add(o.getDModel());
            }
        }
        return aos;
    }

    public List<String> getIncludes() {
        List<String> includes = new LinkedList<>();
        for (IAxoObjectInstance o : getObjectInstances()) {
            List<String> i = o.getIncludes();
            if (i != null) {
                includes.addAll(i);
            }
        }
        return Collections.unmodifiableList(includes);
    }

    public List<String> getDepends() {
        List<String> depends = new LinkedList<>();
        for (IAxoObjectInstance o : getObjectInstances()) {
            List<String> i = o.getDModel().getDepends();
            if (i != null) {
                depends.addAll(i);
            }
        }
        return Collections.unmodifiableList(depends);
    }

    public List<String> getModules() {
        List<String> modules = new LinkedList<>();
        for (IAxoObjectInstance o : getObjectInstances()) {
            List<String> i = o.getModules();
            if (i != null) {
                modules.addAll(i);
            }
        }
        // uniqify but keep in order
        List<String> modules2 = new LinkedList<>();
        for (String m : modules) {
            if (!modules2.contains(m)) {
                modules2.add(m);
            }
        }
        return Collections.unmodifiableList(modules2);
    }

    public String getModuleDir(String module) {
        for (AxolotiLibrary lib : Preferences.getPreferences().getLibraries()) {
            File f = new File(lib.getLocalLocation() + "modules/" + module);
            if (f.exists() && f.isDirectory()) {
                return lib.getLocalLocation() + "modules/" + module;
            }
        }
        return null;
    }

    int IID = -1; // iid identifies the patch

    public int getIID() {
        return IID;
    }

    public void createIID() {
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
    public void clearCurrentPreset() {
    }

    public void copyCurrentToInit() {
    }

    public void differenceToPreset() {
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

    public String getCurrentWorkingDirectory() {
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

    public void setWindowPos(Rectangle windowPos) {
        this.windowPos = windowPos;
        firePropertyChange(PATCH_WINDOWPOS, null, windowPos);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        firePropertyChange(PATCH_NOTES, null, notes);
    }

    public String getHelpPatch() {
        return helpPatch;
    }

    public void setHelpPatch(String helpPatch) {
        this.helpPatch = helpPatch;
        firePropertyChange(PATCH_HELP_PATCH, null, helpPatch);
    }

    public List<SDFileReference> getDependendSDFiles() {
        LinkedList<SDFileReference> files = new LinkedList<>();
        for (IAxoObjectInstance o : objectinstances) {
            List<SDFileReference> f2 = o.getFileDepends();
            if (f2 != null) {
                files.addAll(f2);
            }
        }
        return Collections.unmodifiableList(files);
    }

    @Deprecated
    public File getBinFile() {
        String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";
        return new File(buildDir + "/xpatch.bin");
    }

    public List<IAxoObjectInstance> getSelectedObjects() {
        LinkedList<IAxoObjectInstance> selected = new LinkedList<>();
        for (IAxoObjectInstance o : getObjectInstances()) {
            if (o.getSelected()) {
                selected.add(o);
            }
        }
        return selected;
    }

    public void sanityCheck() {
        List<IAxoObjectInstance> objs = getObjectInstances();
        for (IAxoObjectInstance obj : objs) {
            if (obj.getParent() != this) {
                throw new Error("object parent is not patch");
            }
        }
        for (Net n : getNets()) {
            if (n.getParent() != this) {
                throw new Error("object parent is not patch");
            }
            for (InletInstance i : n.getDestinations()) {
                IAxoObjectInstance o = i.getParent();
                if (!objs.contains(o)) {
                    throw new Error("net inlet is not in patch");
                }
            }
            for (OutletInstance i : n.getSources()) {
                IAxoObjectInstance o = i.getParent();
                if (!objs.contains(o)) {
                    throw new Error("net outlet is not in patch");
                }
            }
        }
    }

    AxoAttributeComboBox attrMidiChannel = new AxoAttributeComboBox("midichannel",
            new String[]{"inherit", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"},
            new String[]{"attr_midichannel", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"});
    AxoAttributeComboBox attrMidiPort = new AxoAttributeComboBox("midiport",
            new String[]{"1", "2", "3", "4", "5", "6", "7", "8"},
            new String[]{"0", "1", "2", "3", "4", "5", "6", "7"});
    AxoAttributeComboBox attrPoly = new AxoAttributeComboBox("poly",
            new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"},
            new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"});

    // ------------- new MVC methods

    public final static Property PATCH_LOCKED = new BooleanProperty("Locked", PatchModel.class);
    public final static Property PATCH_FILENAME = new StringPropertyNull("FileNamePath", PatchModel.class);
    public final static ListProperty PATCH_OBJECTINSTANCES = new ListProperty("ObjectInstances", PatchModel.class);
    public final static ListProperty PATCH_NETS = new ListProperty("Nets", PatchModel.class);
    public final static Property PATCH_AUTHOR = new StringPropertyNull("Author", PatchModel.class);
    public final static Property PATCH_LICENSE = new StringPropertyNull("License", PatchModel.class);
    public final static Property PATCH_ATTRIBUTIONS = new StringPropertyNull("Attributions", PatchModel.class);
    public final static Property PATCH_SUBPATCHMODE = new ObjectProperty("SubPatchMode", SubPatchMode.class, PatchModel.class);
    public final static Property PATCH_NPRESETENTRIES = new IntegerProperty("NPresetEntries", PatchModel.class);
    public final static Property PATCH_NPRESETS = new IntegerProperty("NPresets", PatchModel.class);
    public final static Property PATCH_NMODULATIONSOURCES = new IntegerProperty("NModulationSources", PatchModel.class);
    public final static Property PATCH_NMODULATIONTARGETSPERSOURCE = new IntegerProperty("NModulationTargetsPerSource", PatchModel.class);
    public final static Property PATCH_MIDICHANNEL = new IntegerProperty("MidiChannel", PatchModel.class);
    public final static Property PATCH_MIDIPORT = new IntegerProperty("MidiPort", PatchModel.class);
    public final static Property PATCH_MIDISELECTOR = new BooleanProperty("MidiSelector", PatchModel.class);
    public final static StringProperty PATCH_NOTES = new StringProperty("Notes", PatchModel.class);
    public final static StringProperty PATCH_HELP_PATCH = new StringProperty("HelpPatch", PatchModel.class);
    public final static Property PATCH_WINDOWPOS = new ObjectProperty("WindowPos", Rectangle.class, PatchModel.class);
    public final static Property PATCH_RECALLPRESET = new IntegerProperty("RecallPreset", PatchModel.class);
//    public final static ListProperty PATCH_MODULATORS = new ListProperty("Modulators", PatchModel.class);

    private final static Property[] PROPERTIES = {
        PATCH_LOCKED,
        PATCH_FILENAME,
        PATCH_OBJECTINSTANCES,
        PATCH_NETS,
        PATCH_AUTHOR,
        PATCH_LICENSE,
        PATCH_ATTRIBUTIONS,
        PATCH_SUBPATCHMODE,
        PATCH_NPRESETENTRIES,
        PATCH_NPRESETS,
        PATCH_NMODULATIONSOURCES,
        PATCH_NMODULATIONTARGETSPERSOURCE,
        PATCH_MIDICHANNEL,
        PATCH_MIDISELECTOR,
        PATCH_WINDOWPOS,
        PATCH_NOTES,
        PATCH_HELP_PATCH,};

    @Override
    public List<Property> getProperties() {
        return Collections.unmodifiableList(Arrays.asList(PROPERTIES));
    }

    public String getFileNamePath() {
        if (FileNamePath == null) {
            return "";
        }
        return FileNamePath;
    }

    public void setFileNamePath(String FileNamePath) {
        String oldValue = this.FileNamePath;
        this.FileNamePath = FileNamePath;
        firePropertyChange(
                PATCH_FILENAME,
                oldValue, FileNamePath);
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
        firePropertyChange(
                PATCH_LOCKED,
                null, locked);
    }

    public List<IAxoObjectInstance> getObjectInstances() {
        return ListUtils.export(objectinstances);
    }

    public void setObjectInstances(List<IAxoObjectInstance> objectinstances) {
        List<IAxoObjectInstance> old_value = this.objectinstances;
        this.objectinstances = objectinstances;
        firePropertyChange(
                PATCH_OBJECTINSTANCES,
                old_value, objectinstances);
    }

    public List<Net> getNets() {
        return ListUtils.export(nets);
    }

    public void setNets(List<Net> nets) {
        List<Net> old_value = this.nets;
        this.nets = nets;
        firePropertyChange(
                PATCH_NETS,
                old_value, nets);
    }

    public String getAuthor() {
        return StringUtils.denullString(getSettings().Author);
    }

    public void setAuthor(String Author) {
        getSettings().Author = Author;
        firePropertyChange(
                PATCH_AUTHOR,
                null, Author);
    }

    public String getLicense() {
        return StringUtils.denullString(getSettings().License);
    }

    public void setLicense(String License) {
        getSettings().License = License;
        firePropertyChange(
                PATCH_LICENSE,
                null, License);
    }

    public String getAttributions() {
        return StringUtils.denullString(getSettings().Attributions);
    }

    public void setAttributions(String Attributions) {
        getSettings().Attributions = Attributions;
        firePropertyChange(
                PATCH_ATTRIBUTIONS,
                null, Attributions);
    }

    public SubPatchMode getSubPatchMode() {
        return settings.subpatchmode;
    }

    public void setSubPatchMode(SubPatchMode mode) {
        settings.subpatchmode = mode;
        AxoObjectInstancePatcher aoip = getParent();
        if (aoip != null) {
            AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getDModel();
            if (mode == SubPatchMode.polyphonic
                    || mode == SubPatchMode.polychannel
                    || mode == SubPatchMode.polyexpression) {
                aop.getController().addAttribute(attrPoly);
            } else {
                aop.getController().removeAttribute(attrPoly);
            }
        }
        firePropertyChange(
                PATCH_SUBPATCHMODE,
                null, mode);
    }

    public Integer getNPresetEntries() {
        if (settings.NPresetEntries == null) {
            return 8;
        }
        return settings.NPresetEntries;
    }

    public void setNPresetEntries(Integer n) {
        settings.NPresetEntries = n;
        firePropertyChange(
                PATCH_NPRESETENTRIES,
                null, n);
    }

    public Integer getNPresets() {
        if (settings.NPresets == null) {
            return 8;
        }
        return settings.NPresets;
    }

    public void setNPresets(Integer n) {
        settings.NPresets = n;
        firePropertyChange(
                PATCH_NPRESETS,
                null, n);
    }

    public Integer getNModulationSources() {
        if (settings.NModulationSources == null) {
            return 8;
        }
        return settings.NModulationSources;
    }

    public void setNModulationSources(Integer n) {
        settings.NModulationSources = n;
        firePropertyChange(
                PATCH_NMODULATIONSOURCES,
                null, n);
    }

    public Integer getNModulationTargetsPerSource() {
        if (settings.NModulationTargetsPerSource == null) {
            return 1;
        }
        return settings.NModulationTargetsPerSource;
    }

    public void setNModulationTargetsPerSource(Integer n) {
        settings.NModulationTargetsPerSource = n;
        firePropertyChange(
                PATCH_NMODULATIONTARGETSPERSOURCE,
                null, n);
    }

    public Integer getMidiChannel() {
        if (settings.MidiChannel == null) {
            return 1;
        }
        return settings.MidiChannel;
    }

    public void setMidiChannel(Integer n) {
        settings.MidiChannel = n;
        firePropertyChange(
                PATCH_MIDICHANNEL,
                null, n);
    }

    public Integer getMidiPort() {
        if (settings.MidiPort == null) {
            return 1;
        }
        return settings.MidiPort;
    }

    public void setMidiPort(Integer n) {
        settings.MidiPort = n;
        firePropertyChange(
                PATCH_MIDIPORT,
                null, n);
    }

    public Boolean getMidiSelector() {
        if (settings.HasMidiChannelSelector == null) {
            return false;
        }
        return settings.HasMidiChannelSelector;
    }

    public void setMidiSelector(Boolean b) {
        settings.HasMidiChannelSelector = b;
        AxoObjectInstancePatcher aoip = getParent();
        if (aoip != null) {
            AxoObjectPatcher aop = (AxoObjectPatcher) aoip.getDModel();
            if (b) {
                aop.getController().addAttribute(attrMidiChannel);
                aop.getController().addAttribute(attrMidiPort);
            } else {
                aop.getController().removeAttribute(attrMidiChannel);
                aop.getController().removeAttribute(attrMidiPort);
            }
        }
        firePropertyChange(
                PATCH_MIDISELECTOR,
                null, b);
    }

    public List<Modulator> getModulators() {
        List<Modulator> modulators = new LinkedList<>();
        for (IAxoObjectInstance obji : getObjectInstances()) {
            modulators.addAll(obji.getModulators());
        }
        return modulators;
    }

    public Integer getRecallPreset() {
        return null;
    }

    public void setRecallPreset(Integer v) {
        firePropertyChange(
                PATCH_RECALLPRESET,
                null, v);
    }

    public void setParent(AxoObjectInstancePatcher container) {
        this.container = container;
        setSubPatchMode(getSubPatchMode());
        setMidiSelector(getMidiSelector());
    }

    @Override
    public void setDocumentRoot(AbstractDocumentRoot documentRoot) {
        super.setDocumentRoot(documentRoot);
        if (objectinstances != null) {
            for (IAxoObjectInstance objInst : objectinstances) {
                objInst.setDocumentRoot(documentRoot);
            }
        }
        for (Net net : getNets()) {
            net.setDocumentRoot(documentRoot);
        }
    }

}
