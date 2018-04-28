package axoloti.piccolo.patch.object.inlet;

import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.piccolo.components.PJackInputComponent;
import axoloti.piccolo.components.PLabelComponent;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewAbstract;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class PInletInstanceZombieView extends PInletInstanceView implements IIoletInstanceView {

    public PInletInstanceZombieView(IoletInstanceController controller, PAxoObjectInstanceViewAbstract o) {
        super(controller, o);

        setLayout(new BoxLayout(getProxyComponent(), BoxLayout.LINE_AXIS));
        setMaximumSize(new Dimension(32767, 14));

        jack = new PJackInputComponent(this);
        jack.setForeground(getModel().getDataType().GetColor());

        addChild(jack);
        addToSwingProxy(Box.createHorizontalStrut(2));

        addChild(new PLabelComponent(getModel().getName()));
        addToSwingProxy(Box.createHorizontalGlue());
        this.addInputEventListener(getInputEventHandler());
    }
}
