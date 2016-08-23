package components.piccolo;

import axoloti.Theme;
import axoloti.piccolo.PatchPNode;
import axoloti.utils.Constants;
import javax.swing.JLabel;
import org.piccolo2d.extras.pswing.PSwing;

public class PLabelComponent extends PatchPNode {

    private final PSwing textNode;
    private final JLabel label;

    public PLabelComponent(String text) {
        label = new JLabel(text);
        textNode = new PSwing(label);
        label.setFont(Constants.FONT);
        label.setForeground(Theme.getCurrentTheme().Label_Text);
        label.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        textNode.setPickable(false);
        setPickable(false);
        setMinimumSize(label.getMinimumSize());
        setMaximumSize(label.getMaximumSize());
        setPreferredSize(label.getPreferredSize());
        setSize(label.getSize());
        addChild(textNode);
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
    }
}
