package axoloti.piccolo.outlets;

import axoloti.outlets.IOutletInstanceView;
import axoloti.outlets.OutletInstanceZombie;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewAbstract;
import components.piccolo.PJackOutputComponent;
import components.piccolo.PLabelComponent;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class POutletInstanceZombieView extends POutletInstanceView implements IOutletInstanceView {

    OutletInstanceZombie outletInstanceZombie;

    public POutletInstanceZombieView(OutletInstanceZombie outletInstanceZombie, PAxoObjectInstanceViewAbstract o) {
        super(outletInstanceZombie, o);
        this.outletInstanceZombie = outletInstanceZombie;
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        addToSwingProxy(Box.createHorizontalGlue());
        addChild(new PLabelComponent(outletInstanceZombie.getName()));
        addToSwingProxy(Box.createHorizontalStrut(2));
        jack = new PJackOutputComponent(this);
        jack.setForeground(outletInstanceZombie.getDataType().GetColor());
        addChild(jack);
        addInputEventListener(getInputEventHandler());
    }
}
