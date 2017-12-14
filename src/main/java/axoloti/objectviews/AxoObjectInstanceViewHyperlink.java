package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.AxoObjectInstanceHyperlink;
import axoloti.object.ObjectInstanceController;
import components.LabelComponent;
import components.control.ACtrlComponent;
import components.control.PulseButtonComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class AxoObjectInstanceViewHyperlink extends AxoObjectInstanceViewAbstract {

    private PulseButtonComponent button;

    public AxoObjectInstanceViewHyperlink(ObjectInstanceController controller, PatchViewSwing patchView) {
        super(controller, patchView);
    }

    @Override
    public AxoObjectInstanceHyperlink getModel() {
        return (AxoObjectInstanceHyperlink) super.getModel();
    }

    public void PostConstructor() {
        super.PostConstructor();
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        button = new PulseButtonComponent();
        button.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(ACtrlComponent.PROP_VALUE)) {
                    if (evt.getNewValue().equals(1.0)) {
                        getModel().Launch();
                    }
                }
            }
        });
        add(button);
        add(Box.createHorizontalStrut(5));
        InstanceLabel = new LabelComponent(getModel().getInstanceName());
        InstanceLabel.setAlignmentX(LEFT_ALIGNMENT);
        InstanceLabel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addInstanceNameEditor();
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                AxoObjectInstanceViewHyperlink.this.mousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                AxoObjectInstanceViewHyperlink.this.mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        InstanceLabel.addMouseMotionListener(this);
        add(InstanceLabel);
        setLocation(getModel().getX(), getModel().getY());

        resizeToGrid();
        setVisible(true);
    }

    @Override
    public void showInstanceName(String s) {
        super.showInstanceName(s);
        resizeToGrid();
    }
}
