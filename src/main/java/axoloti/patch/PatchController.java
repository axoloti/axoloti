package axoloti.patch;

import axoloti.Axoloti;
import axoloti.Modulation;
import axoloti.Modulator;
import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import axoloti.object.AxoObject;
import axoloti.object.AxoObjectFile;
import axoloti.object.AxoObjectFromPatch;
import axoloti.object.AxoObjectPatcher;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.objectlibrary.AxoObjects;
import axoloti.object.IAxoObject;
import axoloti.object.ObjectController;
import axoloti.object.inlet.Inlet;
import static axoloti.patch.PatchModel.USE_EXECUTION_ORDER;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.patch.object.AxoObjectInstance;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.AxoObjectInstanceFactory;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.inlet.InletInstance;
import axoloti.patch.object.iolet.IoletInstance;
import axoloti.patch.object.outlet.OutletInstance;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.preferences.Preferences;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDFileReference;
import axoloti.utils.Constants;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

public class PatchController extends AbstractController<PatchModel, IView, ObjectInstanceController> {

    protected PatchController(PatchModel model) {
        super(model);
        init();
    }

    private void init() {

        // Now it is the time to cleanup the model, replace object instances with linked objects
        ArrayList<IAxoObjectInstance> unlinked_object_instances = new ArrayList<>(getModel().objectinstances);
        getModel().objectinstances.clear();
        for (IAxoObjectInstance unlinked_object_instance : unlinked_object_instances) {
            add_unlinked_objectinstance(unlinked_object_instance);
        }
        PromoteOverloading(true);
    }

    public QCmdProcessor GetQCmdProcessor() {
        return QCmdProcessor.getQCmdProcessor();
    }

    public void RecallPreset(int i) {
        GetQCmdProcessor().AppendToQueue(new QCmdRecallPreset(i));
    }

