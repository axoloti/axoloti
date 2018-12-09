package axoloti.piccolo.patch.object;

import axoloti.patch.PatchModel;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.piccolo.components.control.PButtonComponent;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.swingui.patch.PatchFrame;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;

public class PAxoObjectInstanceViewPatcher extends PAxoObjectInstanceView {

    private PButtonComponent buttonUpdate;
    private PatchFrame pf;

    public PAxoObjectInstanceViewPatcher(IAxoObjectInstance objectInstance, PatchViewPiccolo p) {
        super(objectInstance, p);
        initComponents();
    }

    @Override
    public AxoObjectInstancePatcher getDModel() {
        return (AxoObjectInstancePatcher) super.getDModel();
    }

    public void initSubpatchFrame() {
        if (pf == null) {
            PatchModel subpatch = getDModel().getSubPatchModel();
            pf = new PatchFrame(subpatch, true);
            subpatch.getController().addView(pf);
        }
    }

    public void edit() {
        initSubpatchFrame();
        pf.setState(java.awt.Frame.NORMAL);
        pf.setVisible(true);
    }

    private void initComponents() {
        PButtonComponent BtnEdit = new PButtonComponent("edit", this);
        BtnEdit.setAlignmentX(LEFT_ALIGNMENT);
        BtnEdit.setAlignmentY(TOP_ALIGNMENT);
        BtnEdit.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void fire() {
                edit();
            }
        });
        addChild(BtnEdit);
        resizeToGrid();
    }

    @Override
    public void unlock() {
        super.unlock();
        if (buttonUpdate != null) {
            buttonUpdate.setEnabled(true);
        }
    }

    @Override
    public void lock() {
        super.lock();
        if (buttonUpdate != null) {
            buttonUpdate.setEnabled(false);
        }
    }

    @Override
    public void dispose() {
        if (pf != null) {
            pf.close();
            pf = null;
        }
    }
}
