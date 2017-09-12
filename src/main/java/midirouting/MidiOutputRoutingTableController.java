package midirouting;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class MidiOutputRoutingTableController extends AbstractController<MidiOutputRoutingTable, IView, AbstractController> {

    public MidiOutputRoutingTableController(MidiOutputRoutingTable model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

}
