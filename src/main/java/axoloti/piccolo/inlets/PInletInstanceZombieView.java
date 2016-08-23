package axoloti.piccolo.inlets;

import axoloti.inlets.IInletInstanceView;
import axoloti.inlets.InletInstanceZombie;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewAbstract;
import components.piccolo.PJackInputComponent;
import components.piccolo.PLabelComponent;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class PInletInstanceZombieView extends PInletInstanceView implements IInletInstanceView {

    InletInstanceZombie inletInstanceZombie;

    public PInletInstanceZombieView(InletInstanceZombie inletInstanceZombie, PAxoObjectInstanceViewAbstract o) {
        super(inletInstanceZombie, o);
        this.inletInstanceZombie = inletInstanceZombie;
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        jack = new PJackInputComponent(this);
        jack.setForeground(inletInstanceZombie.getDataType().GetColor());

        addChild(jack);
        addToSwingProxy(Box.createHorizontalStrut(2));
        addChild(new PLabelComponent(inletInstanceZombie.inletname));
        addToSwingProxy(Box.createHorizontalGlue());
        this.addInputEventListener(getInputEventHandler());
    }

}
