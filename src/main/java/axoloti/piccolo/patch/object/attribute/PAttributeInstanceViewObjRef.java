package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceObjRef;
import axoloti.piccolo.components.PTextFieldComponent;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

class PAttributeInstanceViewObjRef extends PAttributeInstanceViewString {

    PTextFieldComponent TFObjName;

    PAttributeInstanceViewObjRef(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceObjRef getDModel() {
        return (AttributeInstanceObjRef) super.getDModel();
    }

    private void initComponents() {
        TFObjName = new PTextFieldComponent(getDModel().getValue());
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
            public void keyTyped(PInputEvent ke) {
                if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
                    TFObjName.transferFocus(ke, patchView);
                }
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                getDModel().getController().addMetaUndo("edit attribute " + getDModel().getName());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                getDModel().getController().changeValue(TFObjName.getText());
            }
        });

        TFObjName.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getDModel().getController().changeValue(TFObjName.getText());
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
    }

    @Override
    public void lock() {
        if (TFObjName != null) {
            TFObjName.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
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
