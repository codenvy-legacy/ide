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
package org.exoplatform.ide.client.navigation.handler;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.Settings;
import org.exoplatform.ide.client.navigation.control.ShowHideHiddenFilesControl;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideHiddenFilesCommandHandler.java Apr 2, 2012 10:27:21 AM azatsarynnyy $
 */
public class ShowHideHiddenFilesCommandHandler implements ShowHideHiddenFilesHandler, InitializeServicesHandler,
                                                          ApplicationSettingsReceivedHandler {
    /** Stores the pattern for hidden files. */
    private String hiddenFilesPattern = "";

    private ApplicationSettings applicationSettings;

    public ShowHideHiddenFilesCommandHandler() {
        IDE.getInstance().addControl(new ShowHideHiddenFilesControl());

        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        hiddenFilesPattern = event.getApplicationConfiguration().getHiddenFiles();

        boolean showHiddenFiles = applicationSettings.getValueAsBoolean(Settings.SHOW_HIDDEN_FILES);
        IDE.fireEvent(new ShowHideHiddenFilesEvent(showHiddenFiles));
    }

    /** @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client
     * .navigation.event.ShowHideHiddenFilesEvent) */
    @Override
    public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event) {
        if (event.isFilesShown()) {
            DirectoryFilter.get().setPattern("");
        } else {
            DirectoryFilter.get().setPattern(hiddenFilesPattern);
        }
    }

    /** @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     * .exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent) */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();

        if (applicationSettings.getValueAsBoolean(Settings.SHOW_HIDDEN_FILES) == null) {
            applicationSettings.setValue(Settings.SHOW_HIDDEN_FILES, Boolean.FALSE, Store.COOKIES);
        }
    }
}
