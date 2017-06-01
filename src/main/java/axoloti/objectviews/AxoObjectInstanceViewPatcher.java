package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstancePatcher;
import components.ButtonComponent;

public class AxoObjectInstanceViewPatcher extends AxoObjectInstanceView {

    AxoObjectInstancePatcher model;
    private ButtonComponent BtnUpdate;

    public AxoObjectInstanceViewPatcher(AxoObjectInstancePatcher model, PatchViewSwing patchView) {
        super(model, patchView);
        this.model = model;
    }

    public void edit() {
        model.init();
        model.pf.setState(java.awt.Frame.NORMAL);
        model.pf.setVisible(true);
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
                model.updateObj();
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
