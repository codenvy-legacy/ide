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
import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.gwt.client.FactoryServiceClient;
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.api.workspace.gwt.client.WorkspaceServiceClient;
import com.codenvy.api.workspace.shared.dto.WorkspaceDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentUser;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.WindowActionEvent;
import com.codenvy.ide.api.icon.Icon;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.theme.Style;
import com.codenvy.ide.api.theme.Theme;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.core.ComponentRegistry;
import com.codenvy.ide.logger.AnalyticsEventLoggerExt;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringMapUnmarshaller;
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
import com.google.web.bindery.event.shared.HandlerRegistration;

import java.util.HashMap;
import java.util.Map;

/**
 * Performs initial application startup.
 *
 * @author Nikolay Zamosenchuk
 */
public class BootstrapController implements ProjectActionHandler {

    private final DtoUnmarshallerFactory       dtoUnmarshallerFactory;
    private final AnalyticsEventLoggerExt      analyticsEventLoggerExt;
    private final IconRegistry                 iconRegistry;
    private final ThemeAgent                   themeAgent;
    private final Provider<ComponentRegistry>  componentRegistry;
    private final Provider<WorkspacePresenter> workspaceProvider;
    private final ExtensionInitializer         extensionInitializer;
    private final FactoryServiceClient         factoryService;
    private final UserProfileServiceClient     userProfileService;
    private final WorkspaceServiceClient       workspaceServiceClient;
    private final PreferencesManagerImpl       preferencesManager;
    private final StyleInjector                styleInjector;
    private final CoreLocalizationConstant     coreLocalizationConstant;
    private final EventBus                     eventBus;
    private final ActionManager                actionManager;
    private       AppContext                   appContext;

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
                               FactoryServiceClient factoryService,
                               EventBus eventBus,
                               AppContext appContext,
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
        this.factoryService = factoryService;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.iconRegistry = iconRegistry;
        this.themeAgent = themeAgent;
        this.actionManager = actionManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.analyticsEventLoggerExt = analyticsEventLoggerExt;

        // Register DTO providers
        dtoRegistrar.registerDtoProviders();

        // Register default icons
        registerDefaultIcons(resources);

