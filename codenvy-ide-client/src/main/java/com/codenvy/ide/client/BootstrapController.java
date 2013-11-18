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
package com.codenvy.ide.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.resources.marshal.UserUnmarshaller;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
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
     * @param workspaceProvider
     * @param styleInjector
     * @param extensionInitializer
     * @param preferencesManager
     * @param userService
     * @param resourceProvider
     * @param dtoRegistrar
     */
    @Inject
    public BootstrapController(final ComponentRegistry componentRegistry, final Provider<WorkspacePresenter> workspaceProvider,
                               StyleInjector styleInjector, final ExtensionInitializer extensionInitializer,
                               final PreferencesManagerImpl preferencesManager, UserClientService userService,
                               final ResourceProvider resourceProvider, DtoRegistrar dtoRegistrar) {
        styleInjector.inject();
        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_base.js").setWindow(ScriptInjector.TOP_WINDOW)
                      .setCallback(new Callback<Void, Exception>() {
                          @Override
                          public void onFailure(Exception reason) {
                          }

                          @Override
                          public void onSuccess(Void result) {
                              ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_parsers.js")
                                            .setWindow(ScriptInjector.TOP_WINDOW).inject();
                          }
                      }).inject();


        try {
            dtoRegistrar.registerDtoProviders();
            UserUnmarshaller unmarshaller = new UserUnmarshaller();
            userService.getUser(new AsyncRequestCallback<User>(unmarshaller) {
                @Override
                protected void onSuccess(final User user) {
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

                            String userId = user.getUserId();
                            if (userId.equals("__anonim")) {
                                workspacePresenter.setVisibleLoginButton(true);
                                workspacePresenter.setVisibleLogoutButton(false);
                            } else {
                                workspacePresenter.setVisibleLoginButton(false);
                                workspacePresenter.setVisibleLogoutButton(true);
                            }

                            // Display IDE
                            workspacePresenter.go(mainPanel);
                            //Display list of projects in project explorer
                            resourceProvider.showListProjects();
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
