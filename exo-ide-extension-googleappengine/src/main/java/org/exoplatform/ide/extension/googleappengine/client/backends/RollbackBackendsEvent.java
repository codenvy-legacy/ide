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

/**
 * Event occurs, when user tries to rollback backend(s).
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 29, 2012 5:22:37 PM anya $
 */
public class RollbackBackendsEvent extends GwtEvent<RollbackBackendsHandler> {
    /** Type, used to register the event. */
    public static final GwtEvent.Type<RollbackBackendsHandler> TYPE = new GwtEvent.Type<RollbackBackendsHandler>();

    /** Rollback all backends. */
    private boolean all;

    /** Name of backend to rollback. */
    private String backendName;

    /**
     * @param all
     *         rollback all backends
     */
    public RollbackBackendsEvent(boolean all) {
        this.all = all;
        backendName = null;
    }

    /**
     * @param backendName
     *         name of backend to rollback
     */
    public RollbackBackendsEvent(String backendName) {
        this.backendName = backendName;
        this.all = false;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<RollbackBackendsHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RollbackBackendsHandler handler) {
        handler.onRollbackBackend(this);
    }

    /** @return the all */
    public boolean isAll() {
        return all;
    }

    /** @return the backendName */
    public String getBackendName() {
        return backendName;
    }
}
