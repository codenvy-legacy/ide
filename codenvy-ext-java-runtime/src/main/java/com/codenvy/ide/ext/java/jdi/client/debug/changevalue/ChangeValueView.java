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
package com.codenvy.ide.ext.java.jdi.client.debug.changevalue;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link ChangeValuePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ChangeValueView extends View<ChangeValueView.ActionDelegate> {
    /** Needs for delegate some function into ChangeValue view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having pressed the Change button. */
        void onChangeClicked();

        /** Performs any actions appropriate in response to the user having changed value. */
        void onValueChanged();
    }

    /** @return changed value */
    @NotNull
    String getValue();

    /**
     * Set new value.
     *
     * @param value
     *         new value
     */
    void setValue(@NotNull String value);

    /**
     * Change the enable state of the evaluate button.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableChangeButton(boolean isEnable);

    /** Give focus to expression field. */
    void focusInValueField();

    /** Select all text in expression field. */
    void selectAllText();

    /**
     * Set title for value field.
     *
     * @param title
     *         new title for value field
     */
    void setValueTitle(@NotNull String title);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}