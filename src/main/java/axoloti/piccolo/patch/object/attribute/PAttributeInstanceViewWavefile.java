package axoloti.piccolo.patch.object.attribute;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.attribute.AttributeInstance;
import axoloti.patch.object.attribute.AttributeInstanceWavefile;
import axoloti.piccolo.components.PTextFieldComponent;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

class PAttributeInstanceViewWavefile extends PAttributeInstanceView {

    PTextFieldComponent TFwaveFilename;

    PAttributeInstanceViewWavefile(AttributeInstance attribute, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attribute, axoObjectInstanceView);
        initComponents();
    }

    @Override
    public AttributeInstanceWavefile getDModel() {
        return (AttributeInstanceWavefile) super.getDModel();
    }

    private void initComponents() {
        TFwaveFilename = new PTextFieldComponent(getDModel().getWaveFilename());
        Dimension d = TFwaveFilename.getSize();
        d.width = 128;
        d.height = 22;
        TFwaveFilename.setMaximumSize(d);
        TFwaveFilename.setMinimumSize(d);
        TFwaveFilename.setPreferredSize(d);
        TFwaveFilename.setSize(d);
        addChild(TFwaveFilename);
        TFwaveFilename.addInputEventListener(new PBasicInputEventHandler() {
            @Override
            public void keyTyped(PInputEvent ke) {
            }

            @Override
            public void keyPressed(PInputEvent ke) {
                repaint();
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
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
