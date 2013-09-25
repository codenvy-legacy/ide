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
        void onOKClicked();
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