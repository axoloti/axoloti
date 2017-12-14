package axoloti.piccolo.attributeviews;

import axoloti.TextEditor;
import axoloti.attribute.AttributeInstanceTextEditor;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.control.PButtonComponent;

public class PAttributeInstanceViewTextEditor extends PAttributeInstanceViewString {

    AttributeInstanceTextEditor attributeInstance;
    PButtonComponent bEdit;
    TextEditor editor;

    public PAttributeInstanceViewTextEditor(AttributeInstanceTextEditor attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    void showEditor() {
        if (editor == null) {/*
            editor = new TextEditor(attributeInstance.getStringRef(), null);
            // fixme DocumentWindow null arg was: getPatchView().getPatchController().getPatchFrame());
            editor.setTitle(attributeInstance.getObjectInstance().getInstanceName() + "/" + attributeInstance.getModel().getName());
            editor.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {
                    //attributeInstance.setValueBeforeAdjustment(attributeInstance.getStringRef().s);
                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    //if (!attributeInstance.getValueBeforeAdjustment().equals(attributeInstance.getStringRef().s)) {
                    //    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                    //}
                }
            });*/
        }
        editor.setState(java.awt.Frame.NORMAL);
        editor.setVisible(true);
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        bEdit = new PButtonComponent("Edit", axoObjectInstanceView);
        addChild(bEdit);
        bEdit.addActListener(new PButtonComponent.ActListener() {
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
    public String getString() {
        return attributeInstance.getValue();
    }

    @Override
    public void setString(String sText) {
        if (editor != null) {
            editor.SetText(sText);
        }
    }
}
