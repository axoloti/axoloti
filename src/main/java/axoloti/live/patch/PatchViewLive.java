package axoloti.live.patch;

import axoloti.abstractui.PatchView;
import axoloti.chunks.ChunkData;
import axoloti.chunks.ChunkParser;
import axoloti.chunks.Cpatch_display;
import axoloti.chunks.FourCC;
import axoloti.chunks.FourCCs;
import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.live.patch.parameter.ParameterInstanceLiveView;
import axoloti.mvc.View;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDFileReference;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import qcmds.QCmdChangeWorkingDirectory;
import qcmds.QCmdCompileModule;
import qcmds.QCmdCompilePatch;
import qcmds.QCmdCreateDirectory;
import qcmds.QCmdLock;
import qcmds.QCmdMemRead;
import qcmds.QCmdProcessor;
import qcmds.QCmdStart;
import qcmds.QCmdStop;
import qcmds.QCmdUploadPatch;

/**
 *
 * @author jtaelman
 */
public class PatchViewLive extends View<PatchModel> {

    final PatchViewCodegen patchViewCodegen;
    final List<ParameterInstanceLiveView> parameterInstanceViews;

    public PatchViewLive(PatchModel patchModel, PatchViewCodegen patchViewCodegen) {
        super(patchModel);
        this.patchViewCodegen = patchViewCodegen;
        parameterInstanceViews = new ArrayList<>(patchViewCodegen.getParameterInstances().size());
        init(patchModel);
    }

    private void init(PatchModel patchModel) {
        for (ParameterInstanceView v : patchViewCodegen.getParameterInstances()) {
            ParameterInstanceLiveView v1 = new ParameterInstanceLiveView(this, v.getDModel(), v.getIndex());
            v.getDModel().getController().addView(v1);
            parameterInstanceViews.add(v1);
        }
        patchModel.getController().addView(this);
        // only after view is added...
        // but disabled for now... testing invited!

        // enableAutoRecompile();
        //
        // currently only triggers on adding/deleting nets, and adding/deleting objects
        // TODO: auto-recompile: add views to trigger recompilation on changing attributes, nets, and object changes
    }

    private boolean needsPresetUpdate = false;

    public boolean getNeedsPresetUpdate() {
        return needsPresetUpdate;
    }

    public void setNeedsPresetUpdate() {
        needsPresetUpdate = true;
    }

    public void clearNeedsPresetUpdate() {
        needsPresetUpdate = false;
    }

    public byte[] getUpdatedPresetTable() {
        PatchModel patchModel = getDModel();
        byte pb[] = new byte[patchModel.getNPresets() * patchModel.getNPresetEntries() * 8];
        int p = 0;
        for (int i = 0; i < patchModel.getNPresets(); i++) {
            int pi[] = patchViewCodegen.distillPreset(i + 1);
            for (int j = 0; j < patchModel.getNPresetEntries() * 2; j++) {
                pb[p++] = (byte) (pi[j]);
                pb[p++] = (byte) (pi[j] >> 8);
                pb[p++] = (byte) (pi[j] >> 16);
                pb[p++] = (byte) (pi[j] >> 24);
            }
        }
        return pb;
    }

    private boolean auto_recompile = false;

    void enableAutoRecompile() {
        auto_recompile = true;
    }

    public void distributeDataToDisplays(ByteBuffer dispData) {
        dispData.rewind();
        for (DisplayInstanceView d : patchViewCodegen.getDisplayInstances()) {
            d.processByteBuffer(dispData);
        }
    }

