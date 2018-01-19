package axoloti.piccolo.components;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.border.Border;

import org.piccolo2d.extras.pswing.PSwing;

import axoloti.piccolo.patch.PatchPNode;
import axoloti.preferences.Theme;
import axoloti.utils.Constants;

import static java.awt.Component.LEFT_ALIGNMENT;

public class PLabelComponent extends PatchPNode {

    private final PSwing textNode;
    private final JLabel label;
    private boolean trackLabelSize = true;

    public PLabelComponent(String text) {
        this(text, true);
    }

    public PLabelComponent(String text, boolean trackLabelSize) {
        this.trackLabelSize = trackLabelSize;
        getProxyComponent().setAlignmentX(LEFT_ALIGNMENT);
        label = new JLabel(text);
        textNode = new PSwing(label);
        label.setFont(Constants.FONT);
        label.setForeground(Theme.getCurrentTheme().Label_Text);
        label.setBackground(Theme.getCurrentTheme().Object_Default_Background);
        textNode.setPickable(false);
        setPickable(false);
        updateDimensions();
        addChild(textNode);
    }

    private void updateDimensions() {
        if(trackLabelSize) {
            textNode.updateBounds();
            Dimension d = new Dimension((int) textNode.getBoundsReference().width,
                                        (int) textNode.getBoundsReference().height);
            setMinimumSize(d);
            setMaximumSize(d);
            setPreferredSize(d);
            setSize(d);
        }
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
        updateDimensions();
    }

    @Override
    public void setBorder(Border border) {
        label.setBorder(border);
        updateDimensions();
    }
}
