package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceSDFile;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PTextFieldComponent;
import components.piccolo.control.PButtonComponent;
import java.awt.Dimension;
import javax.swing.JFileChooser;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAttributeInstanceViewSDFile extends PAttributeInstanceViewString {

    AttributeInstanceSDFile attributeInstance;

    PTextFieldComponent TFFileName;
    PButtonComponent ButtonChooseFile;

    public PAttributeInstanceViewSDFile(AttributeInstanceSDFile attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFFileName = new PTextFieldComponent(attributeInstance.getValue());
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
                attributeInstance.setValue(TFFileName.getText());
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
                //TFFileName.transferFocus(ke, getPatchView());
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                //attributeInstance.setValueBeforeAdjustment(TFFileName.getText());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                //if (!TFFileName.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                //    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                //}
            }
        });
        ButtonChooseFile = new PButtonComponent("choose", axoObjectInstanceView);
        ButtonChooseFile.addActListener(new PButtonComponent.ActListener() {
            @Override
            public void OnPushed() {
                JFileChooser fc = new JFileChooser(attributeInstance.getObjectInstance().getPatchModel().GetCurrentWorkingDirectory());
                int returnVal = fc.showOpenDialog(null); // FIXME: parent frame, was: getPatchView().getPatchController().getPatchFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = attributeInstance.toRelative(fc.getSelectedFile());
                    TFFileName.setText(f);
                    if (!f.equals(attributeInstance.getValue())) {
                        attributeInstance.setValue(f);
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
    public String getString() {
        return attributeInstance.getValue();
    }

    @Override
    public void setString(String tableName) {
        attributeInstance.setValue(tableName);
        if (TFFileName != null) {
            TFFileName.setText(tableName);
        }
    }
}
