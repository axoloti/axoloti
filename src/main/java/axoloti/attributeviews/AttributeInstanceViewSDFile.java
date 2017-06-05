package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.utils.Constants;
import components.ButtonComponent;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewSDFile extends AttributeInstanceViewString {

    JTextField TFFileName;
    JLabel vlabel;
    ButtonComponent ButtonChooseFile;

    public AttributeInstanceViewSDFile(AttributeInstanceSDFile attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceSDFile getAttributeInstance() {
        return (AttributeInstanceSDFile) super.getAttributeInstance();
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFFileName = new JTextField(getAttributeInstance().getString());
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
                getAttributeInstance().setString(TFFileName.getText());
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
                getAttributeInstance().setValueBeforeAdjustment(TFFileName.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFFileName.getText().equals(getAttributeInstance().getValueBeforeAdjustment())) {
                    getAttributeInstance().getObjectInstance().getPatchModel().setDirty();
                }
            }
        });
        ButtonChooseFile = new ButtonComponent("choose");
        ButtonChooseFile.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                JFileChooser fc = new JFileChooser(getAttributeInstance().getObjectInstance().getPatchModel().GetCurrentWorkingDirectory());
                int returnVal = fc.showOpenDialog(null // FIXME: parent frame
                        );
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = getAttributeInstance().toRelative(fc.getSelectedFile());
                    TFFileName.setText(f);
                    if (!f.equals(getAttributeInstance().getString())) {
                        getAttributeInstance().setString(f);
                        attributeInstance.getObjectInstance().getPatchModel().setDirty();
                    }
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
    public String getString() {
        return getAttributeInstance().getString();
    }

    @Override
    public void setString(String tableName) {
        getAttributeInstance().setString(tableName);
        if (TFFileName != null) {
            TFFileName.setText(tableName);
        }
    }
}
