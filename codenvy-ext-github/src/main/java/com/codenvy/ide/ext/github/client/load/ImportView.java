/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

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