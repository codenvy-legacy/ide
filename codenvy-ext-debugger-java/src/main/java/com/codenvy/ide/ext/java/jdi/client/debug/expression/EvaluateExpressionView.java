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
package com.codenvy.ide.ext.java.jdi.client.debug.expression;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

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
        void onExpressionValueChanged();
    }

    /**
     * Get expression field value.
     *
     * @return {@link String}
     */
    @NotNull
    String getExpression();

    /**
     * Set expression field value.
     *
     * @param expression
     */
    void setExpression(@NotNull String expression);

    /**
     * Set result field value.
     *
     * @param value
     *         result field value
     */
    void setResult(@NotNull String value);

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