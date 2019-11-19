package axoloti.patch.object.attribute;

/**
 *
 * @author jtaelman
 */
import axoloti.object.attribute.AxoAttributeComboBox;
import axoloti.object.attribute.AxoAttributeMidiInPort;
import axoloti.patch.object.AxoObjectInstance;
import static axoloti.patch.object.attribute.AttributeInstance.ATTR_VALUE;
import java.beans.PropertyChangeEvent;
import org.simpleframework.xml.Attribute;

/**
 *
 * @author Johannes Taelman
 */
public class AttributeInstanceMidiInPort extends AttributeInstanceString<AxoAttributeMidiInPort> {

    @Attribute(name = "selection", required = false)
    private String selection;

    AttributeInstanceMidiInPort() {
    }

    AttributeInstanceMidiInPort(AxoAttributeMidiInPort attribute, AxoObjectInstance axoObj1) {
        super(attribute, axoObj1);
    }

    @Override
    public String CValue() {
        if (getDModel().getCEntries().isEmpty()) {
            return "";
        }
        String s = getDModel().getCEntries().get(getSelectedIndex());
        if (s != null) {
            return s;
        } else {
            return "";
        }
    }

    @Override
    public String getValue() {
        if (selection == null) {
            return "";
        }
        return selection;
    }

    @Override
    protected void setValueString(String selection) {
        String oldvalue = this.selection;
        if (getDModel().getMenuEntries().isEmpty()) {
            // no menu entries present
            this.selection = null;
        } else {
            int selectedIndex = getIndex(selection);
            selection = getDModel().getMenuEntries().get(selectedIndex);
            this.selection = selection;
        }
        firePropertyChange(
                ATTR_VALUE,
                oldvalue, this.selection);
    }

    public int getIndex(String selection) {
        int selectedIndex = 0;
        if (selection == null) {
            return 0;
        }
        for (int i = 0; i < getDModel().getMenuEntries().size(); i++) {
            if (selection.equals(getDModel().getMenuEntries().get(i))) {
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    public int getSelectedIndex() {
        return getIndex(selection);
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        super.modelPropertyChange(evt);
        if (AxoAttributeComboBox.ATOM_CENTRIES.is(evt)) {
            firePropertyChange(AxoAttributeComboBox.ATOM_CENTRIES, evt.getOldValue(), evt.getNewValue());
        } else if (AxoAttributeComboBox.ATOM_MENUENTRIES.is(evt)) {
            firePropertyChange(AxoAttributeComboBox.ATOM_MENUENTRIES, evt.getOldValue(), evt.getNewValue());
        }
    }

}
