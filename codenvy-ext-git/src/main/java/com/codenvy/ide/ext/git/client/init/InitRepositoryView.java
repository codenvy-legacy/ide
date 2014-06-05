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
package com.codenvy.ide.ext.git.client.init;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link InitRepositoryPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface InitRepositoryView extends View<InitRepositoryView.ActionDelegate> {
    /** Needs for delegate some function into InitRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOkClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

   /** @return work directory */
    @NotNull
    String getWorkDir();

    /**
     * Set value of workDir field.
     *
     * @param workDir
     *         work directory
     */
    void setWorkDir(@NotNull String workDir);

    /**
     * Change the enable state of the ok button.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableOkButton(boolean enable);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}