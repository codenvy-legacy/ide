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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that notifies of changed Toggle item state Expressions.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ChangeToggleItemStateEvent extends GwtEvent<ChangeToggleItemStateHandler> {
    public static final GwtEvent.Type<ChangeToggleItemStateHandler> TYPE = new Type<ChangeToggleItemStateHandler>();

    private final int idExpression;

    /**
     * Create event.
     *
     * @param idExpression
     */
    public ChangeToggleItemStateEvent(int idExpression) {
        this.idExpression = idExpression;
    }

    /**
     * Resturns expressions id.
     *
     * @return
     */
    public int getIdExpression() {
        return idExpression;
    }

    /** {@inheritDoc} */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ChangeToggleItemStateHandler> getAssociatedType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    protected void dispatch(ChangeToggleItemStateHandler handler) {
        handler.onStateChanged(this);
    }
}