package axoloti.live.patch;

import axoloti.codegen.patch.PatchViewCodegen;
import axoloti.codegen.patch.object.display.DisplayInstanceView;
import axoloti.codegen.patch.object.parameter.ParameterInstanceView;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.connection.ILivePatch;
import axoloti.connection.IPatchCB;
import axoloti.connection.PatchLoadFailedException;
import axoloti.job.GlobalJobProcessor;
import axoloti.job.IJob;
import axoloti.live.patch.parameter.ParameterInstanceLiveView;
import axoloti.mvc.View;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.shell.CompilePatchResult;
import axoloti.shell.ExecutionFailedException;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDFileReference;
import java.beans.PropertyChangeEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author jtaelman
 */
public class PatchViewLive extends View<PatchModel> implements IPatchCB {

    final PatchViewCodegen patchViewCodegen;
    final Runnable openEditor;
    final List<ParameterInstanceLiveView> parameterInstanceViews;
    ILivePatch patch;

    public PatchViewLive(PatchModel patchModel, PatchViewCodegen patchViewCodegen, Runnable openEditor) {
        super(patchModel);
        this.patchViewCodegen = patchViewCodegen;
        this.openEditor = openEditor;
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
        TargetModel.getTargetModel().getController().addView(this);
        // only after view is added...
        // but disabled for now... testing invited!
        //enableAutoRecompile();
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
        // TODO: auto-recompile is broken...
        auto_recompile = true;
    }

