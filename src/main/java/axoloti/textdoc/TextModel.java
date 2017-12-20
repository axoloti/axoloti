package axoloti.textdoc;

import axoloti.mvc.AbstractModel;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class TextModel extends AbstractModel {

    String text = "";

    public static final StringProperty TEXT = new StringProperty("Text", TextModel.class);

    public TextModel(String text) {
        this.text = text;
    }

    @Override
    public List<Property> getProperties() {
        List<Property> list = new ArrayList<>();
        list.add(TEXT);
        return list;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        firePropertyChange(TEXT, null, text);
    }

}
