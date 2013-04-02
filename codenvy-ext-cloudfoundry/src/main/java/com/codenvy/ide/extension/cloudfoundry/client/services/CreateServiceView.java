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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.api.mvp.View;

import java.util.LinkedHashMap;

/**
 * The view of {@link CreateServicePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateServiceView extends View<CreateServiceView.ActionDelegate> {
    /** Needs for delegate some function into CreateService view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user
         * having pressed the Create button.
         */
        public void onCreateClicked();

        /**
         * Performs any actions appropriate in response to the user
         * having pressed the Cancel button.
         */
        public void onCancelClicked();
    }

    /**
     * Returns selected system's service.
     *
     * @return system's service.
     */
    public String getSystemServices();

    /**
     * Sets available services.
     *
     * @param services
     *         available services
     */
    public void setServices(LinkedHashMap<String, String> services);

    /**
     * Returns service's name.
     *
     * @return service's name
     */
    public String getName();

    /**
     * Sets service's name.
     *
     * @param name
     */
    public void setName(String name);

    /** Show dialog. */
    public void showDialog();

    /** Close dialog. */
    public void close();
}