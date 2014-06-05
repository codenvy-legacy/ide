/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.toolbar;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This Lock Layer for Popup Menu uses as root for for Popup Menus and uses for closing all visible popups when user clicked outside one of
 * them.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
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
