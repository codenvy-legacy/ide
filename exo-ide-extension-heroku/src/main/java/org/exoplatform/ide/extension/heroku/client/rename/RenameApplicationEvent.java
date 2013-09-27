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
package org.exoplatform.ide.extension.heroku.client.rename;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to rename application on Heroku.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 2, 2011 11:50:31 AM anya $
 */
public class RenameApplicationEvent extends GwtEvent<RenameApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<RenameApplicationHandler> TYPE = new GwtEvent.Type<RenameApplicationHandler>();

    /** Application to rename. */
    private String application;

    /**
     * @param application
     *         application to rename, may be <code>null</code>
     */
    public RenameApplicationEvent(String application) {
        this.application = application;
    }

    public RenameApplicationEvent() {
        this.application = null;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RenameApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RenameApplicationHandler handler) {
        handler.onRenameApplication(this);
    }

    /** @return the application application to rename, may be <code>null</code> */
    public String getApplication() {
        return application;
    }
}
