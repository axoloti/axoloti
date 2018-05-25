package axoloti.swingui;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 *
 * @author jtaelman
 */
public class TransparentCursor {

    private TransparentCursor() {
    }

    private static Cursor transparentCursor;

    public static Cursor get() {
        if (transparentCursor == null) {
            transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(), null);
        }
        return transparentCursor;
    }

}
