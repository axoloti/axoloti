package axoloti.attributeviews;

import axoloti.TextEditor;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceTextEditor;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.ButtonComponent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.JLabel;

class AttributeInstanceViewTextEditor extends AttributeInstanceViewString {

    ButtonComponent bEdit;
    JLabel vlabel;

    public AttributeInstanceViewTextEditor(AttributeInstanceTextEditor attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceTextEditor getAttributeInstance() {
        return (AttributeInstanceTextEditor) super.getAttributeInstance();
    }

    void showEditor() {
        if (getAttributeInstance().editor == null) {
            getAttributeInstance().editor = new TextEditor(getAttributeInstance().getStringRef(), null);
            // FIXME: null DocumentWindow arg, was: 
            // getPatchView().getPatchController().getPatchFrame());
            getAttributeInstance().editor.setTitle(attributeInstance.getObjectInstance().getInstanceName() + "/" + attributeInstance.getModel().getName());
            getAttributeInstance().editor.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    getAttributeInstance().setValueBeforeAdjustment(getAttributeInstance().getStringRef().s);
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    if (!getAttributeInstance().getValueBeforeAdjustment().equals(getAttributeInstance().getStringRef().s)) {
                        attributeInstance.getObjectInstance().getPatchModel().setDirty();
                    }
                }
            });
        }
        getAttributeInstance().editor.setState(java.awt.Frame.NORMAL);
        getAttributeInstance().editor.setVisible(true);
    }

    @Override
    public void PostConstructor() {
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
        getAttributeInstance().setValue(sText);
        if (getAttributeInstance().editor != null) {
            getAttributeInstance().editor.SetText(sText);
        }
    }
}
