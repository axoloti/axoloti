package axoloti.swingui.patch.object;

import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.swingui.components.ButtonComponent;
import axoloti.swingui.objecteditor.AxoObjectEditor;
import axoloti.swingui.patch.PatchViewSwing;

class AxoObjectInstanceViewPatcherObject extends AxoObjectInstanceView {

    ButtonComponent BtnEdit;
    AxoObjectEditor editor;

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
        return (AxoObjectInstancePatcherObject) getController().getModel();
    }

    public void edit() {
        if (editor == null) {
            editor = new AxoObjectEditor(getModel().getController());
        } else {
            editor.updateReferenceXML();
        }
        editor.setVisible(true);
        editor.toFront();
    }

    public boolean isEditorOpen() {
        return editor != null && editor.isVisible();
    }

    @Override
    public void dispose() {
        if (editor != null) {
            editor.close();
            editor = null;
        }
    }

}
