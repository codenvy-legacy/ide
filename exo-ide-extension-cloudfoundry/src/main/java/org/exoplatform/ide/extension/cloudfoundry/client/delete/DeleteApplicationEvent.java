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
package org.exoplatform.ide.extension.cloudfoundry.client.delete;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

/**
 * Event, occurs after pressing Delete Application command.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeleteApplicationEvent.java Jul 15, 2011 10:38:59 AM vereshchaka $
 */
public class DeleteApplicationEvent extends GwtEvent<DeleteApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<DeleteApplicationHandler> TYPE = new GwtEvent.Type<DeleteApplicationHandler>();

    private String applicationName;

    private String server;

    private PAAS_PROVIDER paasProvider;

    /**
     *
     */
    public DeleteApplicationEvent(PAAS_PROVIDER paasProvider) {
        super();
        this.applicationName = null;
        this.server = null;
        this.paasProvider = paasProvider;
    }

    /**
     * 
     * @param applicationName
     * @param server
     * @param paasProvider
     */
    public DeleteApplicationEvent(String applicationName, String server, PAAS_PROVIDER paasProvider) {
        super();
        this.applicationName = applicationName;
        this.server = server;
        this.paasProvider = paasProvider;
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

    /** @return the applicationName */
    public String getApplicationName() {
        return applicationName;
    }

    /** @return the server */
    public String getServer() {
        return server;
    }

    /** @return paas provider */
    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }
}
