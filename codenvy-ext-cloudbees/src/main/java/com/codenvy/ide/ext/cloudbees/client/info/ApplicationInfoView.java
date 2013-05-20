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
package com.codenvy.ide.ext.cloudbees.client.info;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link ApplicationInfoPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ApplicationInfoView extends View<ApplicationInfoView.ActionDelegate> {
    /** Needs for delegate some function into CreateApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        public void onOKClicked();
    }

    /**
     * Sets application's id.
     *
     * @param id
     */
    void setAppId(String id);

    /**
     * Sets application's title.
     *
     * @param title
     */
    void setAppTitle(String title);

    /**
     * Sets application's server pool.
     *
     * @param serverPool
     */
    void setServerPool(String serverPool);

    /**
     * Sets application's status.
     *
     * @param status
     */
    void setAppStatus(String status);

    /**
     * Sets application's container.
     *
     * @param container
     */
    void setAppContainer(String container);

    /**
     * Sets application's idle timeout.
     *
     * @param timeout
     */
    void setIdleTimeout(String timeout);

    /**
     * Sets application's max memory.
     *
     * @param maxMemory
     */
    void setMaxMemory(String maxMemory);

    /**
     * Sets application's security mode.
     *
     * @param securityMode
     */
    void setSecurityMode(String securityMode);

    /**
     * Sets application's cluster size.
     *
     * @param size
     */
    void setClusterSize(String size);

    /**
     * Sets application's url.
     *
     * @param url
     */
    void setUrl(String url);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}