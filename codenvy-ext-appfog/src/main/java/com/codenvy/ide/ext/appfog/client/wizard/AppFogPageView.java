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
package com.codenvy.ide.ext.appfog.client.wizard;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link AppFogPagePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface AppFogPageView extends View<AppFogPageView.ActionDelegate> {
    /** Needs for delegate some function into AppFog view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed application name. */
        void onNameChanged();

        /** Performs any actions appropriate in response to the user having changed url. */
        void onUrlChanged();

        /** Performs any actions appropriate in response to the user having changed infrastructure. */
        void onInfraChanged();
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
     * Returns target.
     *
     * @return target
     */
    String getTarget();

    /**
     * Sets target.
     *
     * @param target
     */
    void setTarget(String target);


    /**
     * Returns selected infrastructure.
     *
     * @return infrastructure
     */
    String getInfra();


    /**
     * Selects infrastructure.
     *
     * @param infra
     *         infrastructure
     */
    void setInfra(String infra);

    /**
     * Sets infrastructures.
     *
     * @param infras
     *         infrastructures
     */
    void setInfras(JsonArray<String> infras);
}