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
package com.codenvy.ide.api.event;

import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that notifies of changed Core Expressions
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ActivePartChangedEvent extends GwtEvent<ActivePartChangedHandler> {
    public static Type<ActivePartChangedHandler> TYPE = new Type<ActivePartChangedHandler>();

    private final PartPresenter activePart;

    /**
     * @param expressions
     *         the map of ID's and current values
     */
    public ActivePartChangedEvent(PartPresenter activePart) {
        this.activePart = activePart;
    }

    @Override
    public Type<ActivePartChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return instance of Active Part */
    public PartPresenter getActivePart() {
        return activePart;
    }

    @Override
    protected void dispatch(ActivePartChangedHandler handler) {
        handler.onActivePartChanged(this);
    }
}
