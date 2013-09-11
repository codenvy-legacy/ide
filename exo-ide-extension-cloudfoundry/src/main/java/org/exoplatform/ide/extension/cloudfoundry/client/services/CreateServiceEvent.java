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

/**
 * Event occurs, when user tries to create service.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 16, 2012 12:31:52 PM anya $
 */
public class CreateServiceEvent extends GwtEvent<CreateServiceHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<CreateServiceHandler> TYPE = new GwtEvent.Type<CreateServiceHandler>();

    /** Handler for successful provisioned service creation. */
    private ProvisionedServiceCreatedHandler provisionedServiceCreatedHandler;

    private final PAAS_PROVIDER paasProvider;

    /**
     * @param provisionedServiceCreatedHandler
     *         handler for successful provisioned service creation
     * @param paasProvider 
     */
    public CreateServiceEvent(ProvisionedServiceCreatedHandler provisionedServiceCreatedHandler, PAAS_PROVIDER paasProvider) {
        this.provisionedServiceCreatedHandler = provisionedServiceCreatedHandler;
        this.paasProvider = paasProvider;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateServiceHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(CreateServiceHandler handler) {
        handler.onCreateService(this);
    }

    /** @return the provisionedServiceCreatedHandler */
    public ProvisionedServiceCreatedHandler getProvisionedServiceCreatedHandler() {
        return provisionedServiceCreatedHandler;
    }

    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }
}
