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
package com.codenvy.ide.ext.cloudbees.client.create;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link CreateApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CreateApplicationView extends View<CreateApplicationView.ActionDelegate> {
    /** Needs for delegate some function into CreateApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Create button. */
        void onCreateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed some information. */
        void onValueChanged();
    }

    /**
     * Returns application's name.
     *
     * @return application's name
     */
    String getName();

    /**
     * Sets application's name.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Returns application's url.
     *
     * @return url
     */
    String getUrl();

    /**
     * Sets application's url.
     *
     * @param url
     */
    void setUrl(String url);

    /**
     * Returns domain.
     *
     * @return domain
     */
    String getDomain();

    /**
     * Sets domain.
     *
     * @param domain
     */
    void setDomain(String domain);

    /**
     * Set the list of domains.
     *
     * @param domains
     */
    void setDomainValues(JsonArray<String> domains);

    /**
     * Sets whether Create button is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnableCreateButton(boolean enable);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}