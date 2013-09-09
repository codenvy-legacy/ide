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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;

/**
 * Event occurs, when user tries to manage Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ManageServicesEvent extends GwtEvent<ManageServicesHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<ManageServicesHandler> TYPE = new GwtEvent.Type<ManageServicesHandler>();

    /** Application. */
    private AppfogApplication application;

    public ManageServicesEvent(AppfogApplication application) {
        this.application = application;
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

    public AppfogApplication getApplication() {
        return application;
    }
}
