package axoloti.object;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.AbstractView;
import java.awt.Point;

/**
 *
 * @author jtaelman
 */
public class ObjectInstanceController extends AbstractController<AxoObjectInstanceAbstract, AbstractView> {

    public static final String OBJ_LOCATION = "Location";
    public static final String OBJ_INSTANCENAME = "InstanceName";

    public ObjectInstanceController(AxoObjectInstanceAbstract model, AbstractDocumentRoot documentRoot) {
        super(model, documentRoot);
    }
    
    public void changeLocation(int x, int y) {
        if ((getModel().getX() != x)||(getModel().getY() != y)) {
            Point p = new Point(x,y);
            setModelUndoableProperty(OBJ_LOCATION, p);        
        }
    }

    public void changeInstanceName(String s) {
        setModelUndoableProperty(OBJ_INSTANCENAME, s);
    }
}
