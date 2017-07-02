package axoloti.piccolo.objectviews;

import axoloti.PatchViewPiccolo;
import axoloti.object.AxoObjectInstancePatcherObject;
import axoloti.object.AxoObjectPatcherObject;
import axoloti.objecteditor.AxoObjectEditor;
import components.piccolo.control.PButtonComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import javax.swing.SwingUtilities;

public class PAxoObjectInstanceViewPatcherObject extends PAxoObjectInstanceView {

    AxoObjectInstancePatcherObject model;
    PButtonComponent BtnEdit;
    AxoObjectEditor aoe;

    public PAxoObjectInstanceViewPatcherObject(AxoObjectInstancePatcherObject model, PatchViewPiccolo p) {
        super(model, p);
        this.model = model;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        BtnEdit = new PButtonComponent("edit", this);
        BtnEdit.setAlignmentX(LEFT_ALIGNMENT);
        BtnEdit.setAlignmentY(TOP_ALIGNMENT);
        BtnEdit.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                edit();
            }
        });
        addChild(BtnEdit);
        resizeToGrid();
        translate(model.getX(), model.getY());
    }

    @Override
    public void OpenEditor() {
        edit();
    }

    public void edit() {
        if (model.getAxoObject() == null) {
            //model.setAxoObject(new AxoObjectPatcherObject());
            model.getAxoObject().setDescription("");
        }
        if (aoe == null) {
            aoe = new AxoObjectEditor(model.getAxoObject().createController(null, null));
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
}
