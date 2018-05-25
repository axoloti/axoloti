package axoloti.utils;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import org.piccolo2d.event.PInputEvent;

public class KeyUtils {

    private KeyUtils() {
    }

    public static boolean isControlOrCommand(int code) {
        return code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_META;
    }

    public static Boolean isKeyCodeControlOrCommand(KeyEvent ke) {
        return KeyStroke.getKeyStrokeForEvent(ke).equals(
                KeyStroke.getKeyStroke(ke.getKeyCode(), Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
                || (ke.getKeyCode() == KeyEvent.VK_CONTROL || ke.getKeyCode() == KeyEvent.VK_META);
    }

    public static Boolean isControlOrCommandDown(InputEvent me) {
        return me.isMetaDown() || me.isControlDown();
    }

    public static Boolean isControlOrCommandDown(PInputEvent ke) {
        return ke.isMetaDown() || ke.isControlDown();
    }

    public static Boolean isIgnoreModifierDown(KeyEvent ke) {
        return ke.isAltDown() || ke.isAltGraphDown() || ke.isControlDown() || ke.isMetaDown();
    }

    public static Boolean isIgnoreModifierDown(PInputEvent ke) {
        return ke.isAltDown() || ke.isControlDown() || ke.isMetaDown();
    }

    public static final int CONTROL_OR_CMD_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
}
