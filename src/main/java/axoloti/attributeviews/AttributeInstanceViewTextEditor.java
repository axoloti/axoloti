package axoloti.attributeviews;

import axoloti.DocumentWindow;
import axoloti.TextEditor;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceTextEditor;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.ButtonComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

class AttributeInstanceViewTextEditor extends AttributeInstanceViewString {

    ButtonComponent bEdit;
    JLabel vlabel;

    AttributeInstanceViewTextEditor(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceTextEditor getModel() {
        return (AttributeInstanceTextEditor) super.getModel();
    }

    void showEditor() {
        if (getModel().editor == null) {
            DocumentWindow dw = (DocumentWindow) SwingUtilities.getWindowAncestor(this);
            getModel().editor = new TextEditor(AttributeInstanceTextEditor.ATTR_VALUE, getController(), dw);
            getModel().editor.setTitle(getController().getParent().getModel().getInstanceName() + "/" + getModel().getModel().getName());
        }
        getModel().editor.setState(java.awt.Frame.NORMAL);
        getModel().editor.setVisible(true);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        bEdit = new ButtonComponent("Edit");
        add(bEdit);
        bEdit.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                showEditor();
            }
        });
    }

    @Override
    public void Lock() {
        if (bEdit != null) {
            bEdit.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (bEdit != null) {
            bEdit.setEnabled(true);
        }
    }

    @Override
    public void setString(String sText) {
        getModel().setValue(sText);
        if (getModel().editor != null) {
            getModel().editor.SetText(sText);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

    }

}
