/*
 * Copyright (c) 2008-2012, Piccolo2D project, http://piccolo2d.org
 * Copyright (c) 1998-2008, University of Maryland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * None of the name of the University of Maryland, the name of the Piccolo2D project, or the names of its
 * contributors may be used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package axoloti.piccolo.patch;

import axoloti.piccolo.PUtils;
import axoloti.preferences.Preferences;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import org.piccolo2d.PCamera;
import org.piccolo2d.PCanvas;
import org.piccolo2d.event.PBasicInputEventHandler;
import org.piccolo2d.event.PInputEvent;
import org.piccolo2d.event.PInputEventFilter;

/**
 * Zoom event handler that scales the camera view transform in response to mouse
 * wheel events.
 *
 * @since 2.0
 */
public class PatchMouseWheelZoomEventHandler extends PBasicInputEventHandler {

    /**
     * Default scale factor, <code>0.1d</code>.
     */
    static final double DEFAULT_SCALE_FACTOR = 0.1d;

    /**
     * Scale factor.
     */
    private double scaleFactor = DEFAULT_SCALE_FACTOR;

    /**
     * Zoom mode.
     */
    private ZoomMode zoomMode = ZoomMode.ZOOM_ABOUT_CANVAS_CENTER;

    /**
     * Create a new mouse wheel zoom event handler.
     */
    public PatchMouseWheelZoomEventHandler() {
        super();
        PInputEventFilter eventFilter = new PInputEventFilter();
        eventFilter.rejectAllEventTypes();
        eventFilter.setAcceptsMouseWheelRotated(true);
        setEventFilter(eventFilter);
    }

    /**
     * Return the scale factor for this mouse wheel zoom event handler. Defaults
     * to <code>DEFAULT_SCALE_FACTOR</code>.
     *
     * @see #DEFAULT_SCALE_FACTOR
     * @return the scale factor for this mouse wheel zoom event handler
     */
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * Set the scale factor for this mouse wheel zoom event handler to
     * <code>scaleFactor</code>.
     *
     * @param scaleFactor scale factor for this mouse wheel zoom event handler
     */
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * Switch to zoom about mouse mode.
     *
     * @see ZoomMode#ZOOM_ABOUT_MOUSE
     */
    public void zoomAboutMouse() {
        zoomMode = ZoomMode.ZOOM_ABOUT_MOUSE;
    }

    /**
     * Switch to zoom about canvas center mode.
     *
     * @see ZoomMode#ZOOM_ABOUT_CANVAS_CENTER
     */
    public void zoomAboutCanvasCenter() {
        zoomMode = ZoomMode.ZOOM_ABOUT_CANVAS_CENTER;
    }

    /**
     * Switch to zoom about view center mode.
     *
     * @see ZoomMode#ZOOM_ABOUT_VIEW_CENTER
     */
    public void zoomAboutViewCenter() {
        zoomMode = ZoomMode.ZOOM_ABOUT_VIEW_CENTER;
    }

    /**
     * Return the zoom mode for this mouse wheel zoom event handler. Defaults to
     * <code>ZoomMode.ZOOM_ABOUT_CANVAS_CENTER</code>.
     *
     * @return the zoom mode for this mouse wheel zoom event handler
     */
    ZoomMode getZoomMode() {
        return zoomMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseWheelRotated(final PInputEvent event) {
        if (!Preferences.getPreferences().getMouseWheelPan()
                || event.isControlDown()
                || event.isMetaDown()) {
            PCamera camera = event.getCamera();

                // here is the reason for reimplementing this final framework class:
                // reversing the mouse wheel direction
                double scale = 1.0d + -1 * event.getWheelRotation() * scaleFactor;
                if(PUtils.viewScaleWithinLimits(camera.getViewScale(), scale)) {
                    Point2D viewAboutPoint = getViewAboutPoint(event);
                    camera.scaleViewAboutPoint(scale, viewAboutPoint.getX(), viewAboutPoint.getY());
                }
                event.getInputManager().setMouseFocus(null);
        }
    }

    /**
     * Return the view about point for the specified event according to the
     * current zoom mode.
     *
     * @param event input event
     * @return the view about point for the specified event according to the
     * current zoom mode
     */
    private Point2D getViewAboutPoint(final PInputEvent event) {
        switch (zoomMode) {
            case ZOOM_ABOUT_MOUSE:
                return event.getPosition();
            case ZOOM_ABOUT_CANVAS_CENTER:
                Rectangle canvasBounds = ((PCanvas) event.getComponent()).getBounds();
                Point2D canvasCenter = new Point2D.Double(canvasBounds.getCenterX(), canvasBounds.getCenterY());
                event.getPath().canvasToLocal(canvasCenter, event.getCamera());
                return event.getCamera().localToView(canvasCenter);
            case ZOOM_ABOUT_VIEW_CENTER:
                return event.getCamera().getBoundsReference().getCenter2D();
        }
        throw new IllegalArgumentException("illegal zoom mode " + zoomMode);
    }

    /**
     * Zoom mode.
     */
    enum ZoomMode {
        /**
         * Zoom about mouse mode.
         */
        ZOOM_ABOUT_MOUSE,
        /**
         * Zoom about canvas center mode.
         */
        ZOOM_ABOUT_CANVAS_CENTER,
        /**
         * Zoom about view center mode.
         */
        ZOOM_ABOUT_VIEW_CENTER;
    }
}
