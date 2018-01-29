package axoloti.piccolo.patch.object;

import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.ObjectInstancePatcherController;
import axoloti.piccolo.components.control.PButtonComponent;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.swingui.patch.PatchFrame;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import qcmds.QCmdProcessor;

public class PAxoObjectInstanceViewPatcher extends PAxoObjectInstanceView {

    private PButtonComponent BtnUpdate;
    private PatchFrame pf;

    public PAxoObjectInstanceViewPatcher(ObjectInstanceController controller, PatchViewPiccolo p) {
        super(controller, p);
    }

    @Override
    public AxoObjectInstancePatcher getModel() {
        return (AxoObjectInstancePatcher) super.getModel();
    }

    @Override
    public ObjectInstancePatcherController getController() {
        return (ObjectInstancePatcherController) super.getController();
    }

    public void initSubpatchFrame() {
        if (pf == null) {
            pf = new PatchFrame(getController().getSubPatchController(), QCmdProcessor.getQCmdProcessor(), true);
            getController().getSubPatchController().addView(pf);
        }
    }

    public void edit() {
        initSubpatchFrame();
        pf.setState(java.awt.Frame.NORMAL);
        pf.setVisible(true);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        PButtonComponent BtnEdit = new PButtonComponent("edit", this);
        BtnEdit.setAlignmentX(LEFT_ALIGNMENT);
        BtnEdit.setAlignmentY(TOP_ALIGNMENT);
        BtnEdit.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                edit();
            }
        });
        addChild(BtnEdit);
        resizeToGrid();
    }

    @Override
    public void Unlock() {
        super.Unlock();
        if (BtnUpdate != null) {
            BtnUpdate.setEnabled(true);
        }
    }

    @Override
    public void Lock() {
        super.Lock();
        if (BtnUpdate != null) {
            BtnUpdate.setEnabled(false);
        }
    }

    @Override
    public void dispose() {
        if (pf != null) {
            pf.Close();
            pf = null;
        }
    }
}
