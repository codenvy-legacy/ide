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
 * Event, occurs after pressing Stop Application command.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class StopApplicationEvent extends GwtEvent<StopApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<StopApplicationHandler> TYPE = new GwtEvent.Type<StopApplicationHandler>();

    private String applicationName;
    
    private String server;

    /**
     *
     */
    public StopApplicationEvent() {
        this.server = null;
    }

    /** @param applicationName */
    public StopApplicationEvent(String applicationName, String server) {
        super();
        this.applicationName = applicationName;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StopApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(StopApplicationHandler handler) {
        handler.onStopApplication(this);
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