    private ReschedulableTimer timerTask;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                        if (timerTask != null) {
                            timerTask.purge();
                        }
                        System.out.println("do recompile...");
                        goLive(getDModel());
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    public void scheduleRecompile() {
        if (timerTask == null) {
            timerTask = new ReschedulableTimer();
            timerTask.schedule(runnable, 1000 /* milliseconds */);
        } else {
            timerTask.reschedule(1000 /* milliseconds */);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (!auto_recompile) {
            return;
        }
        if (PatchModel.PATCH_NETS.is(evt)
                || PatchModel.PATCH_OBJECTINSTANCES.is(evt)) {
            scheduleRecompile();
        }

    }

    @Override
    public void dispose() {
        for (ParameterInstanceLiveView pilv : parameterInstanceViews) {
            pilv.getDModel().getController().removeView(pilv);
        }
        model.getController().removeView(this);
    }

    public List<ParameterInstanceLiveView> getParameterInstances() {
        return Collections.unmodifiableList(parameterInstanceViews);
    }

    static public void goLive(PatchModel patchModel) {
        PatchController patchController = patchModel.getController();
        QCmdProcessor qCmdProcessor = QCmdProcessor.getQCmdProcessor();

        CConnection.getConnection().setPatch(null);
        try {
            qCmdProcessor.waitQueueFinished();
        } catch (Exception ex) {
            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
        }
        qCmdProcessor.appendToQueue(new QCmdStop());
        if (CConnection.getConnection().getSDCardPresent()) {

            String f = "/" + patchController.getSDCardPath();
            //System.out.println("pathf" + f);
            if (TargetModel.getTargetModel().getSDCardInfo().find(f) == null) {
                qCmdProcessor.appendToQueue(new QCmdCreateDirectory(f));
            }
            qCmdProcessor.appendToQueue(new QCmdChangeWorkingDirectory(f));
            patchController.uploadDependentFiles(f);
        } else {
            // issue warning when there are dependent files
            ArrayList<SDFileReference> files = patchModel.getDependendSDFiles();
            if (files.size() > 0) {
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, "Patch requires file {0} on SDCard, but no SDCard mounted", files.get(0).targetPath);
            }
        }

        PatchViewCodegen pvcg = patchController.writeCode();
        qCmdProcessor.setPatchController(null);
        for (String module : patchModel.getModules()) {
            qCmdProcessor.appendToQueue(
                    new QCmdCompileModule(patchController,
                            module,
                            patchModel.getModuleDir(module)));
        }
        qCmdProcessor.appendToQueue(new QCmdCompilePatch(patchController));
        qCmdProcessor.appendToQueue(new QCmdUploadPatch());
        PatchViewLive pvl = new PatchViewLive(patchModel, pvcg);
        qCmdProcessor.appendToQueue(new QCmdStart(pvl));
        qCmdProcessor.appendToQueue(new QCmdLock(pvl));
        qCmdProcessor.appendToQueue(new QCmdMemRead(CConnection.getConnection().getTargetProfile().getPatchAddr(), 8, new IConnection.MemReadHandler() {
            @Override
            public void done(ByteBuffer mem) {
                int signature = mem.getInt();
                int rootchunk_addr = mem.getInt();

                qCmdProcessor.appendToQueue(new QCmdMemRead(rootchunk_addr, 8, new IConnection.MemReadHandler() {
                    @Override
                    public void done(ByteBuffer mem) {
                        int fourcc = mem.getInt();
                        int length = mem.getInt();
                        System.out.println("rootchunk " + FourCC.format(fourcc) + " len = " + length);

                        qCmdProcessor.appendToQueue(new QCmdMemRead(rootchunk_addr, length + 8, new IConnection.MemReadHandler() {
                            @Override
                            public void done(ByteBuffer mem) {
                                ChunkParser cp = new ChunkParser(mem);
                                ChunkData cd = cp.getOne(FourCCs.PATCH_DISPLAY);
                                if (cd != null) {
                                    Cpatch_display cpatch_display = new Cpatch_display(cd);
                                    CConnection.getConnection().setDisplayAddr(cpatch_display.pDisplayVector, cpatch_display.nDisplayVector);
                                }
                            }
                        }));
                    }
                }));
            }
        }));
    }

}
