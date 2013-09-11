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
package org.exoplatform.ide.git.client.push;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to push changes from local repository to remote one. Implement {@link PushToRemoteHandler} handler to
 * process the event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 4, 2011 9:32:53 AM anya $
 */
public class PushToRemoteEvent extends GwtEvent<PushToRemoteHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<PushToRemoteHandler> TYPE = new GwtEvent.Type<PushToRemoteHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PushToRemoteHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(PushToRemoteHandler handler) {
        handler.onPushToRemote(this);
    }

}
