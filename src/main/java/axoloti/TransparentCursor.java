package axoloti;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 *
 * @author jtaelman
 */
public class TransparentCursor {

    public static Cursor transparentCursor;

    public static Cursor get() {
        if (transparentCursor == null) {
            JFrame frame = new JFrame();
            transparentCursor = frame.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
            frame.dispose();
        }
        return transparentCursor;
    }

}
