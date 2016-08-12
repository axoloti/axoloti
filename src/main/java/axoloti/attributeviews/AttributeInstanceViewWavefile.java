package axoloti.attributeviews;

import axoloti.attribute.AttributeInstanceWavefile;
import axoloti.objectviews.AxoObjectInstanceView;
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

    AttributeInstanceWavefile attributeInstance;
    JTextField TFwaveFilename;
    JLabel vlabel;

    public AttributeInstanceViewWavefile(AttributeInstanceWavefile attributeInstance, AxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFwaveFilename = new JTextField(attributeInstance.getWaveFilename());
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
                attributeInstance.setWaveFilename(TFwaveFilename.getText());
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
                attributeInstance.setValueBeforeAdjustment(TFwaveFilename.getText());
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!TFwaveFilename.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().SetDirty();
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
