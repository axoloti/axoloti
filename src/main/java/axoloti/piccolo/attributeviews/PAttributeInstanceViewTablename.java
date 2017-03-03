package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceTablename;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PTextFieldComponent;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAttributeInstanceViewTablename extends PAttributeInstanceViewString {

    AttributeInstanceTablename attributeInstance;
    PTextFieldComponent TFtableName;

    public PAttributeInstanceViewTablename(AttributeInstanceTablename attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFtableName = new PTextFieldComponent(attributeInstance.getString());
        Dimension d = TFtableName.getSize();
        d.width = 128;
        d.height = 22;
        TFtableName.setMaximumSize(d);
        TFtableName.setMinimumSize(d);
        TFtableName.setPreferredSize(d);
        TFtableName.setSize(d);
        addChild(TFtableName);

        TFtableName.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                attributeInstance.setString(TFtableName.getText());
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
        TFtableName.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void keyTyped(PInputEvent ke) {
                TFtableName.transferFocus(ke, getPatchView());
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                attributeInstance.setValueBeforeAdjustment(TFtableName.getText());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                if (!TFtableName.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                }

            }
        });
    }

    @Override
    public void Lock() {
        if (TFtableName != null) {
            TFtableName.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFtableName != null) {
            TFtableName.setEnabled(true);
        }
    }

    @Override
    public String getString() {
        return attributeInstance.getString();
    }

    @Override
    public void setString(String tableName) {
        attributeInstance.setString(tableName);
        if (TFtableName != null) {
            TFtableName.setText(tableName);
        }
    }
}
