package axoloti.swingui.patch.object.attribute;

import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceController;
import axoloti.patch.object.attribute.AttributeInstanceWavefile;
import axoloti.abstractui.IAxoObjectInstanceView;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewWavefile extends AttributeInstanceView {

    JTextField TFwaveFilename;

    AttributeInstanceViewWavefile(AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceWavefile getModel() {
        return (AttributeInstanceWavefile) super.getModel();
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();
        TFwaveFilename = new JTextField(getModel().getWaveFilename());
        Dimension d = TFwaveFilename.getSize();
        d.width = 128;
        d.height = 22;
        TFwaveFilename.setMaximumSize(d);
        TFwaveFilename.setMinimumSize(d);
        TFwaveFilename.setPreferredSize(d);
        TFwaveFilename.setSize(d);
        add(TFwaveFilename);
        TFwaveFilename.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                repaint();
            }
        });
        TFwaveFilename.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                getModel().setWaveFilename(TFwaveFilename.getText());
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
        TFwaveFilename.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
    }

    @Override
    public void Lock() {
        if (TFwaveFilename != null) {
            TFwaveFilename.setEnabled(false);
        }
    }

    @Override
    public void UnLock() {
        if (TFwaveFilename != null) {
            TFwaveFilename.setEnabled(true);
        }
    }
}