    @Override
    public void distributeDataToDisplays(ByteBuffer dispData) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                dispData.rewind();
                for (DisplayInstanceView d : patchViewCodegen.getDisplayInstances()) {
                    d.processByteBuffer(dispData);
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ReschedulableTimer timerTask;

    private void recompile() {
            try {
                SwingUtilities.invokeAndWait(() -> {
                        dispose();
                        if (timerTask != null) {
                            timerTask.purge();
                        }
                        System.out.println("do recompile...");
                        goLive();
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
    };

    public void scheduleRecompile() {
        if (timerTask == null) {
            timerTask = new ReschedulableTimer();
            timerTask.schedule(() -> {
                recompile();
            }, 1000 /* milliseconds */);
        } else {
            timerTask.reschedule(1000 /* milliseconds */);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (PatchModel.PATCH_RECALLPRESET.is(evt)) {
            try {
                patch.transmitRecallPreset((Integer) (evt.getNewValue()));
            } catch (IOException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (PatchModel.PATCH_LOCKED.is(evt)) {
            if (evt.getNewValue() == Boolean.FALSE) {
                if (patch != null) {
                    try {
                        patch.transmitStop();
                    } catch (IOException ex) {
                        Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    dispose();
                }
            }
        } else if (TargetModel.CONNECTION.is(evt)) {
            IConnection conn = (IConnection) evt.getNewValue();
            if (conn == null) {
                // target disconnected
                dispose();
            }
        } else if (PatchModel.PATCH_NETS.is(evt)
                || PatchModel.PATCH_OBJECTINSTANCES.is(evt)) {
            getDModel().getController().setLocked(false);
            if (auto_recompile) {
                scheduleRecompile();
            }
        }
    }

    @Override
    public void dispose() {
//        Logger.getLogger(PatchViewLive.class.getName()).log(Level.INFO, "dispose");
//        try {
//            throw new Exception();
//        } catch (Exception ex) {
//            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
//        }
        if (patch != null) {
            patch = null;
            getDModel().setLocked(false);
            for (ParameterInstanceLiveView pilv : parameterInstanceViews) {
                pilv.getDModel().getController().removeView(pilv);
            }
            TargetModel.getTargetModel().removePoller(pollHandler);
            model.getController().setLocked(false);
            model.getController().removeView(this);
            TargetModel.getTargetModel().getController().removeView(this);
        }
    }

    public List<ParameterInstanceLiveView> getParameterInstances() {
        return Collections.unmodifiableList(parameterInstanceViews);
    }

    private void pollParameters() {
        final CompletableFuture<byte[]> bf = new CompletableFuture<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                for (ParameterInstanceLiveView p : getParameterInstances()) {
                    if (p.getNeedsTransmit()) {
                        bf.complete(p.TXData());
                        p.clearNeedsTransmit();
                        return;
                    }
                }
                bf.complete(null);

            });
            byte[] b;
            try {
                b = bf.get();
                if (b != null) {
                    patch.transmitParameterChange(b);
                }
            } catch (ExecutionException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private void pollPresetUpdates() {

        if (getNeedsPresetUpdate()) {
            byte[] pb = getUpdatedPresetTable();
            clearNeedsPresetUpdate();
            try {
                patch.sendUpdatedPreset(pb);
            } catch (IOException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final Runnable pollHandler = () -> {
        try {
            if (patch != null) {
                pollParameters();
            }
            if (patch != null) {
                patch.pollDisplays();
            }
            if (patch != null) {
                pollPresetUpdates();
            }
        } catch (java.lang.NullPointerException ex) {
            Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
        }
    };

    public void goLive() {
        IJob j = (ctx) -> {
            try {
                IConnection conn = CConnection.getConnection();
                PatchController patchController = getDModel().getController();
                //conn.transmitStop(null);
                if (conn.getSDCardPresent()) {

                    String f = "/" + patchController.getSDCardPath();
                    //System.out.println("pathf" + f);
                    // TODO : patch no longer starts in its working directory...
//                    TargetModel targetModel = TargetModel.getTargetModel();
//                    SDCardInfo sdci = targetModel.getSDCardInfo();
//                    if ((sdci != null) && (sdci.find(f) == null)) {
//                        targetModel.createDirectory(f, ctx);
//                    }
//                    conn.transmitChangeWorkingDirectory(f);
                    patchController.uploadDependentFiles(f, ctx);
                } else {
                    // issue warning when there are dependent files
                    List<SDFileReference> files = getDModel().getDependendSDFiles();
                    if (files.size() > 0) {
                        Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, "Patch requires file {0} on SDCard, but no SDCard mounted", files.get(0).targetPath);
                    }
                }
                PatchViewCodegen codegen = new PatchViewCodegen(getDModel());
                String c = codegen.generateCode4();
                CompilePatchResult cpr = getDModel().getController().compile(c);
                if (cpr.getElf() != null) {
                    getDModel().getController().uploadDependentFiles(cpr.getFiledeps(), "", ctx);
                    final boolean use_sdcard_for_live = false;
                    final boolean use_sdram_for_live = true;
                    if (use_sdcard_for_live) {
                        ByteArrayInputStream inputStreamElf = new ByteArrayInputStream(cpr.getElf());
                        Calendar cal = Calendar.getInstance();
                        String fn = "/xpatch.elf";
                        conn.upload(fn, inputStreamElf, cal, cpr.getElf().length, ctx);
                        patch = conn.transmitStart(fn, this);
                    } else if (use_sdram_for_live) {
                        patch = conn.transmitStartLive(cpr.getElf(), getDModel().getController().getSDCardPath(), this, ctx);
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    ctx.doInSync(() -> {
                        getDModel().getController().setLocked(true);

                        TargetModel.getTargetModel().addPoller(pollHandler);
                    });
                }
            } catch (ExecutionFailedException ex) {
                ctx.doInSync(() -> {
                    getDModel().getController().setLocked(false);
                });
            } catch (IOException ex) {
                ctx.doInSync(() -> {
                    getDModel().getController().setLocked(false);
                });
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PatchLoadFailedException ex) {
                ctx.doInSync(() -> {
                    getDModel().getController().setLocked(false);
                });
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, "{0}", ex.getMessage());
            }
        };
        GlobalJobProcessor.getJobProcessor().exec(j);
    }

    @Override
    public void patchStopped() {
        SwingUtilities.invokeLater(() -> {
            dispose();
        });
    }

    @Override
    public void setDspLoad(int dspLoad) {
        // TODO
    }

    @Override
    public void paramChange(int index, int value) {
        SwingUtilities.invokeLater(() -> {
            if (!getDModel().getLocked()) {
                return;
            }
            if (index >= getParameterInstances().size()) {
                Logger.getLogger(PatchViewLive.class
                        .getName()).log(Level.INFO, "Rx paramchange index out of range{0} {1}", new Object[]{index, value});

                return;
            }
            ParameterInstanceLiveView pi = getParameterInstances().get(index);

            if (pi == null) {
                Logger.getLogger(PatchViewLive.class
                        .getName()).log(Level.INFO, "Rx paramchange parameterInstance null{0} {1}", new Object[]{index, value});
                return;
            }

            if (!pi.getNeedsTransmit()) {
                pi.getDModel().setValue(pi.getDModel().int32ToVal(value));
                pi.clearNeedsTransmit();
            }

//                System.out.println("rcv ppc objname:" + pi.axoObj.getInstanceName() + " pname:"+ pi.name);
        });
    }

    @Override
    public void openEditor() {
        if (openEditor != null) {
            openEditor.run();
        }
    }

}
