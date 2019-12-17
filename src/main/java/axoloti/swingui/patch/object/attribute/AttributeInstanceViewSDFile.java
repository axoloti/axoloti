package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceSDFile;
import axoloti.swingui.components.ButtonComponent;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewSDFile extends AttributeInstanceViewString {

    private JTextField textFieldFileName;
    private ButtonComponent buttonChooseFile;

    AttributeInstanceViewSDFile(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceSDFile getDModel() {
        return (AttributeInstanceSDFile) super.getDModel();
    }

    private void initComponents() {
        textFieldFileName = new JTextField(getDModel().getValue());
        Dimension d = textFieldFileName.getSize();
        d.width = 128;
        d.height = 22;
        textFieldFileName.setFont(Constants.FONT);
        textFieldFileName.setMaximumSize(d);
        textFieldFileName.setMinimumSize(d);
        textFieldFileName.setPreferredSize(d);
        textFieldFileName.setSize(d);
        add(textFieldFileName);
        textFieldFileName.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                model.getController().changeValue(textFieldFileName.getText());
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
        textFieldFileName.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                model.getController().addMetaUndo("edit attribute " + getDModel().getName(), focusEdit);
            }

            @Override
            public void focusLost(FocusEvent e) {
                model.getController().changeValue(textFieldFileName.getText());
            }
        });
        buttonChooseFile = new ButtonComponent("choose");
        buttonChooseFile.addActListener(new ButtonComponent.ActListener() {
            @Override
            public void fire() {
                JFileChooser fc = new JFileChooser(getDModel().getParent().getParent().getCurrentWorkingDirectory());
                Window window = SwingUtilities.getWindowAncestor(AttributeInstanceViewSDFile.this);
                int returnVal = fc.showOpenDialog(window);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String f = getDModel().toRelative(fc.getSelectedFile());
                    model.getController().changeValue(f);
                }
            }
        });
        add(buttonChooseFile);
    }

    @Override
    public void lock() {
        if (textFieldFileName != null) {
            textFieldFileName.setEnabled(false);
        }
        if (buttonChooseFile != null) {
            buttonChooseFile.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (textFieldFileName != null) {
            textFieldFileName.setEnabled(true);
        }
        if (buttonChooseFile != null) {
            buttonChooseFile.setEnabled(true);
        }
    }

    @Override
    public void setString(String tableName) {
        if (textFieldFileName == null) {
            return;
        }
        if (!textFieldFileName.getText().equals(tableName)) {
            textFieldFileName.setText(tableName);
        }
    }
}
