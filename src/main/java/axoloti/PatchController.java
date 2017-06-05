package axoloti;

import axoloti.inlets.IInletInstanceView;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.outlets.IOutletInstanceView;
import java.awt.Dimension;
import java.awt.Point;
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
    
    public PatchController(PatchModel model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
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
        if (getModel().isDirty()) {
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

    private void finalizeModelChange(boolean changeOccurred) {
        if (changeOccurred) {
            setDirty();
        }
    }

    public Net disconnect(IInletInstanceView ii) {
        if (!isLocked()) {
            Net net = ii.getInletInstance().disconnect();
            finalizeModelChange(net != null);
            return net;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "Can't disconnect: locked!");
            return null;
        }
    }

    public Net disconnect(IOutletInstanceView oi) {
        if (!isLocked()) {
            Net net = oi.getOutletInstance().disconnect();
            finalizeModelChange(net != null);
            return net;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "Can't disconnect: locked!");
            return null;
        }
    }

    public Net AddConnection(IInletInstanceView il, IOutletInstanceView ol) {
        if (!isLocked()) {
            Net net = getModel().AddConnection(il.getInletInstance(), ol.getOutletInstance());
            return net;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "can't add connection: locked");
            return null;
        }
    }

    public Net AddConnection(IInletInstanceView il, IInletInstanceView ol) {
        if (!isLocked()) {
            Net net = getModel().AddConnection(il.getInletInstance(), ol.getInletInstance());
            return net;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "Can't add connection: locked!");
            return null;
        }
    }

    public void deleteNet(IInletInstanceView ii) {
        if (!isLocked()) {
            Net net = ii.getInletInstance().deleteNet();
            finalizeModelChange(net != null);
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "Can't delete: locked!");
        }
    }

    public void deleteNet(IOutletInstanceView oi) {
        if (!isLocked()) {
            Net net = oi.getOutletInstance().deleteNet();
            finalizeModelChange(net != null);
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "Can't delete: locked!");
        }
    }

    public void setFileNamePath(String FileNamePath) {
        setModelProperty(PATCH_FILENAME, FileNamePath);
        getModel().setFileNamePath(FileNamePath);
    }

    public boolean delete(IAxoObjectInstanceView o) {
        boolean succeeded = getModel().delete((AxoObjectInstanceAbstract) o.getModel());
        o.getModel().Close();
        return succeeded;
    }

    public AxoObjectInstanceAbstract AddObjectInstance(AxoObjectAbstract obj, Point loc) {
        if (!isLocked()) {
            AxoObjectInstanceAbstract object = getModel().AddObjectInstance(obj, loc);
            return object;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "can't add connection: locked!");
            return null;
        }
    }

    public String GetCurrentWorkingDirectory() {
        return getModel().GetCurrentWorkingDirectory();
    }

    public void setDirty() {
        getModel().setDirty();
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

    @Deprecated
    public Point getViewLocationOnScreen() {
        // fake it for now
        return new Point(100,100); //patchView.getLocationOnScreen();
    }

    public AxoObjectInstanceAbstract ChangeObjectInstanceType(AxoObjectInstanceAbstract obj, AxoObjectAbstract objType) {
        AxoObjectInstanceAbstract newObject = getModel().ChangeObjectInstanceType(obj, objType);
        return newObject;
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


}
