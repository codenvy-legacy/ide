/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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
