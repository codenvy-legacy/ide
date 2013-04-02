/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.gwtframework.ui.client.tab.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event fires before closing tab.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CloseTabEvent extends GwtEvent<CloseTabHandler> {

    /** Event type. */
    public static final GwtEvent.Type<CloseTabHandler> TYPE = new GwtEvent.Type<CloseTabHandler>();

    /** Id of tab to be closed. */
    private String tabId;

    /**
     *
     */
    private boolean closingCanceled = false;

    /**
     * Creates a new instance of this event.
     *
     * @param tabId
     */
    public CloseTabEvent(String tabId) {
        this.tabId = tabId;
    }

    /**
     * Gets ID of tab to be closed.
     *
     * @return
     */
    public String getTabId() {
        return tabId;
    }

    /** Cancels closing the tab. */
    public void cancelClosing() {
        closingCanceled = true;
    }

    /** @return  */
    public boolean isClosingCanceled() {
        return closingCanceled;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CloseTabHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(CloseTabHandler handler) {
        handler.onCloseTab(this);
    }

}
