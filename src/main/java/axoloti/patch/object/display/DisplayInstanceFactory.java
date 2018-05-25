package axoloti.patch.object.display;

import axoloti.object.atom.AtomDefinitionController;
import axoloti.object.display.Display;
import axoloti.object.display.DisplayBool32;
import axoloti.object.display.DisplayFrac32SChart;
import axoloti.object.display.DisplayFrac32SDial;
import axoloti.object.display.DisplayFrac32UChart;
import axoloti.object.display.DisplayFrac32UDial;
import axoloti.object.display.DisplayFrac32VBar;
import axoloti.object.display.DisplayFrac32VBarDB;
import axoloti.object.display.DisplayFrac32VU;
import axoloti.object.display.DisplayFrac4ByteVBar;
import axoloti.object.display.DisplayFrac4UByteVBar;
import axoloti.object.display.DisplayFrac4UByteVBarDB;
import axoloti.object.display.DisplayFrac8S128VBar;
import axoloti.object.display.DisplayFrac8U128VBar;
import axoloti.object.display.DisplayInt32Bar16;
import axoloti.object.display.DisplayInt32Bar32;
import axoloti.object.display.DisplayInt32HexLabel;
import axoloti.object.display.DisplayInt32Label;
import axoloti.object.display.DisplayNoteLabel;
import axoloti.object.display.DisplayVScale;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceFactory {

    private DisplayInstanceFactory() {
    }

    public static DisplayInstance createView(Display model) {
        AtomDefinitionController controller = model.getController();
        DisplayInstance view;
        if (model instanceof DisplayBool32) {
            view = new DisplayInstanceBool32(model);
        } else if (model instanceof DisplayFrac32SChart) {
            view = new DisplayInstanceFrac32SChart((DisplayFrac32SChart) model);
        } else if (model instanceof DisplayFrac32SDial) {
            view = new DisplayInstanceFrac32SDial((DisplayFrac32SDial) model);
        } else if (model instanceof DisplayFrac32UChart) {
            view = new DisplayInstanceFrac32UChart((DisplayFrac32UChart) model);
        } else if (model instanceof DisplayFrac32UDial) {
            view = new DisplayInstanceFrac32UDial((DisplayFrac32UDial) model);
        } else if (model instanceof DisplayFrac32VBar) {
            view = new DisplayInstanceFrac32VBar((DisplayFrac32VBar) model);
        } else if (model instanceof DisplayFrac32VBarDB) {
            view = new DisplayInstanceFrac32VBarDB((DisplayFrac32VBarDB) model);
        } else if (model instanceof DisplayFrac32VU) {
            view = new DisplayInstanceFrac32VU((DisplayFrac32VU) model);
        } else if (model instanceof DisplayFrac4ByteVBar) {
            view = new DisplayInstanceFrac4ByteVBar((DisplayFrac4ByteVBar) model);
        } else if (model instanceof DisplayFrac4UByteVBar) {
            view = new DisplayInstanceFrac4UByteVBar((DisplayFrac4UByteVBar) model);
        } else if (model instanceof DisplayFrac4UByteVBarDB) {
            view = new DisplayInstanceFrac4UByteVBarDB((DisplayFrac4UByteVBarDB) model);
        } else if (model instanceof DisplayFrac8S128VBar) {
            view = new DisplayInstanceFrac8S128VBar((DisplayFrac8S128VBar) model);
        } else if (model instanceof DisplayFrac8U128VBar) {
            view = new DisplayInstanceFrac8U128VBar((DisplayFrac8U128VBar) model);
        } else if (model instanceof DisplayInt32Bar16) {
            view = new DisplayInstanceInt32Bar16((DisplayInt32Bar16) model);
        } else if (model instanceof DisplayInt32Bar32) {
            view = new DisplayInstanceInt32Bar32((DisplayInt32Bar32) model);
        } else if (model instanceof DisplayInt32HexLabel) {
            view = new DisplayInstanceInt32HexLabel((DisplayInt32HexLabel) model);
        } else if (model instanceof DisplayInt32Label) {
            view = new DisplayInstanceInt32Label((DisplayInt32Label) model);
        } else if (model instanceof DisplayNoteLabel) {
            view = new DisplayInstanceNoteLabel((DisplayNoteLabel) model);
        } else if (model instanceof DisplayVScale) {
            view = new DisplayInstanceVScale((DisplayVScale) model);
        } else {
            view = null;
            throw new Error("unkown display type: " + model.toString());
        }
        controller.addView(view);
        return view;
    }
}
