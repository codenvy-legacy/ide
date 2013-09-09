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
