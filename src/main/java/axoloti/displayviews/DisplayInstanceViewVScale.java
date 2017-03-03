package axoloti.displayviews;

import axoloti.displays.DisplayInstanceVScale;
import components.displays.VValueLabelsComponent;

public class DisplayInstanceViewVScale extends DisplayInstanceView {

    DisplayInstanceVScale displayInstance;
    private VValueLabelsComponent vlabels;

    public DisplayInstanceViewVScale(DisplayInstanceVScale displayInstance) {
        super(displayInstance);
        this.displayInstance = displayInstance;
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
}