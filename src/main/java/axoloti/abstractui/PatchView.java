package axoloti.abstractui;

import axoloti.chunks.ChunkData;
import axoloti.chunks.ChunkParser;
import axoloti.chunks.Cpatch_display;
import axoloti.chunks.FourCC;
import axoloti.chunks.FourCCs;
import axoloti.connection.CConnection;
import axoloti.connection.IConnection;
import axoloti.datatypes.DataType;
import axoloti.mvc.AbstractController;
import axoloti.mvc.AbstractDocumentRoot;
import axoloti.mvc.array.ArrayView;
import axoloti.object.AxoObjectFromPatch;
import axoloti.patch.PatchController;
import axoloti.patch.PatchModel;
import axoloti.patch.PatchViewCodegen;
import axoloti.patch.PatchViewportView;
import axoloti.patch.net.Net;
import axoloti.patch.net.NetController;
import axoloti.patch.object.IAxoObjectInstance;
import axoloti.patch.object.ObjectInstanceController;
import axoloti.patch.object.parameter.ParameterInstance;
import axoloti.swingui.ObjectSearchFrame;
import axoloti.swingui.patch.PatchFrame;
import axoloti.swingui.patch.PatchViewSwing;
import axoloti.swingui.patch.net.NetView;
import axoloti.swingui.patch.object.AxoObjectInstanceViewAbstract;
import axoloti.swingui.patch.object.AxoObjectInstanceViewFactory;
import axoloti.swingui.patch.object.iolet.IoletAbstract;
import axoloti.target.TargetModel;
import axoloti.target.fs.SDFileReference;
import java.awt.Dimension;
import java.awt.Point;
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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import qcmds.QCmdChangeWorkingDirectory;
import qcmds.QCmdCompileModule;
import qcmds.QCmdCompilePatch;
import qcmds.QCmdCreateDirectory;
import qcmds.QCmdLock;
import qcmds.QCmdMemRead;
import qcmds.QCmdProcessor;
import qcmds.QCmdStart;
import qcmds.QCmdStop;
import qcmds.QCmdUploadPatch;

public abstract class PatchView extends PatchAbstractView {

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

    public PatchView(PatchController patchController) {
        super(patchController);
    }

    public void PostConstructor() {


    }

    public List<IAxoObjectInstanceView> getObjectInstanceViews() {
        return objectInstanceViews;
    }

    public abstract PatchViewportView getViewportView();

    public void initViewportView() {
    }

    public abstract Point getLocationOnScreen();

    public abstract void requestFocus();

    public abstract void AdjustSize();

    abstract public void setCordsInBackground(boolean cordsInBackground);

    public void paste(String v, Point pos, boolean restoreConnectionsToExternalOutlets) {
        getController().addMetaUndo("Paste");
        getController().paste(v, pos, restoreConnectionsToExternalOutlets);
    }

    public void setFileNamePath(String FileNamePath) {
        getController().setFileNamePath(FileNamePath);
    }


    public ObjectSearchFrame osf;

    public void ShowClassSelector(Point p, AxoObjectInstanceViewAbstract o, String searchString) {
        if (isLocked()) {
            return;
        }
        if (osf == null) {
            osf = new ObjectSearchFrame(getController());
        }
        osf.Launch(p, o, searchString);
    }

    private Map<DataType, Boolean> cableTypeEnabled = new HashMap<DataType, Boolean>();

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

    public AxoObjectFromPatch ObjEditor;

    public void setObjEditor(AxoObjectFromPatch ObjEditor) {
        this.ObjEditor = ObjEditor;
    }

