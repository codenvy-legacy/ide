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
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.ide.Constants;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.user.UserInfo;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.ui.Icon;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.theme.Style;
import com.codenvy.ide.api.ui.theme.Theme;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController {

    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private final ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient;
    private final ProjectTypeDescriptorRegistry       projectTypeDescriptorRegistry;
    private final IconRegistry                        iconRegistry;
    private final ThemeAgent                          themeAgent;
    private final Provider<ComponentRegistry> componentRegistry;
    private final Provider<WorkspacePresenter> workspaceProvider;
    private final ExtensionInitializer extensionInitializer;
    private final ResourceProvider resourceProvider;
    private final UserProfileServiceClient userProfileService;
    private final PreferencesManagerImpl preferencesManager;
    private final UserInfo userInfo;
    private final StyleInjector styleInjector;

    /**
     * Create controller.
     *
     * @param componentRegistry
     * @param workspaceProvider
     * @param styleInjector
     * @param extensionInitializer
     * @param preferencesManager
     * @param userProfileService
     * @param projectTypeDescriptionServiceClient
     * @param projectTypeDescriptorRegistry
     * @param resourceProvider
     * @param dtoRegistrar
     * @param themeAgent
     */
    @Inject
    public BootstrapController(Provider<ComponentRegistry> componentRegistry,
                               Provider<WorkspacePresenter> workspaceProvider,
                               ExtensionInitializer extensionInitializer,
                               ResourceProvider resourceProvider,
                               UserProfileServiceClient userProfileService,
                               PreferencesManagerImpl preferencesManager,
                               UserInfo userInfo,
                               StyleInjector styleInjector,

                               DtoRegistrar dtoRegistrar,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               Resources resources,

                               final ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient,
                               final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                               final IconRegistry iconRegistry,
                               final ThemeAgent themeAgent) {

        this.componentRegistry = componentRegistry;
        this.workspaceProvider = workspaceProvider;
        this.extensionInitializer = extensionInitializer;
        this.resourceProvider = resourceProvider;
        this.userProfileService = userProfileService;
        this.preferencesManager = preferencesManager;
        this.userInfo = userInfo;
        this.styleInjector = styleInjector;

        this.projectTypeDescriptionServiceClient = projectTypeDescriptionServiceClient;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.iconRegistry = iconRegistry;
        this.themeAgent = themeAgent;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

        // Register DTO providers
        dtoRegistrar.registerDtoProviders();

        // Register default icons
        registerDefaultIcons(resources);

        // Inject CodeMirror scripts
        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_base.js").setWindow(ScriptInjector.TOP_WINDOW)
            .setCallback(new Callback<Void, Exception>() {
                @Override
                public void onSuccess(Void result) {
                    ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_parsers.js").setWindow(ScriptInjector.TOP_WINDOW).
                        setCallback(new Callback<Void, Exception>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadUserProfile();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.error(BootstrapController.class, "Unable to inject CodeMirror parsers", e);
                                initializationFailed("Unable to inject CodeMirror parsers");
                            }
                        }).inject();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.error(BootstrapController.class, "Unable to inject CodeMirror", e);
                    initializationFailed("Unable to inject CodeMirror");
                }
            }).inject();
    }

    /** Get User profile, restore preferences and theme */
    private void loadUserProfile() {
        userProfileService.getCurrentProfile(null,
                 new AsyncRequestCallback<Profile>(dtoUnmarshallerFactory.newUnmarshaller(Profile.class)) {
                     @Override
                     protected void onSuccess(final Profile profile) {
                         userInfo.setProfile(profile);
                         /**
                          * Profile received, restore preferences and theme
                          */
                         preferencesManager.load(profile.getPreferences());
                         setTheme();
                         styleInjector.inject();

                         Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                             @Override
                             public void execute() {
                                 initializeComponentRegistry();
                             }
                         });
                     }

                     @Override
                     protected void onFailure(Throwable exception) {
                         // load Codenvy for anonymous user
                         setTheme();
                         styleInjector.inject();
                         Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                             @Override
                             public void execute() {
                                 initializeComponentRegistry();
                             }
                         });
                     }
                 }
            );
    }

    /** Initialize Component Registry, start extensions */
    private void initializeComponentRegistry() {
        componentRegistry.get().start(new Callback<Void, ComponentException>() {
            @Override
            public void onSuccess(Void result) {
                // Instantiate extensions
                extensionInitializer.startExtensions();

                // Register project types
                registerProjectTypes();
            }

            @Override
            public void onFailure(ComponentException caught) {
                Log.error(BootstrapController.class, "Unable to start component " + caught.getComponent(), caught);
                initializationFailed("Unable to start component " + caught.getComponent());
            }
        });
    }

    /** Register project types */
    private void registerProjectTypes() {
        projectTypeDescriptionServiceClient.getProjectTypes(new AsyncRequestCallback<Array<ProjectTypeDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectTypeDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectTypeDescriptor> result) {
                for (int i = 0; i < result.size(); i++) {
                    if (!result.get(i).getProjectTypeId().equalsIgnoreCase(Constants.NAMELESS_ID)) {
                        projectTypeDescriptorRegistry.registerDescriptor(result.get(i));
                    }
                }

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        displayIDE();
                    }
                });
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(BootstrapController.class, "Unable to get list of project types", exception);
                initializationFailed("Unable to get list of project types");
            }
        });
    }

    /** Displays the IDE */
    private void displayIDE() {
        // Start UI
        SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();

        RootLayoutPanel.get().add(mainPanel);
        WorkspacePresenter workspacePresenter = workspaceProvider.get();

        // Display 'Update extension' button if IDE is launched in SDK runner
        workspacePresenter.setUpdateButtonVisibility(
                Config.getStartupParam("h") != null && Config.getStartupParam("p") != null);

        // Display IDE
        workspacePresenter.go(mainPanel);

        if (Config.getProjectName() == null) {
            resourceProvider.refreshRoot();
        } else {
            resourceProvider.getProject(Config.getProjectName(),
                    new AsyncCallback<Project>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            resourceProvider.refreshRoot();
                        }

                        @Override
                        public void onSuccess(Project project) {
                        }
                    });
        }
    }

    /** Applying user defined Theme. */
    private void setTheme() {
        String storedThemeId = preferencesManager.getValue("Theme");
        storedThemeId = storedThemeId != null ? storedThemeId : themeAgent.getCurrentThemeId();
        Theme themeToSet = storedThemeId != null ? themeAgent.getTheme(storedThemeId) : themeAgent.getDefault();
        Style.setTheme(themeToSet);
        themeAgent.setCurrentThemeId(themeToSet.getId());
    }

    private void registerDefaultIcons(Resources resources) {
        iconRegistry.registerIcon(new Icon("default.projecttype.small.icon", "default/project.png", resources.defaultProject()));
        iconRegistry.registerIcon(new Icon("default.folder.small.icon", "default/folder.png", resources.defaultFolder()));
        iconRegistry.registerIcon(new Icon("default.file.small.icon", "default/file.png", resources.defaultFile()));
        iconRegistry.registerIcon(new Icon("default", "default/default.jpg", resources.defaultIcon()));
    }

    /**
     * Handles any of initialization errors.
     * Tries to call predefined IDE.eventHandlers.ideInitializationFailed function.
     *
     * @param message error message
     */
    private native void initializationFailed(String message) /*-{
        try {
            $wnd.IDE.eventHandlers.initializationFailed(message);
        } catch (e) {
            console.log(e.message);
        }
    }-*/;

}
