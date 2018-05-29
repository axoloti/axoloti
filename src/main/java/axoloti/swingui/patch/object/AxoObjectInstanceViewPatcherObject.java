package axoloti.swingui.patch.object;

import axoloti.abstractui.IAbstractEditor;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.swingui.components.ButtonComponent;
import axoloti.swingui.objecteditor.ObjectEditorFactory;
import axoloti.swingui.patch.PatchViewSwing;

class AxoObjectInstanceViewPatcherObject extends AxoObjectInstanceView {

    private ButtonComponent buttonEdit;
    private IAbstractEditor editor;

    AxoObjectInstanceViewPatcherObject(AxoObjectInstancePatcherObject objectInstance, PatchViewSwing patchView) {
        super(objectInstance, patchView);
        initComponents();
    }

    private void initComponents() {
        buttonEdit = new ButtonComponent("edit");
        buttonEdit.setAlignmentX(LEFT_ALIGNMENT);
        buttonEdit.setAlignmentY(TOP_ALIGNMENT);
        buttonEdit.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void fire() {
                openEditor();
            }
        });
        add(buttonEdit);
        resizeToGrid();
    }

    @Override
    public void openEditor() {
        if (editor == null) {
            editor = ObjectEditorFactory.createObjectEditor(model.getDModel());
        }
        editor.toFront();
    }

    @Override
    public void dispose() {
        if (editor != null) {
            editor.close();
            editor = null;
        }
    }

}
