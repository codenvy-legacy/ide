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
import com.codenvy.api.workspace.gwt.client.WorkspaceServiceClient;
import com.codenvy.api.workspace.gwt.client.WorkspaceServiceClientImpl;
import com.codenvy.ide.Resources;
import com.codenvy.ide.about.AboutView;
import com.codenvy.ide.about.AboutViewImpl;
import com.codenvy.ide.actions.ActionManagerImpl;
import com.codenvy.ide.actions.delete.DeleteProvider;
import com.codenvy.ide.actions.delete.ItemReferenceDeleteProvider;
import com.codenvy.ide.actions.delete.ProjectDescriptorDeleteProvider;
import com.codenvy.ide.actions.delete.ProjectReferenceDeleteProvider;
import com.codenvy.ide.actions.find.FindActionView;
import com.codenvy.ide.actions.find.FindActionViewImpl;
import com.codenvy.ide.actions.rename.ItemReferenceRenameProvider;
import com.codenvy.ide.actions.rename.ProjectReferenceRenameProvider;
import com.codenvy.ide.actions.rename.RenameProvider;
import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.build.BuildContext;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.icon.IconRegistry;
import com.codenvy.ide.api.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.EditorPartStack;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.PartStack;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.PartStackView;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.preferences.PreferencesAgent;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.codenvy.ide.api.projecttree.TreeStructureProvider;
import com.codenvy.ide.api.projecttree.TreeStructureProviderRegistry;
import com.codenvy.ide.api.projecttree.generic.GenericTreeStructureProvider;
import com.codenvy.ide.api.projecttype.ProjectTypeDescriptorRegistry;
import com.codenvy.ide.api.projecttype.wizard.ProjectTypeWizardRegistry;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.text.DocumentFactory;
import com.codenvy.ide.api.theme.Theme;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.api.wizard.DefaultWizardFactory;
import com.codenvy.ide.api.wizard.WizardDialog;
import com.codenvy.ide.api.wizard.WizardDialogFactory;
import com.codenvy.ide.build.BuildContextImpl;
import com.codenvy.ide.contexmenu.ContextMenuView;
import com.codenvy.ide.contexmenu.ContextMenuViewImpl;
import com.codenvy.ide.core.IconRegistryImpl;
import com.codenvy.ide.core.ProjectStateHandler;
import com.codenvy.ide.core.ProjectTypeDescriptorRegistryImpl;
import com.codenvy.ide.core.StandardComponentInitializer;
import com.codenvy.ide.core.editor.EditorAgentImpl;
import com.codenvy.ide.core.editor.EditorRegistryImpl;
import com.codenvy.ide.core.editor.ResourceDocumentProvider;
import com.codenvy.ide.core.projecttree.TreeStructureProviderRegistryImpl;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.ExtensionManagerPresenter;
import com.codenvy.ide.extension.ExtensionManagerView;
import com.codenvy.ide.extension.ExtensionManagerViewImpl;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.filetypes.FileTypeRegistryImpl;
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
import com.codenvy.ide.part.FocusManager;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.part.PartStackPresenter.PartStackEventHandler;
import com.codenvy.ide.part.PartStackViewImpl;
import com.codenvy.ide.part.console.ConsolePartPresenter;
import com.codenvy.ide.part.console.ConsolePartView;
import com.codenvy.ide.part.console.ConsolePartViewImpl;
import com.codenvy.ide.part.editor.EditorPartStackPresenter;
import com.codenvy.ide.part.editor.EditorPartStackView;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerViewImpl;
import com.codenvy.ide.preferences.PreferencesAgentImpl;
import com.codenvy.ide.preferences.PreferencesManagerImpl;
import com.codenvy.ide.preferences.PreferencesView;
import com.codenvy.ide.preferences.PreferencesViewImpl;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.selection.SelectionAgentImpl;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesView;
import com.codenvy.ide.texteditor.openedfiles.ListOpenedFilesViewImpl;
import com.codenvy.ide.theme.AppearancePresenter;
import com.codenvy.ide.theme.AppearanceView;
import com.codenvy.ide.theme.AppearanceViewImpl;
import com.codenvy.ide.theme.DarkTheme;
import com.codenvy.ide.theme.ThemeAgentImpl;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarMainPresenter;
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
import com.codenvy.ide.wizard.WizardDialogPresenter;
import com.codenvy.ide.wizard.WizardDialogView;
import com.codenvy.ide.wizard.WizardDialogViewImpl;
import com.codenvy.ide.wizard.project.ProjectTypeWizardRegistryImpl;
import com.codenvy.ide.workspace.PartStackPresenterFactory;
import com.codenvy.ide.workspace.PartStackViewFactory;
import com.codenvy.ide.workspace.WorkspacePresenter;
import com.codenvy.ide.workspace.WorkspaceView;
import com.codenvy.ide.workspace.WorkspaceViewImpl;
import com.google.gwt.core.client.GWT;
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
        bind(AppContext.class).in(Singleton.class);
        bind(ProjectStateHandler.class).asEagerSingleton();
        bind(BuildContext.class).to(BuildContextImpl.class).in(Singleton.class);

        install(new GinFactoryModuleBuilder().implement(PartStackView.class, PartStackViewImpl.class).build(PartStackViewFactory.class));
        install(new GinFactoryModuleBuilder().implement(PartStack.class, PartStackPresenter.class).build(PartStackPresenterFactory.class));

        bind(PreferencesManager.class).to(PreferencesManagerImpl.class).in(Singleton.class);
        bind(NotificationManager.class).to(NotificationManagerImpl.class).in(Singleton.class);
        bind(ThemeAgent.class).to(ThemeAgentImpl.class).in(Singleton.class);
        bind(DtoFactory.class).in(Singleton.class);
        bind(DtoUnmarshallerFactory.class).in(Singleton.class);
        bind(AsyncRequestFactory.class).in(Singleton.class);
        bind(MessageBus.class).to(MessageBusImpl.class).in(Singleton.class);
        bind(FileTypeRegistry.class).to(FileTypeRegistryImpl.class).in(Singleton.class);

        bind(AnalyticsEventLogger.class).to(AnalyticsEventLoggerImpl.class).in(Singleton.class);
        bind(AnalyticsEventLoggerExt.class).to(AnalyticsEventLoggerImpl.class).in(Singleton.class);

        // GWT-clients for Codenvy API services
        bind(UserServiceClient.class).to(UserServiceClientImpl.class).in(Singleton.class);
        bind(WorkspaceServiceClient.class).to(WorkspaceServiceClientImpl.class).in(Singleton.class);
        bind(UserProfileServiceClient.class).to(UserProfileServiceClientImpl.class).in(Singleton.class);
        bind(ProjectServiceClient.class).to(ProjectServiceClientImpl.class).in(Singleton.class);
        bind(ProjectImportersServiceClient.class).to(ProjectImportersServiceClientImpl.class).in(Singleton.class);
        bind(ProjectTypeDescriptionServiceClient.class).to(ProjectTypeDescriptionServiceClientImpl.class).in(Singleton.class);

        bind(TreeStructureProviderRegistry.class).to(TreeStructureProviderRegistryImpl.class).in(Singleton.class);
        bind(TreeStructureProvider.class).to(GenericTreeStructureProvider.class).in(Singleton.class);

        bind(ProjectTypeWizardRegistry.class).to(ProjectTypeWizardRegistryImpl.class).in(Singleton.class);

        apiBindingConfigure();
        coreUiConfigure();
        editorAPIconfigure();
        configureDeleteProviders();
        configureRenameProviders();
    }

    /** API Bindings, binds API interfaces to the implementations */
    private void apiBindingConfigure() {
        // Agents
        bind(KeyBindingAgent.class).to(KeyBindingManager.class).in(Singleton.class);
        bind(SelectionAgent.class).to(SelectionAgentImpl.class).in(Singleton.class);
        bind(WorkspaceAgent.class).to(WorkspacePresenter.class).in(Singleton.class);
        bind(PreferencesAgent.class).to(PreferencesAgentImpl.class).in(Singleton.class);
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

    /** Configure Core UI components, resources and views */
    protected void coreUiConfigure() {
        GinMultibinder<PreferencesPagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencesPagePresenter.class);
        prefBinder.addBinding().to(AppearancePresenter.class);
        prefBinder.addBinding().to(ExtensionManagerPresenter.class);

        GinMultibinder<Theme> themeBinder = GinMultibinder.newSetBinder(binder(), Theme.class);
        themeBinder.addBinding().to(DarkTheme.class);
//        themeBinder.addBinding().to(LightTheme.class);

        // Resources
        bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);
        // Views
        bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
        bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);

        bind(ToolbarView.class).to(ToolbarViewImpl.class);
        bind(ToolbarPresenter.class).annotatedWith(MainToolbar.class).to(ToolbarMainPresenter.class).in(Singleton.class);

        bind(ContextMenuView.class).to(ContextMenuViewImpl.class).in(Singleton.class);
        bind(NotificationManagerView.class).to(NotificationManagerViewImpl.class).in(Singleton.class);
