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
package org.exoplatform.ide.extension.heroku.client.info;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user tries to view application's information. Implement {@link ShowApplicationInfoHandler} to handle event.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 11:22:18 AM anya $
 */
public class ShowApplicationInfoEvent extends GwtEvent<ShowApplicationInfoHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<ShowApplicationInfoHandler> TYPE = new GwtEvent.Type<ShowApplicationInfoHandler>();

    /** Application's name. */
    private String applicationName;

    public ShowApplicationInfoEvent() {
        this.applicationName = null;
    }

    /**
     * @param applicationName
     *         application's name to display properties, may be null
     */
    public ShowApplicationInfoEvent(String applicationName) {
        this.applicationName = applicationName;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShowApplicationInfoHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ShowApplicationInfoHandler handler) {
        handler.onShowApplicationInfo(this);
    }

    /** @return the applicationName application's name to display properties */
    public String getApplicationName() {
        return applicationName;
    }
}
