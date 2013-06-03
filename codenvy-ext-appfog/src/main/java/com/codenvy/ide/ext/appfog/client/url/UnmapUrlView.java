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
package com.codenvy.ide.ext.appfog.client.url;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link UnmapUrlPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface UnmapUrlView extends View<UnmapUrlView.ActionDelegate> {
    /** Needs for delegate some function into UnmapUrl view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Map url button. */
        void onMapUrlClicked();

        /**
         * Performs any actions appropriate in response to the user having pressed the Unmap url button.
         *
         * @param url
         *         url what needs to unmap
         */
        void onUnMapUrlClicked(String url);

        /** Performs any actions appropriate in response to the user having pressed the Create button. */
        void onMapUrlChanged();
    }

    /**
     * Returns map url.
     *
     * @return map url
     */
    String getMapUrl();

    /**
     * Sets map url.
     *
     * @param url
     */
    void setMapUrl(String url);

    /**
     * Sets registered urls.
     *
     * @param urls
     */
    void setRegisteredUrls(JsonArray<String> urls);

    /**
     * Sets whether Map url button is enabled.
     *
     * @param enable
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setEnableMapUrlButton(boolean enable);

    /** Show dialog. */
    void showDialog();

    /** Close dialog. */
    void close();
}