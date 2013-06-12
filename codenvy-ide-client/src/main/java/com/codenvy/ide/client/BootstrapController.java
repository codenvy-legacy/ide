/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.client;

import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.client.extensionsPart.ExtensionsPage;
import com.codenvy.ide.client.marshaller.UserUnmarshaller;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Performs initial application startup
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class BootstrapController {
    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspacePeresenter
     * @param styleInjector
     * @param extensionInitializer
     * @param extensionsPage
     * @param preferencesManager
     * @param userService
     */
    @Inject
    public BootstrapController(final ComponentRegistry componentRegistry, final Provider<WorkspacePresenter> workspaceProvider,
                               StyleInjector styleInjector, final ExtensionInitializer extensionInitializer,
                               final ExtensionsPage extensionsPage, final PreferencesManagerImpl preferencesManager,
                               UserClientService userService) {
        styleInjector.inject();

        try {
            DtoClientImpls.UserImpl user = DtoClientImpls.UserImpl.make();
            UserUnmarshaller unmarshaller = new UserUnmarshaller(user);
            userService.getUser(new AsyncRequestCallback<User>(unmarshaller) {
                @Override
                protected void onSuccess(User user) {
                    JsonStringMap<String> attributes = user.getProfileAttributes();
                    preferencesManager.load(attributes);

                    // initialize components
                    componentRegistry.start(new Callback<Void, ComponentException>() {
                        @Override
                        public void onSuccess(Void result) {
                            // instantiate extensions
                            extensionInitializer.startExtensions();
                            // Start UI
                            SimplePanel mainPanel = new SimplePanel();
                            RootLayoutPanel.get().add(mainPanel);
                            WorkspacePresenter workspacePresenter = workspaceProvider.get();
                            // Display IDE
                            workspacePresenter.go(mainPanel);
                            // TODO FOR DEMO
                            workspacePresenter.openPart(extensionsPage, PartStackType.EDITING);
                        }

                        @Override
                        public void onFailure(ComponentException caught) {
                            Log.error(BootstrapController.class, "FAILED to start service:" + caught.getComponent(), caught);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(BootstrapController.class, exception);
                }
            });
        } catch (RequestException e) {
            Log.error(BootstrapController.class, e);
        }
    }
}
