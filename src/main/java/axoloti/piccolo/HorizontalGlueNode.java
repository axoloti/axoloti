package axoloti.piccolo;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class HorizontalGlueNode extends PatchPNode {

    public void expand() {
        Rectangle2D parentBounds = getParent().getFullBounds();
        double extraSpace = parentBounds.getWidth();
        Iterator childIterator = getParent().getChildrenIterator();
        while (childIterator.hasNext()) {
            PatchPNode child = (PatchPNode) childIterator.next();
            if (child != this) {
                extraSpace -= child.getFullBounds().getWidth();
            }
        }
        setBounds(0, 0, extraSpace, 10);
    }
}
