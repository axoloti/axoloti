package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import components.displays.VValueLabelsComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewVScale extends DisplayInstanceView {

    private VValueLabelsComponent vlabels;

    DisplayInstanceViewVScale(DisplayInstanceController controller) {
        super(controller);
    }

    @Override
    void PostConstructor() {
        super.PostConstructor();

        vlabels = new VValueLabelsComponent(-60, 10, 10);
        add(vlabels);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
