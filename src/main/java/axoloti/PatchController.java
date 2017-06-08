package axoloti;

import axoloti.inlets.InletInstance;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.mvc.array.ArrayController;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.ObjectInstanceController;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.outlets.OutletInstance;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import qcmds.QCmdCompileModule;
import qcmds.QCmdCompilePatch;
import qcmds.QCmdProcessor;
import qcmds.QCmdRecallPreset;
import qcmds.QCmdUploadFile;

public class PatchController extends AbstractController<PatchModel, AbstractView> {

    public final static String PATCH_LOCKED = "Locked";
    public final static String PATCH_FILENAME = "FileNamePath";
    public final static String PATCH_DSPLOAD = "DspLoad";
    public final static String PATCH_OBJECTINSTANCES = "Objectinstances";
    public final static String PATCH_NETS = "Nets";

    public PatchController(PatchModel model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
        objectInstanceControllers = new ArrayController(model.objectinstances, documentRoot);
        netControllers = new ArrayController(model.nets, documentRoot);
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
        for(String module : getModel().getModules()) {
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

    public void UploadToSDCard(String sdfilename) {
        getModel().WriteCode();
        Logger.getLogger(PatchFrame.class.getName()).log(Level.INFO, "sdcard filename:{0}", sdfilename);
        QCmdProcessor qcmdprocessor = QCmdProcessor.getQCmdProcessor();
        qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
        for(String module : getModel().getModules()) {
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

    public boolean delete(ObjectInstanceController o) {
        boolean deletionSucceeded = false;
        if (o == null) {
            return deletionSucceeded;
        }
        for (InletInstance ii : o.getModel().getInletInstances()) {
            disconnect(ii);
        }
        for (OutletInstance oi : o.getModel().getOutletInstances()) {
            disconnect(oi);
        }
        int i;
        for (i = getModel().Modulators.size() - 1; i >= 0; i--) {
            Modulator m1 = getModel().Modulators.get(i);
            if (m1.objinst == o.getModel()) {
                getModel().Modulators.remove(m1);
                for (Modulation mt : m1.Modulations) {
                    mt.destination.removeModulation(mt);
                }
            }
        }
        AxoObjectAbstract t = o.getModel().getType();
        if (o != null) {
            //            o.Close();
            t.DeleteInstance(o.getModel());
        }
        boolean succeeded = objectInstanceControllers.remove(o.getModel());
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
            AxoObjectInstanceAbstract objinst = obj.CreateInstance(getModel(), n + i, loc);

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

    public void WriteCode() {
        getModel().WriteCode();
    }

    public void setPresetUpdatePending(boolean updatePending) {
        getModel().presetUpdatePending = updatePending;
    }

    public boolean isPresetUpdatePending() {
        return getModel().presetUpdatePending;
    }

    Dimension GetSize() {
        // hmmm don't know which view...
        return new Dimension(500,500); // patchView.GetSize();
    }

    public PatchSettings getSettings() {
        return getModel().settings;
    }

    public void ShowCompileFail() {
        // TODO: fixme
        // patchView.ShowCompileFail();
    }

    void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        getModel().paste(v, pos, restoreConnectionsToExternalOutlets);
    }

    @Deprecated
    public void repaintPatchView() {
        // TODO: fixme
        //patchView.repaint();
    }

    @Deprecated // needs to ask PatchView
    public Point getViewLocationOnScreen() {
        // fake it for now
        return new Point(100,100); //patchView.getLocationOnScreen();
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
        setModelProperty(PATCH_LOCKED, (Boolean)locked);
    }

    void setDspLoad(int DSPLoad) {
        setModelProperty(PATCH_DSPLOAD, (Integer)DSPLoad);
    }

    public Net getNetDraggingModel() {
        return new Net(getModel());
    }

    // ------------- new objectinstances MVC stuff
    ArrayController<ObjectInstanceController> objectInstanceControllers;
    ArrayController<NetController> netControllers;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PATCH_OBJECTINSTANCES)) {
            objectInstanceControllers.syncControllers();
        }
        super.propertyChange(evt);
    }

    public NetController getNetFromInlet(InletInstance il) {
        for (NetController c : netControllers) {
            for (InletInstance d : c.getModel().dest) {
                if (d == il) {
                    return c;
                }
            }
        }
        return null;
    }

    public NetController getNetFromOutlet(OutletInstance il) {
        for (NetController c : netControllers) {
            for (OutletInstance d : c.getModel().source) {
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
            Net n = new Net(getModel());
            NetController nc = (NetController) netControllers.add(n);
            nc.connectInlet(il);
            nc.connectOutlet(ol);
            Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: new net added");
            return n;
        } else if (n1 == n2) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: already connected");
            return null;
        } else if ((n1 != null) && (n2 == null)) {
            if (n1.getModel().source.isEmpty()) {
                Logger.getLogger(PatchModel.class.getName()).log(Level.FINE, "connect: adding outlet to inlet net");
                n1.connectOutlet(ol);
                return n1.getModel();
            } else {
                disconnect(il);
                Net n = new Net(getModel());
                NetController nc = (NetController) netControllers.add(n);
                nc.connectInlet(il);
                nc.connectOutlet(ol);
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
        if (il.getObjectInstance().patchModel != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        if (ol.getObjectInstance().patchModel != getModel()) {
            Logger.getLogger(PatchModel.class.getName()).log(Level.INFO, "can't connect: different patch");
            return null;
        }
        NetController n1, n2;
        n1 = getNetFromInlet(il);
        n2 = getNetFromInlet(ol);
        if ((n1 == null) && (n2 == null)) {
            Net n = new Net(getModel());
            NetController nc = (NetController) netControllers.add(n);
            nc.connectInlet(il);
            nc.connectInlet(ol);
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
            if (n.getModel().source.isEmpty() && n.getModel().dest.size() == 1) {
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
            if (n.getModel().dest.isEmpty() && n.getModel().source.size() == 1) {
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

}
