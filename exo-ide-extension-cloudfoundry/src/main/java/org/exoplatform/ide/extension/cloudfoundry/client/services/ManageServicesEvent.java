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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;

/**
 * Event occurs, when user tries to manage CloudFoundr services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 10:40:23 AM anya $
 */
public class ManageServicesEvent extends GwtEvent<ManageServicesHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<ManageServicesHandler> TYPE = new GwtEvent.Type<ManageServicesHandler>();

    /** Application. */
    private CloudFoundryApplication application;

    private final PAAS_PROVIDER paasProvider;

    public ManageServicesEvent(CloudFoundryApplication application, PAAS_PROVIDER paasProvider) {
        super();
        this.application = application;
        this.paasProvider = paasProvider;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ManageServicesHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ManageServicesHandler handler) {
        handler.onManageServices(this);
    }

    /** @return {@link CloudFoundryApplication application} application */
    public CloudFoundryApplication getApplication() {
        return application;
    }

    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }
}
