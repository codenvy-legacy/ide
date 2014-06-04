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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.shared.Remote;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link RemotePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface RemoteView extends View<RemoteView.ActionDelegate> {
    /** Needs for delegate some function into Applications view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Add button. */
        void onAddClicked();

        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /**
         * Performs any action in response to the user having select remote.
         *
         * @param remote
         *         selected Remote
         */
        void onRemoteSelected(@NotNull Remote remote);
    }

    /**
     * Sets available remote repositories into special place on the view.
     *
     * @param remotes
     *         list of available remote repositories.
     */
    void setRemotes(@NotNull Array<Remote> remotes);

    /**
     * Change the enable state of the delete button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableDeleteButton(boolean enabled);

    /**
     * Returns whether the view is shown.
     *
     * @return <code>true</code> if the view is shown, and
     *         <code>false</code> otherwise
     */
    boolean isShown();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}