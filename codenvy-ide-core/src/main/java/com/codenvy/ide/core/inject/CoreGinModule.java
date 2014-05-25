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
package com.codenvy.ide.core.inject;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.gwt.client.ProjectImportersServiceClientImpl;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.gwt.client.ProjectServiceClientImpl;
import com.codenvy.api.project.gwt.client.ProjectTypeDescriptionServiceClient;
import com.codenvy.api.project.gwt.client.ProjectTypeDescriptionServiceClientImpl;
import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.gwt.client.UserProfileServiceClientImpl;
import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.api.user.gwt.client.UserServiceClientImpl;
import com.codenvy.ide.Resources;
import com.codenvy.ide.about.AboutView;
import com.codenvy.ide.about.AboutViewImpl;
import com.codenvy.ide.actions.ActionManagerImpl;
import com.codenvy.ide.actions.find.FindActionView;
import com.codenvy.ide.actions.find.FindActionViewImpl;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.GenericModelProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.api.ui.theme.Theme;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
import com.codenvy.ide.api.ui.wizard.DefaultWizardFactory;
import com.codenvy.ide.api.ui.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.ui.wizard.WizardDialog;
import com.codenvy.ide.api.ui.wizard.WizardDialogFactory;
import com.codenvy.ide.api.ui.workspace.EditorPartStack;
import com.codenvy.ide.api.ui.workspace.PartStack;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.contexmenu.ContextMenuView;
import com.codenvy.ide.contexmenu.ContextMenuViewImpl;
import com.codenvy.ide.core.IconRegistryImpl;
import com.codenvy.ide.core.StandardComponentInitializer;
import com.codenvy.ide.core.editor.DefaultEditorProvider;
import com.codenvy.ide.core.editor.EditorAgentImpl;
import com.codenvy.ide.core.editor.EditorRegistryImpl;
import com.codenvy.ide.core.editor.ResourceDocumentProvider;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.ExtensionManagerPresenter;
import com.codenvy.ide.extension.ExtensionManagerView;
import com.codenvy.ide.extension.ExtensionManagerViewImpl;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.importproject.ImportProjectView;
import com.codenvy.ide.importproject.ImportProjectViewImpl;
import com.codenvy.ide.keybinding.KeyBindingManager;
import com.codenvy.ide.logger.AnalyticsEventLoggerExt;
import com.codenvy.ide.logger.AnalyticsEventLoggerImpl;
import com.codenvy.ide.menu.MainMenuView;
import com.codenvy.ide.menu.MainMenuViewImpl;
import com.codenvy.ide.navigation.NavigateToFileView;
import com.codenvy.ide.navigation.NavigateToFileViewImpl;
import com.codenvy.ide.notification.NotificationManagerImpl;
import com.codenvy.ide.notification.NotificationManagerView;
import com.codenvy.ide.notification.NotificationManagerViewImpl;
import com.codenvy.ide.openproject.OpenProjectView;
import com.codenvy.ide.openproject.OpenProjectViewImpl;
import com.codenvy.ide.outline.OutlinePartPresenter;
import com.codenvy.ide.outline.OutlinePartView;
import com.codenvy.ide.outline.OutlinePartViewImpl;
import com.codenvy.ide.part.EditorPartStackPresenter;
import com.codenvy.ide.part.EditorPartStackView;
import com.codenvy.ide.part.FocusManager;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.part.PartStackPresenter.PartStackEventHandler;
import com.codenvy.ide.part.PartStackViewImpl;
import com.codenvy.ide.part.console.ConsolePartPresenter;
import com.codenvy.ide.part.console.ConsolePartView;
import com.codenvy.ide.part.console.ConsolePartViewImpl;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerViewImpl;
import com.codenvy.ide.preferences.PreferencesAgentImpl;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.preferences.PreferencesView;
import com.codenvy.ide.preferences.PreferencesViewImpl;
import com.codenvy.ide.projecttype.SelectProjectTypeView;
import com.codenvy.ide.projecttype.SelectProjectTypeViewImpl;
import com.codenvy.ide.rename.RenameResourceView;
import com.codenvy.ide.rename.RenameResourceViewImpl;
import com.codenvy.ide.resources.ResourceProviderComponent;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.selection.SelectionAgentImpl;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesView;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesViewImpl;
import com.codenvy.ide.theme.AppearancePresenter;
import com.codenvy.ide.theme.AppearanceView;
import com.codenvy.ide.theme.AppearanceViewImpl;
import com.codenvy.ide.theme.DarkTheme;
import com.codenvy.ide.theme.LightTheme;
import com.codenvy.ide.theme.ThemeAgentImpl;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.toolbar.ToolbarView;
import com.codenvy.ide.toolbar.ToolbarViewImpl;
import com.codenvy.ide.ui.loader.IdeLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.upload.UploadFileView;
import com.codenvy.ide.upload.UploadFileViewImpl;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.MessageBusImpl;
import com.codenvy.ide.welcome.WelcomePartView;
import com.codenvy.ide.welcome.WelcomePartViewImpl;
import com.codenvy.ide.wizard.WizardDialogPresenter;
import com.codenvy.ide.wizard.WizardDialogView;
import com.codenvy.ide.wizard.WizardDialogViewImpl;
import com.codenvy.ide.wizard.newproject.PaaSAgentImpl;
import com.codenvy.ide.wizard.newproject.ProjectTypeDescriptorRegistryImpl;
import com.codenvy.ide.wizard.newproject.ProjectWizardView;
import com.codenvy.ide.wizard.newproject.ProjectWizardViewImpl;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPageView;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPageViewImpl;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePageView;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePageViewImpl;
import com.codenvy.ide.wizard.project.ProjectTypeWizardRegistryImpl;
import com.codenvy.ide.workspace.PartStackPresenterFactory;
import com.codenvy.ide.workspace.PartStackViewFactory;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.codenvy.ide.workspace.WorkspaceView;
import com.codenvy.ide.workspace.WorkspaceViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.gwt.user.client.Window;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/** @author Nikolay Zamosenchuk */
@ExtensionGinModule
public class CoreGinModule extends AbstractGinModule {

