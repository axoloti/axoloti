package axoloti.swingui.patch.object.outlet;

import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class OutletInstanceZombieView extends OutletInstanceView {

    public OutletInstanceZombieView(IoletInstanceController controller, AxoObjectInstanceViewAbstract o) {
        super(controller, o);
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createHorizontalGlue());
        add(new LabelComponent(getModel().getName()));
        add(Box.createHorizontalStrut(2));
        jack = new axoloti.swingui.components.JackOutputComponent(this);
        jack.setForeground(getModel().getDataType().GetColor());
        add(jack);
    }
}
