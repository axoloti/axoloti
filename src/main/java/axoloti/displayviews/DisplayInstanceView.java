package axoloti.displayviews;

import axoloti.displays.DisplayInstance;
import axoloti.displays.DisplayInstanceBool32;
import axoloti.displays.DisplayInstanceController;
import axoloti.displays.DisplayInstanceFrac32SChart;
import axoloti.displays.DisplayInstanceFrac32SDial;
import axoloti.displays.DisplayInstanceFrac32UChart;
import axoloti.displays.DisplayInstanceFrac32UDial;
import axoloti.displays.DisplayInstanceFrac32VBar;
import axoloti.displays.DisplayInstanceFrac32VBarDB;
import axoloti.displays.DisplayInstanceFrac32VU;
import axoloti.displays.DisplayInstanceFrac4ByteVBar;
import axoloti.displays.DisplayInstanceFrac4UByteVBar;
import axoloti.displays.DisplayInstanceFrac4UByteVBarDB;
import axoloti.displays.DisplayInstanceFrac8S128VBar;
import axoloti.displays.DisplayInstanceFrac8U128VBar;
import axoloti.displays.DisplayInstanceInt32Bar16;
import axoloti.displays.DisplayInstanceInt32Bar32;
import axoloti.displays.DisplayInstanceInt32HexLabel;
import axoloti.displays.DisplayInstanceInt32Label;
import axoloti.displays.DisplayInstanceNoteLabel;
import axoloti.displays.DisplayInstanceVScale;
import components.LabelComponent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public abstract class DisplayInstanceView extends JPanel implements IDisplayInstanceView {

    final DisplayInstance displayInstance;
    DisplayInstanceController controller;
    
    DisplayInstanceView(DisplayInstance displayInstance, DisplayInstanceController controller) {
        this.displayInstance = displayInstance;
        this.controller = controller;
    }

    public void PostConstructor() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        if ((displayInstance.getDefinition().noLabel == null) || (displayInstance.getDefinition().noLabel == false)) {
            add(new LabelComponent(displayInstance.getDefinition().getName()));
        }
        setSize(getPreferredSize());
	String description = displayInstance.getDefinition().getDescription();
	if (description != null) {
            setToolTipText(description);
        }
    }

    @Override
    public abstract void updateV();

    public static DisplayInstanceView createView(DisplayInstanceController controller, IDisplayInstanceView obj) {
        DisplayInstance model = controller.getModel();
        DisplayInstanceView view;
        if (model instanceof DisplayInstanceBool32) {
            view = new DisplayInstanceViewBool32((DisplayInstanceBool32) model, controller);
        } else if (model instanceof DisplayInstanceFrac32SChart) {
            view = new DisplayInstanceViewFrac32SChart((DisplayInstanceFrac32SChart) model, controller);
        } else if (model instanceof DisplayInstanceFrac32SDial) {
            view = new DisplayInstanceViewFrac32SDial((DisplayInstanceFrac32SDial) model, controller);
        } else if (model instanceof DisplayInstanceFrac32UChart) {
            view = new DisplayInstanceViewFrac32UChart((DisplayInstanceFrac32UChart) model, controller);
        } else if (model instanceof DisplayInstanceFrac32UDial) {
            view = new DisplayInstanceViewFrac32UDial((DisplayInstanceFrac32UDial) model, controller);
        } else if (model instanceof DisplayInstanceFrac32VBar) {
            view = new DisplayInstanceViewFrac32VBar((DisplayInstanceFrac32VBar) model, controller);
        } else if (model instanceof DisplayInstanceFrac32VBarDB) {
            view = new DisplayInstanceViewFrac32VBarDB((DisplayInstanceFrac32VBarDB) model, controller);
        } else if (model instanceof DisplayInstanceFrac32VU) {
            view = new DisplayInstanceViewFrac32VU((DisplayInstanceFrac32VU) model, controller);
        } else if (model instanceof DisplayInstanceFrac4ByteVBar) {
            view = new DisplayInstanceViewFrac4ByteVBar((DisplayInstanceFrac4ByteVBar) model, controller);
        } else if (model instanceof DisplayInstanceFrac4UByteVBar) {
            view = new DisplayInstanceViewFrac4UByteVBar((DisplayInstanceFrac4UByteVBar) model, controller);
        } else if (model instanceof DisplayInstanceFrac4UByteVBarDB) {
            view = new DisplayInstanceViewFrac4UByteVBarDB((DisplayInstanceFrac4UByteVBarDB) model, controller);
        } else if (model instanceof DisplayInstanceFrac8S128VBar) {
            view = new DisplayInstanceViewFrac8S128VBar((DisplayInstanceFrac8S128VBar) model, controller);
        } else if (model instanceof DisplayInstanceFrac8U128VBar) {
            view = new DisplayInstanceViewFrac8U128VBar((DisplayInstanceFrac8U128VBar) model, controller);
        } else if (model instanceof DisplayInstanceInt32Bar16) {
            view = new DisplayInstanceViewInt32Bar16((DisplayInstanceInt32Bar16) model, controller);
        } else if (model instanceof DisplayInstanceInt32Bar32) {
            view = new DisplayInstanceViewInt32Bar32((DisplayInstanceInt32Bar32) model, controller);
        } else if (model instanceof DisplayInstanceInt32HexLabel) {
            view = new DisplayInstanceViewInt32HexLabel((DisplayInstanceInt32HexLabel) model, controller);
        } else if (model instanceof DisplayInstanceInt32Label) {
            view = new DisplayInstanceViewInt32Label((DisplayInstanceInt32Label) model, controller);
        } else if (model instanceof DisplayInstanceNoteLabel) {
            view = new DisplayInstanceViewNoteLabel((DisplayInstanceNoteLabel) model, controller);
        } else if (model instanceof DisplayInstanceVScale) {
            view = new DisplayInstanceViewVScale((DisplayInstanceVScale) model, controller);
        } else {
            view = null;
        }
        view.PostConstructor();
        controller.addView(view);
        return view;
    }    

    @Override
    public DisplayInstanceController getController() {
        return controller;
    }
}
