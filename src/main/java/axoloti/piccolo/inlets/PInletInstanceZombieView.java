package axoloti.piccolo.inlets;

import axoloti.patch.object.iolet.IoletInstanceController;
import axoloti.abstractui.IIoletInstanceView;
import axoloti.patch.object.inlet.InletInstanceZombie;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewAbstract;
import axoloti.piccolo.components.PJackInputComponent;
import axoloti.piccolo.components.PLabelComponent;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class PInletInstanceZombieView extends PInletInstanceView implements IIoletInstanceView {

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
        addChild(new PLabelComponent(inletInstanceZombie.getName()));
        addToSwingProxy(Box.createHorizontalGlue());
        this.addInputEventListener(getInputEventHandler());
    }

    @Override
    public IoletInstanceController getController() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
