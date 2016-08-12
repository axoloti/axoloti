package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceTablename;
import axoloti.objectviews.AxoObjectInstanceView;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AttributeInstanceViewTablename extends AttributeInstanceViewString {

    AttributeInstanceTablename attributeInstance;
    JTextField TFtableName;
    JLabel vlabel;

    public AttributeInstanceViewTablename(AttributeInstanceTablename attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFtableName = new JTextField(attributeInstance.getString());
        Dimension d = TFtableName.getSize();
        d.width = 128;
        d.height = 22;
        TFtableName.setFont(Constants.FONT);
        TFtableName.setMaximumSize(d);
        TFtableName.setMinimumSize(d);
        TFtableName.setPreferredSize(d);
        TFtableName.setSize(d);
        add(TFtableName);

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

        TFtableName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                attributeInstance.setValueBeforeAdjustment(TFtableName.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFtableName.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().SetDirty();
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