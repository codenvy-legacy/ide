/*
 * Copyright (C) 2010 eXo Platform SAS.
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
 * Created by The eXo Platform SAS.
 *
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
        new RestServicesDiscoveryPresenter();
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
