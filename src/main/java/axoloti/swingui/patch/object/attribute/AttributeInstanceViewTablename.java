package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceTablename;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewTablename extends AttributeInstanceViewString {

    JTextField TFtableName;

    AttributeInstanceViewTablename(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceTablename getModel() {
        return (AttributeInstanceTablename) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        TFtableName = new JTextField(getModel().getValue());
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
                getController().changeValue(TFtableName.getText());
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
                getController().addMetaUndo("edit attribute " + getModel().getName(), focusEdit);
            }

            @Override
            public void focusLost(FocusEvent e) {
                getController().changeValue(TFtableName.getText());
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
    public void setString(String tableName) {
        if (TFtableName != null) {
            if (tableName == null) {
                tableName = "";
            }
            if (!tableName.equals(TFtableName.getText())) {
                TFtableName.setText(tableName);
            }
        }
    }
}
