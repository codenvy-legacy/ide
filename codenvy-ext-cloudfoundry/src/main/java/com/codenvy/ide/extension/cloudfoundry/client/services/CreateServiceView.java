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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.extension.cloudfoundry.shared.SystemService;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link CreateServicePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateServiceView extends View<CreateServiceView.ActionDelegate> {
    /** Needs for delegate some function into CreateService view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Create button. */
        void onCreateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Returns selected system's service.
     *
     * @return system's service.
     */
    String getSystemServices();

    /**
     * Sets available services.
     *
     * @param services
     *         available services
     */
    void setServices(JsonArray<SystemService> services);

    /**
     * Returns service's name.
     *
     * @return service's name
     */
    String getName();

    /**
     * Sets service's name.
     *
     * @param name
     */
    void setName(String name);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}