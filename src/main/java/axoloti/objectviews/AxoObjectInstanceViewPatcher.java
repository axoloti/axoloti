package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstance;
import axoloti.object.AxoObjectInstancePatcher;
import axoloti.object.ObjectInstanceController;
import components.ButtonComponent;

public class AxoObjectInstanceViewPatcher extends AxoObjectInstanceView {

    private ButtonComponent BtnUpdate;

    public AxoObjectInstanceViewPatcher(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
    }

    @Override
    public AxoObjectInstancePatcher getModel() {
        return (AxoObjectInstancePatcher) super.getModel();
    }

    public void edit() {
        getModel().init();
        getModel().pf.setState(java.awt.Frame.NORMAL);
        getModel().pf.setVisible(true);
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
        BtnUpdate = new ButtonComponent("update");
        BtnUpdate.setAlignmentX(LEFT_ALIGNMENT);
        BtnUpdate.setAlignmentY(TOP_ALIGNMENT);
        BtnUpdate.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                getModel().updateObj();
            }
        });
        add(BtnUpdate);
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
