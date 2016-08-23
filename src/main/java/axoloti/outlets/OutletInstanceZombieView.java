package axoloti.outlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class OutletInstanceZombieView extends OutletInstanceView {

    private final OutletInstanceZombie outletInstanceZombie;

    public OutletInstanceZombieView(OutletInstanceZombie outletInstanceZombie, AxoObjectInstanceViewAbstract o) {
        super(outletInstanceZombie, o);
        this.outletInstanceZombie = outletInstanceZombie;
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(Box.createHorizontalGlue());
        add(new LabelComponent(outletInstanceZombie.outletname));
        add(Box.createHorizontalStrut(2));
        jack = new components.JackOutputComponent(this);
        jack.setForeground(outletInstanceZombie.getDataType().GetColor());
        add(jack);
    }
}
