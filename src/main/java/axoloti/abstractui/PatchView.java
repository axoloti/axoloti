package axoloti.abstractui;

import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.View;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.patch.net.Net;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.swingui.ObjectSearchFrame;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

public abstract class PatchView extends View<PatchModel> {

    // shortcut patch names
    protected final static String patchComment = "patch/comment";
    protected final static String patchInlet = "patch/inlet";
    protected final static String patchOutlet = "patch/outlet";
    protected final static String patchAudio = "audio/";
    protected final static String patchAudioOut = "audio/out stereo";
    protected final static String patchMidi = "midi";
    protected final static String patchMidiKey = "midi/in/keyb";
    protected final static String patchDisplay = "disp/";

    protected List<IAxoObjectInstanceView> objectInstanceViews = Collections.emptyList();
    protected List<INetView> netViews = Collections.emptyList();

    public PatchView(PatchModel patchModel) {
        super(patchModel);
    }

    abstract public void postConstructor();

    public List<IAxoObjectInstanceView> getObjectInstanceViews() {
        return Collections.unmodifiableList(objectInstanceViews);
    }

    public abstract PatchViewportView getViewportView();

    public abstract Point getLocationOnScreen();

    public abstract void requestFocus();

    public abstract void updateSize();

    abstract public void setCordsInBackground(boolean cordsInBackground);

    private PatchController getPatchController() {
        return model.getController();
    }

