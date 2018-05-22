package axoloti.abstractui;

import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.IModel;
import axoloti.mvc.View;
import axoloti.mvc.array.ArrayView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import qcmds.QCmdProcessor;

public abstract class PatchView extends View<PatchModel> {

    // shortcut patch names
    public final static String patchComment = "patch/comment";
    public final static String patchInlet = "patch/inlet";
    public final static String patchOutlet = "patch/outlet";
    public final static String patchAudio = "audio/";
    public final static String patchAudioOut = "audio/out stereo";
    public final static String patchMidi = "midi";
    public final static String patchMidiKey = "midi/in/keyb";
    public final static String patchDisplay = "disp/";

    public List<IAxoObjectInstanceView> objectInstanceViews = new ArrayList<>();
    public List<INetView> netViews = new ArrayList<>();

    public PatchView(PatchModel patchModel) {
        super(patchModel);
    }

    abstract public void postConstructor();

    public List<IAxoObjectInstanceView> getObjectInstanceViews() {
        return objectInstanceViews;
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

    public void showClassSelector(Point p, AxoObjectInstanceViewAbstract o, String searchString) {
        if (isLocked()) {
            return;
        }
        if (osf == null) {
            osf = new ObjectSearchFrame(model);
        }
        osf.launch(p, o, searchString);
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

    public abstract void add(IAxoObjectInstanceView v);

    public abstract void remove(IAxoObjectInstanceView v);

    public abstract void remove(INetView view);

    public abstract void removeAllObjectViews();

    public abstract void removeAllNetViews();

    public abstract void add(INetView v);

    public PatchModel getSelectedObjects() {
        PatchModel p = new PatchModel();
        for (IAxoObjectInstanceView o : getObjectInstanceViews()) {
            if (o.getDModel().getSelected()) {
                p.objectinstances.add(o.getDModel());
            }
        }
        p.nets = new ArrayList<>();
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
                p.nets.add(n.getDModel());
            }
        }
        return p;
    }

    public enum Direction {
        UP, LEFT, DOWN, RIGHT
    }

    public void moveSelectedAxoObjInstances(Direction dir, int xsteps, int ysteps) {
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
            List<IAxoObjectInstance> selection = getPatchController().getSelectedObjects();
            if (!selection.isEmpty()) {
                for (IAxoObjectInstance o : selection) {
                    Point p = o.getLocation();
                    p.x = p.x + xstep;
                    p.y = p.y + ystep;
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
        PatchFrame pf = new PatchFrame(pm, QCmdProcessor.getQCmdProcessor());
        long ChronoFrameCreated = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoFrameCreated " + (ChronoFrameCreated - ChronoControllerCreated));
        long ChronoAddView = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoAddViewCreated " + (ChronoAddView - ChronoFrameCreated));
        pf.setVisible(true);
        return pf;
    }

    public static void openPatch(String name, InputStream stream) {
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchModel patchModel = serializer.read(PatchModel.class, stream);
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

        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchModel patchModel = serializer.read(PatchModel.class, f);
            PatchFrame pf = openPatchModel(patchModel, f.getAbsolutePath());
            return pf;
        } catch (java.lang.reflect.InvocationTargetException ite) {
            if (ite.getTargetException() instanceof PatchModel.PatchVersionException) {
                PatchModel.PatchVersionException pve = (PatchModel.PatchVersionException) ite.getTargetException();
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, "Patch produced with newer version of Axoloti {0} {1}",
                        new Object[]{f.getAbsoluteFile(), pve.getMessage()});
            } else {
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ite);
            }
            return null;
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

    public List<INetView> getNetViews() {
        return netViews;
    }

    public boolean isLocked() {
        return getPatchController().isLocked();
    }

    public void showPreset(int i) {
        // TODO: fix preset logic
        /*
         ArrayList<IAxoObjectInstanceView> objectInstanceViewsClone = (ArrayList<IAxoObjectInstanceView>) objectInstanceViews.clone();
         for (IAxoObjectInstanceView o : objectInstanceViewsClone) {
         for (IParameterInstanceView p : o.getParameterInstanceViews()) {
         p.ShowPreset(i);
         }
         }
         */
    }

    HashMap<IModel, IAxoObjectInstanceView> view_cache = new HashMap<>();

    abstract public IAxoObjectInstanceViewFactory getAxoObjectInstanceViewFactory();

    ArrayView<IAxoObjectInstanceView, IAxoObjectInstance> objectInstanceViewSync = new ArrayView<IAxoObjectInstanceView, IAxoObjectInstance>() {
        @Override
        protected IAxoObjectInstanceView viewFactory(IAxoObjectInstance model) {
            IAxoObjectInstance model1 = (IAxoObjectInstance) model;
            IAxoObjectInstanceView view = view_cache.get(model1);
            if (view == null) {
                view = getAxoObjectInstanceViewFactory().createView(model1, PatchView.this);
            }
            add(view);
            return view;
        }

        @Override
        protected void updateUI(List<IAxoObjectInstanceView> views) {
            updateSize();
        }

        @Override
        protected void removeView(IAxoObjectInstanceView view) {
            view.dispose();
            remove(view);
            view_cache.put(view.getDModel(), view);
        }

    };

    abstract public INetView createNetView(Net net, PatchView patchView);

    ArrayView<INetView, Net> netViewSync = new ArrayView<INetView, Net>() {
        @Override
        protected INetView viewFactory(Net net) {
            INetView view = createNetView(net, PatchView.this);
            add(view);
            view.repaint();
            return view;
        }

        @Override
        protected void updateUI(List<INetView> views) {
        }

        @Override
        protected void removeView(INetView view) {
            remove(view);
        }

    };

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
        } else if (PatchModel.PATCH_OBJECTINSTANCES.is(evt)) {
            objectInstanceViews = objectInstanceViewSync.sync(objectInstanceViews, model.getObjectInstances());
        } else if (PatchModel.PATCH_NETS.is(evt)) {
            netViews = netViewSync.sync(netViews, model.getNets());
        }
    }

    public DropTarget dt = new DropTarget() {

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
