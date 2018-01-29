package axoloti.swingui.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceBool32;
import axoloti.patch.object.display.DisplayInstanceController;
import axoloti.patch.object.display.DisplayInstanceFrac32SChart;
import axoloti.patch.object.display.DisplayInstanceFrac32SDial;
import axoloti.patch.object.display.DisplayInstanceFrac32UChart;
import axoloti.patch.object.display.DisplayInstanceFrac32UDial;
import axoloti.patch.object.display.DisplayInstanceFrac32VBar;
import axoloti.patch.object.display.DisplayInstanceFrac32VBarDB;
import axoloti.patch.object.display.DisplayInstanceFrac32VU;
import axoloti.patch.object.display.DisplayInstanceFrac4ByteVBar;
import axoloti.patch.object.display.DisplayInstanceFrac4UByteVBar;
import axoloti.patch.object.display.DisplayInstanceFrac4UByteVBarDB;
import axoloti.patch.object.display.DisplayInstanceFrac8S128VBar;
import axoloti.patch.object.display.DisplayInstanceFrac8U128VBar;
import axoloti.patch.object.display.DisplayInstanceInt32Bar16;
import axoloti.patch.object.display.DisplayInstanceInt32Bar32;
import axoloti.patch.object.display.DisplayInstanceInt32HexLabel;
import axoloti.patch.object.display.DisplayInstanceInt32Label;
import axoloti.patch.object.display.DisplayInstanceNoteLabel;
import axoloti.patch.object.display.DisplayInstanceVScale;

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
