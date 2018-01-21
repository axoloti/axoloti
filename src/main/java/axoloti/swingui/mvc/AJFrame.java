package axoloti.swingui.mvc;

import axoloti.abstractui.DocumentWindow;
import axoloti.abstractui.DocumentWindowList;
import axoloti.mvc.AbstractController;
import axoloti.mvc.IView;
import java.awt.HeadlessException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author jtaelman
 */
public abstract class AJFrame<T extends AbstractController> extends JFrame implements DocumentWindow, IView<T> {

    final DocumentWindow parent;

    final T controller;

    public AJFrame(T controller, DocumentWindow parent) throws HeadlessException {
        super();
        this.controller = controller;
        this.parent = parent;
        super.setIconImage(new ImageIcon(getClass().getResource("/resources/axoloti_icon.png")).getImage());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                unregisterDocumentWindow();
            }

            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                registerDocumentWindow();
            }
        });

        registerDocumentWindow();
    }

    @Override
    public T getController() {
        return controller;
    }

    private void registerDocumentWindow() {
        if (parent == null) {
            DocumentWindowList.RegisterWindow(this);
        } else if (!parent.getChildDocuments().contains(this)) {
            parent.getChildDocuments().add(this);
        }
    }

    private void unregisterDocumentWindow() {
        if (parent == null) {
            DocumentWindowList.UnregisterWindow(this);
        } else {
            parent.getChildDocuments().remove(this);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        getController().removeView(this);
        unregisterDocumentWindow();
    }

    @Override
    public void toFront() {
        if (!isVisible()) {
            setVisible(true);
        }
        setState(java.awt.Frame.NORMAL);
        super.toFront();
    }

}
