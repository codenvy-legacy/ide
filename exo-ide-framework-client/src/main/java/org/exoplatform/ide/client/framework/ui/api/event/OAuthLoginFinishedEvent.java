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
package org.exoplatform.ide.client.framework.ui.api.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event occurs, when user logged in to GiHub.
 * 
 * @author <a href="mailto:dvishinskiy@codenvy.com">Dmitriy Vyshinskiy</a>
 */
public class OAuthLoginFinishedEvent extends GwtEvent<OAuthLoginFinishedHandler> {
    /** Type used to register the event. */
    public static final GwtEvent.Type<OAuthLoginFinishedHandler> TYPE = new GwtEvent.Type<OAuthLoginFinishedHandler>();
    public int                                                   status;

    public OAuthLoginFinishedEvent() {
    }


    public OAuthLoginFinishedEvent(int status) {
        this.status = status;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<OAuthLoginFinishedHandler> getAssociatedType() {
        return TYPE;
    }

    public int getStatus() {
        return status;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(OAuthLoginFinishedHandler handler) {
        handler.onOAuthLoginFinished(this);
    }

}
