package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceVScale;
import components.displays.VValueLabelsComponent;
import java.beans.PropertyChangeEvent;

class DisplayInstanceViewVScale extends DisplayInstanceView {

    private VValueLabelsComponent vlabels;

    public DisplayInstanceViewVScale(DisplayInstanceVScale displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
    }

    @Override
    public void updateV() {
    }

    @Override
    public void PostConstructor() {
        super.PostConstructor();

        vlabels = new VValueLabelsComponent(-60, 10, 10);
        add(vlabels);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
