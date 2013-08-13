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
package com.codenvy.ide.extension.cloudfoundry.client.url;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link UnmapUrlPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
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