package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.ObjectInstanceController;
import axoloti.objecteditor.AxoObjectEditor;
import components.ButtonComponent;
import javax.swing.SwingUtilities;

public class AxoObjectInstanceViewPatcherObject extends AxoObjectInstanceView {

    ButtonComponent BtnEdit;
    AxoObjectEditor aoe;

    public AxoObjectInstanceViewPatcherObject(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        BtnEdit = new ButtonComponent("edit");
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
    public void OpenEditor() {
        edit();
    }

    @Override
    public AxoObjectInstancePatcherObject getModel() {
        return (AxoObjectInstancePatcherObject) controller.getModel();
    }

    public void edit() {
        if (aoe == null) {
            aoe = new AxoObjectEditor(getModel().getController());
        } else {
            aoe.updateReferenceXML();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                aoe.setState(java.awt.Frame.NORMAL);
                aoe.setVisible(true);
            }
        });
    }

    public boolean isEditorOpen() {
        return aoe != null && aoe.isVisible();
    }

    @Override
    public void dispose() {
        if (aoe != null) {
            aoe.Close();
            aoe = null;
        }
    }

}
