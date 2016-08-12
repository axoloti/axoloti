package axoloti.object;

import javax.swing.JPanel;

public class TitleBarPanel extends JPanel {
    private AxoObjectInstanceAbstract axoObj;
    
    TitleBarPanel(AxoObjectInstanceAbstract axoObj) {
        this.axoObj = axoObj;
    }
}
