package axoloti.swingui.patch.object;

import axoloti.patch.object.AxoObjectInstanceHyperlink;
import axoloti.swingui.components.LabelComponent;
import static axoloti.swingui.components.control.ACtrlComponent.PROP_VALUE;
import axoloti.swingui.components.control.PulseButtonComponent;
import axoloti.swingui.patch.PatchViewSwing;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;

class AxoObjectInstanceViewHyperlink extends AxoObjectInstanceViewAbstract {

    private PulseButtonComponent button;

    AxoObjectInstanceViewHyperlink(AxoObjectInstanceHyperlink objectInstance, PatchViewSwing patchView) {
        super(objectInstance, patchView);
        initComponents();
    }

    @Override
    public AxoObjectInstanceHyperlink getDModel() {
        return (AxoObjectInstanceHyperlink) super.getDModel();
    }

    private void initComponents() {
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        button = new PulseButtonComponent();
        button.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PROP_VALUE)) {
                    if (evt.getNewValue().equals(1.0)) {
                        getDModel().launch();
                    }
                }
            }
        });
        add(button);
        add(Box.createHorizontalStrut(5));
        instanceLabel = new LabelComponent(getDModel().getInstanceName());
        instanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        instanceLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.consume();
                }
            }

        });

        add(instanceLabel);
        setLocation(getDModel().getX(), getDModel().getY());

        resizeToGrid();
        setVisible(true);
    }

    @Override
    public void showInstanceName(String s) {
        super.showInstanceName(s);
        resizeToGrid();
    }
}
