package axoloti.piccolo.patch.object.display;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.abstractui.IDisplayInstanceView;
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

public class PDisplayInstanceViewFactory {

    private PDisplayInstanceViewFactory() {
    }

    public static IDisplayInstanceView createView(DisplayInstance model, IAxoObjectInstanceView obj) {
        DisplayInstanceController controller = model.getController();
        PDisplayInstanceView view;
        if (model instanceof DisplayInstanceBool32) {
            view = new PDisplayInstanceViewBool32(model, obj);
        } else if (model instanceof DisplayInstanceFrac32SChart) {
            view = new PDisplayInstanceViewFrac32SChart(model, obj);
        } else if (model instanceof DisplayInstanceFrac32SDial) {
            view = new PDisplayInstanceViewFrac32SDial(model, obj);
        } else if (model instanceof DisplayInstanceFrac32UChart) {
            view = new PDisplayInstanceViewFrac32UChart(model, obj);
        } else if (model instanceof DisplayInstanceFrac32UDial) {
            view = new PDisplayInstanceViewFrac32UDial(model, obj);
        } else if (model instanceof DisplayInstanceFrac32VBar) {
            view = new PDisplayInstanceViewFrac32VBar(model, obj);
        } else if (model instanceof DisplayInstanceFrac32VBarDB) {
            view = new PDisplayInstanceViewFrac32VBarDB(model, obj);
        } else if (model instanceof DisplayInstanceFrac32VU) {
            view = new PDisplayInstanceViewFrac32VU(model, obj);
        } else if (model instanceof DisplayInstanceFrac4ByteVBar) {
            view = new PDisplayInstanceViewFrac4ByteVBar(model, obj);
        } else if (model instanceof DisplayInstanceFrac4UByteVBar) {
            view = new PDisplayInstanceViewFrac4UByteVBar(model, obj);
        } else if (model instanceof DisplayInstanceFrac4UByteVBarDB) {
            view = new PDisplayInstanceViewFrac4UByteVBarDB(model, obj);
        } else if (model instanceof DisplayInstanceFrac8S128VBar) {
            view = new PDisplayInstanceViewFrac8S128VBar(model, obj);
        } else if (model instanceof DisplayInstanceFrac8U128VBar) {
            view = new PDisplayInstanceViewFrac8U128VBar(model, obj);
        } else if (model instanceof DisplayInstanceInt32Bar16) {
            view = new PDisplayInstanceViewInt32Bar16(model, obj);
        } else if (model instanceof DisplayInstanceInt32Bar32) {
            view = new PDisplayInstanceViewInt32Bar32(model, obj);
        } else if (model instanceof DisplayInstanceInt32HexLabel) {
            view = new PDisplayInstanceViewInt32HexLabel(model, obj);
        } else if (model instanceof DisplayInstanceInt32Label) {
            view = new PDisplayInstanceViewInt32Label(model, obj);
        } else if (model instanceof DisplayInstanceNoteLabel) {
            view = new PDisplayInstanceViewNoteLabel(model, obj);
        } else if (model instanceof DisplayInstanceVScale) {
            view = new PDisplayInstanceViewVScale(model, obj);
        } else {
            view = null;
            throw new Error("unkown Display type");
        }
        controller.addView(view);
        return view;
    }
}
