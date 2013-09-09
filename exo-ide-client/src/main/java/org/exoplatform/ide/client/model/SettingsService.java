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
package org.exoplatform.ide.client.model;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;

/**
 * Service to save and to get settings.
 * <p/>
 * Settings can be stored in cookies, registry or both places.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: SettingsService.java Mar 30, 2011 11:06:10 AM vereshchaka $
 */
public abstract class SettingsService {

    private static SettingsService instance;

    protected SettingsService() {
        instance = this;
    }

    public static SettingsService getInstance() {
        return instance;
    }

    /**
     * Save application settings to registry.
     *
     * @param applicationSettings
     * @param callback
     */
    public abstract void saveSettingsToServer(ApplicationSettings applicationSettings,
                                              AsyncRequestCallback<ApplicationSettings> callback) throws RequestException;

    /**
     * Save application settings to cookies.
     *
     * @param applicationSettings
     */
    public abstract void saveSettingsToCookies(ApplicationSettings applicationSettings);

    /**
     * Restore application settings from cookies.
     *
     * @param applicationSettings
     */
    public abstract void restoreFromCookies(ApplicationSettings applicationSettings);

}
