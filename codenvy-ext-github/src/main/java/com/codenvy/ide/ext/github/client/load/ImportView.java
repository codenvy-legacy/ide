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
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link ImportPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ImportView extends View<ImportView.ActionDelegate> {
    /** Needs for delegate some function into ImportPresenter view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Finish button. */
        void onFinishClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /**
         * Performs any actions appropriate in response to the user having selected a repository.
         *
         * @param repository
         *         selected repository
         */
        void onRepositorySelected(@NotNull ProjectData repository);

        /** Performs any actions appropriate in response to the user having changed account field. */
        void onAccountChanged();
    }

    /**
     * Set project name into place on view.
     *
     * @param projectName
     *         name what need to show
     */
    void setProjectName(@NotNull String projectName);

    /** @return project name */
    @NotNull
    String getProjectName();

    /**
     * Set available repositories for account.
     *
     * @param repositories
     *         available repositories
     */
    void setRepositories(@NotNull Array<ProjectData> repositories);

    /**
     * Change the enable state of the finish button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableFinishButton(boolean enabled);

    /** @return account name */
    @NotNull
    String getAccountName();

    /**
     * Set available account names.
     *
     * @param names
     *         available names
     */
    void setAccountNames(@NotNull Array<String> names);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}