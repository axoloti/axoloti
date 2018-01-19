package axoloti.swingui.patch.object;

import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.ObjectInstancePatcherController;
import axoloti.swingui.components.ButtonComponent;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.PatchViewSwing;
import qcmds.QCmdProcessor;

class AxoObjectInstanceViewPatcher extends AxoObjectInstanceView {

    private PatchFrame pf;

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
    public void dispose() {
        if (pf != null) {
            pf.Close();
            pf = null;
        }
    }
}
