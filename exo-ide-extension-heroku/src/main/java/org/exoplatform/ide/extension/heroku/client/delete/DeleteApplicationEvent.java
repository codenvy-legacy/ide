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
package org.exoplatform.ide.extension.heroku.client.delete;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to delete application from Heroku. Implement {@link DeleteApplicationHandler} to handle event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 5:13:19 PM anya $
 */
public class DeleteApplicationEvent extends GwtEvent<DeleteApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<DeleteApplicationHandler> TYPE = new GwtEvent.Type<DeleteApplicationHandler>();

    /** Application to delete. */
    private String application;

    /**
     * @param application
     *         application to delete, may be <code>null</code>
     */
    public DeleteApplicationEvent(String application) {
        this.application = application;
    }

    public DeleteApplicationEvent() {
        this.application = null;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DeleteApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(DeleteApplicationHandler handler) {
        handler.onDeleteApplication(this);
    }

    /** @return the application application to delete, may be <code>null</code> */
    public String getApplication() {
        return application;
    }
}
