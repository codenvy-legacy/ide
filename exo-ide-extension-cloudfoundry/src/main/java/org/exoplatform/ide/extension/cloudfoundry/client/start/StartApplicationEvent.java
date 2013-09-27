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
package org.exoplatform.ide.extension.cloudfoundry.client.start;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

/**
 * Event, occurs after pressing Start Application command.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: StartApplicationEvent.java Jul 12, 2011 3:51:16 PM vereshchaka $
 */
public class StartApplicationEvent extends GwtEvent<StartApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<StartApplicationHandler> TYPE = new GwtEvent.Type<StartApplicationHandler>();

    private String                                             applicationName;

    private PAAS_PROVIDER                                      paasProvider;

    private String                                             server;

    /**
     *
     */
    public StartApplicationEvent(PAAS_PROVIDER paasProvider) {
        super();
        this.paasProvider = paasProvider;
    }

    /**
     * @param applicationName
     * @param server
     * @param paasProvider
     */
    public StartApplicationEvent(String applicationName, String server, PAAS_PROVIDER paasProvider) {
        super();
        this.applicationName = applicationName;
        this.server = server;
        this.paasProvider = paasProvider;
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

    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }

    public String getServer() {
        return server;
    }

}