    public void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        getPatchController().addMetaUndo("Paste");
        getPatchController().paste(v, pos, restoreConnectionsToExternalOutlets);
    }

    protected ObjectSearchFrame osf;

    public void showClassSelector(Point patchLoc, Point screenLoc, AxoObjectInstanceViewAbstract o, String searchString) {
        if (isLocked()) {
            return;
        }
        if (osf == null) {
            osf = new ObjectSearchFrame(model);
        }
        osf.launch(patchLoc, screenLoc, o, searchString);
    }

    private Map<DataType, Boolean> cableTypeEnabled = new HashMap<>();

    public void setCableTypeEnabled(DataType type, boolean enabled) {
        cableTypeEnabled.put(type, enabled);
    }

    public Boolean isCableTypeEnabled(DataType type) {
        if (cableTypeEnabled.containsKey(type)) {
            return cableTypeEnabled.get(type);
        } else {
            return true;
        }
    }

    public void showCompileFail() {
        getPatchController().setLocked(false);
    }

    public PatchModel getSelectedObjects() {
        PatchModel p = new PatchModel();
        List<IAxoObjectInstance> objs = new ArrayList<>();
        for (IAxoObjectInstanceView o : getObjectInstanceViews()) {
            if (o.getDModel().getSelected()) {
                objs.add(o.getDModel());
            }
        }
        p.setObjectInstances(objs);

        List<Net> nets = new ArrayList<>();
        for (INetView n : netViews) {
            int sel = 0;
            for (IIoletInstanceView i : n.getInletViews()) {
                if (i.getObjectInstanceView().getDModel().getSelected()) {
                    sel++;
                }
            }
            for (IIoletInstanceView i : n.getOutletViews()) {
                if (i.getObjectInstanceView().getDModel().getSelected()) {
                    sel++;
                }
            }
            if (sel > 0) {
                nets.add(n.getDModel());
            }
        }
        p.setNets(nets);
        return p;
    }

    public enum Direction {
        UP, LEFT, DOWN, RIGHT
    }

    protected void moveSelectedAxoObjInstances(Direction dir, int xsteps, int ysteps) {
        if (!isLocked()) {
            int xgrid = 1;
            int ygrid = 1;
            int xstep = 0;
            int ystep = 0;
            switch (dir) {
                case DOWN:
                    ystep = ysteps;
                    ygrid = ysteps;
                    break;
                case UP:
                    ystep = -ysteps;
                    ygrid = ysteps;
                    break;
                case LEFT:
                    xstep = -xsteps;
                    xgrid = xsteps;
                    break;
                case RIGHT:
                    xstep = xsteps;
                    xgrid = xsteps;
                    break;
            }
            List<IAxoObjectInstance> selection = getDModel().getSelectedObjects();
            if (!selection.isEmpty()) {
                for (IAxoObjectInstance o : selection) {
                    Point p = o.getLocation();
                    p.x += xstep;
                    p.y += ystep;
                    p.x = xgrid * (p.x / xgrid);
                    p.y = ygrid * (p.y / ygrid);
                    o.getController().changeLocation(p.x, p.y);
                }
                getPatchController().fixNegativeObjectCoordinates();
                updateSize();
            }
        } else {
            Logger.getLogger(PatchView.class.getName()).log(Level.INFO, "can't move: locked");
        }
    }


    public Dimension getInitialSize() {
        int mx = 100; // min size
        int my = 100;
        if (objectInstanceViews != null) {
            for (IAxoObjectInstanceView i : objectInstanceViews) {

                Dimension s = i.getPreferredSize();

                int ox = i.getLocation().x + (int) s.getWidth();
                int oy = i.getLocation().y + (int) s.getHeight();

                if (ox > mx) {
                    mx = ox;
                }
                if (oy > my) {
                    my = oy;
                }
            }
        }
        // adding more, as getPreferredSize is not returning true dimension of
        // object
        return new Dimension(mx + 300, my + 300);
    }

    public boolean save(File f) {
        boolean b = model.save(f);
        getPatchController().getDocumentRoot().markSaved();
        return b;
    }

    public static PatchFrame openPatchModel(PatchModel pm, String fileNamePath) {
        if (fileNamePath == null) {
            fileNamePath = "untitled";
        }
        pm.setFileNamePath(fileNamePath);
        long ChronoStart = Calendar.getInstance().getTimeInMillis();
        AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
        pm.setDocumentRoot(documentRoot);
        documentRoot.getUndoManager().discardAllEdits();
        long ChronoControllerCreated = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoControllerCreated " + (ChronoControllerCreated - ChronoStart));
        PatchFrame pf = new PatchFrame(pm);
        long ChronoFrameCreated = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoFrameCreated " + (ChronoFrameCreated - ChronoControllerCreated));
        long ChronoAddView = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoAddViewCreated " + (ChronoAddView - ChronoFrameCreated));
        pf.setVisible(true);
        return pf;
    }

    public static void openPatch(String name, InputStream stream) {
        try {
            PatchModel patchModel = PatchModel.open(name, stream);
            PatchFrame pf = openPatchModel(patchModel, name);
            pf.setVisible(true);

        } catch (Exception ex) {
            Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PatchFrame openPatchInvisible(File f) {
        for (DocumentWindow dw : DocumentWindowList.getList()) {
            if (f.equals(dw.getFile())) {
                if (dw instanceof PatchFrame) {
                    return (PatchFrame) dw;
                } else {
                    return null;
                }
            }
        }

        try {
            PatchModel patchModel = PatchModel.open(f);
            PatchFrame pf = openPatchModel(patchModel, f.getAbsolutePath());
            return pf;
        } catch (Exception ex) {
            Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static PatchFrame openPatch(File f) {
        PatchFrame pf = openPatchInvisible(f);
        pf.toFront();
        return pf;
    }

    @Deprecated
    public void updateNetVisibility() {
        for (INetView n : netViews) {
            DataType d = n.getDModel().getDataType();
            if (d != null) {
                n.setVisible(isCableTypeEnabled(d));
            }
        }
    }

    @Override
    public void dispose() {
        getPatchController().setLocked(false);
    }

    public Dimension getPatchSize() {
        int mx = 0;
        int my = 0;
        for (IAxoObjectInstanceView o : objectInstanceViews) {
            Point p = o.getLocation();
            Dimension s = o.getSize();
            int px = p.x + s.width;
            int py = p.y + s.height;
            if (px > mx) {
                mx = px;
            }
            if (py > my) {
                my = py;
            }
        }
        return new Dimension(mx, my);
    }

    public IAxoObjectInstanceView findObjectInstanceView(IAxoObjectInstance o) {
        if (o == null) {
            throw new Error("findObjectInstanceView argument is null");
        }
        for (IAxoObjectInstanceView o2 : objectInstanceViews) {
            if (o2.getDModel() == o) {
                return o2;
            }
        }
        return null;
    }

    public INetView findNetView(IInletInstanceView io) {
        if (netViews == null) {
            return null;
        }

        for (INetView netView : netViews) {
            for (IIoletInstanceView d : netView.getInletViews()) {
                if (d == io) {
                    return netView;
                }
            }
        }
        return null;
    }

    public INetView findNetView(IOutletInstanceView io) {
        if (netViews == null) {
            return null;
        }

        for (INetView netView : netViews) {
            for (IIoletInstanceView d : netView.getOutletViews()) {
                if (d == io) {
                    return netView;
                }
            }
        }
        return null;
    }

    public boolean isLocked() {
        return getPatchController().isLocked();
    }

    private int presetEditActive = 0;

    public void showPreset(int i) {
        presetEditActive = i;
        for (IAxoObjectInstanceView o : objectInstanceViews) {
            for (IParameterInstanceView p : o.getParameterInstanceViews()) {
                p.update();
            }
         }
    }

    public int getPresetEditActive() {
        return presetEditActive;
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (PatchModel.PATCH_LOCKED.is(evt)) {
            if ((Boolean) evt.getNewValue() == false) {
                for (IAxoObjectInstanceView o : objectInstanceViews) {
                    o.unlock();
                }
            } else {
                for (IAxoObjectInstanceView o : objectInstanceViews) {
                    o.lock();
                }
            }
        }
    }

    protected final DropTarget dt = new DropTarget() {

        @Override
        public synchronized void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public synchronized void drop(DropTargetDropEvent dtde) {
            Transferable t = dtde.getTransferable();
            if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    List<File> flist = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    for (File f : flist) {
                        if (f.exists() && f.canRead()) {
                            /* TODO: needs review
                             AxoObjectAbstract o = new AxoObjectFromPatch(f);
                             String fn = f.getCanonicalPath();
                             if (getController().GetCurrentWorkingDirectory() != null
                             && fn.startsWith(getController().GetCurrentWorkingDirectory())) {
                             o.createdFromRelativePath = true;
                             }

                             getController().addObjectInstance(o, dtde.getLocation());
                             */
                        }
                    }
                    dtde.dropComplete(true);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
            super.drop(dtde);
        }
    };

    public void scrollTo(Component c) {
        Rectangle rect = SwingUtilities.convertRectangle(c.getParent(), c.getBounds(), getViewportView().getComponent());
        getViewportView().getComponent().scrollRectToVisible(rect);
    }

}
