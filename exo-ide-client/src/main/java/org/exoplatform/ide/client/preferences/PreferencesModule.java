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
package org.exoplatform.ide.client.preferences;

import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.about.AboutIDEPresenter;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedEvent;
import org.exoplatform.ide.client.framework.control.ControlsUpdatedHandler;
import org.exoplatform.ide.client.framework.discovery.RestDiscoveryService;
import org.exoplatform.ide.client.framework.invite.GoogleContactsService;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.preference.Preferences;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.hotkeys.CustomizeHotKeysPresenter;
import org.exoplatform.ide.client.hotkeys.HotKeyManager;
import org.exoplatform.ide.client.hotkeys.HotKeysPreferenceItem;
import org.exoplatform.ide.client.hotkeys.show.ShowHotKeysPresenter;
import org.exoplatform.ide.client.operation.autocompletion.AutocompletionHandler;
import org.exoplatform.ide.client.restdiscovery.RestServicesDiscoveryPresenter;
import org.exoplatform.ide.client.theme.SelectThemePresenter;
import org.exoplatform.ide.client.theme.ThemePreferenceItem;
import org.exoplatform.ide.client.toolbar.CustomizeToolbarPresenter;
import org.exoplatform.ide.client.toolbar.ToolbarPreferenceItem;
import org.exoplatform.ide.vfs.client.VirtualFileSystemFactory;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class PreferencesModule implements InitializeServicesHandler, ControlsUpdatedHandler,
                                          ApplicationSettingsReceivedHandler {

    private IDEConfiguration applicationConfiguration;

    private ApplicationSettings applicationSettings;

    private List<Control> controls;

    public PreferencesModule() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);
        IDE.addHandler(ControlsUpdatedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

        Preferences.get().addPreferenceItem(new ToolbarPreferenceItem(new CustomizeToolbarPresenter()));
        Preferences.get().addPreferenceItem(new HotKeysPreferenceItem(new CustomizeHotKeysPresenter()));
        Preferences.get().addPreferenceItem(new ThemePreferenceItem(new SelectThemePresenter()));
//      Preferences.get().addPreferenceItem(new WorkspacePreferenceItem(new SelectWorkspacePresenter()));

        new AutocompletionHandler();
        new AboutIDEPresenter();
        new ShowHotKeysPresenter();
        new PreferencesPresenter();
    }

    public void onInitializeServices(InitializeServicesEvent event) {
        applicationConfiguration = event.getApplicationConfiguration();
        new VirtualFileSystemFactory(applicationConfiguration.getContext());
        new RestDiscoveryService(Utils.getRestContext(), Utils.getWorkspaceName());
        new HotKeyManager(controls, applicationSettings);
        new GoogleContactsService();
    }

    public void onControlsUpdated(ControlsUpdatedEvent event) {
        controls = event.getControls();
    }

    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();
    }

}
