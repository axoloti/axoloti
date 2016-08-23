package axoloti.inlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class InletInstanceZombieView extends InletInstanceView {

    private final InletInstanceZombie inletInstanceZombie;

    public InletInstanceZombieView(InletInstanceZombie inletInstanceZombie, AxoObjectInstanceViewAbstract o) {
        super(inletInstanceZombie, o);
        this.inletInstanceZombie = inletInstanceZombie;
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        jack = new components.JackInputComponent(this);
        jack.setForeground(inletInstanceZombie.getDataType().GetColor());
        add(jack);
        add(Box.createHorizontalStrut(2));
        add(new LabelComponent(inletInstanceZombie.inletname));
        add(Box.createHorizontalGlue());
    }

}
