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
import axoloti.objectviews.IAxoObjectInstanceView;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceViewFactory {

    public static DisplayInstanceView createView(DisplayInstanceController controller, IAxoObjectInstanceView obj) {
        DisplayInstance model = controller.getModel();
        DisplayInstanceView view;
        if (model instanceof DisplayInstanceBool32) {
            view = new DisplayInstanceViewBool32(controller);
        } else if (model instanceof DisplayInstanceFrac32SChart) {
            view = new DisplayInstanceViewFrac32SChart(controller);
        } else if (model instanceof DisplayInstanceFrac32SDial) {
            view = new DisplayInstanceViewFrac32SDial(controller);
        } else if (model instanceof DisplayInstanceFrac32UChart) {
            view = new DisplayInstanceViewFrac32UChart(controller);
        } else if (model instanceof DisplayInstanceFrac32UDial) {
            view = new DisplayInstanceViewFrac32UDial(controller);
        } else if (model instanceof DisplayInstanceFrac32VBar) {
            view = new DisplayInstanceViewFrac32VBar(controller);
        } else if (model instanceof DisplayInstanceFrac32VBarDB) {
            view = new DisplayInstanceViewFrac32VBarDB(controller);
        } else if (model instanceof DisplayInstanceFrac32VU) {
            view = new DisplayInstanceViewFrac32VU(controller);
        } else if (model instanceof DisplayInstanceFrac4ByteVBar) {
            view = new DisplayInstanceViewFrac4ByteVBar(controller);
        } else if (model instanceof DisplayInstanceFrac4UByteVBar) {
            view = new DisplayInstanceViewFrac4UByteVBar(controller);
        } else if (model instanceof DisplayInstanceFrac4UByteVBarDB) {
            view = new DisplayInstanceViewFrac4UByteVBarDB(controller);
        } else if (model instanceof DisplayInstanceFrac8S128VBar) {
            view = new DisplayInstanceViewFrac8S128VBar(controller);
        } else if (model instanceof DisplayInstanceFrac8U128VBar) {
            view = new DisplayInstanceViewFrac8U128VBar(controller);
        } else if (model instanceof DisplayInstanceInt32Bar16) {
            view = new DisplayInstanceViewInt32Bar16(controller);
        } else if (model instanceof DisplayInstanceInt32Bar32) {
            view = new DisplayInstanceViewInt32Bar32(controller);
        } else if (model instanceof DisplayInstanceInt32HexLabel) {
            view = new DisplayInstanceViewInt32HexLabel(controller);
        } else if (model instanceof DisplayInstanceInt32Label) {
            view = new DisplayInstanceViewInt32Label(controller);
        } else if (model instanceof DisplayInstanceNoteLabel) {
            view = new DisplayInstanceViewNoteLabel(controller);
        } else if (model instanceof DisplayInstanceVScale) {
            view = new DisplayInstanceViewVScale(controller);
        } else {
            view = null;
            throw new Error("unkown Display type");
        }
        view.PostConstructor();
        controller.addView(view);
        return view;
    }
}
