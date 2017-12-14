package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.utils.Constants;
import components.ButtonComponent;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewSDFile extends AttributeInstanceViewString {

    JTextField TFFileName;
    JLabel vlabel;
    ButtonComponent ButtonChooseFile;

    AttributeInstanceViewSDFile(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceSDFile getModel() {
        return (AttributeInstanceSDFile) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        TFFileName = new JTextField(getModel().getValue());
        Dimension d = TFFileName.getSize();
        d.width = 128;
        d.height = 22;
        TFFileName.setFont(Constants.FONT);
        TFFileName.setMaximumSize(d);
        TFFileName.setMinimumSize(d);
        TFFileName.setPreferredSize(d);
        TFFileName.setSize(d);
        add(TFFileName);
        TFFileName.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                //getController().changeValue(TFFileName.getText());
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
        TFFileName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getController().setModelUndoableProperty(AttributeInstanceSDFile.ATTR_VALUE,TFFileName.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                getController().setModelUndoableProperty(AttributeInstanceSDFile.ATTR_VALUE,TFFileName.getText());
            }
        });
        ButtonChooseFile = new ButtonComponent("choose");
        ButtonChooseFile.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                JFileChooser fc = new JFileChooser(getController().getParent().getParent().getModel().GetCurrentWorkingDirectory());
                Window window = SwingUtilities.getWindowAncestor(AttributeInstanceViewSDFile.this);
                int returnVal = fc.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = getModel().toRelative(fc.getSelectedFile());
                    getController().setModelUndoableProperty(AttributeInstanceSDFile.ATTR_VALUE,f);
                }
            }
        });
        add(ButtonChooseFile);
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
