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
package com.codenvy.ide.extension.cloudfoundry.client.info;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link ApplicationInfoPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ApplicationInfoView extends View<ApplicationInfoView.ActionDelegate> {
    /** Needs for delegate some function into CreateApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOKClicked();
    }

    /**
     * Sets application's name.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Sets application's state.
     *
     * @param state
     */
    void setState(String state);

    /**
     * Sets application's instances.
     *
     * @param instances
     */
    void setInstances(String instances);

    /**
     * Sets application's version.
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Sets application's disk.
     *
     * @param disk
     */
    void setDisk(String disk);

    /**
     * Sets application's memory.
     *
     * @param memory
     */
    void setMemory(String memory);

    /**
     * Sets application's stack.
     *
     * @param stack
     */
    void setStack(String stack);

    /**
     * Sets application's model.
     *
     * @param model
     */
    void setModel(String model);

    /**
     * Sets application's uris.
     *
     * @param applications
     *         application's uris
     */
    void setApplicationUris(JsonArray<String> applications);

    /**
     * Sets application's services.
     *
     * @param services
     *         application's services
     */
    void setApplicationServices(JsonArray<String> services);

    /**
     * Sets application's environments.
     *
     * @param environments
     *         application's enviroments.
     */
    void setApplicationEnvironments(JsonArray<String> environments);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}