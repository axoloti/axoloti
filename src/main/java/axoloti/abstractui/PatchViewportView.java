package axoloti.abstractui;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public interface PatchViewportView {

    JComponent getComponent();

    double getViewScale();

    JScrollPane getScrollPane();

    void zoomIn();

    void zoomOut();

    void zoomDefault();
}
