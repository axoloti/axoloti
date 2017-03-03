package axoloti.piccolo;

import java.awt.Cursor;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PPanEventHandler;

public class PatchPanEventHandler extends PPanEventHandler {

    protected void startDrag(final PInputEvent event) {
        super.startDrag(event);
        event.pushCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    protected void endDrag(final PInputEvent event) {
        super.endDrag(event);
        event.popCursor();
    }
}
