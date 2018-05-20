package axoloti.swingui.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceWavefile;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

class AttributeInstanceViewWavefile extends AttributeInstanceView {

    JTextField TFwaveFilename;

    AttributeInstanceViewWavefile(AttributeInstance controller, IAxoObjectInstanceView axoObjectInstanceView) {
        super(controller, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceWavefile getDModel() {
        return (AttributeInstanceWavefile) super.getDModel();
    }

    private void initComponents() {
        TFwaveFilename = new JTextField(getDModel().getWaveFilename());
        Dimension d = TFwaveFilename.getSize();
        d.width = 128;
        d.height = 22;
        TFwaveFilename.setMaximumSize(d);
        TFwaveFilename.setMinimumSize(d);
        TFwaveFilename.setPreferredSize(d);
        TFwaveFilename.setSize(d);
        add(TFwaveFilename);
        TFwaveFilename.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent ke) {
                repaint();
            }
        });
        TFwaveFilename.getDocument().addDocumentListener(new DocumentListener() {

            void update() {
                getDModel().setWaveFilename(TFwaveFilename.getText());
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
    public void lock() {
        if (TFwaveFilename != null) {
            TFwaveFilename.setEnabled(false);
        }
    }

    @Override
    public void unlock() {
        if (TFwaveFilename != null) {
            TFwaveFilename.setEnabled(true);
        }
    }
}
