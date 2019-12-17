package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceSDFile;
import axoloti.piccolo.components.PTextFieldComponent;
import axoloti.piccolo.components.control.PButtonComponent;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;



class PAttributeInstanceViewSDFile extends PAttributeInstanceViewString {

    PTextFieldComponent TFFileName;
    PButtonComponent buttonChooseFile;

    PAttributeInstanceViewSDFile(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceSDFile getDModel() {
        return (AttributeInstanceSDFile) super.getDModel();
    }

    private void initComponents() {
        TFFileName = new PTextFieldComponent(getDModel().getValue());
        Dimension d = TFFileName.getSize();
        d.width = 128;
        d.height = 22;
        TFFileName.setMaximumSize(d);
        TFFileName.setMinimumSize(d);
        TFFileName.setPreferredSize(d);
        TFFileName.setSize(d);
        addChild(TFFileName);
        TFFileName.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            getDModel().getController().changeValue(TFFileName.getText());
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

        TFFileName.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void keyTyped(PInputEvent ke) {
                if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
                    TFFileName.transferFocus(ke, patchView);
                }
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                getDModel().getController().changeValue(TFFileName.getText());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                getDModel().getController().changeValue(TFFileName.getText());
            }
        });
        buttonChooseFile = new PButtonComponent("choose", axoObjectInstanceView);
        buttonChooseFile.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void fire() {
                JFileChooser fc = new JFileChooser(getDModel().getParent().getParent().getCurrentWorkingDirectory());
                Window window = SwingUtilities.getWindowAncestor(PAttributeInstanceViewSDFile.this.getProxyComponent());
                int returnVal = fc.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = getDModel().toRelative(fc.getSelectedFile());
                    if (!f.equals(getDModel().getValue())) {
                        getDModel().getController().changeValue(f);
                    }
                }
            }
        });
        addChild(buttonChooseFile);
    }

    @Override
    public void lock() {
        if (TFFileName != null) {
            TFFileName.setEnabled(false);
        }
        if (buttonChooseFile != null) {
            buttonChooseFile.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (TFFileName != null) {
            TFFileName.setEnabled(true);
        }
        if (buttonChooseFile != null) {
            buttonChooseFile.setEnabled(true);
        }
    }

    @Override
    public void setString(String tableName) {
        if (TFFileName != null) {
            TFFileName.setText(tableName);
        }
    }
}
