package axoloti.piccolo;

import axoloti.abstractui.IAxoObjectInstanceView;
import axoloti.object.IAxoObject;
import axoloti.patch.PatchModel;
import axoloti.patch.object.AxoObjectInstanceAbstract;
import axoloti.patch.object.AxoObjectInstanceFactory;
import axoloti.piccolo.patch.PatchPCanvas;
import axoloti.piccolo.patch.PatchViewPiccolo;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewAbstract;
import axoloti.piccolo.patch.object.PAxoObjectInstanceViewFactory;
import axoloti.swingui.ObjectSearchFrame;
import java.awt.Dimension;
import java.awt.Point;

public class PObjectSearchFrame extends ObjectSearchFrame {

    private double scale = 1.0;
    private final PatchViewPiccolo patchView;

    public PObjectSearchFrame(PatchModel patchModel, PatchViewPiccolo patchView) {
        super(patchModel);
        this.patchView = patchView;
        this.scale = patchView.getViewportView().getViewScale();
    }

    @Override
    public void setPreview(IAxoObject o) {
        setPreview(o, false);
    }

    private void setPreview(IAxoObject o, boolean scaleChanged) {
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
            expandJTreeToEl(o);
            getListView().setSelectedValue(o, true);
            if (getListView().getSelectedValue() != o) {
            }
            AxoObjectInstanceAbstract objectInstance = AxoObjectInstanceFactory.createView(o, null, "dummy", new Point(5, 5));
            // TODO: piccolo review


            PAxoObjectInstanceViewAbstract objectInstanceView
                    = PAxoObjectInstanceViewFactory.createView(
                            objectInstance,
                            patchView);

            getMainView().removeAll();
            PatchPCanvas container = new PatchPCanvas();
            container.setVisible(true);
            getMainView().setLayout(null);

            container.setEnabled(false);
            container.getCamera().scale(scale);
            container.getLayer().addChild(objectInstanceView);

            Dimension preferredSize = new Dimension(
                (int) ((objectInstanceView.getBounds().width + 10) * scale),
                (int) ((objectInstanceView.getBounds().height + 10) * scale));
            container.setBounds(
                0, 0,
                (int) ((preferredSize.width + 10) * scale),
                (int) ((preferredSize.height + 10) * scale));
            container.setPreferredSize(preferredSize);
            getMainView().add(container);

            Dimension oldPreferredSize = getMainView().getPreferredSize();
            getMainView().setPreferredSize(preferredSize);

            objectInstanceView.resizeToGrid();
            objectInstanceView.repaint();
            getMainView().revalidate();
            getMainView().repaint();

            IAxoObject t = objectInstanceView.getDModel().getDModel();
            if (t != null) {
                String description = t.getDescription() == null || t.getDescription().isEmpty() ? o.getDescription() : t.getDescription();
                String path = t.getPath() == null ? o.getPath() : t.getPath();
                String author = t.getAuthor() == null ? o.getAuthor() : t.getAuthor();
                String license = t.getLicense() == null ? o.getLicense() : t.getLicense();
                StringBuilder txt = new StringBuilder(description);
                if ((path != null) && (!path.isEmpty())) {
                    txt.append("\n<p>\nPath: ").append(path);
                }
                if ((author != null) && (!author.isEmpty())) {
                    txt.append("\n<p>\nAuthor: ").append(author);
                }
                if ((license != null) && (!license.isEmpty())) {
                    txt.append("\n<p>\nLicense: ").append(license);
                }
                getTextPane().setText(txt.toString());
            }
            getTextPane().setCaretPosition(0);
        }
    }

    @Override
    public void launch(Point patchLoc, Point screenLoc, IAxoObjectInstanceView o, String searchString) {
        super.launch(patchLoc, screenLoc, o, searchString, false);

        if (scale != patchView.getViewportView().getViewScale()) {
            this.scale = patchView.getViewportView().getViewScale();
            setPreview(previewObj, true);
        }
    }
}
