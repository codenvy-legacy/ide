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
package com.codenvy.ide.extension.runner.client.run.customenvironments;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

/**
 * The view of {@link CustomEnvironmentsPresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface CustomEnvironmentsView extends View<CustomEnvironmentsView.ActionDelegate> {

    /**
     * Set state of the 'Remove' button.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setRemoveButtonEnabled(boolean isEnabled);

    /**
     * Set state of the 'Edit' button.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEditButtonEnabled(boolean isEnabled);

    /**
     * Set existing environment.
     *
     * @param environments
     *         environments array
     */
    void setEnvironments(Array<CustomEnvironment> environments);

    /** Close the dialog. */
    void closeDialog();

    /** Show the dialog. */
    void showDialog();

    /** Needs for delegate some function into view. */
    public interface ActionDelegate {
        /** Called when 'Add' button clicked. */
        void onAddClicked();

        /** Called when 'Remove' button clicked. */
        void onRemoveClicked();

        /** Called when 'Edit' button clicked. */
        void onEditClicked();

        /** Called when 'Close' button clicked. */
        void onCloseClicked();

        /** Called when environment selected. */
        void onEnvironmentSelected(CustomEnvironment projectName);
    }
}
