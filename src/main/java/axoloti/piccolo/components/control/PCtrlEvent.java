package axoloti.piccolo.components.control;

public class PCtrlEvent {

    double value;
    PCtrlComponentAbstract source;

    PCtrlEvent(PCtrlComponentAbstract source, double value) {
        this.value = value;
        this.source = source;
    }

    public PCtrlComponentAbstract getSource() {
        return source;
    }

    public double getValue() {
        return value;
    }
}
