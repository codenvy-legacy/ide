/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.client;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;

import com.codenvy.api.analytics.logger.EventLogger;
import com.codenvy.api.project.gwt.client.ProjectTypeDescriptionServiceClient;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.api.workspace.gwt.client.WorkspaceServiceClient;
import com.codenvy.api.workspace.shared.dto.WorkspaceDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.WindowActionEvent;
import com.codenvy.ide.api.icon.Icon;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.projecttype.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.theme.Style;
import com.codenvy.ide.api.theme.Theme;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.logger.AnalyticsEventLoggerExt;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.toolbar.PresentationFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.UUID;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.Map;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController {

    private final DtoUnmarshallerFactory              dtoUnmarshallerFactory;
    private final AnalyticsEventLoggerExt             analyticsEventLoggerExt;
    private final DtoFactory                          dtoFactory;
    private final ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient;
    private final ProjectTypeDescriptorRegistry       projectTypeDescriptorRegistry;
    private final IconRegistry                        iconRegistry;
    private final ThemeAgent                          themeAgent;
    private final Provider<ComponentRegistry>         componentRegistry;
    private final Provider<WorkspacePresenter>        workspaceProvider;
    private final ExtensionInitializer                extensionInitializer;
    private final UserProfileServiceClient            userProfileService;
    private final WorkspaceServiceClient              workspaceServiceClient;
    private final PreferencesManagerImpl              preferencesManager;
    private final StyleInjector                       styleInjector;
    private final CoreLocalizationConstant            coreLocalizationConstant;
    private final EventBus eventBus;
    private final ActionManager actionManager;

    /** Create controller. */
    @Inject
    public BootstrapController(Provider<ComponentRegistry> componentRegistry,
                               Provider<WorkspacePresenter> workspaceProvider,
                               ExtensionInitializer extensionInitializer,
                               UserProfileServiceClient userProfileService,
                               WorkspaceServiceClient workspaceServiceClient,
                               PreferencesManagerImpl preferencesManager,
                               StyleInjector styleInjector,
                               CoreLocalizationConstant coreLocalizationConstant,
                               DtoRegistrar dtoRegistrar,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               AnalyticsEventLoggerExt analyticsEventLoggerExt,
                               Resources resources,
                               EventBus eventBus,
                               DtoFactory dtoFactory,

                               final ProjectTypeDescriptionServiceClient projectTypeDescriptionServiceClient,
                               final ProjectTypeDescriptorRegistry projectTypeDescriptorRegistry,
                               final IconRegistry iconRegistry,
                               final ThemeAgent themeAgent,
                               ActionManager actionManager) {
        this.componentRegistry = componentRegistry;
        this.workspaceProvider = workspaceProvider;
        this.extensionInitializer = extensionInitializer;
        this.userProfileService = userProfileService;
        this.workspaceServiceClient = workspaceServiceClient;
        this.preferencesManager = preferencesManager;
        this.styleInjector = styleInjector;
        this.coreLocalizationConstant = coreLocalizationConstant;
        this.eventBus = eventBus;
        this.dtoFactory = dtoFactory;
        this.projectTypeDescriptionServiceClient = projectTypeDescriptionServiceClient;
        this.projectTypeDescriptorRegistry = projectTypeDescriptorRegistry;
        this.iconRegistry = iconRegistry;
        this.themeAgent = themeAgent;
        this.actionManager = actionManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.analyticsEventLoggerExt = analyticsEventLoggerExt;

        // Register DTO providers
        dtoRegistrar.registerDtoProviders();

        // Register default icons
        registerDefaultIcons(resources);

        // Inject CodeMirror scripts
        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_base.js").setWindow(ScriptInjector.TOP_WINDOW)
                      .setCallback(new Callback<Void, Exception>() {
                          @Override
                          public void onSuccess(Void result) {
                              ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "codemirror2_parsers.js")
                                            .setWindow(ScriptInjector.TOP_WINDOW).
                                      setCallback(new Callback<Void, Exception>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              loadWorkspace();
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

    /**
     * Fetches current workspace and saves it to Config.
     */
    private void loadWorkspace() {
        workspaceServiceClient.getWorkspace(Config.getWorkspaceId(),
                    new AsyncRequestCallback<WorkspaceDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(WorkspaceDescriptor.class)) {
                        @Override
                        protected void onSuccess(WorkspaceDescriptor result) {
                            Config.setCurrentWorkspace(result);
                            loadUserProfile();
                        }

                        @Override
                        protected void onFailure(Throwable throwable) {
                            Log.error(BootstrapController.class, "Unable to get Workspace", throwable);
                            initializationFailed("Unable to get Workspace");
                        }
                    }
               );
    }

    /** Get User profile, restore preferences and theme */
    private void loadUserProfile() {
        userProfileService.getCurrentProfile(null,
                 new AsyncRequestCallback<ProfileDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(ProfileDescriptor.class)) {
                     @Override
                     protected void onSuccess(final ProfileDescriptor profile) {
                         Config.setCurrentProfile(profile);

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
                     protected void onFailure(Throwable error) {
                         Log.error(BootstrapController.class, "Unable to get Profile", error);
                         initializationFailed("Unable to get Profile");
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
                        projectTypeDescriptorRegistry.registerDescriptor(result.get(i));
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
        workspacePresenter.setUpdateButtonVisibility(Config.getStartupParam("h") != null && Config.getStartupParam("p") != null);

        // Display IDE
        workspacePresenter.go(mainPanel);

        Document.get().setTitle(coreLocalizationConstant.codenvyTabTitle());

        processStartupParameters();

        // Bind browser's window events
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(Window.ClosingEvent event) {
                eventBus.fireEvent(WindowActionEvent.createWindowClosingEvent(event));
            }
        });
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                eventBus.fireEvent(WindowActionEvent.createWindowClosedEvent());
            }
        });

        final String sessionID = UUID.uuid();
        elemental.html.Window window = Browser.getWindow();

        window.addEventListener(Event.FOCUS, new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                sessionIsStarted(sessionID);
            }
        }, true);

        window.addEventListener(Event.BLUR, new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                sessionIsStopped(sessionID);
            }
        }, true);

        //This is necessary to forcibly print the first log
        sessionIsStarted(sessionID);
    }

    private void sessionIsStarted(String sessionID) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("SESSION-ID", sessionID);

        analyticsEventLoggerExt.logEvent(EventLogger.SESSION_STARTED, parameters);

        if (Config.getCurrentWorkspace() != null && Config.getCurrentWorkspace().isTemporary()) {
            analyticsEventLoggerExt.logEvent(EventLogger.SESSION_FACTORY_STARTED, parameters);
        }
    }

    private void sessionIsStopped(String sessionID) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("SESSION-ID", sessionID);

        analyticsEventLoggerExt.logEvent(EventLogger.SESSION_FINISHED, parameters);

        if (Config.getCurrentWorkspace() != null && Config.getCurrentWorkspace().isTemporary()) {
            analyticsEventLoggerExt.logEvent(EventLogger.SESSION_FACTORY_STOPPED, parameters);
        }
    }

    private void processStartupParameters() {
        final String projectNameToOpen = Config.getProjectName();
        if (projectNameToOpen != null) {
            ProjectReference projectToOpen = dtoFactory.createDto(ProjectReference.class).withName(projectNameToOpen);
            eventBus.fireEvent(new OpenProjectEvent(projectToOpen));
            processStartupAction();
        } else {
            processStartupAction();
        }
    }

    private void processStartupAction() {
        final String startupAction = Config.getStartupParam("action");
        if (startupAction != null) {
            Action action = actionManager.getAction(startupAction);
            if (action != null) {
                ActionEvent e = new ActionEvent("", new PresentationFactory().getPresentation(action), actionManager, 0);
                action.update(e);
                if (e.getPresentation().isEnabled() && e.getPresentation().isVisible()) {
                    action.actionPerformed(e);
                }
            }
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
