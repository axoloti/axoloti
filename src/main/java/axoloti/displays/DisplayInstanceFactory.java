package axoloti.displays;

import axoloti.atom.AtomDefinition;
import axoloti.atom.AtomDefinitionController;

/**
 *
 * @author jtaelman
 */
public class DisplayInstanceFactory {

    public static DisplayInstance createView(AtomDefinitionController controller) {
        AtomDefinition model = controller.getModel();
        DisplayInstance view;
        if (model instanceof DisplayBool32) {
            view = new DisplayInstanceBool32(controller);
        } else if (model instanceof DisplayFrac32SChart) {
            view = new DisplayInstanceFrac32SChart(controller);
        } else if (model instanceof DisplayFrac32SDial) {
            view = new DisplayInstanceFrac32SDial(controller);
        } else if (model instanceof DisplayFrac32UChart) {
            view = new DisplayInstanceFrac32UChart(controller);
        } else if (model instanceof DisplayFrac32UDial) {
            view = new DisplayInstanceFrac32UDial(controller);
        } else if (model instanceof DisplayFrac32VBar) {
            view = new DisplayInstanceFrac32VBar(controller);
        } else if (model instanceof DisplayFrac32VBarDB) {
            view = new DisplayInstanceFrac32VBarDB(controller);
        } else if (model instanceof DisplayFrac32VU) {
            view = new DisplayInstanceFrac32VU(controller);
        } else if (model instanceof DisplayFrac4ByteVBar) {
            view = new DisplayInstanceFrac4ByteVBar(controller);
        } else if (model instanceof DisplayFrac4UByteVBar) {
            view = new DisplayInstanceFrac4UByteVBar(controller);
        } else if (model instanceof DisplayFrac4UByteVBarDB) {
            view = new DisplayInstanceFrac4UByteVBarDB(controller);
        } else if (model instanceof DisplayFrac8S128VBar) {
            view = new DisplayInstanceFrac8S128VBar(controller);
        } else if (model instanceof DisplayFrac8U128VBar) {
            view = new DisplayInstanceFrac8U128VBar(controller);
        } else if (model instanceof DisplayInt32Bar16) {
            view = new DisplayInstanceInt32Bar16(controller);
        } else if (model instanceof DisplayInt32Bar32) {
            view = new DisplayInstanceInt32Bar32(controller);
        } else if (model instanceof DisplayInt32HexLabel) {
            view = new DisplayInstanceInt32HexLabel(controller);
        } else if (model instanceof DisplayInt32Label) {
            view = new DisplayInstanceInt32Label(controller);
        } else if (model instanceof DisplayNoteLabel) {
            view = new DisplayInstanceNoteLabel(controller);
        } else if (model instanceof DisplayVScale) {
            view = new DisplayInstanceVScale(controller);
        } else {
            view = null;
            throw new Error("unkown display type: " + model.toString());
        }
        controller.addView(view);
        return view;
    }
}
