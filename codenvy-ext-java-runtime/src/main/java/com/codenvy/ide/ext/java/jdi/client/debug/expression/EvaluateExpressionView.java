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
package com.codenvy.ide.ext.java.jdi.client.debug.expression;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link EvaluateExpressionPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface EvaluateExpressionView extends View<EvaluateExpressionView.ActionDelegate> {
    /** Needs for delegate some function into EvaluateExpression view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having pressed the Evaluate button. */
        void onEvaluateClicked();

        /** Performs any actions appropriate in response to the user having changed expression. */
        void onValueExpressionChanged();
    }

    /**
     * Get expression field value.
     *
     * @return {@link String}
     */
    String getExpression();

    /**
     * Set expression field value.
     *
     * @param expression
     */
    void setExpression(String expression);

    /**
     * Set result field value.
     *
     * @param value
     *         result field value
     */
    void setResult(String value);

    /**
     * Change the enable state of the evaluate button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableEvaluateButton(boolean enabled);

    /** Give focus to expression field. */
    void focusInExpressionField();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}