package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.objectviews.AxoObjectInstanceView;
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

public class AttributeInstanceViewSDFile extends AttributeInstanceViewString {

    AttributeInstanceSDFile attributeInstance;

    JTextField TFFileName;
    JLabel vlabel;
    ButtonComponent ButtonChooseFile;

    public AttributeInstanceViewSDFile(AttributeInstanceSDFile attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFFileName = new JTextField(attributeInstance.getString());
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
                attributeInstance.setString(TFFileName.getText());
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
                attributeInstance.setValueBeforeAdjustment(TFFileName.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFFileName.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().SetDirty();
                }
            }
        });
        ButtonChooseFile = new ButtonComponent("choose");
        ButtonChooseFile.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                JFileChooser fc = new JFileChooser(attributeInstance.getObjectInstance().getPatchModel().GetCurrentWorkingDirectory());
                int returnVal = fc.showOpenDialog(getPatchView().getPatchController().getPatchFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = attributeInstance.toRelative(fc.getSelectedFile());
                    TFFileName.setText(f);
                    if (!f.equals(attributeInstance.getString())) {
                        attributeInstance.setString(f);
                        attributeInstance.getObjectInstance().getPatchModel().SetDirty();
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
        return attributeInstance.getString();
    }

    @Override
    public void setString(String tableName) {
        attributeInstance.setString(tableName);
        if (TFFileName != null) {
            TFFileName.setText(tableName);
        }
    }
}