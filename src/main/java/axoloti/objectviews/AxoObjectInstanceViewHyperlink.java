package axoloti.objectviews;

import axoloti.PatchViewSwing;
import axoloti.object.AxoObjectInstanceHyperlink;
import components.LabelComponent;
import components.control.ACtrlEvent;
import components.control.ACtrlListener;
import components.control.PulseButtonComponent;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class AxoObjectInstanceViewHyperlink extends AxoObjectInstanceViewAbstract {

    AxoObjectInstanceHyperlink model;
    private PulseButtonComponent button;

    public AxoObjectInstanceViewHyperlink(AxoObjectInstanceHyperlink model, PatchViewSwing patchView) {
        super(model, patchView);
        this.model = model;
    }

    public void PostConstructor() {
        super.PostConstructor();
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        button = new PulseButtonComponent();
        button.addACtrlListener(new ACtrlListener() {
            @Override
            public void ACtrlAdjusted(ACtrlEvent e) {
                if (e.getValue() == 1.0) {
                    model.Launch();
                }
            }

            @Override
            public void ACtrlAdjustmentBegin(ACtrlEvent e) {
            }

            @Override
            public void ACtrlAdjustmentFinished(ACtrlEvent e) {
            }
        });
        add(button);
        add(Box.createHorizontalStrut(5));
        InstanceLabel = new LabelComponent(model.getInstanceName());
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
        setLocation(model.getX(), model.getY());

        resizeToGrid();
    }

    @Override
    public void setInstanceName(String s) {
        super.setInstanceName(s);
        resizeToGrid();
    }
}
