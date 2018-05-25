package axoloti.swingui.patch.object.display;

import axoloti.abstractui.IDisplayInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.LabelComponent;
import axoloti.swingui.mvc.ViewPanel;
import java.beans.PropertyChangeEvent;
import javax.swing.BoxLayout;

public abstract class DisplayInstanceView extends ViewPanel<DisplayInstance> implements IDisplayInstanceView {

    private LabelComponent label;

    DisplayInstanceView(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        label = new LabelComponent("");
        add(label);
        setSize(getPreferredSize());
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (DisplayInstance.NAME.is(evt)) {
            label.setText((String) evt.getNewValue());
            doLayout();
        } else if (DisplayInstance.NOLABEL.is(evt)) {
            Boolean b = (Boolean) evt.getNewValue();
            if (b == null) {
                b = false;
            }
            label.setVisible(!b);
        } else if (DisplayInstance.DESCRIPTION.is(evt)) {
            String s = (String) evt.getNewValue();
            if ((s != null) && (s.isEmpty())) {
                s = null;
            }
            setToolTipText(s);
        }
    }

    @Override
    public void dispose() {
    }

}
