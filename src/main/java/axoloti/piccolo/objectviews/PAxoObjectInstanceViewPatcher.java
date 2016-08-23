package axoloti.piccolo.objectviews;

import axoloti.PatchViewPiccolo;
import axoloti.object.AxoObjectInstancePatcher;
import components.piccolo.control.PButtonComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;

public class PAxoObjectInstanceViewPatcher extends PAxoObjectInstanceView {

    AxoObjectInstancePatcher model;
    private PButtonComponent BtnUpdate;

    public PAxoObjectInstanceViewPatcher(AxoObjectInstancePatcher model, PatchViewPiccolo p) {
        super(model, p);
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
        BtnUpdate = new PButtonComponent("update", this);
        BtnUpdate.setAlignmentX(LEFT_ALIGNMENT);
        BtnUpdate.setAlignmentY(TOP_ALIGNMENT);
        BtnUpdate.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                model.updateObj();
                getPatchView().getPatchController().pushUndoState();
            }
        });
        addChild(BtnUpdate);
        resizeToGrid();
        translate(model.getX(), model.getY());
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
