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
package com.codenvy.ide.ext.cloudbees.client.project;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CloudBeesProjectPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CloudBeesProjectView extends View<CloudBeesProjectView.ActionDelegate> {
    /** Needs for delegate some function into CloudBeesProject view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Update button. */
        void onUpdateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /** Performs any actions appropriate in response to the user having pressed the Info button. */
        void onInfoClicked();
    }

    /**
     * Returns application's name.
     *
     * @return application's name
     */
    String getApplicationName();

    /**
     * Sets application's name
     *
     * @param name
     */
    void setApplicationName(String name);

    /**
     * Returns application's url.
     *
     * @return application's url
     */
    String getApplicationUrl();

    /**
     * Sets application's url.
     *
     * @param url
     */
    void setApplicationUrl(String url);

    /**
     * Returns application's instances.
     *
     * @return application's instances
     */
    String getApplicationInstances();

    /**
     * Sets application's instances.
     *
     * @param instances
     */
    void setApplicationInstances(String instances);

    /**
     * Returns application's status.
     *
     * @return application's status
     */
    String getApplicationStatus();

    /**
     * Sets application's status.
     *
     * @param status
     */
    void setApplicationStatus(String status);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}