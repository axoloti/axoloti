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
import axoloti.patch.object.parameter.ParameterInstanceController;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDFileReference;
import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PatchViewLive extends View<PatchController> {

    final PatchViewCodegen pvcg;
    final List<ParameterInstanceLiveView> parameterInstanceViews;

    public PatchViewLive(PatchController controller, PatchViewCodegen pvcg) {
        super(controller);
        this.pvcg = pvcg;
        parameterInstanceViews = new ArrayList<>(pvcg.getParameterInstances().size());
        for (ParameterInstanceView v : pvcg.getParameterInstances()) {
            ParameterInstanceController c = v.getController();
            ParameterInstanceLiveView v1 = new ParameterInstanceLiveView(c, v.getIndex());
            c.addView(v1);
            parameterInstanceViews.add(v1);
        }
    }

    public void distributeDataToDisplays(ByteBuffer dispData) {
        dispData.rewind();
        for (DisplayInstanceView d : pvcg.displayInstances) {
            d.ProcessByteBuffer(dispData);
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void dispose() {
    }

    public List<ParameterInstanceLiveView> getParameterInstances() {
        return parameterInstanceViews;
    }

    static public void GoLive(PatchModel patchModel) {

        PatchController patchController = patchModel.getControllerFromModel();
        QCmdProcessor qCmdProcessor = QCmdProcessor.getQCmdProcessor();

        qCmdProcessor.AppendToQueue(new QCmdStop());
        if (CConnection.GetConnection().GetSDCardPresent()) {

            String f = "/" + patchController.getSDCardPath();
            //System.out.println("pathf" + f);
            if (TargetModel.getTargetModel().getSDCardInfo().find(f) == null) {
                qCmdProcessor.AppendToQueue(new QCmdCreateDirectory(f));
            }
            qCmdProcessor.AppendToQueue(new QCmdChangeWorkingDirectory(f));
            patchController.UploadDependentFiles(f);
        } else {
            // issue warning when there are dependent files
            ArrayList<SDFileReference> files = patchModel.GetDependendSDFiles();
            if (files.size() > 0) {
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, "Patch requires file {0} on SDCard, but no SDCard mounted", files.get(0).targetPath);
            }
        }

        PatchViewCodegen pvcg = patchController.WriteCode();
        qCmdProcessor.setPatchController(null);
        for (String module : patchModel.getModules()) {
            qCmdProcessor.AppendToQueue(
                    new QCmdCompileModule(patchController,
                            module,
                            patchModel.getModuleDir(module)));
        }
        qCmdProcessor.AppendToQueue(new QCmdCompilePatch(patchController));
        qCmdProcessor.AppendToQueue(new QCmdUploadPatch());
        PatchViewLive pvl = new PatchViewLive(patchController, pvcg);
        qCmdProcessor.AppendToQueue(new QCmdStart(pvl));
        qCmdProcessor.AppendToQueue(new QCmdLock(pvl));
        qCmdProcessor.AppendToQueue(new QCmdMemRead(CConnection.GetConnection().getTargetProfile().getPatchAddr(), 8, new IConnection.MemReadHandler() {
            @Override
            public void Done(ByteBuffer mem) {
                int signature = mem.getInt();
                int rootchunk_addr = mem.getInt();

                qCmdProcessor.AppendToQueue(new QCmdMemRead(rootchunk_addr, 8, new IConnection.MemReadHandler() {
                    @Override
                    public void Done(ByteBuffer mem) {
                        int fourcc = mem.getInt();
                        int length = mem.getInt();
                        System.out.println("rootchunk " + FourCC.Format(fourcc) + " len = " + length);

                        qCmdProcessor.AppendToQueue(new QCmdMemRead(rootchunk_addr, length + 8, new IConnection.MemReadHandler() {
                            @Override
                            public void Done(ByteBuffer mem) {
                                ChunkParser cp = new ChunkParser(mem);
                                ChunkData cd = cp.GetOne(FourCCs.PATCH_DISPLAY);
                                if (cd != null) {
                                    Cpatch_display cpatch_display = new Cpatch_display(cd);
                                    CConnection.GetConnection().setDisplayAddr(cpatch_display.pDisplayVector, cpatch_display.nDisplayVector);
                                }
                            }
                        }));
                    }
                }));
            }
        }));
    }

}
