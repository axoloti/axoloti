package axoloti.swingui.mvc;

import axoloti.utils.KeyUtils;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

/**
 *
 * @author jtaelman
 */
public class UndoUI implements UndoableEditListener {

    private final UndoManager undoManager;

    public UndoUI(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    private class UndoAction extends AbstractAction {

        UndoAction() {
            super("Undo");
            UndoAction.this.setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undoManager.undo();
            update();
            redoAction.update();
        }

        void update() {
            if (undoManager.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    private class RedoAction extends AbstractAction {

        RedoAction() {
            super("Redo");
            RedoAction.this.setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            undoManager.redo();
            update();
            undoAction.update();
        }

        void update() {
            if (undoManager.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }

    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();

    public JMenuItem createMenuItemUndo() {
        JMenuItem menuUndo = new JMenuItem(undoAction);
        menuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                KeyUtils.CONTROL_OR_CMD_MASK));
        return menuUndo;
    }

    public JMenuItem createMenuItemRedo() {
        JMenuItem menuRedo = new JMenuItem(redoAction);
        menuRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                KeyUtils.CONTROL_OR_CMD_MASK | KeyEvent.SHIFT_DOWN_MASK));
        return menuRedo;
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        undoAction.update();
        redoAction.update();
    }

}
