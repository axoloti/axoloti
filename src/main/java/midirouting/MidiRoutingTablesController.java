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

    public final static String MRTS_INPUT = "InputRoutingTables";
    public final static String MRTS_OUTPUT = "OutputRoutingTable";

    public final static String[] PROPERTYNAMES = new String[]{
        MRTS_INPUT,
        MRTS_OUTPUT
    };

    @Override
    public String[] getPropertyNames() {
        return PROPERTYNAMES;
    }

}
