/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

package com.codenvy.ide.util.dom;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventRemover;
import elemental.events.MouseEvent;
import elemental.html.Element;

import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.google.gwt.user.client.Timer;


/**
 * An {@link EventListener} implementation that parses the low-level events and
 * produces high-level gestures, such as triple-click.
 * <p/>
 * This class differs subtly from the native {@link Event#CLICK} and
 * {@link Event#DBLCLICK} events by dispatching the respective callback on mouse
 * down instead of mouse up. This API difference allows clients to easily handle
 * for example the double-click-and-drag case.
 */
public class MouseGestureListener {

  /*
   * TODO: When we have time, look into learning the native OS's
   * delay by checking timing between CLICK and DBLCLICK events
   */
    /**
     * The maximum time in milliseconds between clicks to consider the latter
     * click to be part of the same gesture as the previous click.
     */
    public static final int MAX_CLICK_TIMEOUT_MS = 250;

    public static Remover createAndAttach(Element element, Callback callback) {
        MouseGestureListener instance = new MouseGestureListener(callback);
        final EventRemover eventRemover = element.addEventListener(
                Event.MOUSEDOWN, instance.captureListener, false);
        return new Remover() {
            @Override
            public void remove() {
                eventRemover.remove();
            }
        };
    }

    /**
     * An interface that receives callbacks from the {@link MouseGestureListener}
     * when gestures occur.
     */
    public interface Callback {
        /**
         * @return false to abort any handling of subsequent mouse events in this
         *         gesture
         */
        boolean onClick(int clickCount, MouseEvent event);

        void onDrag(MouseEvent event);

        void onDragRelease(MouseEvent event);
    }

    private final Callback callback;

    private final MouseCaptureListener captureListener = new MouseCaptureListener() {
        @Override
        protected boolean onMouseDown(MouseEvent evt) {
            return handleNativeMouseDown(evt);
        }

        @Override
        protected void onMouseMove(MouseEvent evt) {
            handleNativeMouseMove(evt);
        }

        @Override
        protected void onMouseUp(MouseEvent evt) {
            handleNativeMouseUp(evt);
        }
    };

    private boolean hasDragInThisGesture;
    private int     numberOfClicks;
    private final Timer resetClickStateTimer = new Timer() {
        @Override
        public void run() {
            resetClickState();
        }
    };

    private MouseGestureListener(Callback callback) {
        this.callback = callback;
    }

    private boolean handleNativeMouseDown(MouseEvent event) {
        numberOfClicks++;

        if (!callback.onClick(numberOfClicks, event)) {
            resetClickState();
            return false;
        }

    /*
     * If the user does not click again within this timeout, we will revert
     * back to a clean state
     */
        resetClickStateTimer.schedule(MAX_CLICK_TIMEOUT_MS);

        return true;
    }

    private void handleNativeMouseMove(MouseEvent event) {
        if (!hasDragInThisGesture) {
            // Dragging the mouse resets the click state
            resetClickState();
            hasDragInThisGesture = true;
        }

        callback.onDrag(event);
    }

    private void handleNativeMouseUp(MouseEvent event) {
        if (hasDragInThisGesture) {
            callback.onDragRelease(event);
            hasDragInThisGesture = false;
        }
    }

    private void resetClickState() {
        numberOfClicks = 0;
        resetClickStateTimer.cancel();
    }
}
