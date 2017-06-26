package axoloti.piccolo;

import axoloti.ObjectSearchFrame;
import axoloti.PatchController;
import axoloti.PatchViewPiccolo;
import axoloti.object.AxoObjectAbstract;
import axoloti.object.AxoObjectInstanceAbstract;
import axoloti.object.IAxoObject;
import axoloti.objectviews.IAxoObjectInstanceView;
import axoloti.piccolo.objectviews.PAxoObjectInstanceViewAbstract;
import java.awt.Dimension;
import java.awt.Point;

public class PObjectSearchFrame extends ObjectSearchFrame {

    double scale = 1.0;

    public PObjectSearchFrame(PatchController patchController) {
        super(patchController);
    }

    @Override
    public void SetPreview(IAxoObject o) {
        SetPreview(o, false);
    }

    private void SetPreview(IAxoObject o, boolean scaleChanged) {
        if (o == null) {
            previewObj = null;
            type = null;
            getMainView().removeAll();
            getMainView().repaint();
            return;
        }
        if (o != previewObj || scaleChanged) {
            previewObj = o;
            type = o;
            ExpandJTreeToEl(o);
            getListView().setSelectedValue(o, true);
            if (getListView().getSelectedValue() != o) {
            }
            AxoObjectInstanceAbstract objectInstance = null;//o.CreateInstance(null, "dummy", new Point(0, 0));
            PAxoObjectInstanceViewAbstract objectInstanceView = null;
            // TODO: PICCOLO view factory
            //... = (PAxoObjectInstanceViewAbstract) objectInstance.createView((PatchViewPiccolo) patchController.getPatchView());

            getMainView().removeAll();
            PatchPCanvas container = new PatchPCanvas();
            container.setVisible(true);
            getMainView().setLayout(null);
            scale = 1.0; // patchController.getPatchView().getViewportView().getViewScale();
            container.setEnabled(false);
            container.getCamera().scale(scale);
            container.getLayer().addChild(objectInstanceView);
            container.setBounds(0, 0, (int) (objectInstanceView.getBounds().width * scale),
                    (int) (objectInstanceView.getBounds().height * scale));
            Dimension preferredSize = new Dimension(container.getBounds().width,
                    container.getBounds().height);
            container.setPreferredSize(preferredSize);
            getMainView().setPreferredSize(preferredSize);
            getMainView().add(container);
            objectInstanceView.repaint();
            getMainView().revalidate();
            getMainView().repaint();

            IAxoObject t = objectInstanceView.getModel().getType();
            if (t != null) {
                String description = t.getDescription() == null || t.getDescription().isEmpty() ? o.getDescription() : t.getDescription();
                String path = t.getPath() == null ? o.getPath() : t.getPath();
                String author = t.getAuthor() == null ? o.getAuthor() : t.getAuthor();
                String license = t.getLicense() == null ? o.getLicense() : t.getLicense();
                String txt = description;
                if ((path != null) && (!path.isEmpty())) {
                    txt += "\n<p>\nPath: " + path;
                }
                if ((author != null) && (!author.isEmpty())) {
                    txt += "\n<p>\nAuthor: " + author;
                }
                if ((license != null) && (!license.isEmpty())) {
                    txt += "\n<p>\nLicense: " + license;
                }
                getTextPane().setText(txt);
            }
            getTextPane().setCaretPosition(0);
        }
    }

    @Override
    public void Launch(Point patchLoc, IAxoObjectInstanceView o, String searchString) {
        super.Launch(patchLoc, o, searchString, false);

// FIXME
//        if (patchController.getPatchView().getViewportView().getViewScale() != scale) {
//            SetPreview(previewObj, true);
//        }
    }
}