    /** {@inheritDoc} */
    @Override
    protected void configure() {
        // generic bindings
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(Loader.class).to(IdeLoader.class).in(Singleton.class);
        bind(Resources.class).in(Singleton.class);
        bind(ExtensionRegistry.class).in(Singleton.class);
        bind(StandardComponentInitializer.class).in(Singleton.class);
        install(new GinFactoryModuleBuilder().implement(PartStackView.class, PartStackViewImpl.class).build(PartStackViewFactory.class));
        install(new GinFactoryModuleBuilder().implement(PartStack.class, PartStackPresenter.class).build(PartStackPresenterFactory.class));
        bind(PreferencesManager.class).to(PreferencesManagerImpl.class).in(Singleton.class);
        bind(NotificationManager.class).to(NotificationManagerImpl.class).in(Singleton.class);
        bind(ThemeAgent.class).to(ThemeAgentImpl.class).in(Singleton.class);
        bind(DtoFactory.class).in(Singleton.class);
        bind(DtoUnmarshallerFactory.class).in(Singleton.class);
        bind(AsyncRequestFactory.class).in(Singleton.class);
        bind(MessageBus.class).to(MessageBusImpl.class).in(Singleton.class);
        bind(AnalyticsEventLogger.class).to(AnalyticsEventLoggerImpl.class).in(Singleton.class);
        bind(AnalyticsEventLoggerExt.class).to(AnalyticsEventLoggerImpl.class).in(Singleton.class);
        // client services
        bind(UserServiceClient.class).to(UserServiceClientImpl.class).in(Singleton.class);
        bind(UserProfileServiceClient.class).to(UserProfileServiceClientImpl.class).in(Singleton.class);
        bind(ProjectServiceClient.class).to(ProjectServiceClientImpl.class).in(Singleton.class);
        bind(ProjectImportersServiceClient.class).to(ProjectImportersServiceClientImpl.class).in(Singleton.class);
        bind(ProjectTypeDescriptionServiceClient.class).to(ProjectTypeDescriptionServiceClientImpl.class).in(Singleton.class);
        bind(ProjectTypeWizardRegistry.class).to(ProjectTypeWizardRegistryImpl.class).in(Singleton.class);
        apiBindingConfigure();
        resourcesAPIconfigure();
        coreUiConfigure();
        editorAPIconfigure();
    }

    /** API Bindings, binds API interfaces to the implementations */
    private void apiBindingConfigure() {
        // Agents
        bind(KeyBindingAgent.class).to(KeyBindingManager.class).in(Singleton.class);
        bind(SelectionAgent.class).to(SelectionAgentImpl.class).in(Singleton.class);
        bind(WorkspaceAgent.class).to(WorkspacePresenter.class).in(Singleton.class);
        bind(PreferencesAgent.class).to(PreferencesAgentImpl.class).in(Singleton.class);
        bind(PaaSAgent.class).to(PaaSAgentImpl.class).in(Singleton.class);
        bind(ProjectTypeDescriptorRegistry.class).to(ProjectTypeDescriptorRegistryImpl.class).in(Singleton.class);
        bind(IconRegistry.class).to(IconRegistryImpl.class).in(Singleton.class);
        // UI Model
        bind(EditorPartStack.class).to(EditorPartStackPresenter.class).in(Singleton.class);
        install(new GinFactoryModuleBuilder().implement(WizardDialog.class, WizardDialogPresenter.class).build(WizardDialogFactory.class));
        install(new GinFactoryModuleBuilder().build(DefaultWizardFactory.class));
        bind(WizardDialogView.class).to(WizardDialogViewImpl.class);
        // Parts
        bind(ConsolePart.class).to(ConsolePartPresenter.class).in(Singleton.class);
        bind(OutlinePart.class).to(OutlinePartPresenter.class).in(Singleton.class);
        bind(ProjectExplorerPart.class).to(ProjectExplorerPartPresenter.class).in(Singleton.class);
        bind(ActionManager.class).to(ActionManagerImpl.class).in(Singleton.class);
    }

