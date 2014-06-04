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
        void onVariableValueChanged();
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