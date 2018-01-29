package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceObjRef;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewObjRef extends AttributeInstanceViewString {

    JTextField TFObjName;

    AttributeInstanceViewObjRef(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceObjRef getModel() {
        return (AttributeInstanceObjRef) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        TFObjName = new JTextField(getModel().getValue());
        Dimension d = TFObjName.getSize();
        d.width = 92;
        d.height = 22;
        TFObjName.setFont(Constants.FONT);
        TFObjName.setMaximumSize(d);
        TFObjName.setMinimumSize(d);
        TFObjName.setPreferredSize(d);
        TFObjName.setSize(d);
        add(TFObjName);
        TFObjName.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
                if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
                    transferFocus();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }
        });
        TFObjName.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getController().setModelUndoableProperty(AttributeInstanceObjRef.ATTR_VALUE, TFObjName.getText());
                        }
                    });
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
        TFObjName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getController().addMetaUndo("edit attribute " + getModel().getName());
            }

            @Override
            public void focusLost(FocusEvent e) {
                getController().setModelUndoableProperty(AttributeInstanceObjRef.ATTR_VALUE,TFObjName.getText());
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
    public void setString(String objName) {
        if (TFObjName != null) {
            if (!TFObjName.getText().equals(objName)) {
                TFObjName.setText(objName);
            }
        }
    }
}
