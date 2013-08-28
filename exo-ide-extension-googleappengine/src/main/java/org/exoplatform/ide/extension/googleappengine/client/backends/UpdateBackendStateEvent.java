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
