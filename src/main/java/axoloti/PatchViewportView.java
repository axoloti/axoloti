package axoloti;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public interface PatchViewportView {

    public JComponent getComponent();

    public double getViewScale();

    public JScrollPane getScrollPane();

    public void zoomIn();

    public void zoomOut();

    public void zoomDefault();
}
