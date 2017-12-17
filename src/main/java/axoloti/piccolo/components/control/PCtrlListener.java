package axoloti.piccolo.components.control;

public interface PCtrlListener {

    void PCtrlAdjustmentBegin(PCtrlEvent e);

    void PCtrlAdjusted(PCtrlEvent e);

    void PCtrlAdjustmentFinished(PCtrlEvent e);
}
