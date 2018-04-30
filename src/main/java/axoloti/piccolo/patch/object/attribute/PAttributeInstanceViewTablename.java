package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceTablename;
import axoloti.piccolo.components.PTextFieldComponent;
import java.awt.Dimension;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

class PAttributeInstanceViewTablename extends PAttributeInstanceViewString {

    PTextFieldComponent TFtableName;

    public PAttributeInstanceViewTablename(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
        initComponents();
    }

    public PatchView getPatchView() {
        return axoObjectInstanceView.getPatchView();
    }

    @Override
    public AttributeInstanceTablename getModel() {
        return (AttributeInstanceTablename) super.getModel();
    }

    private void initComponents() {
        TFtableName = new PTextFieldComponent(getModel().getValue());
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
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getController().changeValue(TFtableName.getText());
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
        TFtableName.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void keyTyped(PInputEvent ke) {
                TFtableName.transferFocus(ke, getPatchView());
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                getController().addMetaUndo("edit attribute " + getModel().getName());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
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
            TFtableName.setText(tableName);
        }
    }
}
