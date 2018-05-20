package axoloti.piccolo.patch.object.inlet;

import axoloti.patch.object.inlet.InletInstance;
import axoloti.piccolo.components.PJackInputComponent;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewAbstract;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class PInletInstanceZombieView extends PInletInstanceView {

    public PInletInstanceZombieView(InletInstance inletInstance, PAxoObjectInstanceViewAbstract o) {
        super(inletInstance, o);
        initComponent();
    }

    private void initComponent() {
        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        jack = new PJackInputComponent(this);
        jack.setForeground(getDModel().getDataType().getColor());

        addChild(jack);
        addToSwingProxy(Box.createHorizontalStrut(2));

        addChild(new PLabelComponent(getDModel().getName()));
        addToSwingProxy(Box.createHorizontalGlue());
        this.addInputEventListener(getInputEventHandler());
    }
}
