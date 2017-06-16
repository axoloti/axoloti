package axoloti.inlets;

import axoloti.objectviews.AxoObjectInstanceViewAbstract;
import components.LabelComponent;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class InletInstanceZombieView extends InletInstanceView {


    public InletInstanceZombieView(InletInstanceController controller, AxoObjectInstanceViewAbstract o) {
        super( controller, o);
    }

    @Override
    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        jack = new components.JackInputComponent(this);
        jack.setForeground(getModel().getDataType().GetColor());
        add(jack);
        add(Box.createHorizontalStrut(2));
        add(new LabelComponent(getModel().inletname));
        add(Box.createHorizontalGlue());
    }

}
