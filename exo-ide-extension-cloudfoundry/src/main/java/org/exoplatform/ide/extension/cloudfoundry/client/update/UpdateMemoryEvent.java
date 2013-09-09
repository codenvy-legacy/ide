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
package org.exoplatform.ide.extension.cloudfoundry.client.update;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;

/**
 * Event, occurs after pressing Update Memory command.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MapUrlEvent.java Jul 18, 2011 9:22:33 AM vereshchaka $
 */
public class UpdateMemoryEvent extends GwtEvent<UpdateMemoryHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<UpdateMemoryHandler> TYPE = new GwtEvent.Type<UpdateMemoryHandler>();

    private final PAAS_PROVIDER paasProvider;

    /**
     * @param paasProvider
     */
    public UpdateMemoryEvent(PAAS_PROVIDER paasProvider) {
        super();
        this.paasProvider = paasProvider;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateMemoryHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(UpdateMemoryHandler handler) {
        handler.onUpdateMemory(this);
    }

    public PAAS_PROVIDER getPaasProvider() {
        return paasProvider;
    }

}
