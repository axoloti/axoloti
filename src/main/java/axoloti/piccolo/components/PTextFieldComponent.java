package axoloti.piccolo.components;

import axoloti.abstractui.PatchView;
import axoloti.piccolo.patch.PatchPNode;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;
import javax.swing.text.Document;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.extras.pswing.PSwing;

public class PTextFieldComponent extends PatchPNode implements PFocusable {

    private final PSwing textFieldNode;
    private final JTextField textField;

    public PTextFieldComponent() {
        this("");
    }

    public PTextFieldComponent(String text) {
        textField = new JTextField(text);
        textFieldNode = new PSwing(textField);
        textField.setFont(Constants.FONT);
        textField.setFocusTraversalKeysEnabled(false);
        initComponent();
    }

    private void initComponent() {
        setMinimumSize(textField.getMinimumSize());
        setMaximumSize(textField.getMaximumSize());
        setPreferredSize(textField.getPreferredSize());
        setSize(textField.getSize());
        addChild(textFieldNode);
    }

    public String getText() {
        return textField.getText();
    }

    public Document getDocument() {
        return textField.getDocument();
    }

    @Override
    public void setEnabled(boolean enabled) {
        textField.setEnabled(enabled);
    }

    public void setText(String text) {
        textField.setText(text);
    }

    @Override
    public void setPreferredSize(Dimension d) {
        super.setPreferredSize(d);
        textField.setPreferredSize(d);
    }

    @Override
    public void setMinimumSize(Dimension d) {
        super.setMinimumSize(d);
        textField.setMinimumSize(d);
    }

    @Override
    public void setMaximumSize(Dimension d) {
        super.setMaximumSize(d);
        textField.setMaximumSize(d);
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        textField.setSize(d);
    }

    public void selectAll() {
        textField.selectAll();
    }

    public void addActionListener(ActionListener al) {
        textField.addActionListener(al);
    }

    @Override
    public void grabFocus() {
        textField.requestFocus();
        getRoot().getDefaultInputManager().setKeyboardFocus(getInputEventListeners()[0]);
    }

    private int focusableIndex;

    @Override
    public void setFocusableIndex(int index) {
        focusableIndex = index;
    }

    @Override
    public int getFocusableIndex() {
        return focusableIndex;
    }

    public void transferFocus(PInputEvent ke, PatchView view) {
        PatchViewPiccolo patchViewPiccolo = (PatchViewPiccolo) view;
        if (ke.getKeyChar() == KeyEvent.VK_ENTER
                || ke.getKeyChar() == KeyEvent.VK_TAB) {
            patchViewPiccolo.getCanvas().requestFocus();
            patchViewPiccolo.transferFocus(this);
        }
    }
}
