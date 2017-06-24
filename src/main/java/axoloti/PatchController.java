package axoloti;

import static axoloti.PatchModel.USE_EXECUTION_ORDER;
import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectFile;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectInstanceFactory;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.object.ObjectController;
import axoloti.object.ObjectInstanceController;
import axoloti.object.ObjectInstancePatcherController;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.outlets.OutletInstance;
import axoloti.utils.Constants;
import axoloti.utils.Preferences;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import qcmds.QCmdCompileModule;
import qcmds.QCmdCompilePatch;
import qcmds.QCmdProcessor;
import qcmds.QCmdRecallPreset;
import qcmds.QCmdUploadFile;

public class PatchController extends AbstractController<PatchModel, AbstractView, AbstractController> {

    public final static String PATCH_LOCKED = "Locked";
    public final static String PATCH_FILENAME = "FileNamePath";
    public final static String PATCH_DSPLOAD = "DspLoad";
    public final static String PATCH_OBJECTINSTANCES = "Objectinstances";
    public final static String PATCH_NETS = "Nets";

    public PatchController(PatchModel model, AbstractDocumentRoot documentRoot, AbstractController parentController) {
        super(model, documentRoot, parentController);
        if (model.settings == null) {
            model.settings = new PatchSettings();
        }
        // Now it is the time to cleanup the model, replace object instances with linked objects
        ArrayList<AxoObjectInstanceAbstract> unlinked_object_instances = (ArrayList<AxoObjectInstanceAbstract>) model.objectinstances.getArray().clone();
        model.objectinstances.clear();
        for (AxoObjectInstanceAbstract unlinked_object_instance : unlinked_object_instances) {
            add_unlinked_objectinstance(unlinked_object_instance);
        }

        objectInstanceControllers = new ArrayController<ObjectInstanceController, AxoObjectInstanceAbstract, PatchController>(model.objectinstances, documentRoot, this) {

            @Override
            public ObjectInstanceController createController(AxoObjectInstanceAbstract model, AbstractDocumentRoot documentRoot, PatchController parent) {
                if (model instanceof AxoObjectInstancePatcher) {
                    return new ObjectInstancePatcherController((AxoObjectInstancePatcher) model, documentRoot, parent);
                } else {
                    return new ObjectInstanceController(model, documentRoot, parent);
                }
            }
        };
        netControllers = new ArrayController<NetController, Net, PatchController>(model.nets, documentRoot, this) {

            @Override
            public NetController createController(Net model, AbstractDocumentRoot documentRoot, PatchController parent) {
                return new NetController(model, documentRoot, parent);
            }
        };
    }

    QCmdProcessor GetQCmdProcessor() {
        return QCmdProcessor.getQCmdProcessor();
    }

    public void RecallPreset(int i) {
        GetQCmdProcessor().AppendToQueue(new QCmdRecallPreset(i));
    }

    public void ShowPreset(int i) {
        // TODO : fix preset logic
        //patchView.ShowPreset(i);
    }

    public void Compile() {
        for (String module : getModel().getModules()) {
            GetQCmdProcessor().AppendToQueue(new QCmdCompileModule(this,
                    module,
                    getModel().getModuleDir(module)));
        }
        GetQCmdProcessor().AppendToQueue(new QCmdCompilePatch(this));
    }

