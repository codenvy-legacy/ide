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
package org.exoplatform.ide.git.client.init;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to initialize local repository. Implement {@link InitRepositoryHandler} handler.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 23, 2011 5:53:29 PM anya $
 */
public class InitRepositoryEvent extends GwtEvent<InitRepositoryHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<InitRepositoryHandler> TYPE = new GwtEvent.Type<InitRepositoryHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<InitRepositoryHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(InitRepositoryHandler handler) {
        handler.onInitRepository(this);
    }

}
