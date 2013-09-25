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