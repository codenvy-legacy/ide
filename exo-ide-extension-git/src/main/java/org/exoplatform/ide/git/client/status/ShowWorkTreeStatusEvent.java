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
package org.exoplatform.ide.git.client.status;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when user tries to view the Git work tree status.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 28, 2011 3:04:41 PM anya $
 */
public class ShowWorkTreeStatusEvent extends GwtEvent<ShowWorkTreeStatusHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<ShowWorkTreeStatusHandler> TYPE = new GwtEvent.Type<ShowWorkTreeStatusHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public Type<ShowWorkTreeStatusHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowWorkTreeStatusHandler handler) {
        handler.onShowWorkTreeStatus(this);
    }

}