        // Inject ZeroClipboard script
        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "ZeroClipboard.min.js").setWindow(ScriptInjector.TOP_WINDOW)
                      .setCallback(new Callback<Void, Exception>() {
                          @Override
                          public void onSuccess(Void result) {
                          }

                          @Override
                          public void onFailure(Exception e) {
                              Log.error(getClass(), "Unable to inject ZeroClipboard.min.js", e);
                          }
                      }).inject();

        loadWorkspace();
    }

    /**
     * Fetches current workspace and saves it to Config.
     */
    private void loadWorkspace() {
        workspaceServiceClient.getWorkspace(Config.getWorkspaceId(),
                                            new AsyncRequestCallback<WorkspaceDescriptor>(
                                                    dtoUnmarshallerFactory.newUnmarshaller(WorkspaceDescriptor.class)) {
                                                @Override
                                                protected void onSuccess(WorkspaceDescriptor result) {
                                                    Config.setCurrentWorkspace(result);
                                                    appContext.setWorkspace(result);
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
        userProfileService.getCurrentProfile(new AsyncRequestCallback<ProfileDescriptor>(
                                                     dtoUnmarshallerFactory.newUnmarshaller(ProfileDescriptor.class)) {
                                                 @Override
                                                 protected void onSuccess(final ProfileDescriptor profile) {
                                                     appContext.setCurrentUser(new CurrentUser(profile));
                                                     loadPreferences();
                                                 }

                                                 @Override
                                                 protected void onFailure(Throwable error) {
                                                     Log.error(BootstrapController.class, "Unable to get Profile", error);
                                                     initializationFailed("Unable to get Profile");
                                                 }
                                             }
                                            );
    }

    private void loadPreferences() {
        userProfileService.getPreferences(new AsyncRequestCallback<Map<String, String>>(new StringMapUnmarshaller()) {
            @Override
            protected void onSuccess(Map<String, String> preferences) {
                preferencesManager.load(preferences);
                setTheme();
                styleInjector.inject();

                loadFactory();
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(BootstrapController.class, "Unable to load user preferences", exception);
                initializationFailed("Unable to load preferences");
            }
        });
    }

    private void loadFactory() {
        String factoryParams = null;
        boolean encoded = false;
        if (Config.getStartupParam("id") != null) {
            factoryParams = Config.getStartupParam("id");
            encoded = true;
        } else if (Config.getStartupParam("v") != null) {
            factoryParams = Config.getStartupParams();
        }

        if (factoryParams != null) {
            factoryService.getFactory(factoryParams, encoded,
                                      new AsyncRequestCallback<Factory>(dtoUnmarshallerFactory.newUnmarshaller(Factory.class)) {
                                          @Override
                                          protected void onSuccess(Factory factory) {
                                              appContext.setFactory(factory);
                                              initializeComponentRegistry();
                                          }

                                          @Override
                                          protected void onFailure(Throwable error) {
                                              Log.error(BootstrapController.class, "Unable to get Profile", error);
                                              initializationFailed("Unable to get Profile");
                                          }
                                      }
                                     );
            return;
        }

        initializeComponentRegistry();
    }

    private void initializeComponentRegistry() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                doInitializeComponentRegistry();
            }
        });
    }

    /** Initialize Component Registry, start extensions */
    private void doInitializeComponentRegistry() {
        componentRegistry.get().start(new Callback<Void, ComponentException>() {
            @Override
            public void onSuccess(Void result) {
                // Instantiate extensions
                extensionInitializer.startExtensions();

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        displayIDE();
                    }
                });
            }

            @Override
            public void onFailure(ComponentException caught) {
                Log.error(BootstrapController.class, "Unable to start component " + caught.getComponent(), caught);
                initializationFailed("Unable to start component " + caught.getComponent());
            }
        });
    }

    /** Displays the IDE */
    private void displayIDE() {
        // Start UI
        SimpleLayoutPanel mainPanel = new SimpleLayoutPanel();

        RootLayoutPanel.get().add(mainPanel);

        // Make sure the root panel creates its own stacking context
        RootLayoutPanel.get().getElement().getStyle().setZIndex(1);

        WorkspacePresenter workspacePresenter = workspaceProvider.get();

        // Display 'Update extension' button if IDE is launched in SDK runner
        workspacePresenter.setUpdateButtonVisibility(Config.getStartupParam("h") != null && Config.getStartupParam("p") != null);

        // Display IDE
        workspacePresenter.go(mainPanel);

        Document.get().setTitle(coreLocalizationConstant.codenvyTabTitle());

        processStartupParameters();

        final AnalyticsSessions analyticsSessions = new AnalyticsSessions();

        // Bind browser's window events
        Window.addWindowClosingHandler(new Window.ClosingHandler() {
            @Override
            public void onWindowClosing(Window.ClosingEvent event) {
                onWindowClose(analyticsSessions);
                eventBus.fireEvent(WindowActionEvent.createWindowClosingEvent(event));
            }
        });
        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                onWindowClose(analyticsSessions);
                eventBus.fireEvent(WindowActionEvent.createWindowClosedEvent());
            }
        });

        elemental.html.Window window = Browser.getWindow();

        window.addEventListener(Event.FOCUS, new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                onFocusIn(analyticsSessions, false);
            }
        }, true);

        window.addEventListener(Event.BLUR, new EventListener() {
            @Override
            public void handleEvent(Event evt) {
                onFocusOut(analyticsSessions, false);
            }
        }, true);

        onFocusIn(analyticsSessions, true); // This is necessary to forcibly print the very first event
    }

    private void onFocusIn(AnalyticsSessions analyticsSessions, boolean force) {
        if (analyticsSessions.getIdleTime() > 600000) { // 10 min
            analyticsSessions.makeNew();
            force = true;
        }

        analyticsSessions.setHasFocus(true);
        logSessionUsageEvent(analyticsSessions, force);
    }

    private void onWindowClose(AnalyticsSessions analyticsSessions) {
        if (analyticsSessions.isHasFocus() || analyticsSessions.getIdleTime() <= 60000) { // 1 min
            logSessionUsageEvent(analyticsSessions, true);
        }
    }

    private void onFocusOut(AnalyticsSessions analyticsSessions, boolean force) {
        analyticsSessions.setHasFocus(false);
        logSessionUsageEvent(analyticsSessions, force);
    }

    private void logSessionUsageEvent(AnalyticsSessions analyticsSessions, boolean force) {
        if (force || analyticsSessions.getIdleTime() > 60000) { // 1 min, don't log frequently than once per minute
            Map<String, String> parameters = new HashMap<>();
            parameters.put("SESSION-ID", analyticsSessions.getId());

            analyticsEventLoggerExt.logEvent(EventLogger.SESSION_USAGE, parameters);

            if (Config.getCurrentWorkspace() != null && Config.getCurrentWorkspace().isTemporary()) {
                analyticsEventLoggerExt.logEvent(EventLogger.SESSION_FACTORY_USAGE, parameters);
            }

            analyticsSessions.updateLastLogTime();
        }
    }

    HandlerRegistration handlerRegistration = null;

    private void processStartupParameters() {
        final String projectNameToOpen = Config.getProjectName();
        if (projectNameToOpen != null) {
            handlerRegistration = eventBus.addHandler(ProjectActionEvent.TYPE, this);
            eventBus.fireEvent(new OpenProjectEvent(projectNameToOpen));
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

    //process action only after opening project
    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        processStartupAction();
        if (handlerRegistration != null) {
            handlerRegistration.removeHandler();
        }
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {

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
     * @param message
     *         error message
     */
    private native void initializationFailed(String message) /*-{
        try {
            $wnd.IDE.eventHandlers.initializationFailed(message);
        } catch (e) {
            console.log(e.message);
        }
    }-*/;


    private static class AnalyticsSessions {
        private String  id;
        private long    lastLogTime;
        private boolean hasFocus;

        private AnalyticsSessions() {
            makeNew();
        }

        public String getId() {
            return id;
        }

        public void updateLastLogTime() {
            lastLogTime = System.currentTimeMillis();
        }

        public void makeNew() {
            this.id = UUID.uuid();
            this.lastLogTime = System.currentTimeMillis();
            this.hasFocus = false;
        }

        public long getIdleTime() {
            return System.currentTimeMillis() - lastLogTime;
        }

        public boolean isHasFocus() {
            return hasFocus;
        }

        public void setHasFocus(boolean hasFocus) {
            this.hasFocus = hasFocus;
        }
    }
}
