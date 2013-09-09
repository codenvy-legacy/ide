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
package org.exoplatform.ide.extension.appfog.client.start;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event, occurs after pressing Start Application command.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class StartApplicationEvent extends GwtEvent<StartApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<StartApplicationHandler> TYPE = new GwtEvent.Type<StartApplicationHandler>();

    private String applicationName;
    
    private String server;

    /**
     *
     */
    public StartApplicationEvent() {
        this.server = null;
    }

    /** @param applicationName */
    public StartApplicationEvent(String applicationName, String server) {
        super();
        this.applicationName = applicationName;
        this.server = server;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StartApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(StartApplicationHandler handler) {
        handler.onStartApplication(this);
    }

    /** @return the applicationName */
    public String getApplicationName() {
        return applicationName;
    }
    
    /** @return the server */
    public String getServer() {
        return server;
    }

}
