package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceTextEditor;
import axoloti.piccolo.components.control.PButtonComponent;
import axoloti.swingui.TextEditor;
import javax.swing.SwingUtilities;

class PAttributeInstanceViewTextEditor extends PAttributeInstanceViewString {

    PButtonComponent bEdit;

    PAttributeInstanceViewTextEditor(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceTextEditor getDModel() {
        return (AttributeInstanceTextEditor) super.getDModel();
    }

    void showEditor() {
        if (getDModel().getEditor() == null) {
            DocumentWindow dw = (DocumentWindow) SwingUtilities.getWindowAncestor(this.getProxyComponent());
            TextEditor textEditor = new TextEditor(AttributeInstanceTextEditor.ATTR_VALUE, getDModel(), dw);
            textEditor.setTitle(getDModel().getParent().getInstanceName() + "/" + getDModel().getDModel().getName());
            getDModel().setEditor(textEditor);
        }
        getDModel().getEditor().toFront();
    }

    private void initComponents() {
        bEdit = new PButtonComponent("Edit", axoObjectInstanceView);
        addChild(bEdit);
        bEdit.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void fire() {
                showEditor();
            }
        });
    }

    @Override
    public void lock() {
        if (bEdit != null) {
            bEdit.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (bEdit != null) {
            bEdit.setEnabled(true);
        }
    }

    @Override
    public void setString(String sText) {
        getDModel().setValue(sText);
        /*
        if (getDModel().editor != null) {
            getDModel().editor.setText(sText);
        }*/
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
