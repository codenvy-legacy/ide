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
package org.exoplatform.ide.git.client.remove;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to remove selected items in browser from commit. Implement {@link RemoveFilesHandler} handler to process
 * event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 3:54:53 PM anya $
 */
public class RemoveFilesEvent extends GwtEvent<RemoveFilesHandler> {

    /** Type used to register this event. */
    public static final GwtEvent.Type<RemoveFilesHandler> TYPE = new GwtEvent.Type<RemoveFilesHandler>();

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public GwtEvent.Type<RemoveFilesHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RemoveFilesHandler handler) {
        handler.onRemoveFiles(this);
    }

}
