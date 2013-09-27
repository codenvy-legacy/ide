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

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.WindowResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Window component that can be modal or not and dragged by user.
 * It has close and maximize/restore buttons.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 2, 2011 10:04:33 AM anya $
 */
public class Window extends DialogBox implements CloseClickHandler, HasCloseClickHandlers, MaximizeHandler,
                                                 HasMaximizeHandlers, RestoreHandler, HasRestoreHandlers {
    /** List of {@link CloseClickHandler} handlers. */
    private List<CloseClickHandler> closeClickHandlers = new ArrayList<CloseClickHandler>();

    /** List of {@link MaximizeHandler} handlers. */
    private List<MaximizeHandler> maximizeHandlers = new ArrayList<MaximizeHandler>();

    /** List of {@link RestoreHandler} handlers. */
    private List<RestoreHandler> restoreHandlers = new ArrayList<RestoreHandler>();

    /** List of window buttons. */
    private List<WindowButton> buttons = new ArrayList<WindowButton>();

    /** Intermediate widget is used for adding one widget with event handlers to the element. */
    private class ControlElement extends ComplexPanel {

        /** @param e */
        public ControlElement(Element e) {
            setElement(e);
            onAttach();
        }

        /**
         * Adds a new child widget to the panel.
         *
         * @param w
         *         the widget to be added
         */
        @Override
        public void add(Widget w) {
            add(w, getElement());
        }
    }

    /** Header (caption) element of the window. */
    private ControlElement headerElement;

    /** Close window button. */
    private CloseButton closeButton;

    /** Maximize window button. */
    protected MaximizeButton maximizeButton;

    /** Title of the window. */
    private String title;

    /** Icon, shown near window's title. */
    private Image icon;

    /** Window's Height. */
    private int windowHeight;

    /** Window's width. */
    private int windowWidth;

    /** Can close window. */
    private boolean canClose;

    /** Can maximize window. */
    private boolean canMaximize;

    /** Is this window maximized */
    private boolean maximized;

    /** The stored value of window's width, before maximize operation (used for restore back). */
    private int windowWidthBeforeMaximize;

    /** The stored value of window's height, before maximize operation (used for restore back). */
    private int windowHeigthBeforeMaximize;

    protected Panel parent;

    private boolean processDragging = false;

    private int dragStartClientX;

    private int dragStartClientY;

    private int dragStartElementX;

    private int dragStartElementY;

    protected Window() {
        WindowResource.INSTANCE.css().ensureInjected();
        headerElement = getHeaderElement();

        closeButton = new CloseButton(this);
        addWindowButton(closeButton);

        maximizeButton = new MaximizeButton(this, this);
        addWindowButton(maximizeButton);

        setModal(false);
        setCanMaximize(false);
        setCanClose(true);
        setAnimationEnabled(false);

        getElement().getStyle().setPosition(Position.ABSOLUTE);
    }

    /**
     * @param title
     *         title of the window
     */
    public Window(String title) {
        this();
        setTitle(title);
    }

    /**
     * @param title
     *         title of the window
     * @param icon
     *         icon of the window
     */
    public Window(String title, Image icon) {
        this();
        setIcon(icon);
        setTitle(title);
    }

    /**
     * Get header element from created GWT dialog box.
     * In DOM it is div with class name = "dialogTopCenterInner".
     *
     * @return {@link ControlElement} header element
     */
    private ControlElement getHeaderElement() {
        NodeList<Element> nodes = getElement().getElementsByTagName("div");
        for (int i = 0; i < nodes.getLength(); i++) {
            if ("dialogTopCenterInner".equals(nodes.getItem(i).getClassName())) {
                return new ControlElement(nodes.getItem(i));
            }
        }
        return new ControlElement(null);
    }

    /** @see com.google.gwt.user.client.ui.DialogBox#beginDragging(com.google.gwt.event.dom.client.MouseDownEvent) */
    @Override
    protected void beginDragging(MouseDownEvent event) {
        if (maximized) {
            return;
        }

        //Change cursor type:
        addStyleName(WindowResource.INSTANCE.css().captionOver());
        processDragging = true;

        dragStartClientX = event.getClientX();
        dragStartClientY = event.getClientY();

        String left = getElement().getStyle().getLeft();
        if (left.endsWith("px")) {
            left = left.substring(0, left.length() - 2);
        }

        String top = getElement().getStyle().getTop();
        if (top.endsWith("px")) {
            top = top.substring(0, top.length() - 2);
        }

        dragStartElementX = left.isEmpty() ? 0 : Integer.parseInt(left);
        dragStartElementY = top.isEmpty() ? 0 : Integer.parseInt(top);

        super.beginDragging(event);
    }

    protected void continueDragging(MouseMoveEvent event) {
        if (parent == null) {
            super.continueDragging(event);
            return;
        }

        if (!processDragging) {
            return;
        }

        int newX = event.getClientX() - dragStartClientX + dragStartElementX;
        int newY = event.getClientY() - dragStartClientY + dragStartElementY;
        setPopupPosition(newX, newY);
    }

    /** @see com.google.gwt.user.client.ui.DialogBox#endDragging(com.google.gwt.event.dom.client.MouseUpEvent) */
    @Override
    protected void endDragging(MouseUpEvent event) {
        //Change cursor type:
        removeStyleName(WindowResource.INSTANCE.css().captionOver());
        processDragging = false;
        super.endDragging(event);
    }

    /** @see org.exoplatform.gwtframework.ui.client.window.HasCloseClickHandlers#addCloseClickHandler(org.exoplatform.gwtframework.ui
     * .client.window.CloseClickHandler) */
    public void addCloseClickHandler(CloseClickHandler handler) {
        closeClickHandlers.add(handler);
    }

    /** @see org.exoplatform.gwtframework.ui.client.window.CloseClickHandler#onCloseClick() */
    public void onCloseClick() {
        for (CloseClickHandler closeClickHandler : closeClickHandlers) {
            closeClickHandler.onCloseClick();
        }

        destroy();
    }

    /** @return the title title at the header of the window */
    public String getTitle() {
        return title;
    }

    /**
     * Set title at the header of the window.
     *
     * @param title
     *         the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        formTitle();
    }

    /** @return the icon */
    public Image getIcon() {
        return icon;
    }

    /**
     * Set icon displayed near the window's title.
     *
     * @param icon
     *         the icon to set
     */
    public void setIcon(Image icon) {
        this.icon = icon;
        formTitle();
    }

    /** Form the display title of the window (icon + title). */
    private void formTitle() {
        com.google.gwt.user.client.Element e = getCaption().asWidget().getElement();
        while (e.hasChildNodes()) {
            e.removeChild(e.getFirstChild());
        }

        if (icon != null) {
            icon.getElement().getStyle().setFloat(Float.LEFT);
            e.appendChild(icon.getElement());
        }

        if (title != null) {
            HTML t = new HTML(title);
            t.getElement().getStyle().setFloat(Float.LEFT);
            t.getElement().getStyle().setProperty("lineHeight", "16px");
            if (icon != null) {
                t.getElement().getStyle().setMarginLeft(4, Unit.PX);
            }

            e.appendChild(t.getElement());
        }
    }

    /** @return the height */
    public int getHeight() {
        int h = getElement().getAbsoluteBottom() - getElement().getAbsoluteTop();
        return (h > 0) ? h : this.windowHeight;
    }

    /**
     * @param height
     *         the height to set
     */
    public void setHeight(int height) {
        this.windowHeight = height;
        setHeight(height + "px");
    }

    /** @return the width */
    public int getWidth() {
        int w = getElement().getAbsoluteRight() - getElement().getAbsoluteLeft();
        return (w > 0) ? w : this.windowWidth;
    }

    /**
     * @param width
     *         the width to set
     */
    public void setWidth(int width) {
        this.windowWidth = width;
        setWidth(width + "px");
    }

    /** @return the canClose */
    public boolean isCanClose() {
        return canClose;
    }

    /**
     * Set whether window has close button for it's closing.
     *
     * @param canClose
     *         the canClose to set
     */
    public void setCanClose(boolean canClose) {
        this.canClose = canClose;
        closeButton.setVisible(canClose);
    }

    /** @return the canMaximize */
    public boolean isCanMaximize() {
        return canMaximize;
    }

    /**
     * Set whether window has maximize/restore button for maximizing/restoring back.
     * By default the value is <b>false</b>.
     *
     * @param canMaximize
     *         the canMaximize to set
     */
    public void setCanMaximize(boolean canMaximize) {
        this.canMaximize = canMaximize;
        maximizeButton.setVisible(canMaximize);
    }

    /**
     * Adds button to window's header.
     *
     * @param windowButton
     */
    public void addWindowButton(WindowButton windowButton) {
        buttons.add(windowButton);
        headerElement.add(windowButton);
    }

    /**
     * Removes button from window's header.
     *
     * @param windowButton
     */
    public void removeWindowButton(WindowButton windowButton) {
        buttons.remove(windowButton);
        headerElement.remove(windowButton);
    }

    /** Destroy the window. */
    public void destroy() {
        if (getGlassElement() != null) {
            getGlassElement().removeFromParent();
        }

        removeFromParent();
    }

    /** @see org.exoplatform.gwtframework.ui.client.window.HasMaximizeHandlers#addMaximizeHandler(org.exoplatform.gwtframework.ui.client
     * .window.MaximizeHandler) */
    public void addMaximizeHandler(MaximizeHandler handler) {
        maximizeHandlers.add(handler);
    }

    /** @see org.exoplatform.gwtframework.ui.client.window.MaximizeHandler#onMaximize() */
    public void onMaximize() {
        for (MaximizeHandler maximizeHandler : maximizeHandlers) {
            if (maximizeHandler != null) {
                maximizeHandler.onMaximize();
            }
        }
        //Store the size of the window:
        windowWidthBeforeMaximize = getWidth();
        windowHeigthBeforeMaximize = getHeight();

        //Set the (0, 0) position of the window and the maximum allowed size:
        setPopupPosition(0, 0);

        int maximizedWidth = 0;
        int maximizedHeight = 0;

        if (parent == null) {
            maximizedWidth = com.google.gwt.user.client.Window.getClientWidth();
            maximizedHeight = com.google.gwt.user.client.Window.getClientHeight();
        } else {
            if (parent.getOffsetWidth() == 0) {
                maximizedWidth = com.google.gwt.user.client.Window.getClientWidth();
            } else {
                maximizedWidth = parent.getOffsetWidth();
            }

            if (parent.getOffsetHeight() == 0) {
                maximizedHeight = com.google.gwt.user.client.Window.getClientHeight();
            } else {
                maximizedHeight = parent.getOffsetHeight();
            }
        }

        setWidth(maximizedWidth);
        setHeight(maximizedHeight);
        maximized = true;
    }

    /** @see org.exoplatform.gwtframework.ui.client.window.HasRestoreHandlers#addRestoreHandler(org.exoplatform.gwtframework.ui.client
     * .window.RestoreHandler) */
    public void addRestoreHandler(RestoreHandler handler) {
        restoreHandlers.add(handler);
    }

    /** @see org.exoplatform.gwtframework.ui.client.window.RestoreHandler#onRestore() */
    public void onRestore() {
        for (RestoreHandler restoreHandler : restoreHandlers) {
            restoreHandler.onRestore();
        }

        //Restore the size
        setHeight(windowHeigthBeforeMaximize);
        setWidth(windowWidthBeforeMaximize);
        //Center the window
        center();
        maximized = false;
    }

    public boolean isMaximized() {
        return maximized;
    }

    @Override
    public void center() {
        int width = 0;
        int height = 0;

        if (parent == null || parent.getOffsetWidth() == 0 || parent.getOffsetHeight() == 0) {
            width = com.google.gwt.user.client.Window.getClientWidth();
            height = com.google.gwt.user.client.Window.getClientHeight();
        } else {
            width = parent.getOffsetWidth();
            height = parent.getOffsetHeight();
        }

        int windowWidth = getWidth();
        int windowHeight = getHeight();

        int left = (width - windowWidth) >> 1;
        int top = (height - windowHeight) >> 1;

        getElement().getStyle().setPosition(Position.ABSOLUTE);
        getElement().getStyle().setLeft(left, Unit.PX);
        getElement().getStyle().setTop(top, Unit.PX);
    }

    @Override
    public void show() {
        if (isModal()) {
            setGlassEnabled(true);
        }

        if (parent != null) {
            if (getGlassElement() != null) {
                parent.getElement().appendChild(getGlassElement());
                getGlassElement().getStyle().setWidth(100, Unit.PCT);
                getGlassElement().getStyle().setHeight(100, Unit.PCT);
            }
            parent.add(this);
        } else {
            if (getGlassElement() != null) {
                getGlassElement().getStyle().setWidth(100, Unit.PCT);
                getGlassElement().getStyle().setHeight(100, Unit.PCT);
            }
            RootPanel.get().add(this);
        }

        setVisible(true);
    }

    public void show(Panel parent) {
        this.parent = parent;
        show();
    }

    public void showCentered(Panel parent) {
        show(parent);
        center();
    }

    public void showCentered() {
        show();
        center();
    }

    @Override
    public void hide() {
        setVisible(false);
    }

}
