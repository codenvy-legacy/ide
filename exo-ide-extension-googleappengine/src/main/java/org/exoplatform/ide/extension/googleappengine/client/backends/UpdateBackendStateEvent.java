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
package org.exoplatform.ide.extension.googleappengine.client.backends;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.extension.googleappengine.client.model.State;

/**
 * Event occurs, when user tries to update backend's state.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 30, 2012 2:59:42 PM anya $
 */
public class UpdateBackendStateEvent extends GwtEvent<UpdateBackendStateHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<UpdateBackendStateHandler> TYPE = new GwtEvent.Type<UpdateBackendStateHandler>();

    private String backendName;

    private State state;

    /**
     * @param backendName
     *         backend's name
     * @param state
     *         backend's state
     */
    public UpdateBackendStateEvent(String backendName, State state) {
        this.backendName = backendName;
        this.state = state;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<UpdateBackendStateHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(UpdateBackendStateHandler handler) {
        handler.onUpdateBackendState(this);
    }

    /** @return the backendName */
    public String getBackendName() {
        return backendName;
    }

    /** @return the state */
    public State getState() {
        return state;
    }
}
