package axoloti.swingui.mvc;

import axoloti.mvc.FocusEdit;
import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author jtaelman
 */
public class FocusEditComponent extends FocusEdit {

    private final Component component;

    public FocusEditComponent(Component component) {
        this.component = component;
    }

    @Override
    protected void focus() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(component);
        if (topFrame == null) {
            return;
        }
        topFrame.toFront();

        component.requestFocusInWindow();
        //System.out.println("focusable" + component.isFocusable());
        //System.out.println("focusowner" + component.isFocusOwner());
        //System.out.println("visible" + component.isVisible());
    }

}
