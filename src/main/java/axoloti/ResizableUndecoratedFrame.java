package axoloti;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

public class ResizableUndecoratedFrame extends javax.swing.JFrame implements MouseListener, MouseMotionListener {

    private static final Map<Integer, Integer> cursors = new HashMap<Integer, Integer>();

    static {
        cursors.put(1, Cursor.N_RESIZE_CURSOR);
        cursors.put(2, Cursor.W_RESIZE_CURSOR);
        cursors.put(4, Cursor.S_RESIZE_CURSOR);
        cursors.put(8, Cursor.E_RESIZE_CURSOR);
        cursors.put(3, Cursor.NW_RESIZE_CURSOR);
        cursors.put(9, Cursor.NE_RESIZE_CURSOR);
        cursors.put(6, Cursor.SW_RESIZE_CURSOR);
        cursors.put(12, Cursor.SE_RESIZE_CURSOR);
    }

    private Insets dragInsets = new Insets(5, 5, 5, 5);

    private int direction;
    protected static final int NORTH = 1;
    protected static final int WEST = 2;
    protected static final int SOUTH = 4;
    protected static final int EAST = 8;

    private boolean resizing;
    private Rectangle bounds;
    private Point pressed;

    public ResizableUndecoratedFrame() {
        super();
        getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Get the drag insets
     *
     * @return the drag insets
     */
    Insets getDragInsets() {
        return dragInsets;
    }

    /**
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Point location = e.getPoint();
        direction = 0;

        if (location.x < dragInsets.left) {
            direction += WEST;
        } else if (location.x > getWidth() - dragInsets.right - 1) {
            direction += EAST;
        }

        if (location.y < dragInsets.top) {
            direction += NORTH;
        } else if (location.y > getHeight() - dragInsets.bottom - 1) {
            direction += SOUTH;
        }

        if (direction == 0) {
            //  Mouse is no longer over a resizable border
            getContentPane().setCursor(null);
        } else // use the appropriate resizable cursor
        {
            int cursorType = cursors.get(direction);
            Cursor cursor = Cursor.getPredefinedCursor(cursorType);
            getContentPane().setCursor(cursor);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!resizing) {
            getContentPane().setCursor(null);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (direction == 0) {
            return;
        }

        //  Setup for resizing. All future dragging calculations are done based
        //  on the original bounds of the component and mouse pressed location.
        resizing = true;
        pressed = e.getPoint();
        SwingUtilities.convertPointToScreen(pressed, this);
        bounds = getBounds();
    }

    /**
     * Restore the original state of the Component
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        resizing = false;
    }

    /**
     * Resize the component ensuring that the size is within the minimum and
     * maximum constraints.
     *
     * All calculations are done using the bounds of the component when the
     * resizing started.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (resizing == false) {
            return;
        }
        Point dragged = e.getPoint();
        SwingUtilities.convertPointToScreen(dragged, this);
        changeBounds(direction, bounds, pressed, dragged);
    }

    protected void changeBounds(int direction, Rectangle bounds, Point pressed, Point current) {
        //  Start with original locaton and size

        int x = bounds.x;
        int y = bounds.y;
        int width = bounds.width;
        int height = bounds.height;

        //  Resizing the West or North border affects the size and location
        if (WEST == (direction & WEST)) {
            int drag = getDragDistance(pressed.x, current.x);
            drag = getDragBounded(drag, width, getMinimumSize().width, getMaximumSize().width);

            x -= drag;
            width += drag;
        }

        if (NORTH == (direction & NORTH)) {
            int drag = getDragDistance(pressed.y, current.y);
            drag = getDragBounded(drag, height, getMinimumSize().height, getMaximumSize().height);

            y -= drag;
            height += drag;
        }

        //  Resizing the East or South border only affects the size
        if (EAST == (direction & EAST)) {
            int drag = getDragDistance(current.x, pressed.x);
            drag = getDragBounded(drag, width, getMinimumSize().width, getMaximumSize().width);
            width += drag;
        }

        if (SOUTH == (direction & SOUTH)) {
            int drag = getDragDistance(current.y, pressed.y);
            drag = getDragBounded(drag, height, getMinimumSize().height, getMaximumSize().height);
            height += drag;
        }

        setBounds(x, y, width, height);
        validate();
    }

    /*
     *  Determine how far the mouse has moved from where dragging started
     */
    private int getDragDistance(int larger, int smaller) {
        return larger - smaller;
    }

    /*
     *  Adjust the drag value to be within the minimum and maximum range.
     */
    private int getDragBounded(int drag, int dimension, int minimum, int maximum) {
        if (dimension + drag < minimum) {
            drag = minimum - dimension;
        }
        while (dimension + drag > maximum) {
            drag = maximum - dimension;
        }
        return drag;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
}
