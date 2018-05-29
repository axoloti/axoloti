package axoloti.piccolo.patch.object;

import axoloti.abstractui.IAbstractEditor;
import axoloti.object.AxoObject;
import axoloti.patch.object.AxoObjectInstancePatcherObject;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.piccolo.components.control.PButtonComponent;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.swingui.objecteditor.ObjectEditorFactory;
import static java.awt.Component.LEFT_ALIGNMENT;
import static java.awt.Component.TOP_ALIGNMENT;
import javax.swing.SwingUtilities;

public class PAxoObjectInstanceViewPatcherObject extends PAxoObjectInstanceView {

    private PButtonComponent buttonEdit;
    private IAbstractEditor aoe;

    public PAxoObjectInstanceViewPatcherObject(IAxoObjectInstance objectInstance, PatchViewPiccolo p) {
        super(objectInstance, p);
        initComponents();
    }

    @Override
    public AxoObjectInstancePatcherObject getDModel() {
        return (AxoObjectInstancePatcherObject) super.getDModel();
    }

    private void initComponents() {
        buttonEdit = new PButtonComponent("edit", this);
        buttonEdit.setAlignmentX(LEFT_ALIGNMENT);
        buttonEdit.setAlignmentY(TOP_ALIGNMENT);
        buttonEdit.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void fire() {
                edit();
            }
        });

        addChild(buttonEdit);
        resizeToGrid();
    }

    @Override
    public void openEditor() {
        edit();
    }

    public void edit() {
        if (aoe == null) {
            aoe = ObjectEditorFactory.createObjectEditor((AxoObject) getDModel().getDModel());
        }
        // TODO: piccolo: Review, invokeLater should not be needed:
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // aoe.setState(java.awt.Frame.NORMAL);
                aoe.toFront();
            }
        });
    }

    // TODO: piccolo review: unused method
    private boolean isEditorOpen() {
        return aoe != null;
    }

    @Override
    public void dispose() {
        if (aoe != null) {
            aoe.close();
            aoe = null;
        }
    }
}
