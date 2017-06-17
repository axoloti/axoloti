package axoloti.objectviews;

import axoloti.MainFrame;
import axoloti.PatchFrame;
import axoloti.PatchView;
import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.ObjectInstanceController;
import axoloti.object.ObjectInstancePatcherController;
import components.ButtonComponent;

public class AxoObjectInstanceViewPatcher extends AxoObjectInstanceView {

    private ButtonComponent BtnUpdate;
    public PatchFrame pf;

    public AxoObjectInstanceViewPatcher(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
    }

    @Override
    public ObjectInstancePatcherController getController() {
        return (ObjectInstancePatcherController)super.getController();
    }

    @Override
    public AxoObjectInstancePatcher getModel() {
        return (AxoObjectInstancePatcher) super.getModel();
    }

    public void initSubpatchFrame() {
        PatchView patchView = MainFrame.prefs.getPatchView(getController().subPatchController);
        if (pf == null) {
            pf = new PatchFrame(getController().subPatchController, patchView, MainFrame.mainframe.getQcmdprocessor());
            patchView.setPatchFrame(pf);
        }
        getController().subPatchController.addView(patchView);
        //patchView.setFileNamePath(getInstanceName());
        patchView.PostConstructor();
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
}
