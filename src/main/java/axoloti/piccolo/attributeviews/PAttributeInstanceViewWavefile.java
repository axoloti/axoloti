package axoloti.piccolo.attributeviews;

import axoloti.attribute.AttributeInstanceWavefile;
import axoloti.objectviews.IAxoObjectInstanceView;
import components.piccolo.PTextFieldComponent;
import java.awt.Dimension;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;

public class PAttributeInstanceViewWavefile extends PAttributeInstanceView {

    AttributeInstanceWavefile attributeInstance;
    PTextFieldComponent TFwaveFilename;

    public PAttributeInstanceViewWavefile(AttributeInstanceWavefile attributeInstance, IAxoObjectInstanceView axoObjectInstanceView) {
        super(attributeInstance, axoObjectInstanceView);
        this.attributeInstance = attributeInstance;
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();
        TFwaveFilename = new PTextFieldComponent(attributeInstance.getWaveFilename());
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
                TFwaveFilename.transferFocus(ke, getPatchView());
            }

            @Override
            public void keyPressed(PInputEvent ke) {
                repaint();
            }

            @Override
            public void keyboardFocusGained(PInputEvent e) {
                attributeInstance.setValueBeforeAdjustment(TFwaveFilename.getText());
            }

            @Override
            public void keyboardFocusLost(PInputEvent e) {
                if (!TFwaveFilename.getText().equals(attributeInstance.getValueBeforeAdjustment())) {
                    attributeInstance.getObjectInstance().getPatchModel().setDirty();
                }
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
