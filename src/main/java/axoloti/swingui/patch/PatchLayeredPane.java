package axoloti.swingui.patch;

import axoloti.abstractui.PatchViewportView;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;

public class PatchLayeredPane extends JLayeredPane implements PatchViewportView {

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public double getViewScale() {
        return 1.0f;
    }

    @Override
    public JScrollPane getScrollPane() {
        return new JScrollPane();
    }

    @Override
    public void zoomIn() {
        throw new RuntimeException("Zoom not supported in Swing GUI");
    }

    @Override
    public void zoomOut() {
        throw new RuntimeException("Zoom not supported in Swing GUI");
    }

    @Override
    public void zoomDefault() {
        throw new RuntimeException("Zoom not supported in Swing GUI");
    }
}
