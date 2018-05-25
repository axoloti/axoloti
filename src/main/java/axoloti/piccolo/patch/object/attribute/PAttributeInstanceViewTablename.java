package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.PatchView;
import axoloti.patch.object.attribute.AttributeInstance;
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

    PAttributeInstanceViewTablename(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    public PatchView getPatchView() {
        return axoObjectInstanceView.getPatchView();
    }

    @Override
    public AttributeInstanceTablename getDModel() {
        return (AttributeInstanceTablename) super.getDModel();
    }

    private void initComponents() {
        TFtableName = new PTextFieldComponent(getDModel().getValue());
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
                            getDModel().getController().changeValue(TFtableName.getText());
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
                getDModel().getController().addMetaUndo("edit attribute " + getDModel().getName());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                getDModel().getController().changeValue(TFtableName.getText());

            }
        });
    }

    @Override
    public void lock() {
        if (TFtableName != null) {
            TFtableName.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
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
