package axoloti.swingui.patch.object;

import axoloti.patch.PatchModel;
import axoloti.patch.object.AxoObjectInstancePatcher;
import axoloti.swingui.components.ButtonComponent;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.PatchViewSwing;

class AxoObjectInstanceViewPatcher extends AxoObjectInstanceView {

    private PatchFrame pf;

    AxoObjectInstanceViewPatcher(AxoObjectInstancePatcher objectInstance, PatchViewSwing patchView) {
        super(objectInstance, patchView);
        initComponents();
    }

    @Override
    public AxoObjectInstancePatcher getDModel() {
        return (AxoObjectInstancePatcher) super.getDModel();
    }

    private void initSubpatchFrame() {
        if (pf == null) {
            PatchModel subpatch = getDModel().getSubPatchModel();
            pf = new PatchFrame(subpatch);
            subpatch.getController().addView(pf);
        }
    }

    @Override
    public void openEditor() {
        initSubpatchFrame();
        pf.setState(java.awt.Frame.NORMAL);
        pf.setVisible(true);
    }

    private void initComponents() {
        //updateObj();
        ButtonComponent BtnEdit = new ButtonComponent("edit");
        BtnEdit.setAlignmentX(LEFT_ALIGNMENT);
        BtnEdit.setAlignmentY(TOP_ALIGNMENT);
        BtnEdit.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void fire() {
                openEditor();
            }
        });
        add(BtnEdit);
        resizeToGrid();
    }

    @Override
    public void dispose() {
        if (pf != null) {
            pf.close();
            pf = null;
        }
    }
}
