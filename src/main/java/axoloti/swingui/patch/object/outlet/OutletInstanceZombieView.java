package axoloti.swingui.patch.object.outlet;

import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class OutletInstanceZombieView extends OutletInstanceView {

    public OutletInstanceZombieView(IoletInstanceController controller, AxoObjectInstanceViewAbstract o) {
        super(controller, o);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createHorizontalGlue());
        add(new LabelComponent(getModel().getName()));
        add(Box.createHorizontalStrut(2));
        jack = new JackOutputComponent();
        jack.setForeground(getModel().getDataType().GetColor());
        add(jack);
    }
}
