package axoloti.object;

import axoloti.ZoomUtils;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class TitleBarPanel extends JPanel {
    private AxoObjectInstanceAbstract axoObj;
    
    TitleBarPanel(AxoObjectInstanceAbstract axoObj) {
        this.axoObj = axoObj;
    }
    
    @Override
    public Point getToolTipLocation(MouseEvent event) {
        return ZoomUtils.getToolTipLocation(this, event, axoObj);
    }
}
