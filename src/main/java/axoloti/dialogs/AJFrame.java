package axoloti.dialogs;

import axoloti.DocumentWindow;
import axoloti.DocumentWindowList;
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

    public AJFrame(DocumentWindow parent) throws HeadlessException {
        super();
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
    public JFrame getFrame() {
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        getController().removeView(this);
        unregisterDocumentWindow();
    }

}
