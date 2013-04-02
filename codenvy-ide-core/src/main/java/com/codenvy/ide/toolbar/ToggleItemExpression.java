/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.expressions.AbstractExpression;
import com.codenvy.ide.api.expressions.ExpressionManager;
import com.codenvy.ide.api.expressions.ToggleStateExpression;


/**
 * The implementation of {@link ToggleStateExpression}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToggleItemExpression extends AbstractExpression implements ToggleStateExpression {
    /**
     * Create expression.
     *
     * @param expressionManager
     * @param value
     */
    public ToggleItemExpression(ExpressionManager expressionManager, boolean value) {
        super(expressionManager, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean onStateChanged() {
        value = !value;
        return value;
    }
}