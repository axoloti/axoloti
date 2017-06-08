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


    @Override
    public DisplayInstanceController getController() {
        return controller;
    }
}
