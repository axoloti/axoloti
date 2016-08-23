package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceObjRef;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PTextFieldComponent;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAttributeInstanceViewObjRef extends PAttributeInstanceViewString {

    AttributeInstanceObjRef attributeInstance;

    PTextFieldComponent TFObjName;

    public PAttributeInstanceViewObjRef(AttributeInstanceObjRef attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    public void PostConstructor() {
        super.PostConstructor();
        TFObjName = new PTextFieldComponent(attributeInstance.getString());
        Dimension d = TFObjName.getSize();
        d.width = 92;
        d.height = 22;
        TFObjName.setMaximumSize(d);
        TFObjName.setMinimumSize(d);
        TFObjName.setPreferredSize(d);
        TFObjName.setSize(d);
        addChild(TFObjName);
        TFObjName.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void mouseClicked(PInputEvent e) {
                TFObjName.grabFocus();
            }

            @Override
            public void keyPressed(PInputEvent ke) {
                TFObjName.transferFocus(ke, patchView);
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                attributeInstance.setValueBeforeAdjustment(TFObjName.getText());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                if (!TFObjName.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                }
            }
        });

        TFObjName.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                attributeInstance.setString(TFObjName.getText());
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
    }

    @Override
    public void Lock() {
        if (TFObjName != null) {
            TFObjName.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFObjName != null) {
            TFObjName.setEnabled(true);
        }
    }

    @Override
    public String getString() {
        return attributeInstance.getString();
    }

    @Override
    public void setString(String objName) {
        attributeInstance.setString(objName);
        if (TFObjName != null) {
            TFObjName.setText(objName);
        }
    }
}
