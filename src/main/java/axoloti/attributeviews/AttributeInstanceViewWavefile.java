package axoloti.attributeviews;

import axoloti.attribute.AttributeInstance;
import axoloti.attribute.AttributeInstanceController;
import axoloti.attribute.AttributeInstanceWavefile;
import axoloti.objectviews.IAxoObjectInstanceView;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AttributeInstanceViewWavefile extends AttributeInstanceView {

    JTextField TFwaveFilename;
    JLabel vlabel;

    public AttributeInstanceViewWavefile(AttributeInstanceWavefile attributeInstance, AttributeInstanceController controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, controller, axoObjectInstanceView);
    }

    @Override
    public AttributeInstanceWavefile getAttributeInstance() {
        return (AttributeInstanceWavefile)super.getAttributeInstance();
    }
    
    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFwaveFilename = new JTextField(getAttributeInstance().getWaveFilename());
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
                getAttributeInstance().setWaveFilename(TFwaveFilename.getText());
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
                getAttributeInstance().setValueBeforeAdjustment(TFwaveFilename.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFwaveFilename.getText().equals(getAttributeInstance().getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                }
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
