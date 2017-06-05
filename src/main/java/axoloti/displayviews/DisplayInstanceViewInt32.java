package axoloti.displayviews;

import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceInt32;
import java.beans.PropertyChangeEvent;

abstract class DisplayInstanceViewInt32 extends DisplayInstanceView1 {
    DisplayInstanceInt32 displayInstance;
    
    DisplayInstanceViewInt32(DisplayInstanceInt32 displayInstance, DisplayInstanceController controller) {
        super(displayInstance, controller);
        this.displayInstance = displayInstance;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.\
        updateV();
    }

}