    void UploadDependentFiles(String sdpath) {
        ArrayList<SDFileReference> files = getModel().GetDependendSDFiles();
        for (SDFileReference fref : files) {
            File f = fref.localfile;
            if (f == null) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "File not resolved: {0}", fref.targetPath);
                continue;
            }
            if (!f.exists()) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "File does not exist: {0}", f.getName());
                continue;
            }
            if (!f.canRead()) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Can't read file {0}", f.getName());
                continue;
            }
            String targetfn = fref.targetPath;
            if (targetfn.isEmpty()) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "Target filename empty {0}", f.getName());
                continue;
            }
            if (targetfn.charAt(0) != '/') {
                targetfn = sdpath + "/" + fref.targetPath;
            }
            if (!SDCardInfo.getInstance().exists(targetfn, f.lastModified(), f.length())) {
                GetQCmdProcessor().AppendToQueue(new qcmds.QCmdGetFileInfo(targetfn));
                GetQCmdProcessor().WaitQueueFinished();
                GetQCmdProcessor().AppendToQueue(new qcmds.QCmdPing());
                GetQCmdProcessor().WaitQueueFinished();
                if (!SDCardInfo.getInstance().exists(targetfn, f.lastModified(), f.length())) {
                    if (f.length() > 8 * 1024 * 1024) {
                        Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "file {0} is larger than 8MB, skip uploading", f.getName());
                        continue;
                    }
                    for (int i = 1; i < targetfn.length(); i++) {
                        if (targetfn.charAt(i) == '/') {
                            GetQCmdProcessor().AppendToQueue(new qcmds.QCmdCreateDirectory(targetfn.substring(0, i)));
                            GetQCmdProcessor().WaitQueueFinished();
                        }
                    }
                    GetQCmdProcessor().AppendToQueue(new QCmdUploadFile(f, targetfn));
                } else {
                    Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "file {0} matches timestamp and size, skip uploading", f.getName());
                }
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "file {0} matches timestamp and size, skip uploading", f.getName());
            }
        }
    }

    String GenerateCode3() {
        Preferences prefs = MainFrame.prefs;
/* FIXME: use "controllerObject"
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
*/
        getModel().CreateIID();
        //TODO - use execution order, rather than UI ordering
        if (USE_EXECUTION_ORDER) {
            getModel().SortByExecution();
        } else {
            getModel().SortByPosition();
        }
        PatchViewCodegen codegen = new PatchViewCodegen(this);               
        String c = codegen.GenerateCode4();
        return c;
    }    
 
    public PatchViewCodegen WriteCode() {
//        String c = GenerateCode3();
        getModel().CreateIID();
        if (USE_EXECUTION_ORDER) {
            getModel().SortByExecution();
        } else {
            getModel().SortByPosition();
        }
        PatchViewCodegen codegen = new PatchViewCodegen(this);               
        String c = codegen.GenerateCode4();
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
        return codegen;
    }
   
    
    public void UploadToSDCard(String sdfilename) {
        WriteCode();
        Logger.getLogger(PatchFrame.class.getName()).log(Level.INFO, "sdcard filename:{0}", sdfilename);
        QCmdProcessor qcmdprocessor = QCmdProcessor.getQCmdProcessor();
        qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
        for (String module : getModel().getModules()) {
            qcmdprocessor.AppendToQueue(new QCmdCompileModule(this,
                    module,
                    getModel().getModuleDir(module)
            ));
        }
        qcmdprocessor.AppendToQueue(new qcmds.QCmdCompilePatch(this));
        // create subdirs...

        for (int i = 1; i < sdfilename.length(); i++) {
            if (sdfilename.charAt(i) == '/') {
                qcmdprocessor.AppendToQueue(new qcmds.QCmdCreateDirectory(sdfilename.substring(0, i)));
                qcmdprocessor.WaitQueueFinished();
            }
        }
        qcmdprocessor.WaitQueueFinished();
        Calendar cal;
        if (true) { // getModel().isDirty()) {
            cal = Calendar.getInstance();
        } else {
            cal = Calendar.getInstance();
            if (getFileNamePath() != null && !getFileNamePath().isEmpty()) {
                File f = new File(getFileNamePath());
                if (f.exists()) {
                    cal.setTimeInMillis(f.lastModified());
                }
            }
        }
        qcmdprocessor.AppendToQueue(new qcmds.QCmdUploadPatchSD(sdfilename, cal));

        String dir;
        int i = sdfilename.lastIndexOf("/");
        if (i > 0) {
            dir = sdfilename.substring(0, i);
        } else {
            dir = "";
        }
        UploadDependentFiles(dir);
    }

    public void UploadToSDCard() {
        UploadToSDCard("/" + getSDCardPath() + "/patch.bin");
    }

    public void setFileNamePath(String FileNamePath) {
        setModelProperty(PATCH_FILENAME, FileNamePath);
        getModel().setFileNamePath(FileNamePath);
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
        for (i = getModel().Modulators.size() - 1; i >= 0; i--) {
            Modulator m1 = getModel().Modulators.get(i);
            if (m1.objinst == o) {
                getModel().Modulators.remove(m1);
                for (Modulation mt : m1.Modulations) {
                    mt.destination.removeModulation(mt);
                }
            }
        }
//        o.getController().removeView(o);
        boolean succeeded = objectInstanceControllers.remove(o);
        return succeeded;
    }

    public AxoObjectInstanceAbstract AddObjectInstance(AxoObjectAbstract obj, Point loc) {
        if (!isLocked()) {

            if (obj == null) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "AddObjectInstance NULL");
                return null;
            }
            int i = 1;
            String n = obj.getDefaultInstanceName() + "_";
            while (getModel().GetObjectInstance(n + i) != null) {
                i++;
            }
            AxoObjectInstanceAbstract objinst = AxoObjectInstanceFactory.createView(obj.createController(null, null), this, n + i, loc);

            Modulator[] m = obj.getModulators();
            if (m != null) {
                if (getModel().Modulators == null) {
                    getModel().Modulators = new ArrayList<Modulator>();
                }
                for (Modulator mm : m) {
                    mm.objinst = objinst;
                    getModel().Modulators.add(mm);
                }
            }
            objectInstanceControllers.add(objinst);
            return objinst;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "can't add connection: locked!");
            return null;
        }
    }

    public String GetCurrentWorkingDirectory() {
        return getModel().GetCurrentWorkingDirectory();
    }

    public String getFileNamePath() {
        return getModel().getFileNamePath();
    }

    public String getSDCardPath() {
        String FileNameNoPath = getFileNamePath();
        String separator = System.getProperty("file.separator");
        int lastSeparatorIndex = FileNameNoPath.lastIndexOf(separator);
        if (lastSeparatorIndex > 0) {
            FileNameNoPath = FileNameNoPath.substring(lastSeparatorIndex + 1);
        }
        String FileNameNoExt = FileNameNoPath;
        if (FileNameNoExt.endsWith(".axp") || FileNameNoExt.endsWith(".axs") || FileNameNoExt.endsWith(".axh")) {
            FileNameNoExt = FileNameNoExt.substring(0, FileNameNoExt.length() - 4);
        }
        return FileNameNoExt;
    }

    public void setPresetUpdatePending(boolean updatePending) {
        getModel().presetUpdatePending = updatePending;
    }

    public boolean isPresetUpdatePending() {
        return getModel().presetUpdatePending;
    }

    Dimension GetSize() {
        // hmmm don't know which view...
        return new Dimension(500, 500); // patchView.GetSize();
    }

    public PatchSettings getSettings() {
        return getModel().settings;
    }

    public void ShowCompileFail() {
        // TODO: fixme
        // patchView.ShowCompileFail();
    }

    private AxoObjectInstanceAbstract getObjectAtLocation(int x, int y) {
        for (AxoObjectInstanceAbstract o : getModel().getObjectInstances()) {
            if ((o.getX() == x) && (o.getY() == y)) {
                return o;
            }
        }
        return null;
    }

    void add_unlinked_objectinstance(AxoObjectInstanceAbstract o) {
            o.setPatchModel(getModel());
            if (o instanceof AxoObjectInstancePatcher) {
                AxoObjectPatcher op = new AxoObjectPatcher("patch/patcher", "");
                AxoObjectInstancePatcher linked_object_instance = op.CreateInstance(this, o.getInstanceName(), o.getLocation(), ((AxoObjectInstancePatcher) o).getSubPatchModel());
                linked_object_instance.applyValues(o);
                getModel().objectinstances.add(linked_object_instance);
            } else if (o instanceof AxoObjectInstancePatcherObject) {
                AxoObjectPatcherObject opo = ((AxoObjectInstancePatcherObject) o).ao;
                ObjectController opoc = new ObjectController(opo, getDocumentRoot());
                AxoObjectInstanceAbstract linked_object_instance = new AxoObjectInstancePatcherObject(opoc, getModel(), o.getInstanceName(), o.getLocation());
                opoc.addView(linked_object_instance);
                linked_object_instance.applyValues(o);
                getModel().objectinstances.add(linked_object_instance);
            } else {
                AxoObjectAbstract t = o.resolveType(getModel().GetCurrentWorkingDirectory());
                AxoObjectInstanceAbstract linked_object_instance = AxoObjectInstanceFactory.createView(t.createController(null, null), this, o.getInstanceName(), o.getLocation());
                linked_object_instance.applyValues(o);
                getModel().objectinstances.add(linked_object_instance);
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
            ArrayList<AxoObjectInstanceAbstract> obj2 = (ArrayList<AxoObjectInstanceAbstract>) p.objectinstances.getArray().clone();
            /*
             for (AxoObjectInstanceAbstract o : obj2) {
             o.patchModel = getModel();
             AxoObjectAbstract obj = o.resolveType();
             if (o instanceof AxoObjectInstance)
             getModel().applyType((AxoObjectInstance)o, obj);
             if (obj != null) {
             Modulator[] m = obj.getModulators();
             if (m != null) {
             if (getModel().Modulators == null) {
             getModel().Modulators = new ArrayList<Modulator>();
             }
             for (Modulator mm : m) {
             mm.objinst = o;
             getModel().Modulators.add(mm);
             }
             }
             } else {
             //o.patch = this;
             p.objectinstances.remove(o);
             AxoObjectZombie z = new AxoObjectZombie();                    
             AxoObjectInstanceZombie zombie = new AxoObjectInstanceZombie(z.createController(null, null), getModel(), o.getInstanceName(), new Point(o.getX(), o.getY()));
             zombie.patchModel = getModel();
             zombie.typeName = o.typeName;
             p.objectinstances.add(zombie);
             }
             }
             */
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
                        while (getModel().GetObjectInstance(new_name) != null) {
                            new_name = bs + n++;
                        }
                        while (dict.containsKey(new_name)) {
                            new_name = bs + n++;
                        }
                    } else {
                        while (getModel().GetObjectInstance(new_name) != null) {
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
                add_unlinked_objectinstance(o);
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
            
            // TODO: review pasting nets!
            for (Net n : p.nets) {
                InletInstance connectedInlet = null;
                OutletInstance connectedOutlet = null;
                ArrayList<OutletInstance> source2 = new ArrayList<OutletInstance>();
                for (OutletInstance o : n.getSources()) {
                    String objname = o.getObjname();
                    String outletname = o.getOutletname();
                    if ((objname != null) && (outletname != null)) {
                        String on2 = dict.get(objname);
                        if (on2 != null) {
//                                o.name = on2 + " " + r[1];
                            OutletInstance i = new OutletInstance(on2, outletname);
                            //i.outletname = outletname;
                            source2.add(i);
                        } else if (restoreConnectionsToExternalOutlets) {
                            // this is untested and probably faulty.
                            AxoObjectInstanceAbstract obj = getModel().GetObjectInstance(objname);
                            if ((obj != null) && (connectedOutlet == null)) {
                                OutletInstance oi = obj.GetOutletInstance(outletname);
                                if (oi != null) {
                                    connectedOutlet = oi;
                                }
                            }
                        }
                    }
                }
                n.setSources(source2.toArray(new OutletInstance[]{}));

                ArrayList<InletInstance> dest2 = new ArrayList<InletInstance>();
                for (InletInstance o : n.getDestinations()) {
                    String objname = o.getObjname();
                    String inletname = o.getInletname();
                    if ((objname != null) && (inletname != null)) {
                        String on2 = dict.get(objname);
                        if (on2 != null) {
                            InletInstance i = new InletInstance(on2, inletname);
                            dest2.add(i);
                        }
                    }
                }
                n.setDestinations(dest2.toArray(new InletInstance[]{}));

                if (n.getSources().length + n.getDestinations().length > 1) {
                    if ((connectedInlet == null) && (connectedOutlet == null)) {
                        /*
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
                    }*/
                        netControllers.add(n);
                    }
                }
            }
        } catch (javax.xml.stream.XMLStreamException ex) {
            // silence
        } catch (Exception ex) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Deprecated // needs to ask PatchView
    public Point getViewLocationOnScreen() {
        // fake it for now
        return new Point(100, 100); //patchView.getLocationOnScreen();
    }

    public AxoObjectInstanceAbstract ChangeObjectInstanceType(AxoObjectInstanceAbstract obj, AxoObjectAbstract objType) {
//        AxoObjectInstanceAbstract newObject = getModel().ChangeObjectInstanceType(obj, objType);
//        return newObject;
        return null;
    }

    public boolean isLocked() {
        return getModel().getLocked();
    }

    public void setLocked(boolean locked) {
        setModelProperty(PATCH_LOCKED, (Boolean) locked);
    }

    void setDspLoad(int DSPLoad) {
        setModelProperty(PATCH_DSPLOAD, (Integer) DSPLoad);
    }

    // ------------- new objectinstances MVC stuff
    ArrayController<ObjectInstanceController, AxoObjectInstanceAbstract, PatchController> objectInstanceControllers;
    ArrayController<NetController, Net, PatchController> netControllers;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PATCH_OBJECTINSTANCES)) {
            objectInstanceControllers.syncControllers();
        }
        super.propertyChange(evt);
    }

    public NetController getNetFromInlet(InletInstance il) {
        for (NetController c : netControllers) {
            for (InletInstance d : c.getModel().getDestinations()) {
                if (d == il) {
                    return c;
                }
            }
        }
        return null;
    }

    public NetController getNetFromOutlet(OutletInstance il) {
        for (NetController c : netControllers) {
            for (OutletInstance d : c.getModel().getSources()) {
                if (d == il) {
                    return c;
                }
            }
        }
        return null;
    }

    public Net AddConnection(InletInstance il, OutletInstance ol) {
        if (il.getObjectInstance().getPatchModel() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getObjectInstance().getPatchModel() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        NetController n1, n2;
        n1 = getNetFromInlet(il);
        n2 = getNetFromOutlet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(new OutletInstance[]{ol}, new InletInstance[]{il});
            netControllers.add(n);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: already connected");
            return null;
        } else if ((n1 != null) && (n2 == null)) {
            if (n1.getModel().getSources().length == 0) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: adding outlet to inlet net");
                n1.connectOutlet(ol);
                return n1.getModel();
            } else {
                disconnect(il);
                Net n = new Net(new OutletInstance[]{ol}, new InletInstance[]{il});
                netControllers.add(n);
                getModel().nets.add(n);
                Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
                return n;
            }
        } else if ((n1 == null) && (n2 != null)) {
            n2.connectInlet(il);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: add additional outlet");
            return n2.getModel();
        } else if ((n1 != null) && (n2 != null)) {
            // inlet already has connect, and outlet has another
            // replace
            disconnect(il);
            n2.connectInlet(il);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: replace inlet with existing net");
            return n2.getModel();
        }
        return null;
    }

    public Net AddConnection(InletInstance il, InletInstance ol) {
        if (il == ol) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: same inlet");
            return null;
        }
        if (il.getObjectInstance().getPatchModel() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getObjectInstance().getPatchModel() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        NetController n1, n2;
        n1 = getNetFromInlet(il);
        n2 = getNetFromInlet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(new OutletInstance[]{}, new InletInstance[]{il, ol});
            netControllers.add(n);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: already connected");
        } else if ((n1 != null) && (n2 == null)) {
            n1.connectInlet(ol);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: inlet added");
            return n1.getModel();
        } else if ((n1 == null) && (n2 != null)) {
            n2.connectInlet(il);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: inlet added");
            return n2.getModel();
        } else if ((n1 != null) && (n2 != null)) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: both inlets included in net");
            return null;
        }
        return null;
    }

    public Net disconnect(InletInstance io) {
        NetController n = getNetFromInlet(io);
        if (n != null) {
            if ((n.getModel().getDestinations().length + n.getModel().getSources().length == 2)) {
                delete(n);
            } else {
                n.disconnect(io);
            }
        }

        return null;
    }

    public Net disconnect(OutletInstance io) {
        NetController n = getNetFromOutlet(io);
        if (n != null) {
            if ((n.getModel().getDestinations().length + n.getModel().getSources().length == 2)) {
                delete(n);
            } else {
                n.disconnect(io);
            }
        }
        return null;
    }

    public NetController delete(NetController n) {
        netControllers.remove(n.getModel());
        return n;
    }

    @Deprecated // no longer in use?
    void ExportAxoObj(File f1) {
        String fnNoExtension = f1.getName().substring(0, f1.getName().lastIndexOf(".axo"));
        
        getModel().SortByPosition();
        // cheating here by creating a new controller...
        PatchViewCodegen codegen = new PatchViewCodegen(this);
        AxoObject ao = codegen.GenerateAxoObj(new AxoObject());
        ao.setDescription(getModel().getFileNamePath());
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

    public void SelectNone() {
        for (AxoObjectInstanceAbstract o : getModel().getObjectInstances()) {
            o.setSelected(false);
        }
    }

    void SelectAll() {
        for (AxoObjectInstanceAbstract o : getModel().getObjectInstances()) {
            o.setSelected(true);
        }
    }

    /*
    void deleteSelectedAxoObjectInstanceViews() {
        Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "deleteSelectedAxoObjInstances()");
        if (!isLocked()) {
            ArrayList<ObjectInstanceController> selected = getController().getSelectedObjects();
            if (!selected.isEmpty()) {
                getController().addMetaUndo("delete objects");
                for (ObjectInstanceController o : selected) {
                    getController().delete(o.getModel());
                }
            }
        } else {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "Can't delete: locked!");
        }
    }
*/


    List<ObjectInstanceController> getSelectedObjects() {
        ArrayList<ObjectInstanceController> selected = new ArrayList<>();
        for (ObjectInstanceController o : objectInstanceControllers) {
            if (o.getModel().getSelected()) {
                selected.add(o);
            }
        }
        return selected;
    }

}
