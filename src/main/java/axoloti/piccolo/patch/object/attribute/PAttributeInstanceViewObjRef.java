package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceObjRef;
import axoloti.piccolo.components.PTextFieldComponent;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAttributeInstanceViewObjRef extends PAttributeInstanceViewString {

    PTextFieldComponent TFObjName;

    public PAttributeInstanceViewObjRef(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceObjRef getModel() {
        return (AttributeInstanceObjRef) super.getModel();
    }

    public void PostConstructor() {
        super.PostConstructor();
        TFObjName = new PTextFieldComponent(getModel().getValue());
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
                getController().addMetaUndo("edit attribute " + getModel().getName());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                getController().changeValue(TFObjName.getText());
            }
        });

        TFObjName.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getController().changeValue(TFObjName.getText());
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
