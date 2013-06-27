/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.client.start;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event, occurs after pressing Restart Application command.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class RestartApplicationEvent extends GwtEvent<RestartApplicationHandler> {
    /** Type used to register this event. */
    public static final GwtEvent.Type<RestartApplicationHandler> TYPE = new GwtEvent.Type<RestartApplicationHandler>();

    private String applicationName;
    
    private String server;

    /**
     *
     */
    public RestartApplicationEvent() {
        this.server = null;
    }

    /** @param applicationName */
    public RestartApplicationEvent(String applicationName, String server) {
        super();
        this.applicationName = applicationName;
        this.server = server;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RestartApplicationHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RestartApplicationHandler handler) {
        handler.onRestartApplication(this);
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
