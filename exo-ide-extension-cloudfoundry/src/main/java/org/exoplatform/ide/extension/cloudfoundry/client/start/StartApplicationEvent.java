/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
