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
package org.exoplatform.ide.extension.cloudfoundry.client.project;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

/**
 * Event occurs, when user tries to manage project, deployed to CloudFoundry or Tier3 Web Fabric.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 7, 2011 2:27:55 PM anya $
 */
public class ManageCloudFoundryProjectEvent extends GwtEvent<ManageCloudFoundryProjectHandler> {

    /** Type used to register event. */
    public static final GwtEvent.Type<ManageCloudFoundryProjectHandler> TYPE = new GwtEvent.Type<ManageCloudFoundryProjectHandler>();

    private final PAAS_PROVIDER paasProvider;

    /**
     * @param paasProvider
     */
    public ManageCloudFoundryProjectEvent(PAAS_PROVIDER paasProvider) {
        this.paasProvider = paasProvider;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ManageCloudFoundryProjectHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ManageCloudFoundryProjectHandler handler) {
        handler.onManageCloudFoundryProject(this);
    }

    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }

}