    public void ShowPreset(int i) {
        // TODO: fix preset logic
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

    public void UploadDependentFiles(String sdpath) {
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
            if (!TargetModel.getTargetModel().getSDCardInfo().exists(targetfn, f.lastModified(), f.length())) {
                try {
                    GetQCmdProcessor().AppendToQueue(new qcmds.QCmdGetFileInfo(targetfn));
                    GetQCmdProcessor().WaitQueueFinished();
                    GetQCmdProcessor().AppendToQueue(new qcmds.QCmdPing());
                    GetQCmdProcessor().WaitQueueFinished();
                    if (!TargetModel.getTargetModel().getSDCardInfo().exists(targetfn, f.lastModified(), f.length())) {
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
                } catch (Exception ex) {
                    Logger.getLogger(PatchController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "file {0} matches timestamp and size, skip uploading", f.getName());
            }
        }
    }

    String GenerateCode3() {
        Preferences prefs = Preferences.getPreferences();
        /* TODO: fix "controller object"
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
        //TODO: (enhancement) use execution order, rather than UI ordering
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
        String buildDir = System.getProperty(Axoloti.HOME_DIR) + "/build";
        return WriteCode(buildDir + "/xpatch");
    }

    public PatchViewCodegen WriteCode(String file_basename) {
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
            FileOutputStream f = new FileOutputStream(file_basename + ".cpp");
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

    public void UploadToFlash() {
        try {
            WriteCode();
            QCmdProcessor qcmdprocessor = QCmdProcessor.getQCmdProcessor();
            qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
            qcmdprocessor.AppendToQueue(new qcmds.QCmdCompilePatch(this));
            qcmdprocessor.WaitQueueFinished();
            IConnection conn = CConnection.GetConnection();
            byte[] bb = PatchFileBinary.getPatchFileBinary();
            // TODO: add test if it really fits in the flash partition, issue #409
            qcmdprocessor.AppendToQueue(new qcmds.QCmdWriteMem(conn.getTargetProfile().getSDRAMAddr(), bb));
            qcmdprocessor.AppendToQueue(new qcmds.QCmdCopyPatchToFlash());
        } catch (Exception ex) {
            Logger.getLogger(PatchController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void UploadToSDCard(String sdfilename) {
        try {
            WriteCode();
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "sdcard filename:{0}", sdfilename);
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
            Calendar cal = Calendar.getInstance();
            if (true) { // getModel().isDirty()) {
                // TODO: use time of last modification?
            } else {
                if (getFileNamePath() != null && !getFileNamePath().isEmpty()) {
                    File f = new File(getFileNamePath());
                    if (f.exists()) {
                        cal.setTimeInMillis(f.lastModified());
                    }
                }
            }
            qcmdprocessor.AppendToQueue(new qcmds.QCmdUploadPatchSD(sdfilename, cal));

            Serializer serializer = new Persister();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(256 * 1024);
            try {
                serializer.write(getModel(), baos);
            } catch (Exception ex) {
                Logger.getLogger(AxoObjects.class.getName()).log(Level.SEVERE, null, ex);
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            String sdfnPatch = sdfilename.substring(0, sdfilename.length() - 3) + "axp";
            qcmdprocessor.AppendToQueue(new qcmds.QCmdUploadFile(bais, sdfnPatch));

            String dir;
            int i = sdfilename.lastIndexOf("/");
            if (i > 0) {
                dir = sdfilename.substring(0, i);
            } else {
                dir = "";
            }
            UploadDependentFiles(dir);
        } catch (Exception ex) {
            Logger.getLogger(PatchController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void UploadToSDCard() {
        UploadToSDCard("/" + getSDCardPath() + "/patch.bin");
    }

    public void setFileNamePath(String FileNamePath) {
        setModelProperty(PatchModel.PATCH_FILENAME, FileNamePath);
    }

    public boolean delete(IAxoObjectInstance o) {
        boolean deletionSucceeded = false;
        if (o == null) {
            return deletionSucceeded;
        }
        disconnect(o);
        ((AxoObjectInstanceAbstract) o).dispose();
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
        boolean succeeded = 
                removeUndoableElementFromList(PatchModel.PATCH_OBJECTINSTANCES, o);
//        if (succeeded) {
//            o.dispose();
//        }
        return succeeded;
    }

    public IAxoObjectInstance addObjectInstance(IAxoObject obj, Point loc) {
        if (true) {
            /*!isLocked()) {*/

            if (obj == null) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "AddObjectInstance NULL");
                return null;
            }
            int i = 1;
            String n = obj.getDefaultInstanceName() + "_";
            while (getModel().findObjectInstance(n + i) != null) {
                i++;
            }
            IAxoObjectInstance objinst;
            /*
            if (obj instanceof AxoObjectPatcher) {
                AxoObjectPatcher objp = (AxoObjectPatcher)obj;
                objp.createController(null, this)

            }
            else*/
            objinst = AxoObjectInstanceFactory.createView((ObjectController)obj.getControllerFromModel(), this, n + i, loc);

            Modulator[] m = obj.getModulators();
            if (m != null) {
                if (getModel().Modulators == null) {
                    getModel().Modulators = new ArrayList<>();
                }
                for (Modulator mm : m) {
                    mm.objinst = objinst;
                    getModel().Modulators.add(mm);
                }
            }
            addUndoableElementToList(PatchModel.PATCH_OBJECTINSTANCES, objinst);
            return objinst;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "can't add connection: locked!");
            return null;
        }
    }

    public void fixNegativeObjectCoordinates() {
        int minx = 0;
        int miny = 0;
        for (IAxoObjectInstance o : getModel().getObjectInstances()) {
            Point p = o.getLocation();
            if (p.x < minx) {
                minx = p.x;
            }
            if (p.y < miny) {
                miny = p.y;
            }
        }
        if ((minx < 0) || (miny < 0)) {
            for (IAxoObjectInstance o : getModel().getObjectInstances()) {
                Point p = o.getLocation();
                o.getControllerFromModel().changeLocation(p.x - minx, p.y - miny);
            }
        }
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

    public void ShowCompileFail() {
        getModel().setLocked(false);
    }

    private IAxoObjectInstance getObjectAtLocation(int x, int y) {
        for (IAxoObjectInstance o : getModel().getObjectInstances()) {
            if ((o.getX() == x) && (o.getY() == y)) {
                return o;
            }
        }
        return null;
    }

    void add_unlinked_objectinstance(IAxoObjectInstance o) {
        AxoObjectInstanceAbstract linked_object_instance;
        if (o instanceof AxoObjectInstancePatcher) {
            AxoObjectPatcher op = new AxoObjectPatcher("patch/patcher", "");
            op.setDocumentRoot(getDocumentRoot());
            linked_object_instance = op.CreateInstance(this, o.getInstanceName(), o.getLocation(), ((AxoObjectInstancePatcher) o).getSubPatchModel());
        } else if (o instanceof AxoObjectInstancePatcherObject) {
            AxoObjectPatcherObject opo = ((AxoObjectInstancePatcherObject) o).ao;
            ObjectController opoc = new ObjectController(opo);
            linked_object_instance = new AxoObjectInstancePatcherObject(opoc, getModel(), o.getInstanceName(), o.getLocation());
            opoc.addView(linked_object_instance);
//                linked_object_instance = AxoObjectInstanceFactory.createView(o.createController(null, null), this, o.getInstanceName(), o.getLocation());
        } else {
            IAxoObject t = o.resolveType(getModel().GetCurrentWorkingDirectory());
            linked_object_instance = AxoObjectInstanceFactory.createView((ObjectController)t.getControllerFromModel(), this, o.getInstanceName(), o.getLocation());
        }
        addUndoableElementToList(PatchModel.PATCH_OBJECTINSTANCES, linked_object_instance);
        linked_object_instance.applyValues(o);
        linked_object_instance.setDocumentRoot(getDocumentRoot());
    }

    public void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        if (v.isEmpty()) {
            return;
        }
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchModel p = serializer.read(PatchModel.class, v);
            Map<String, String> dict = new HashMap<>();
            ArrayList<IAxoObjectInstance> obj2 = new ArrayList<>(p.objectinstances);
            /*
             for (AxoObjectInstanceAbstract o : obj2) {
             o.patchModel = getModel();
             AxoObjectAbstract obj = o.resolveType();
             if (o instanceof AxoObjectInstance)
             getModel().applyType((AxoObjectInstance)o, obj);
             if (obj != null) {
             Modulator[] m = obj.getPatchModulators();
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
            for (IAxoObjectInstance o : p.objectinstances) {
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
                        while (getModel().findObjectInstance(new_name) != null) {
                            new_name = bs + n++;
                        }
                        while (dict.containsKey(new_name)) {
                            new_name = bs + n++;
                        }
                    } else {
                        while (getModel().findObjectInstance(new_name) != null) {
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
                o.setLocation(new Point(newposx, newposy));
                add_unlinked_objectinstance(o);
            }

            // TODO: review pasting nets!
            for (Net n : p.nets) {
                InletInstance connectedInlet = null;
                OutletInstance connectedOutlet = null;
                ArrayList<OutletInstance> source2 = new ArrayList<>();
                for (OutletInstance o : n.getSources()) {
                    String objname = o.getObjname();
                    String outletname = o.getName();
                    if ((objname != null) && (outletname != null)) {
                        String on2 = dict.get(objname);
                        if (on2 != null) {
//                                o.name = on2 + " " + r[1];
                            OutletInstance i = new OutletInstance(on2, outletname);
                            //i.outletname = outletname;
                            source2.add(i);
                        } else if (restoreConnectionsToExternalOutlets) {
                            // this is untested and probably faulty.
                            IAxoObjectInstance obj = getModel().findObjectInstance(objname);
                            if ((obj != null) && (connectedOutlet == null)) {
                                OutletInstance oi = obj.findOutletInstance(outletname);
                                if (oi != null) {
                                    connectedOutlet = oi;
                                }
                            }
                        }
                    }
                }
                n.setSources(source2.toArray(new OutletInstance[]{}));

                ArrayList<InletInstance> dest2 = new ArrayList<>();
                for (InletInstance o : n.getDestinations()) {
                    String objname = o.getObjname();
                    String inletname = o.getName();
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
//                        netControllers.add(n);
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

    public void checkCoherency() {
        for (Net n : getModel().getNets()) {
            for (InletInstance i : n.getDestinations()) {
                IAxoObjectInstance o = i.getParent();
                assert (o.getInletInstances().contains(i));
                assert (getModel().getObjectInstances().contains(o));
                assert (getNetFromIolet(i).getModel() == n);
            }
            for (OutletInstance i : n.getSources()) {
                IAxoObjectInstance o = i.getParent();
                assert (o.getOutletInstances().contains(i));
                assert (getModel().getObjectInstances().contains(o));
                assert (getNetFromIolet(i).getModel() == n);
            }
        }
        for (IAxoObjectInstance o : getModel().getObjectInstances()) {
            assert (o.getParent() == getModel());
            for (ParameterInstance p : o.getParameterInstances()) {
                assert (p.getObjectInstance() == o);
            }
            for (InletInstance p : o.getInletInstances()) {
                assert (p.getParent() == o);
            }
            for (OutletInstance p : o.getOutletInstances()) {
                assert (p.getParent() == o);
            }
        }
    }

    public void disconnect(IAxoObjectInstance o) {
        for (InletInstance i : o.getInletInstances()) {
            disconnect(i);
        }
        for (OutletInstance i : o.getOutletInstances()) {
            disconnect(i);
        }
    }

    public void ConvertToEmbeddedObj(IAxoObjectInstance obj) {
        ChangeObjectInstanceType(obj, new AxoObjectPatcherObject());
    }

    public void ConvertToPatchPatcher(IAxoObjectInstance obj) {
        ChangeObjectInstanceType(obj, new AxoObjectPatcher());
    }

    public AxoObjectInstanceAbstract ChangeObjectInstanceType(IAxoObjectInstance obj, IAxoObject objType) {
        try {
            AxoObjectInstanceAbstract newObj;
            if ((objType instanceof AxoObjectPatcher)
                    && (obj.getType() instanceof AxoObjectFromPatch)) {
                // ConvertToPatchPatcher
                AxoObjectPatcher po = (AxoObjectPatcher) objType;
                ObjectController objc = po.createController();
                Strategy strategy = new AnnotationStrategy();
                Serializer serializer = new Persister(strategy);
                AxoObjectFromPatch ofp = (AxoObjectFromPatch) obj.getType();
                PatchModel pm = serializer.read(PatchModel.class, new File(ofp.getPath()));
                newObj = new AxoObjectInstancePatcher(objc, getModel(), obj.getInstanceName(), obj.getLocation(), pm);
                objc.addView(newObj);
            } else if (objType instanceof AxoObjectPatcherObject) {
                // ConvertToEmbeddedObj
                // clone by serialization/deserialization...
                ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
                Strategy strategy = new AnnotationStrategy();
                Serializer serializer = new Persister(strategy);
                serializer.write(obj.getType(), os);
                ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                AxoObjectPatcherObject of = serializer.read(AxoObjectPatcherObject.class, is);
                ObjectController opoc = new ObjectController(of);
                newObj = new AxoObjectInstancePatcherObject(opoc, getModel(), obj.getInstanceName(), obj.getLocation());
                opoc.addView(newObj);
            } else {
                newObj = AxoObjectInstanceFactory.createView(objType.getControllerFromModel(), this, obj.getInstanceName(), obj.getLocation());
            }
            newObj.applyValues(obj);

            addUndoableElementToList(PatchModel.PATCH_OBJECTINSTANCES, newObj);

            for (InletInstance i : obj.getInletInstances()) {
                NetController n = getNetFromIolet(i);
                if (n != null) {
                    for (InletInstance i2 : newObj.getInletInstances()) {
                        if (i2.getName().equals(i.getName())) {
                            n.connectInlet(i2);
                            break;
                        }
                    }
                }
            }
            for (OutletInstance i : obj.getOutletInstances()) {
                NetController n = getNetFromIolet(i);
                if (n != null) {
                    for (OutletInstance i2 : newObj.getOutletInstances()) {
                        if (i2.getName().equals(i.getName())) {
                            n.connectOutlet(i2);
                            break;
                        }
                    }
                }
            }
            disconnect(obj);
            removeUndoableElementFromList(PatchModel.PATCH_OBJECTINSTANCES, obj);
            return newObj;
        } catch (Exception ex) {
            Logger.getLogger(PatchController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean isLocked() {
        return getModel().getLocked();
    }

    public void setLocked(boolean locked) {
        setModelProperty(PatchModel.PATCH_LOCKED, (Boolean) locked);
    }

    public void setDspLoad(int DSPLoad) {
        setModelProperty(PatchModel.PATCH_DSPLOAD, (Integer) DSPLoad);
    }

    // ------------- new objectinstances MVC stuff

    public NetController getNetFromIolet(IoletInstance il) {
        for (Net n : getModel().getNets()) {
            if (il.isSource()) {
                for (OutletInstance d : n.getSources()) {
                    if (d == il) {
                        return (NetController)n.getControllerFromModel();
                    }
                }
            }
            else {
                for (InletInstance d : n.getDestinations()) {
                    if (d == il) {
                        return (NetController)n.getControllerFromModel();
                    }
                }
            }
        }
        return null;
    }

    public Net AddConnection(InletInstance il, OutletInstance ol) {
        if (il.getParent().getParent() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getParent().getParent() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        NetController n1, n2;
        n1 = getNetFromIolet(il);
        n2 = getNetFromIolet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(getModel(), new OutletInstance[]{ol}, new InletInstance[]{il});
            addUndoableElementToList(PatchModel.PATCH_NETS, n);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can''t connect: already connected");
            return null;
        } else if ((n1 != null) && (n2 == null)) {
            if (n1.getModel().getSources().length == 0) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: adding outlet to inlet net");
                n1.connectOutlet(ol);
                return n1.getModel();
            } else {
                disconnect(il);
                Net n = new Net(getModel(), new OutletInstance[]{ol}, new InletInstance[]{il});
                addUndoableElementToList(PatchModel.PATCH_NETS, n);
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
        if (il.getParent().getParent() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getParent().getParent() != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        NetController n1, n2;
        n1 = getNetFromIolet(il);
        n2 = getNetFromIolet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(getModel(), new OutletInstance[]{}, new InletInstance[]{il, ol});
            addUndoableElementToList(PatchModel.PATCH_NETS, n);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can''t connect: already connected");
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

    /**
     * Disconnect inlet, remove net if there are no other connections left
     *
     * @param inlet
     * @return true if successful
     */
    public boolean disconnect(InletInstance inlet) {
        NetController n = getNetFromIolet(inlet);
        if (n == null) {
            return false;
        }
        if ((n.getModel().getDestinations().length + n.getModel().getSources().length == 2)) {
            delete(n);
        } else {
            n.disconnect(inlet);
        }
        return true;
    }

    /**
     * Disconnect outlet, remove net if there are no other connections left
     *
     * @param outlet
     * @return true if successful
     */
    public boolean disconnect(OutletInstance outlet) {
        NetController n = getNetFromIolet(outlet);
        if (n == null) {
            return false;
        }
        if ((n.getModel().getDestinations().length + n.getModel().getSources().length == 2)) {
            delete(n);
        } else {
            n.disconnect(outlet);
        }
        return true;
    }

    public void delete(NetController n) {
        for (InletInstance io : n.getModel().getDestinations()) {
            io.getControllerFromModel().changeConnected(false);
        }
        for (OutletInstance oi : n.getModel().getSources()) {
            oi.getControllerFromModel().changeConnected(false);
        }
        removeUndoableElementFromList(PatchModel.PATCH_NETS, n.getModel());
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
        for (IAxoObjectInstance o : getModel().getObjectInstances()) {
            o.getControllerFromModel().changeSelected(false);
        }
    }

    public void SelectAll() {
        for (IAxoObjectInstance o : getModel().getObjectInstances()) {
            o.getControllerFromModel().changeSelected(true);
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
    public List<ObjectInstanceController> getSelectedObjects() {
        ArrayList<ObjectInstanceController> selected = new ArrayList<>();
        for (IAxoObjectInstance o : getModel().getObjectInstances()) {
            if (o.getSelected()) {
                selected.add((ObjectInstanceController)o.getControllerFromModel());
            }
        }
        return selected;
    }

    public boolean PromoteToOverloadedObj(IAxoObjectInstance axoObjectInstance) {
        if (axoObjectInstance.getType() instanceof AxoObjectFromPatch) {
            return false;
        }
        if (axoObjectInstance.getType() instanceof AxoObjectPatcher) {
            return false;
        }
        if (axoObjectInstance.getType() instanceof AxoObjectPatcherObject) {
            return false;
        }
        String id = axoObjectInstance.getType().getId();
        List<IAxoObject> candidates = AxoObjects.getAxoObjects().GetAxoObjectFromName(id, axoObjectInstance.getParent().GetCurrentWorkingDirectory());
        if (candidates == null) {
            return false;
        }
        if (candidates.isEmpty()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.SEVERE, "could not resolve any candidates {0}", id);
        }
        if (candidates.size() == 1) {
            return false;
        }
        int[] ranking;
        ranking = new int[candidates.size()];
        for (InletInstance j : axoObjectInstance.getInletInstances()) {
            NetController n = getNetFromIolet(j);
            if (n == null) {
                continue;
            }
            DataType d = n.getModel().getDataType();
            if (d == null) {
                continue;
            }
            String name = j.getModel().getName();
            for (int i = 0; i < candidates.size(); i++) {
                IAxoObject o = candidates.get(i);
                Inlet i2 = null;
                for (Inlet i3 : o.getInlets()) {
                    if (name.equals(i3.getName())) {
                        i2 = i3;
                        break;
                    }
                }
                if (i2 == null) {
                    continue;
                }
                if (i2.getDatatype().equals(d)) {
                    ranking[i] += 10;
                } else if (d.IsConvertableToType(i2.getDatatype())) {
                    ranking[i] += 2;
                }
            }
        }
        int max = -1;
        int maxi = 0;
        for (int i = 0; i < candidates.size(); i++) {
            if (ranking[i] > max) {
                max = ranking[i];
                maxi = i;
            }
        }
        IAxoObject selected = candidates.get(maxi);
        int rindex = candidates.indexOf(axoObjectInstance.getType());
        if (rindex >= 0) {
            if (ranking[rindex] == max) {
                selected = axoObjectInstance.getType();
            }
        }
        if (selected == null) {
            //Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.INFO,"no promotion to null" + this + " to " + selected);
            return false;
        }
        if (selected != axoObjectInstance.getType()) {
            Logger.getLogger(AxoObjectInstance.class.getName()).log(Level.FINE, "promoting {0} to {1}", new Object[]{axoObjectInstance, selected});
            ChangeObjectInstanceType(axoObjectInstance, selected);
            return true;
        }
        return false;
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
    AxoObjectInstanceAbstract newObject = addObjectInstance(newObjectType, new Point(oldObject.getX(), oldObject.getY()));
     if (newObject instanceof AxoObjectInstance) {
    transferState((AxoObjectInstance) oldObject, (AxoObjectInstance) newObject);
    }
    return newObject;
    } else if (oldObject instanceof AxoObjectInstanceZombie) {
    AxoObjectInstanceAbstract newObject = addObjectInstance(newObjectType, new Point(oldObject.getX(), oldObject.getY()));
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
        PatchModel patchModel = getModel();
        patchModel.refreshIndexes();
        Set<String> ProcessedInstances = new HashSet<>();
        boolean p = true;
        boolean promotionOccured = false;
        while (p && !(ProcessedInstances.size() == patchModel.objectinstances.size())) {
            p = false;
            for (IAxoObjectInstance o : patchModel.objectinstances) {
                if (!ProcessedInstances.contains(o.getInstanceName())) {
                    ProcessedInstances.add(o.getInstanceName());
                    if (!initial || o.isTypeWasAmbiguous()) {
                        promotionOccured |= PromoteToOverloadedObj(o);
                    }
                    p = true;
                    break;
                }
            }
        }
        if (!(ProcessedInstances.size() == patchModel.objectinstances.size())) {
            for (IAxoObjectInstance o : patchModel.objectinstances) {
                if (!ProcessedInstances.contains(o.getInstanceName())) {
                    Logger.getLogger(PatchModel.class.getName()).log(Level.SEVERE, "PromoteOverloading : fault in {0}", o.getInstanceName());
                }
            }
        }
        return promotionOccured;
    }

}
