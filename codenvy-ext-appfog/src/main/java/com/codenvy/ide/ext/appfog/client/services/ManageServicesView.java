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
package com.codenvy.ide.ext.appfog.client.services;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;

import java.util.List;

/**
 * The view of {@link ManageServicesPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ManageServicesView extends View<ManageServicesView.ActionDelegate> {
    /** Needs for delegate some function into ManageServices view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Add button. */
        void onAddClicked();

        /** Performs any actions appropriate in response to the user having pressed the Delete button. */
        void onDeleteClicked();

        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /**
         * Performs any actions appropriate in response to the user having pressed the Unbind service button.
         *
         * @param service
         *         service what needs to unbind
         */
        void onUnbindServiceClicked(String service);

        /**
         * Performs any actions appropriate in response to the user having pressed the Bind service button.
         *
         * @param service
         *         service what needs to bind
         */
        void onBindServiceClicked(AppfogProvisionedService service);

        /**
         * Performs any actions appropriate in response to the user having selected other service.
         *
         * @param service
         *         selected service
         */
        void onSelectedService(AppfogProvisionedService service);
    }

    /**
     * Sets whether Delete button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnableDeleteButton(boolean enabled);

    /**
     * Sets provisioned services.
     *
     * @param services
     */
    void setProvisionedServices(List<AppfogProvisionedService> services);

    /**
     * Sets bounded services.
     *
     * @param services
     */
    void setBoundedServices(List<String> services);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}