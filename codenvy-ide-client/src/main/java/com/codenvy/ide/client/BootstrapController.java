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

import com.codenvy.api.project.gwt.client.ProjectTypeDescriptionServiceClient;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.theme.Style;
import com.codenvy.ide.api.ui.theme.Theme;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Map;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController {

    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private       PreferencesManagerImpl              preferencesManager;
    private       ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient;
    private       ProjectTypeDescriptorRegistry       projectTypeDescriptorRegistry;
    private       ThemeAgent                          themeAgent;

    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspaceProvider
     * @param styleInjector
     * @param extensionInitializer
     * @param preferencesManager
     * @param userService
     * @param projectTypeDescriptionServiceClient
     *
     * @param projectTypeDescriptorRegistry
     * @param resourceProvider
     * @param dtoRegistrar
     * @param themeAgent
     */
    @Inject
    public BootstrapController(final Provider<ComponentRegistry> componentRegistry,
                               final Provider<WorkspacePresenter> workspaceProvider,
                               final StyleInjector styleInjector,
                               final ExtensionInitializer extensionInitializer,
                               final PreferencesManagerImpl preferencesManager,
                               UserClientService userService,
                               final ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient,
                               final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                               final ResourceProvider resourceProvider,
                               DtoRegistrar dtoRegistrar,
                               final ThemeAgent themeAgent,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.preferencesManager = preferencesManager;
        this.projectTypeDescriptionServiceClient = projectTypeDescriptionServiceClient;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.themeAgent = themeAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

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

        dtoRegistrar.registerDtoProviders();
        userService.getUser(new AsyncRequestCallback<User>(dtoUnmarshallerFactory.newUnmarshaller(User.class)) {
            @Override
            protected void onSuccess(final User user) {
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
    }

    private void setTheme() {
        final String storedThemeId = preferencesManager.getValue("Theme");
        Theme themeToSet = storedThemeId != null ? themeAgent.getTheme(storedThemeId) : themeAgent.getDefault();
        Style.setTheme(themeToSet);
        themeAgent.setCurrentThemeId(themeToSet.getId());
    }

    private void initializeProjectTypeDescriptorRegistry() {
        projectTypeDescriptionServiceClient
                .getProjectTypes(new AsyncRequestCallback<Array<ProjectTypeDescriptor>>(
                        dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectTypeDescriptor.class)) {
                    @Override
                    protected void onSuccess(Array<ProjectTypeDescriptor> result) {
                        projectTypeDescriptorRegistry.registerDescriptors(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(BootstrapController.class, exception);
                    }
                });
    }

}
