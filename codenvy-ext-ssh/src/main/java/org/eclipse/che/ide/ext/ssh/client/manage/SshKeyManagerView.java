/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.ssh.client.manage;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.ext.ssh.dto.KeyItem;

import javax.annotation.Nonnull;

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
        void onViewClicked(@Nonnull KeyItem key);

        /**
         * Performs any actions appropriate in response to the user having pressed the Delete button.
         *
         * @param key
         *         key what need to delete
         */
        void onDeleteClicked(@Nonnull KeyItem key);

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
    void setKeys(@Nonnull Array<KeyItem> keys);
}