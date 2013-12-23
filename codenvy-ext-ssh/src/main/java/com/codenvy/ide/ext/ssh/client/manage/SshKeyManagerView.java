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
package com.codenvy.ide.ext.ssh.client.manage;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.ssh.dto.KeyItem;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link SshKeyManagerPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface SshKeyManagerView extends View<SshKeyManagerView.ActionDelegate> {
    /** Needs for delegate some function into SshKeyManager view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having pressed the View button.
         *
         * @param key
         *         key what need to show
         */
        void onViewClicked(@NotNull KeyItem key);

        /**
         * Performs any actions appropriate in response to the user having pressed the Delete button.
         *
         * @param key
         *         key what need to delete
         */
        void onDeleteClicked(@NotNull KeyItem key);

        /** Performs any actions appropriate in response to the user having pressed the Generate button. */
        void onGenerateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Upload button. */
        void onUploadClicked();

        /** Performs any actions appropriate in response to the user having pressed the GenerateGithubKey button. */
        void onGenerateGithubKeyClicked();
    }

    /**
     * Set keys into view.
     *
     * @param keys
     *         available keys
     */
    void setKeys(@NotNull Array<KeyItem> keys);
}