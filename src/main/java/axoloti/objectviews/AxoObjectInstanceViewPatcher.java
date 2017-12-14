package axoloti.objectviews;

import axoloti.PatchFrame;
import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.ObjectInstanceController;
import axoloti.object.ObjectInstancePatcherController;
import components.ButtonComponent;
import qcmds.QCmdProcessor;

public class AxoObjectInstanceViewPatcher extends AxoObjectInstanceView {

    private ButtonComponent BtnUpdate;
    public PatchFrame pf;

    public AxoObjectInstanceViewPatcher(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
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
            pf = new PatchFrame(getController().getSubPatchController(), QCmdProcessor.getQCmdProcessor());
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
        //updateObj();
        ButtonComponent BtnEdit = new ButtonComponent("edit");
        BtnEdit.setAlignmentX(LEFT_ALIGNMENT);
        BtnEdit.setAlignmentY(TOP_ALIGNMENT);
        BtnEdit.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                edit();
            }
        });
        add(BtnEdit);
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
