package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceObjRef;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewObjRef extends AttributeInstanceViewString {

    private JTextField textFieldObjName;

    AttributeInstanceViewObjRef(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceObjRef getDModel() {
        return (AttributeInstanceObjRef) super.getDModel();
    }

    private void initComponents() {
        textFieldObjName = new JTextField(getDModel().getValue());
        Dimension d = textFieldObjName.getSize();
        d.width = 92;
        d.height = 22;
        textFieldObjName.setFont(Constants.FONT);
        textFieldObjName.setMaximumSize(d);
        textFieldObjName.setMinimumSize(d);
        textFieldObjName.setPreferredSize(d);
        textFieldObjName.setSize(d);
        add(textFieldObjName);
        textFieldObjName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent ke) {
                if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
                    transferFocus();
                }
            }

        });
        textFieldObjName.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                model.getController().changeValue(textFieldObjName.getText());
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
        textFieldObjName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                model.getController().addMetaUndo("edit attribute " + getDModel().getName(), focusEdit);
            }

            @Override
            public void focusLost(FocusEvent e) {
                model.getController().changeValue(textFieldObjName.getText());
            }
        });
    }

    @Override
    public void lock() {
        if (textFieldObjName != null) {
            textFieldObjName.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (textFieldObjName != null) {
            textFieldObjName.setEnabled(true);
        }
    }

    @Override
    public void setString(String objName) {
        if (textFieldObjName != null) {
            if (!textFieldObjName.getText().equals(objName)) {
                textFieldObjName.setText(objName);
            }
        }
    }
}
