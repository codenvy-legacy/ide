/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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