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
package com.codenvy.ide.ext.java.jdi.client.debug.expression;

import com.codenvy.ide.annotations.NotNull;
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