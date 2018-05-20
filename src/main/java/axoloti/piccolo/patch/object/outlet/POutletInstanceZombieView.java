package axoloti.piccolo.patch.object.outlet;

import axoloti.patch.object.outlet.OutletInstance;
import axoloti.piccolo.components.PJackOutputComponent;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewAbstract;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class POutletInstanceZombieView extends POutletInstanceView {

    public POutletInstanceZombieView(OutletInstance outletInstance, PAxoObjectInstanceViewAbstract o) {
        super(outletInstance, o);
        initComponent();
    }

    private void initComponent() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        addToSwingProxy(Box.createHorizontalGlue());
        addChild(new PLabelComponent(getDModel().getName()));
        addToSwingProxy(Box.createHorizontalStrut(2));
        jack = new PJackOutputComponent(this);
        jack.setForeground(getDModel().getDataType().getColor());
        addChild(jack);
        addInputEventListener(getInputEventHandler());
    }
}
