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
package org.exoplatform.ide.git.client.branch;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to view the list of branches. Implement {@link ShowBranchesHandler} handler to process this event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 8, 2011 12:12:37 PM anya $
 */
public class ShowBranchesEvent extends GwtEvent<ShowBranchesHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<ShowBranchesHandler> TYPE = new GwtEvent.Type<ShowBranchesHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowBranchesHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowBranchesHandler handler) {
        handler.onShowBranches(this);
    }

}
