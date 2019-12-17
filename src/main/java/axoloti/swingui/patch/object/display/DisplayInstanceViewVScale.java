package axoloti.swingui.patch.object.display;

import axoloti.patch.object.display.DisplayInstance;
import axoloti.swingui.components.displays.VValueLabelsComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewVScale extends DisplayInstanceView {

    private VValueLabelsComponent vlabels;

    DisplayInstanceViewVScale(DisplayInstance displayInstance) {
        super(displayInstance);
        initComponents();
    }

    private void initComponents() {
        vlabels = new VValueLabelsComponent(-60, 10, 10);
        add(vlabels);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
    }
}