//        bind(PartStackView.class).to(PartStackViewImpl.class);
        bind(PartStackView.class).annotatedWith(Names.named("editorPartStack")).to(EditorPartStackView.class);
        bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);
        bind(ConsolePartView.class).to(ConsolePartViewImpl.class).in(Singleton.class);

        bind(OpenProjectView.class).to(OpenProjectViewImpl.class);
        bind(ImportProjectView.class).to(ImportProjectViewImpl.class);
        bind(UploadFileView.class).to(UploadFileViewImpl.class);
        bind(PreferencesView.class).to(PreferencesViewImpl.class).in(Singleton.class);
        bind(NavigateToFileView.class).to(NavigateToFileViewImpl.class).in(Singleton.class);
        bind(AboutView.class).to(AboutViewImpl.class);
        bind(ListOpenedFilesView.class).to(ListOpenedFilesViewImpl.class);

        bind(ExtensionManagerView.class).to(ExtensionManagerViewImpl.class).in(Singleton.class);
        bind(AppearanceView.class).to(AppearanceViewImpl.class).in(Singleton.class);
        bind(FindActionView.class).to(FindActionViewImpl.class).in(Singleton.class);
    }

    /** Configures binding for Editor API */
    protected void editorAPIconfigure() {
        bind(DocumentFactory.class).to(DocumentFactoryImpl.class).in(Singleton.class);
        bind(CodenvyTextEditor.class).to(TextEditorPresenter.class);
        bind(EditorAgent.class).to(EditorAgentImpl.class).in(Singleton.class);

        bind(EditorRegistry.class).to(EditorRegistryImpl.class).in(Singleton.class);
        bind(DocumentProvider.class).to(ResourceDocumentProvider.class).in(Singleton.class);
        bind(UserActivityManager.class).in(Singleton.class);
        bind(OutlinePartView.class).to(OutlinePartViewImpl.class).in(Singleton.class);
    }

    /** Configure Delete Providers. */
    private void configureDeleteProviders() {
        GinMultibinder<DeleteProvider> multiBinderDeleteProviders = GinMultibinder.newSetBinder(binder(), DeleteProvider.class);
        multiBinderDeleteProviders.addBinding().to(ItemReferenceDeleteProvider.class);
        multiBinderDeleteProviders.addBinding().to(ProjectReferenceDeleteProvider.class);
        multiBinderDeleteProviders.addBinding().to(ProjectDescriptorDeleteProvider.class);
    }

    /** Configure Rename Providers. */
    private void configureRenameProviders() {
        GinMultibinder<RenameProvider> multiBinderRenameProviders = GinMultibinder.newSetBinder(binder(), RenameProvider.class);
        multiBinderRenameProviders.addBinding().to(ItemReferenceRenameProvider.class);
        multiBinderRenameProviders.addBinding().to(ProjectReferenceRenameProvider.class);
    }

    @Provides
    @Named("defaultFileType")
    @Singleton
    protected FileType provideDefaultFileType() {
        Resources res = GWT.create(Resources.class);
        return new FileType(res.defaultFile(), null);
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