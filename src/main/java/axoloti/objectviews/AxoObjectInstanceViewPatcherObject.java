package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.object.ObjectInstanceController;
import axoloti.objecteditor.AxoObjectEditor;
import components.ButtonComponent;
import javax.swing.SwingUtilities;

public class AxoObjectInstanceViewPatcherObject extends AxoObjectInstanceView {

    ButtonComponent BtnEdit;

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
        if (getModel().getAxoObject() == null) {
            getModel().setAxoObject(new AxoObjectPatcherObject());
//            ao.id = "id";
            getModel().getAxoObject().sDescription = "";
        }
        if (getModel().aoe == null) {
            getModel().aoe = new AxoObjectEditor(getModel().getAxoObject().createController(null, null));
        } else {
            getModel().aoe.updateReferenceXML();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getModel().aoe.setState(java.awt.Frame.NORMAL);
                getModel().aoe.setVisible(true);
            }
        });
    }

    public boolean isEditorOpen() {
        return getModel().aoe != null && getModel().aoe.isVisible();
    }
}
