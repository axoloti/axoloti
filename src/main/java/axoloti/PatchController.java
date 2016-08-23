package axoloti;

import axoloti.inlets.IInletInstanceView;
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
import qcmds.QCmdCompilePatch;
import qcmds.QCmdProcessor;
import qcmds.QCmdRecallPreset;
import qcmds.QCmdUploadFile;

public class PatchController {

    public PatchModel patchModel;
    public PatchView patchView;
    public PatchFrame patchFrame;

    public PatchController() {
    }

    public void setPatchModel(PatchModel patchModel) {
        this.patchModel = patchModel;
    }

    public void setPatchView(PatchView patchView) {
        this.patchView = patchView;
    }

    public void setPatchFrame(PatchFrame patchFrame) {
        this.patchFrame = patchFrame;
    }

    QCmdProcessor GetQCmdProcessor() {
        if (patchFrame == null) {
            return null;
        }
        return patchFrame.qcmdprocessor;
    }

    public PatchFrame getPatchFrame() {
        return patchFrame;
    }

    public boolean canUndo() {
        return !isLocked() && patchModel.canUndo();
    }

    public boolean canRedo() {
        return !isLocked() && patchModel.canRedo();
    }

    public void RecallPreset(int i) {
        GetQCmdProcessor().AppendToQueue(new QCmdRecallPreset(i));
    }

    public void ShowPreset(int i) {
        patchView.ShowPreset(i);
    }

    public void Compile() {
        GetQCmdProcessor().AppendToQueue(new QCmdCompilePatch(this));
    }

    void UploadDependentFiles(String sdpath) {
        ArrayList<SDFileReference> files = patchModel.GetDependendSDFiles();
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
        patchModel.WriteCode();
        Logger.getLogger(PatchFrame.class.getName()).log(Level.INFO, "sdcard filename:{0}", sdfilename);
        QCmdProcessor qcmdprocessor = QCmdProcessor.getQCmdProcessor();
        qcmdprocessor.AppendToQueue(new qcmds.QCmdStop());
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
        if (patchModel.isDirty()) {
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
        qcmdprocessor.AppendToQueue(new qcmds.QCmdUploadFile(patchModel.getBinFile(), sdfilename, cal));

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

    private void pushUndoState(boolean changeOccurred) {
        if (changeOccurred) {
            pushUndoState();
        }
    }

    private void finalizeModelChange(boolean changeOccurred) {
        if (changeOccurred) {
            pushUndoState();
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
            Net net = patchModel.AddConnection(il.getInletInstance(), ol.getOutletInstance());
            pushUndoState(net != null);
            return net;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "can't add connection: locked");
            return null;
        }
    }

    public Net AddConnection(IInletInstanceView il, IInletInstanceView ol) {
        if (!isLocked()) {
            Net net = patchModel.AddConnection(il.getInletInstance(), ol.getInletInstance());
            pushUndoState(net != null);
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
        patchModel.setFileNamePath(FileNamePath);
        if (getPatchFrame() != null) {
            getPatchFrame().setTitle(FileNamePath);
        }
    }

    public boolean delete(IAxoObjectInstanceView o) {
        boolean succeeded = patchModel.delete((AxoObjectInstanceAbstract) o.getModel());
        o.getModel().Close();
        return succeeded;
    }

    public AxoObjectInstanceAbstract AddObjectInstance(AxoObjectAbstract obj, Point loc) {
        if (!isLocked()) {
            AxoObjectInstanceAbstract object = patchModel.AddObjectInstance(obj, loc);
            pushUndoState(object != null);
            return object;
        } else {
            Logger.getLogger(PatchController.class.getName()).log(Level.INFO, "can't add connection: locked!");
            return null;
        }
    }

    public String GetCurrentWorkingDirectory() {
        return patchModel.GetCurrentWorkingDirectory();
    }

    public void setDirty() {
        patchModel.setDirty();
    }

    public String getFileNamePath() {
        return patchModel.getFileNamePath();
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
        patchModel.WriteCode();
    }

    public void setPresetUpdatePending(boolean updatePending) {
        patchModel.presetUpdatePending = updatePending;
    }

    public boolean isPresetUpdatePending() {
        return patchModel.presetUpdatePending;
    }

    Dimension GetSize() {
        return patchView.GetSize();
    }

    public PatchSettings getSettings() {
        return patchModel.settings;
    }

    public void ShowCompileFail() {
        patchView.ShowCompileFail();
    }

    void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        patchModel.paste(v, pos, restoreConnectionsToExternalOutlets);
        pushUndoState();
    }

    public void undo() {
        patchModel.undo();
        patchFrame.updateUndoRedoEnabled();
    }

    public void redo() {
        patchModel.redo();
        patchFrame.updateUndoRedoEnabled();
    }

    public void repaintPatchView() {
        patchView.repaint();
    }

    public Point getViewLocationOnScreen() {
        return patchView.getLocationOnScreen();
    }

    public PatchView getPatchView() {
        return patchView;
    }

    public AxoObjectInstanceAbstract ChangeObjectInstanceType(AxoObjectInstanceAbstract obj, AxoObjectAbstract objType) {
        AxoObjectInstanceAbstract newObject = patchModel.ChangeObjectInstanceType(obj, objType);
        pushUndoState(newObject != obj);
        return newObject;
    }

    public boolean isLoadingUndoState() {
        return patchModel.isLoadingUndoState();
    }

    public void clearLoadingUndoState() {
        patchModel.setLoadingUndoState(false);
    }

    public boolean isLocked() {
        return patchModel.isLocked();
    }

    public void setLocked(boolean locked) {
        patchModel.setLocked(locked);
    }

    public void pushUndoState() {
        patchModel.pushUndoState();
        patchFrame.updateUndoRedoEnabled();
    }

    public void popUndoState() {
        patchModel.popUndoState();
    }

    public Net getNetDraggingModel() {
        return new Net(patchModel);
    }
}
