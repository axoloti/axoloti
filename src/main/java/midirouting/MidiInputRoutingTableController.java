package midirouting;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class MidiInputRoutingTableController extends AbstractController<MidiInputRoutingTable, IView, AbstractController> {

    public MidiInputRoutingTableController(MidiInputRoutingTable model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

}
