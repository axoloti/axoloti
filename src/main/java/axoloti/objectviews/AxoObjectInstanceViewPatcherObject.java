package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.objecteditor.AxoObjectEditor;
import components.ButtonComponent;
import javax.swing.SwingUtilities;

public class AxoObjectInstanceViewPatcherObject extends AxoObjectInstanceView {

    AxoObjectInstancePatcherObject model;
    ButtonComponent BtnEdit;

    public AxoObjectInstanceViewPatcherObject(AxoObjectInstancePatcherObject model, PatchViewSwing patchView) {
        super(model, patchView);
        this.model = model;
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

    public void edit() {
        if (model.getAxoObject() == null) {
            model.setAxoObject(new AxoObjectPatcherObject());
//            ao.id = "id";
            model.getAxoObject().sDescription = "";
        }
        if (model.aoe == null) {
            model.aoe = new AxoObjectEditor(model.getAxoObject());
        } else {
            model.aoe.updateReferenceXML();
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                model.aoe.setState(java.awt.Frame.NORMAL);
                model.aoe.setVisible(true);
            }
        });
    }

    public boolean isEditorOpen() {
        return model.aoe != null && model.aoe.isVisible();
    }
}
