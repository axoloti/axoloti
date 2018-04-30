package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstanceController;
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
    PButtonComponent ButtonChooseFile;

    public PAttributeInstanceViewSDFile(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceSDFile getModel() {
        return (AttributeInstanceSDFile) super.getModel();
    }

    private void initComponents() {
        TFFileName = new PTextFieldComponent(getModel().getValue());
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
                            getController().changeValue(TFFileName.getText());
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
                getController().changeValue(TFFileName.getText());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                getController().changeValue(TFFileName.getText());
            }
        });
        ButtonChooseFile = new PButtonComponent("choose", axoObjectInstanceView);
        ButtonChooseFile.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                JFileChooser fc = new JFileChooser(getModel().getParent().getParent().GetCurrentWorkingDirectory());
                Window window = SwingUtilities.getWindowAncestor(PAttributeInstanceViewSDFile.this.getProxyComponent());
                int returnVal = fc.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = getModel().toRelative(fc.getSelectedFile());
                    if (!f.equals(getModel().getValue())) {
                        getController().changeValue(f);
                    }
                }
            }
        });
        addChild(ButtonChooseFile);
    }

    @Override
    public void Lock() {
        if (TFFileName != null) {
            TFFileName.setEnabled(false);
        }
        if (ButtonChooseFile != null) {
            ButtonChooseFile.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFFileName != null) {
            TFFileName.setEnabled(true);
        }
        if (ButtonChooseFile != null) {
            ButtonChooseFile.setEnabled(true);
        }
    }

    @Override
    public void setString(String tableName) {
        if (TFFileName != null) {
            TFFileName.setText(tableName);
        }
    }
}
