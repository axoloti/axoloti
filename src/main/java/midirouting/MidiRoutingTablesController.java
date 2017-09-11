package midirouting;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;

/**
 *
 * @author jtaelman
 */
public class MidiRoutingTablesController extends AbstractController<MidiRoutingTables, IView, AbstractController> {

    public MidiRoutingTablesController(MidiRoutingTables model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

}