    public void ShowCompileFail() {
        getController().setLocked(false);
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
            if (o.getModel().getSelected()) {
                p.objectinstances.add(o.getModel());
            }
        }
        p.nets = new ArrayList<Net>();
        for (INetView n : netViews) {
            int sel = 0;
            for (IIoletInstanceView i : n.getIoletViews()) {
                if (i.getObjectInstanceView().getModel().getSelected()) {
                    sel++;
                }
            }
            if (sel > 0) {
                p.nets.add(n.getController().getModel());
            }
        }
        return p;
    }

    public enum Direction {
        UP, LEFT, DOWN, RIGHT
    }

    public void MoveSelectedAxoObjInstances(Direction dir, int xsteps, int ysteps) {
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
            List<ObjectInstanceController> selection = getController().getSelectedObjects();
            if (!selection.isEmpty()) {
                for (ObjectInstanceController o : selection) {
                    Point p = o.getModel().getLocation();
                    p.x = p.x + xstep;
                    p.y = p.y + ystep;
                    p.x = xgrid * (p.x / xgrid);
                    p.y = ygrid * (p.y / ygrid);
                    o.changeLocation(p.x, p.y);
                }
                getController().fixNegativeObjectCoordinates();
                AdjustSize();
            }
        } else {
            Logger.getLogger(PatchView.class.getName()).log(Level.INFO, "can't move: locked");
        }
    }

    public void GoLive() {

        QCmdProcessor qCmdProcessor = getController().GetQCmdProcessor();

        qCmdProcessor.AppendToQueue(new QCmdStop());
        if (CConnection.GetConnection().GetSDCardPresent()) {

            String f = "/" + getController().getSDCardPath();
            //System.out.println("pathf" + f);
            if (TargetModel.getTargetModel().getSDCardInfo().find(f) == null) {
                qCmdProcessor.AppendToQueue(new QCmdCreateDirectory(f));
            }
            qCmdProcessor.AppendToQueue(new QCmdChangeWorkingDirectory(f));
            getController().UploadDependentFiles(f);
        } else {
            // issue warning when there are dependent files
            ArrayList<SDFileReference> files = getController().getModel().GetDependendSDFiles();
            if (files.size() > 0) {
                Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, "Patch requires file {0} on SDCard, but no SDCard mounted", files.get(0).targetPath);
            }
        }
        getController().ShowPreset(0);
        getController().setPresetUpdatePending(false);
        for (IAxoObjectInstance o : getController().getModel().getObjectInstances()) {
            for (ParameterInstance pi : o.getParameterInstances()) {
                pi.ClearNeedsTransmit();
            }
        }
        PatchViewCodegen pvcg = getController().WriteCode();
        qCmdProcessor.setPatchController(null);
        for(String module : getController().getModel().getModules()) {
           qCmdProcessor.AppendToQueue(
                   new QCmdCompileModule(getController(),
                           module,
                           getController().getModel().getModuleDir(module)));
        }
        qCmdProcessor.AppendToQueue(new QCmdCompilePatch(getController()));
        qCmdProcessor.AppendToQueue(new QCmdUploadPatch());
        qCmdProcessor.AppendToQueue(new QCmdStart(pvcg));
        qCmdProcessor.AppendToQueue(new QCmdLock(pvcg));
        qCmdProcessor.AppendToQueue(new QCmdMemRead(CConnection.GetConnection().getTargetProfile().getPatchAddr(), 8, new IConnection.MemReadHandler() {
            @Override
            public void Done(ByteBuffer mem) {
                int signature = mem.getInt();
                int rootchunk_addr = mem.getInt();

                qCmdProcessor.AppendToQueue(new QCmdMemRead(rootchunk_addr, 8, new IConnection.MemReadHandler() {
                    @Override
                    public void Done(ByteBuffer mem) {
                        int fourcc = mem.getInt();
                        int length = mem.getInt();
                        System.out.println("rootchunk " + FourCC.Format(fourcc) + " len = " + length);

                        qCmdProcessor.AppendToQueue(new QCmdMemRead(rootchunk_addr, length + 8, new IConnection.MemReadHandler() {
                            @Override
                            public void Done(ByteBuffer mem) {
                                ChunkParser cp = new ChunkParser(mem);
                                ChunkData cd = cp.GetOne(FourCCs.PATCH_DISPLAY);
                                if (cd != null) {
                                    Cpatch_display cpatch_display = new Cpatch_display(cd);
                                    CConnection.GetConnection().setDisplayAddr(cpatch_display.pDisplayVector, cpatch_display.nDisplayVector);
                                }
                            }
                        }));
                    }
                }));
            }
        }));
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

    void PreSerialize() {
        // FIXME
        //getController().getModel().windowPos = getPatchFrame().getBounds();
    }

    public boolean save(File f) {
        PreSerialize();
        boolean b = getController().getModel().save(f);
        if (ObjEditor != null) {
            // ObjEditor.UpdateObject();
        }
        return b;
    }

    public static PatchFrame OpenPatchModel(PatchModel pm, String fileNamePath) {
        if (fileNamePath == null) {
            fileNamePath = "untitled";
        }
        pm.setFileNamePath(fileNamePath);
        long ChronoStart = Calendar.getInstance().getTimeInMillis();
        AbstractDocumentRoot documentRoot = new AbstractDocumentRoot();
        PatchController patchController = new PatchController(pm, documentRoot, null);
        long ChronoControllerCreated = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoControllerCreated " + (ChronoControllerCreated - ChronoStart));
        PatchFrame pf = new PatchFrame(patchController, QCmdProcessor.getQCmdProcessor());
        long ChronoFrameCreated = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoFrameCreated " + (ChronoFrameCreated - ChronoControllerCreated));
        patchController.addView(pf);
        long ChronoAddView = Calendar.getInstance().getTimeInMillis();
        System.out.println("ChronoAddViewCreated " + (ChronoAddView - ChronoFrameCreated));
        pf.setVisible(true);
        return pf;
    }

    public static void OpenPatch(String name, InputStream stream) {
        Strategy strategy = new AnnotationStrategy();
        Serializer serializer = new Persister(strategy);
        try {
            PatchModel patchModel = serializer.read(PatchModel.class, stream);
            PatchFrame pf = OpenPatchModel(patchModel, name);
            pf.setVisible(true);

        } catch (Exception ex) {
            Logger.getLogger(PatchView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static PatchFrame OpenPatchInvisible(File f) {
        for (DocumentWindow dw : DocumentWindowList.GetList()) {
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
            PatchFrame pf = OpenPatchModel(patchModel, f.getAbsolutePath());
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

    public static PatchFrame OpenPatch(File f) {
        PatchFrame pf = OpenPatchInvisible(f);
        pf.toFront();
        return pf;
    }

    @Deprecated
    public void updateNetVisibility() {
        for (INetView n : netViews) {
            DataType d = n.getController().getModel().getDataType();
            if (d != null) {
                n.setVisible(isCableTypeEnabled(d));
            }
        }
        //repaint();
    }

    @Override
    public void dispose() {
        getController().setLocked(false);
    }

    public Dimension GetSize() {
        int nx = 0;
        int ny = 0;
        // negative coordinates?
        for (IAxoObjectInstanceView o : objectInstanceViews) {
            Point p = o.getLocation();
            if (p.x < nx) {
                nx = p.x;
            }
            if (p.y < ny) {
                ny = p.y;
            }
        }
        if ((nx < 0) || (ny < 0)) { // move all to positive coordinates
            for (IAxoObjectInstanceView o : objectInstanceViews) {
                Point p = o.getLocation();
                // FIXME
                // o.SetLocation(p.x - nx, p.y - ny);
            }
        }

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

    public IAxoObjectInstanceView getObjectInstanceView(IAxoObjectInstance o) {
        for (IAxoObjectInstanceView o2 : objectInstanceViews) {
            if (o2.getModel() == o) {
                return o2;
            }
        }
        return null;
    }

    public INetView GetNetView(IIoletInstanceView io) {
        if (netViews == null) {
            return null;
        }

        for (INetView netView : netViews) {
            for (IIoletInstanceView d : netView.getIoletViews()) {
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
        return getController().isLocked();
    }

    public void ShowPreset(int i) {
        // TODO: reconstruct preset logic
        /*
        ArrayList<IAxoObjectInstanceView> objectInstanceViewsClone = (ArrayList<IAxoObjectInstanceView>) objectInstanceViews.clone();
        for (IAxoObjectInstanceView o : objectInstanceViewsClone) {
            for (IParameterInstanceView p : o.getParameterInstanceViews()) {
                p.ShowPreset(i);
            }
        }
        */
    }

    ArrayView<IAxoObjectInstanceView> objectInstanceViewSync = new ArrayView<IAxoObjectInstanceView>() {
        @Override
        public IAxoObjectInstanceView viewFactory(AbstractController ctrl) {
            IAxoObjectInstanceView view = AxoObjectInstanceViewFactory.createView((ObjectInstanceController) ctrl, (PatchViewSwing) PatchView.this);
            view.PostConstructor();
            add(view);
            return view;
        }

        @Override
        public void updateUI(List<IAxoObjectInstanceView> views) {
        }

        @Override
        public void removeView(IAxoObjectInstanceView view) {
            view.dispose();
            remove(view);
        }

    };

    ArrayView<INetView> netViewSync = new ArrayView<INetView>() {
        @Override
        public INetView viewFactory(AbstractController ctrl) {
            INetView view = new NetView((NetController) ctrl, (PatchViewSwing) PatchView.this);
            view.PostConstructor();
            ctrl.addView(view);
            add(view);
            view.repaint();
            return view;
        }

        @Override
        public void updateUI(List<INetView> views) {
        }

        @Override
        public void removeView(INetView view) {
            remove(view);
        }

    };

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        if (PatchModel.PATCH_LOCKED.is(evt)) {
            if ((Boolean)evt.getNewValue() == false) {
                for (IAxoObjectInstanceView o : objectInstanceViews) {
                    o.Unlock();
                }
            } else {
                for (IAxoObjectInstanceView o : objectInstanceViews) {
                    o.Lock();
                }
            }
        } else if (PatchModel.PATCH_OBJECTINSTANCES.is(evt)) {
            objectInstanceViews = objectInstanceViewSync.Sync(objectInstanceViews, getController().objectInstanceControllers);
        } else if (PatchModel.PATCH_NETS.is(evt)) {
            netViews = netViewSync.Sync(netViews, getController().netControllers);
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

                            getController().AddObjectInstance(o, dtde.getLocation());
                            */
                        }
                    }
                    dtde.dropComplete(true);
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(PatchViewSwing.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PatchViewSwing.class.getName()).log(Level.SEVERE, null, ex);
                }
                return;
            }
            super.drop(dtde);
        }
    };
}
