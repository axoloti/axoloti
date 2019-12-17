package axoloti.piccolo.patch;

import axoloti.abstractui.PatchViewportView;
import axoloti.piccolo.PUtils;
import axoloti.preferences.Preferences;
import axoloti.preferences.Theme;
import axoloti.utils.Constants;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import javax.swing.JComponent;
import org.piccolo2d.PNode;
import org.piccolo2d.event.PInputEventFilter;
import org.piccolo2d.extras.pswing.PSwingCanvas;
import org.piccolo2d.extras.swing.PScrollPane;
import org.piccolo2d.util.PAffineTransform;
import org.piccolo2d.util.PPaintContext;

public class PatchPCanvas extends PSwingCanvas implements PatchViewportView {

    protected PatchSelectionEventHandler selectionEventHandler;
    protected PatchMouseWheelZoomEventHandler zoomEventHandler;
    private PatchViewPiccolo parent;
    private PPatchBorder patchBorder;

    public PatchPCanvas() {
        // for embededd canvas in ObjectSearchFrame
        this(null);
    }

    public PatchPCanvas(PatchViewPiccolo parent) {
        super();
        this.parent = parent;
//        PDebug.debugPrintFrameRate = true;
//        PDebug.debugThreads = true;
//        PDebug.debugPaintCalls = true;
//        PDebug.debugRegionManagement = true;
//        PDebug.debugBounds = true;
        initComponent();
    }

    private void initComponent() {
        setSize(Constants.PATCH_SIZE, Constants.PATCH_SIZE);
        setLocation(0, 0);
        setBackground(Theme.getCurrentTheme().Patch_Unlocked_Background);
        setPanEventHandler(null);
        PatchPanEventHandler panEventHandler = new PatchPanEventHandler();
        panEventHandler.setEventFilter(new PInputEventFilter(InputEvent.BUTTON2_MASK));
        panEventHandler.setAutopan(false);
        setPanEventHandler(panEventHandler);

        setZoomEventHandler(null);
        zoomEventHandler = new PatchMouseWheelZoomEventHandler();
        zoomEventHandler.zoomAboutMouse();
        addInputEventListener(zoomEventHandler);

        selectionEventHandler = new PatchSelectionEventHandler(getLayer(),
                getLayer(), parent);
        addInputEventListener(selectionEventHandler);

        if (parent != null) {
            getRoot().getDefaultInputManager().setKeyboardFocus(parent.inputEventHandler);
        }

        setAnimatingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        setInteractingRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
        setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);

        setPreferredSize(new Dimension(Constants.PATCH_SIZE, Constants.PATCH_SIZE));
    }

    public PPatchBorder getPatchBorder() {
        return patchBorder;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public double getViewScale() {
        return getCamera().getViewScale();
    }

    public void select(PNode node) {
        selectionEventHandler.select(node);
    }

    public void unselect(PNode node) {
        selectionEventHandler.unselect(node);
    }

    public boolean isSelected(PNode node) {
        return selectionEventHandler.isSelected(node);
    }

    public PatchSelectionEventHandler getSelectionEventHandler() {
        return selectionEventHandler;
    }

    private PatchPNode popupParent;

    public void setPopupParent(PatchPNode icon) {
        this.popupParent = icon;
    }

    public PatchPNode getPopupParent() {
        return popupParent;
    }

    public void clearPopupParent() {
        popupParent = null;
    }

    public boolean isPopupVisible() {
        return popupParent != null;
    }

    private PScrollPane scrollPane;

    @Override
    public PScrollPane getScrollPane() {
        if (scrollPane == null) {
            scrollPane = new PScrollPane();
            scrollPane.setWheelScrollingEnabled(Preferences.getPreferences().getMouseWheelPan());
        }
        return scrollPane;
    }

    @Override
    public void zoomIn() {
        double scale = 1.0d + zoomEventHandler.getScaleFactor();
        if(PUtils.viewScaleWithinLimits(getCamera().getViewScale(), scale)) {
            getCamera().scaleView(scale);
        }
    }

    @Override
    public void zoomOut() {
        double scale = 1.0d - zoomEventHandler.getScaleFactor();
        if(PUtils.viewScaleWithinLimits(getCamera().getViewScale(), scale)) {
            getCamera().scaleView(scale);
        }
    }

    @Override
    public void zoomDefault() {
        // set transform to identity
        getCamera().setViewTransform(new PAffineTransform());
    }
}
