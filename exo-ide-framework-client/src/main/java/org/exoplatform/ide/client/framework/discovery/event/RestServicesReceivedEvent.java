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
package org.exoplatform.ide.client.framework.discovery.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.discovery.RestService;

import java.util.List;

/**
 * Calls from {@link DiscoveryService} when list of REST Services received <br>
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 21, 2010 4:54:55 PM evgen $
 */
public class RestServicesReceivedEvent extends GwtEvent<RestServicesReceivedHandler> {

    public static GwtEvent.Type<RestServicesReceivedHandler> TYPE = new Type<RestServicesReceivedHandler>();

    private List<RestService> restServices;

    /** @param services */
    public RestServicesReceivedEvent(List<RestService> services) {
        this.restServices = services;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RestServicesReceivedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RestServicesReceivedHandler handler) {
        handler.onRestServicesReceived(this);
    }

    /**
     * Get REST Services
     *
     * @return the {@link List} of {@link RestService}
     */
    public List<RestService> getRestServices() {
        return restServices;
    }
}
