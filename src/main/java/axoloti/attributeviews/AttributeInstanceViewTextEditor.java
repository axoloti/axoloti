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

    AttributeInstanceViewTextEditor(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceTextEditor getModel() {
        return (AttributeInstanceTextEditor) super.getModel();
    }

    void showEditor() {
        if (getModel().editor == null) {
            getModel().editor = new TextEditor(getModel().getStringRef(), null);
            // FIXME: null DocumentWindow arg, was: 
            // getPatchView().getPatchController().getPatchFrame());
            getModel().editor.setTitle(getModel().getObjectInstance().getInstanceName() + "/" + getModel().getModel().getName());
            getModel().editor.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    //getModel().setValueBeforeAdjustment(getModel().getStringRef().s);
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    //if (!getModel().getValueBeforeAdjustment().equals(getModel().getStringRef().s)) {
                    //     getModel().getObjectInstance().getPatchModel().setDirty();
                    // }
                }
            });
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
}
