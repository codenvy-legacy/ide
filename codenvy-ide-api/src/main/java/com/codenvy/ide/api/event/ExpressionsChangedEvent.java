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

import com.codenvy.ide.json.JsonIntegerMap;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that notifies of changed Core Expressions
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ExpressionsChangedEvent extends GwtEvent<ExpressionsChangedHandler> {
    public static Type<ExpressionsChangedHandler> TYPE = new Type<ExpressionsChangedHandler>();

    private final JsonIntegerMap<Boolean> expressions;

    /**
     * @param expressions
     *         the map of ID's and current values
     */
    public ExpressionsChangedEvent(JsonIntegerMap<Boolean> expressions) {
        this.expressions = expressions;
    }

    @Override
    public Type<ExpressionsChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return the map, having identifier of the expressions and their new values */
    public JsonIntegerMap<Boolean> getChangedExpressions() {
        return expressions;
    }

    @Override
    protected void dispatch(ExpressionsChangedHandler handler) {
        handler.onExpressionsChanged(this);
    }
}
