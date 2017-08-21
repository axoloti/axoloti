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
