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
package com.codenvy.ide.ext.cloudbees.client.wizard;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link CloudBeesPagePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CloudBeesPageView extends View<CloudBeesPageView.ActionDelegate> {
    /** Needs for delegate some function into CloudBeesPage view. */
    public interface ActionDelegate {
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
}