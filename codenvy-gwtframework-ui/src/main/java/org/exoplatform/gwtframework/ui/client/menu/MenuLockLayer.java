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

package org.exoplatform.gwtframework.ui.client.menu;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * <p/>
 * This Lock Layer for Popup Menu uses as root for for Popup Menus and uses for closing all visible popups when user clicked outside one of
 * them.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuLockLayer extends AbsolutePanel {

    /** Lock Layer uses for locking of screen. Uses for hiding popups. */
    private class LockLayer extends AbsolutePanel {

        public LockLayer() {
            sinkEvents(Event.ONMOUSEDOWN);
        }

        @Override
        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEDOWN:
                    close();
                    break;
            }
        }

    }

    /** Callback which is uses for closing Popup menu. */
    private CloseMenuHandler closeMenuCallback;

    private int topOffset = 20;

    public MenuLockLayer() {

    }

    /**
     * Create Menu Lock Layer.
     *
     * @param closeMenuCallback
     *         - callback which is uses for
     */
    public MenuLockLayer(CloseMenuHandler closeMenuCallback) {
        this(closeMenuCallback, 0);
    }

    public MenuLockLayer(CloseMenuHandler closeMenuCallback, int topOffset) {
        this.closeMenuCallback = closeMenuCallback;
        this.topOffset = topOffset;

        RootPanel.get().add(this, 0, topOffset);
        int width = Window.getClientWidth();
        int height = Window.getClientHeight() - topOffset;
        setWidth("" + width + "px");
        setHeight("" + height + "px");
        DOM.setElementAttribute(getElement(), "id", "menu-lock-layer-id");
        DOM.setStyleAttribute(getElement(), "zIndex", "" + (Integer.MAX_VALUE - 5));

        AbsolutePanel blockMouseEventsPanel = new LockLayer();
        blockMouseEventsPanel.setStyleName("exo-lockLayer");
        int lockWidth = Window.getClientWidth();
        int lockHeight = Window.getClientHeight() - topOffset;
        blockMouseEventsPanel.setWidth("" + lockWidth + "px");
        blockMouseEventsPanel.setHeight("" + lockHeight + "px");
        add(blockMouseEventsPanel, 0, 0);
    }

    public void close() {
        removeFromParent();
        if (closeMenuCallback != null) {
            closeMenuCallback.onCloseMenu();
        }
    }

    public int getTopOffset() {
        return topOffset;
    }

}
