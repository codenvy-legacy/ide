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
package org.exoplatform.gwtframework.ui.client.window;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Window component with ability to resize.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ResizeableWindow extends Window {

    /** Direction of resizing */
    private enum Direction {
        BOTTOM, LEFT, LEFT_BOTTOM, LEFT_TOP, NONE, RIGHT, RIGHT_BOTTOM, RIGHT_TOP, TOP
    }

    /** Left border that enables resize */
    public static final int BORDER_LEFT = 6;

    /** Right border that enables resize */
    public static final int BORDER_RIGHT = 8;

    /** Top border that enables resize */
    public static final int BORDER_TOP = 6;

    /** Bottom border that enables resize */
    public static final int BORDER_BOTTOM = 8;

    /** Minimal window height */
    public static final int MIN_HEIGHT = 100;

    /** Minimal width */
    public static final int MIN_WIDTH = 150;

    /** Height of this window header */
    public static final int HEADER_HEIGHT = 23;

    /** Direction of resizing */
    private Direction direction = Direction.NONE;

    /** This handler uses for capture MouseMove and MouseUp events that is needs for resizing */
    private NativePreviewHandler nativePreviewHandler = new NativePreviewHandler() {

        @Override
        public void onPreviewNativeEvent(NativePreviewEvent event) {
            if (direction == Direction.NONE) {
                return;
            }

            int clientX = event.getNativeEvent().getClientX();
            int clientY = event.getNativeEvent().getClientY();

            if (Event.ONMOUSEUP == event.getTypeInt()) {
                return;
            }

            event.cancel();

            switch (direction) {
                case LEFT:
                    resizeLeft(clientX);
                    break;

                case RIGHT:
                    resizeRight(clientX);
                    break;

                case BOTTOM:
                    resizeBottom(clientY);
                    break;

                case LEFT_BOTTOM:
                    resizeLeft(clientX);
                    resizeBottom(clientY);
                    break;

                case RIGHT_BOTTOM:
                    resizeRight(clientX);
                    resizeBottom(clientY);
                    break;
            }

            resizeChildWidget();
        }

        /**
         * Resize to bottom
         *
         * @param clientY top mouse cursor position
         */
        private void resizeBottom(int clientY) {
            int height = startHeight - (startClientY - clientY);
            setHeight(height);
        }

        /**
         * Resize to left
         *
         * @param clientX left mouse cursor position
         */
        private void resizeLeft(int clientX) {
            int left = startLeftPosition - (startClientX - clientX);
            DOM.setStyleAttribute(thisWindow.getElement(), "left", left + "px");
            int width = startWidth + (startClientX - clientX);
            setWidth(width);
        }

        /**
         * Resize to right
         *
         * @param clientX left mouse cursor position
         */
        private void resizeRight(int clientX) {
            int width = startWidth - (startClientX - clientX);
            setWidth(width);
        }
    };

    /** Handler registration of NativePreviewHandler */
    private HandlerRegistration previewNativeEventHandlerRegistration;

    /** Is now resizing */
    private boolean resizing = false;

    /** Initial left cursor position over browser's window before resizing */
    private int startClientX;

    /** Initial top cursor position over browser's window before resizing */
    private int startClientY;

    /** Initial height of this window before resizing */
    private int startHeight;

    /** Initial left position of this window before resizing */
    private int startLeftPosition;

    /** Initial top position of this window before resizing */
    private int startTopPosition;

    /** Initial width of this window before resizing */
    private int startWidth;

    /** Instance of this window */
    private ResizeableWindow thisWindow;

    /** Width of this window */
    private int windowWidth;

    /** Height of this window */
    private int windowHeight;

    /** Creates new instance of ResizeableWindow */
    public ResizeableWindow() {
    }

    /**
     * Creates new instance of ResizeableWindow with specified title
     *
     * @param title
     *         initial title of this window
     */
    public ResizeableWindow(String title) {
        super(title);

        thisWindow = this;

        DOM.setStyleAttribute(DOM.getParent(getCellElement(1, 0)), "cursor", "w-resize");
        DOM.setStyleAttribute(DOM.getParent(getCellElement(1, 2)), "cursor", "e-resize");

        DOM.setStyleAttribute(DOM.getParent(getCellElement(2, 0)), "cursor", "sw-resize");
        DOM.setStyleAttribute(DOM.getParent(getCellElement(2, 1)), "cursor", "s-resize");
        DOM.setStyleAttribute(DOM.getParent(getCellElement(2, 2)), "cursor", "se-resize");

        DOM.getParent(getCellElement(2, 2));

        sinkEvents(Event.ONMOUSEDOWN | Event.ONMOUSEUP | Event.ONDBLCLICK);
    }

    /**
     * Handling browser's events
     *
     * @see com.google.gwt.user.client.ui.DialogBox#onBrowserEvent(com.google.gwt.user.client.Event)
     */
    @Override
    public void onBrowserEvent(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONMOUSEDOWN:
                if (isStartResizing(event)) {
                    DOM.setCapture(getElement());
                }
                break;

            case Event.ONMOUSEUP:
                if (resizing) {
                    DOM.releaseCapture(getElement());
                    stopResizing();
                }
                break;

            case Event.ONDBLCLICK:
                if (checkMaximizeRestore(event)) {
                }
                break;
        }

        super.onBrowserEvent(event);
    }

    /**
     * Sets new height of this window
     *
     * @see org.exoplatform.gwtframework.ui.client.window.Window#setHeight(int)
     */
    @Override
    public void setHeight(int height) {
        if (height < MIN_HEIGHT) {
            height = MIN_HEIGHT;
        }

        if (getWidget() != null) {
            getWidget().setHeight((height - 29) + "px");
        }

        windowHeight = height;
        super.setHeight(height);
    }

    /**
     * Sets new width of this window
     *
     * @see org.exoplatform.gwtframework.ui.client.window.Window#setWidth(int)
     */
    @Override
    public void setWidth(int width) {
        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }

        if (getWidget() != null) {
            getWidget().setWidth((width - 12) + "px");
        }

        windowWidth = width;
        super.setWidth(width);
    }

    /**
     * Resize child widget after this window was maximized.
     *
     * @see org.exoplatform.gwtframework.ui.client.window.Window#onMaximize()
     */
    @Override
    public void onMaximize() {
        super.onMaximize();
        resizeChildWidget();
    }

    /**
     * Resize child widget after this window was restored from maximized state.
     *
     * @see org.exoplatform.gwtframework.ui.client.window.Window#onRestore()
     */
    @Override
    public void onRestore() {
        super.onRestore();
        resizeChildWidget();
    }

    /**
     * Checks whether the mouse cursor over this window and if so - maximizes or restores this window.
     *
     * @param event
     *         MouseDown event
     * @return <b>true</b> is this window was maximized or restored, <b>false</b> otherwise
     */
    protected boolean checkMaximizeRestore(Event event) {
        int mouseX = event.getClientX() - getAbsoluteLeft();
        int mouseY = event.getClientY() - getAbsoluteTop();

        if (mouseX > BORDER_LEFT && mouseX < getWidth() - BORDER_RIGHT && mouseY > BORDER_TOP && mouseY < HEADER_HEIGHT) {
            maximizeButton.doClick();
            return true;
        }

        return false;
    }

    /**
     * Checks whether the mouse cursor is places over edges of this window and if so starts resizing
     *
     * @param event
     *         MouseDown event
     * @return <b>true</b> is resizing was started, <b>false</b> otherwise
     */
    protected boolean isStartResizing(Event event) {
        int mouseX = event.getClientX() - getAbsoluteLeft();
        int mouseY = event.getClientY() - getAbsoluteTop();

        startLeftPosition = DOM.getIntStyleAttribute(thisWindow.getElement(), "left");
        startTopPosition = DOM.getIntStyleAttribute(thisWindow.getElement(), "top");

        startClientX = event.getClientX();
        startClientY = event.getClientY();

        startWidth = getWidth();
        startHeight = getHeight();

        if (mouseX < BORDER_LEFT) {
            if (mouseY < BORDER_TOP) {
                direction = Direction.LEFT_TOP;
            } else if (mouseY > getHeight() - BORDER_BOTTOM) {
                direction = Direction.LEFT_BOTTOM;
            } else {
                direction = Direction.LEFT;
            }
        } else if (mouseX > getWidth() - BORDER_RIGHT) {
            if (mouseY < BORDER_TOP) {
                direction = Direction.RIGHT_TOP;
            } else if (mouseY > getHeight() - BORDER_BOTTOM) {
                direction = Direction.RIGHT_BOTTOM;
            } else {
                direction = Direction.RIGHT;
            }
        } else {
            if (mouseY < BORDER_TOP) {
                direction = Direction.TOP;
            } else if (mouseY > getHeight() - BORDER_BOTTOM) {
                direction = Direction.BOTTOM;
            } else {
                return false;
            }
        }

        resizing = true;
        previewNativeEventHandlerRegistration = Event.addNativePreviewHandler(nativePreviewHandler);
        return true;
    }

    /** Stops resizing of this window */
    public void stopResizing() {
        previewNativeEventHandlerRegistration.removeHandler();
        resizing = false;
        direction = Direction.NONE;
    }

    /**
     * Add content widget to this window
     *
     * @see com.google.gwt.user.client.ui.SimplePanel#add(com.google.gwt.user.client.ui.Widget)
     */
    @Override
    public void add(Widget w) {
        super.add(w);
        resizeChildWidget();
    }

    /** Resize content widget */
    private void resizeChildWidget() {
        Widget widget = getWidget();
        if (widget == null) {
            return;
        }

        int widgetWidth = windowWidth - 12;
        int widgetHeight = windowHeight - 29;

        widget.setSize(widgetWidth + "px", widgetHeight + "px");

        if (widget instanceof RequiresResize) {
            ((RequiresResize)widget).onResize();
            return;
        }
    }

}
