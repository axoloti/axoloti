package axoloti.inlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class InletInstanceZombieView extends InletInstanceView {
    public InletInstanceZombieView(InletInstanceZombie inletInstanceZombie, AxoObjectInstanceViewAbstract o) {
        super(inletInstanceZombie, o);        
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        jack = new components.JackInputComponent(this);
        add(jack);
        add(Box.createHorizontalStrut(2));
        add(new LabelComponent(inletInstanceZombie.inletname));
        add(Box.createHorizontalGlue());
    }
}
