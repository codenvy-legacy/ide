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

import com.codenvy.ide.collections.IntegerMap;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that notifies of changed Core Expressions
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ExpressionsChangedEvent extends GwtEvent<ExpressionsChangedHandler> {
    public static Type<ExpressionsChangedHandler> TYPE = new Type<ExpressionsChangedHandler>();

    private final IntegerMap<Boolean> expressions;

    /**
     * @param expressions
     *         the map of ID's and current values
     */
    public ExpressionsChangedEvent(IntegerMap<Boolean> expressions) {
        this.expressions = expressions;
    }

    @Override
    public Type<ExpressionsChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the map, having identifier of the expressions and their new values */
    public IntegerMap<Boolean> getChangedExpressions() {
        return expressions;
    }

    @Override
    protected void dispatch(ExpressionsChangedHandler handler) {
        handler.onExpressionsChanged(this);
    }
}