    /** Configures binding for Editor API */
    protected void editorAPIconfigure() {
        bind(DocumentFactory.class).to(DocumentFactoryImpl.class).in(Singleton.class);
        bind(CodenvyTextEditor.class).to(TextEditorPresenter.class);
        bind(EditorAgent.class).to(EditorAgentImpl.class).in(Singleton.class);

        bind(EditorRegistry.class).to(EditorRegistryImpl.class).in(Singleton.class);
        bind(EditorProvider.class).annotatedWith(Names.named("defaultEditor")).to(DefaultEditorProvider.class);
        bind(DocumentProvider.class).to(ResourceDocumentProvider.class).in(Singleton.class);
        bind(UserActivityManager.class).in(Singleton.class);
        bind(OutlinePartView.class).to(OutlinePartViewImpl.class).in(Singleton.class);
    }

    /** Configures binding for Resource API (Resource Manager) */
    protected void resourcesAPIconfigure() {
        bind(ResourceProvider.class).to(ResourceProviderComponent.class).in(Singleton.class);
        // Generic Model Provider
        bind(ModelProvider.class).to(GenericModelProvider.class).in(Singleton.class);
    }

    /** Configure Core UI components, resources and views */
    protected void coreUiConfigure() {
        GinMultibinder<PreferencesPagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencesPagePresenter.class);
        prefBinder.addBinding().to(AppearancePresenter.class);
        prefBinder.addBinding().to(ExtensionManagerPresenter.class);

        GinMultibinder<Theme> themeBinder = GinMultibinder.newSetBinder(binder(), Theme.class);
        themeBinder.addBinding().to(DarkTheme.class);
        themeBinder.addBinding().to(LightTheme.class);

        // Resources
        bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);
        // Views
        bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
        bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);

        bind(ToolbarView.class).to(ToolbarViewImpl.class);
        bind(ToolbarPresenter.class).annotatedWith(MainToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);


        bind(ContextMenuView.class).to(ContextMenuViewImpl.class).in(Singleton.class);
        bind(NotificationManagerView.class).to(NotificationManagerViewImpl.class).in(Singleton.class);
//        bind(PartStackView.class).to(PartStackViewImpl.class);
        bind(PartStackView.class).annotatedWith(Names.named("editorPartStack")).to(EditorPartStackView.class);
        bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);
        bind(ConsolePartView.class).to(ConsolePartViewImpl.class).in(Singleton.class);

        bind(ChooseTemplatePageView.class).to(ChooseTemplatePageViewImpl.class);
        bind(NewProjectPageView.class).to(NewProjectPageViewImpl.class);
        bind(OpenProjectView.class).to(OpenProjectViewImpl.class);
        bind(ImportProjectView.class).to(ImportProjectViewImpl.class);
        bind(UploadFileView.class).to(UploadFileViewImpl.class);
        bind(PreferencesView.class).to(PreferencesViewImpl.class).in(Singleton.class);
        bind(WelcomePartView.class).to(WelcomePartViewImpl.class).in(Singleton.class);
        bind(SelectProjectTypeView.class).to(SelectProjectTypeViewImpl.class).in(Singleton.class);
        bind(NavigateToFileView.class).to(NavigateToFileViewImpl.class).in(Singleton.class);
        bind(RenameResourceView.class).to(RenameResourceViewImpl.class).in(Singleton.class);
        bind(AboutView.class).to(AboutViewImpl.class);
        bind(ListOpenedFilesView.class).to(ListOpenedFilesViewImpl.class);

        bind(ExtensionManagerView.class).to(ExtensionManagerViewImpl.class).in(Singleton.class);
        bind(AppearanceView.class).to(AppearanceViewImpl.class).in(Singleton.class);

        bind(ProjectWizardView.class).to(ProjectWizardViewImpl.class);
        bind(FindActionView.class).to(FindActionViewImpl.class).in(Singleton.class);
    }

    @Provides
    @Named("defaultFileType")
    @Singleton
    protected FileType provideDefaultFileType() {
        //TODO add icon for unknown file
        return new FileType(null, null);
    }

    @Provides
    @Singleton
    protected PartStackEventHandler providePartStackEventHandler(FocusManager partAgentPresenter) {
        return partAgentPresenter.getPartStackHandler();
    }

    @Provides
    @Named("restContext")
    @Singleton
    protected String provideDefaultRestContext() {
        return "/api";
    }

    @Provides
    @Named("workspaceId")
    @Singleton
    protected String provideWorkspaceId() {
        return Config.getWorkspaceId();
    }

    @Provides
    @Named("websocketUrl")
    @Singleton
    protected String provideDefaultWebsocketUrl() {
        boolean isSecureConnection = Window.Location.getProtocol().equals("https:");
        return (isSecureConnection ? "wss://" : "ws://") + Window.Location.getHost() + "/api/ws/" + Config.getWorkspaceId();
    }
}