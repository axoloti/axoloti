package axoloti.outlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class OutletInstanceZombieView extends OutletInstanceView {

    public OutletInstanceZombieView(OutletInstanceController controller, AxoObjectInstanceViewAbstract o) {
        super(controller, o);
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createHorizontalGlue());
        add(new LabelComponent(getModel().outletname));
        add(Box.createHorizontalStrut(2));
        jack = new components.JackOutputComponent(this);
        jack.setForeground(getModel().getDataType().GetColor());
        add(jack);
    }
}
