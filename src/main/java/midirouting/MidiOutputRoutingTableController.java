package midirouting;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IView;
import static midirouting.MidiInputRoutingTableController.PROPERTYNAMES;

/**
 *
 * @author jtaelman
 */
public class MidiOutputRoutingTableController extends AbstractController<MidiOutputRoutingTable, IView, AbstractController> {

    public MidiOutputRoutingTableController(MidiOutputRoutingTable model, AbstractDocumentRoot documentRoot, AbstractController parent) {
        super(model, documentRoot, parent);
    }

    public final static String MIRT_PORTNAME = "PortName";
    public final static String MIRT_MAPPING = "Mapping";

    public final static String[] PROPERTYNAMES = new String[]{
        MIRT_PORTNAME,
        MIRT_MAPPING
    };

    @Override
    public String[] getPropertyNames() {
        return PROPERTYNAMES;
    }
}
