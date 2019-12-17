package axoloti.textdoc;

import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractModel;
import axoloti.mvc.IModel;
import axoloti.property.Property;
import axoloti.property.StringProperty;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jtaelman
 */
public class TextModel extends AbstractModel {

    private String text = "";

    public static final StringProperty TEXT = new StringProperty("Text", TextModel.class);

    public TextModel(String text) {
        this.text = text;
    }

    @Override
    public List<Property> getProperties() {
        return Collections.singletonList(TEXT);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        firePropertyChange(TEXT, null, text);
    }

    @Override
    protected AbstractController createController() {
        return new TextController(this);
    }

    @Override
    public IModel getParent() {
        return null; /* fixme */
    }

}
