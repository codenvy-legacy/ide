/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.api.mvp.View;

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
    void setKey(String value);

    /**
     * Add host to title of dialog.
     *
     * @param host
     *         host what need to add
     */
    void addHostToTitle(String host);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}