package axoloti.swingui.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IDisplayInstanceView;
import axoloti.patch.object.display.DisplayInstance;
import axoloti.patch.object.display.DisplayInstanceBool32;
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

    private DisplayInstanceViewFactory() {
    }

    public static IDisplayInstanceView createView(DisplayInstance model, IAxoObjectInstanceView obj) {
        DisplayInstanceView view;
        if (model instanceof DisplayInstanceBool32) {
            view = new DisplayInstanceViewBool32(model);
        } else if (model instanceof DisplayInstanceFrac32SChart) {
            view = new DisplayInstanceViewFrac32SChart(model);
        } else if (model instanceof DisplayInstanceFrac32SDial) {
            view = new DisplayInstanceViewFrac32SDial(model);
        } else if (model instanceof DisplayInstanceFrac32UChart) {
            view = new DisplayInstanceViewFrac32UChart(model);
        } else if (model instanceof DisplayInstanceFrac32UDial) {
            view = new DisplayInstanceViewFrac32UDial(model);
        } else if (model instanceof DisplayInstanceFrac32VBar) {
            view = new DisplayInstanceViewFrac32VBar(model);
        } else if (model instanceof DisplayInstanceFrac32VBarDB) {
            view = new DisplayInstanceViewFrac32VBarDB(model);
        } else if (model instanceof DisplayInstanceFrac32VU) {
            view = new DisplayInstanceViewFrac32VU(model);
        } else if (model instanceof DisplayInstanceFrac4ByteVBar) {
            view = new DisplayInstanceViewFrac4ByteVBar(model);
        } else if (model instanceof DisplayInstanceFrac4UByteVBar) {
            view = new DisplayInstanceViewFrac4UByteVBar(model);
        } else if (model instanceof DisplayInstanceFrac4UByteVBarDB) {
            view = new DisplayInstanceViewFrac4UByteVBarDB(model);
        } else if (model instanceof DisplayInstanceFrac8S128VBar) {
            view = new DisplayInstanceViewFrac8S128VBar(model);
        } else if (model instanceof DisplayInstanceFrac8U128VBar) {
            view = new DisplayInstanceViewFrac8U128VBar(model);
        } else if (model instanceof DisplayInstanceInt32Bar16) {
            view = new DisplayInstanceViewInt32Bar16(model);
        } else if (model instanceof DisplayInstanceInt32Bar32) {
            view = new DisplayInstanceViewInt32Bar32(model);
        } else if (model instanceof DisplayInstanceInt32HexLabel) {
            view = new DisplayInstanceViewInt32HexLabel(model);
        } else if (model instanceof DisplayInstanceInt32Label) {
            view = new DisplayInstanceViewInt32Label(model);
        } else if (model instanceof DisplayInstanceNoteLabel) {
            view = new DisplayInstanceViewNoteLabel(model);
        } else if (model instanceof DisplayInstanceVScale) {
            view = new DisplayInstanceViewVScale(model);
        } else {
            view = null;
            throw new Error("unkown Display type");
        }
        model.getController().addView(view);
        return view;
    }
}
