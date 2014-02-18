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

import com.codenvy.api.project.gwt.client.ProjectTypeDescriptionClientService;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.theme.Style;
import com.codenvy.ide.api.ui.theme.Theme;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Map;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController {

    private PreferencesManagerImpl              preferencesManager;
    private ProjectTypeDescriptionClientService projectTypeService;
    private ProjectTypeDescriptorRegistry       projectTypeDescriptorRegistry;
    private DtoFactory                          dtoFactory;
    private ThemeAgent                          themeAgent;

    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspaceProvider
     * @param styleInjector
     * @param extensionInitializer
     * @param preferencesManager
     * @param userService
     * @param projectTypeDescriptionService
     * @param projectTypeDescriptorRegistry
     * @param resourceProvider
     * @param dtoRegistrar
     * @param dtoFactory
     * @param themeAgent
     */
    @Inject
    public BootstrapController(final Provider<ComponentRegistry> componentRegistry,
                               final Provider<WorkspacePresenter> workspaceProvider,
                               final StyleInjector styleInjector,
                               final ExtensionInitializer extensionInitializer,
                               final PreferencesManagerImpl preferencesManager,
                               UserClientService userService,
                               final ProjectTypeDescriptionClientService projectTypeDescriptionService,
                               final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                               final ResourceProvider resourceProvider,
                               DtoRegistrar dtoRegistrar,
                               final DtoFactory dtoFactory,
                               final ThemeAgent themeAgent) {
        this.preferencesManager = preferencesManager;
        this.projectTypeService = projectTypeDescriptionService;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.dtoFactory = dtoFactory;
        this.themeAgent = themeAgent;

        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_base.js").setWindow(ScriptInjector.TOP_WINDOW)
                      .setCallback(new Callback<Void, Exception>() {
                          @Override
                          public void onSuccess(Void result) {
                              ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_parsers.js")
                                            .setWindow(ScriptInjector.TOP_WINDOW).inject();
                          }

                          @Override
                          public void onFailure(Exception reason) {
                          }
                      }).inject();

        try {
            dtoRegistrar.registerDtoProviders();
            userService.getUser(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(final String result) {
                    final User user = dtoFactory.createDtoFromJson(result, User.class);
                    Map<String, String> attributes = user.getProfileAttributes();
                    preferencesManager.load(attributes);

                    setTheme();
                    styleInjector.inject();

                    // initialize components
                    componentRegistry.get().start(new Callback<Void, ComponentException>() {
                        @Override
                        public void onSuccess(Void result) {
                            // instantiate extensions
                            extensionInitializer.startExtensions();
                            // Start UI
                            SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();
                            RootLayoutPanel.get().add(mainPanel);
                            WorkspacePresenter workspacePresenter = workspaceProvider.get();

                            workspacePresenter.setUpdateButtonVisibility(Utils.isAppLaunchedInSDKRunner());

                            final boolean isUserLoggedIn = !user.getUserId().equals("__anonim");
                            workspacePresenter.setVisibleLoginButton(!isUserLoggedIn);
                            workspacePresenter.setVisibleLogoutButton(isUserLoggedIn);

                            // Display IDE
                            workspacePresenter.go(mainPanel);
                            // Display list of projects in project explorer
                            resourceProvider.showListProjects();
                        }

                        @Override
                        public void onFailure(ComponentException caught) {
                            Log.error(BootstrapController.class, "FAILED to start service:" + caught.getComponent(), caught);
                        }
                    });

                    initializeProjectTypeDescriptorRegistry();
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

    private void setTheme() {
        final String storedThemeId = preferencesManager.getValue("Theme");
        Theme themeToSet = storedThemeId != null ? themeAgent.getTheme(storedThemeId) : themeAgent.getDefault();
        Style.setTheme(themeToSet);
        themeAgent.setCurrentThemeId(themeToSet.getId());
    }

    private void initializeProjectTypeDescriptorRegistry() {
        try {
            projectTypeService.getProjectTypes(new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    projectTypeDescriptorRegistry
                            .registerDescriptors(dtoFactory.createListDtoFromJson(result, ProjectTypeDescriptor.class));
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
