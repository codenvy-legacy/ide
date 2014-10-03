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
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.api.mvp.View;

import javax.annotation.Nonnull;

/**
 * The view of {@link SshKeyPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface SshKeyView extends View<SshKeyView.ActionDelegate> {
    /** Needs for delegate some function into SshKey view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();
    }

    /**
     * Set ssh key into place on view.
     *
     * @param value
     *         value of ssh key
     */
    void setKey(@Nonnull String value);

    /**
     * Add host to title of dialog.
     *
     * @param host
     *         host what need to add
     */
    void addHostToTitle(@Nonnull String host);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}