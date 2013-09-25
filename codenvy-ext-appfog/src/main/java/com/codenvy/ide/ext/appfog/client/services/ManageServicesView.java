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
package com.codenvy.ide.ext.appfog.client.services;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;
import com.codenvy.ide.json.JsonArray;

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
    void setProvisionedServices(JsonArray<AppfogProvisionedService> services);

    /**
     * Sets bounded services.
     *
     * @param services
     */
    void setBoundedServices(JsonArray<String> services);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}