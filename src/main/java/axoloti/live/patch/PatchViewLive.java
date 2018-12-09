package axoloti.live.patch;

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
import axoloti.job.GlobalJobProcessor;
import axoloti.job.IJob;
import axoloti.live.patch.parameter.ParameterInstanceLiveView;
import axoloti.mvc.View;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.shell.CompileModule;
import axoloti.shell.CompilePatch;
import axoloti.shell.ExecutionFailedException;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDCardInfo;
import axoloti.target.fs.SDFileReference;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
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
        //enableAutoRecompile();
        //
        // currently only triggers on adding/deleting nets, and adding/deleting objects
        // TODO: auto-recompile: add views to trigger recompilation on changing attributes, nets, and object changes
    }

    private boolean needsPresetUpdate = false;

    private int disp_addr;
    private int disp_length;

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

    private void distributeDataToDisplays(ByteBuffer dispData) {
        dispData.rewind();
        for (DisplayInstanceView d : patchViewCodegen.getDisplayInstances()) {
            d.processByteBuffer(dispData);
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
        TargetModel.getTargetModel().removePoller(pollHandler);
        model.getController().removeView(this);
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
                        break;
                    }
                }
                bf.complete(null);

            });
            byte[] b;
            try {
                b = bf.get();
                if (b != null) {
                    CConnection.getConnection().transmitPacket(b);
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

    private void pollDisplays() {
        if ((disp_addr != 0) && (disp_length != 0)) {
            try {
                ByteBuffer mem = CConnection.getConnection().read(disp_addr, disp_length * 4);
                if (mem != null) {
                    SwingUtilities.invokeAndWait(() -> {
                        distributeDataToDisplays(mem);
                    });
                }
            } catch (IOException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void pollPresetUpdates() {

        if (getNeedsPresetUpdate()) {
            byte[] pb = getUpdatedPresetTable();
            clearNeedsPresetUpdate();
            try {
                CConnection.getConnection().sendUpdatedPreset(pb);
            } catch (IOException ex) {
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final Runnable pollHandler = () -> {
        pollParameters();
        pollDisplays();
        pollPresetUpdates();
    };

    public void goLive() {
        IJob j = (ctx) -> {
            try {
                IConnection conn = CConnection.getConnection();
                PatchController patchController = getDModel().getController();
                conn.setPatch(null);
                conn.transmitStop();
                if (conn.getSDCardPresent()) {

                    String f = "/" + patchController.getSDCardPath();
                    //System.out.println("pathf" + f);
                    TargetModel targetModel = TargetModel.getTargetModel();
                    SDCardInfo sdci = targetModel.getSDCardInfo();
                    if ((sdci != null) && (sdci.find(f) == null)) {
                        targetModel.createDirectory(f, ctx);
                    }
                    conn.transmitChangeWorkingDirectory(f);
                    patchController.uploadDependentFiles(f, ctx);
                } else {
                    // issue warning when there are dependent files
                    List<SDFileReference> files = getDModel().getDependendSDFiles();
                    if (files.size() > 0) {
                        Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, "Patch requires file {0} on SDCard, but no SDCard mounted", files.get(0).targetPath);
                    }
                }

                for (String module : getDModel().getModules()) {
                    CompileModule.run(
                            module,
                            getDModel().getModuleDir(module));
                }
                CompilePatch.run();
                TargetModel.getTargetModel().uploadPatchToMemory();
                conn.transmitStart();
                ByteBuffer mem1 = conn.read(conn.getTargetProfile().getPatchAddr(), 8);
                int signature = mem1.getInt();
                int rootchunk_addr = mem1.getInt();

                ByteBuffer mem2 = conn.read(rootchunk_addr, 8);
                int fourcc = mem2.getInt();
                int length = mem2.getInt();
                System.out.println("rootchunk " + FourCC.format(fourcc) + " len = " + length);
                ByteBuffer mem3 = conn.read(rootchunk_addr, length + 8);
                ChunkParser cp = new ChunkParser(mem3);
                ChunkData cd = cp.getOne(FourCCs.PATCH_DISPLAY);
                if (cd != null) {
                    Cpatch_display cpatch_display = new Cpatch_display(cd);
                    disp_addr = cpatch_display.pDisplayVector;
                    disp_length = cpatch_display.nDisplayVector;
                }
                ctx.doInSync(() -> {
                    conn.setPatch(this);
                    getDModel().getController().setLocked(true);

                    TargetModel.getTargetModel().addPoller(pollHandler);
                });
            } catch (ExecutionFailedException | IOException ex) {
                ctx.doInSync(() -> {
                    getDModel().getController().setLocked(false);
                });
                Logger.getLogger(PatchViewLive.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
        GlobalJobProcessor.getJobProcessor().exec(j);
    }

}
