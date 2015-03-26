/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.core.inject;

import org.eclipse.che.api.account.gwt.client.AccountServiceClient;
import org.eclipse.che.api.account.gwt.client.AccountServiceClientImpl;
import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.builder.gwt.client.BuilderServiceClient;
import org.eclipse.che.api.builder.gwt.client.BuilderServiceClientImpl;
import org.eclipse.che.api.factory.gwt.client.FactoryServiceClient;
import org.eclipse.che.api.factory.gwt.client.FactoryServiceClientImpl;
import org.eclipse.che.api.project.gwt.client.ProjectImportersServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectImportersServiceClientImpl;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClientImpl;
import org.eclipse.che.api.project.gwt.client.ProjectTemplateServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectTemplateServiceClientImpl;
import org.eclipse.che.api.project.gwt.client.ProjectTypeServiceClient;
import org.eclipse.che.api.project.gwt.client.ProjectTypeServiceClientImpl;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClient;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClientImpl;
import org.eclipse.che.api.user.gwt.client.UserProfileServiceClient;
import org.eclipse.che.api.user.gwt.client.UserProfileServiceClientImpl;
import org.eclipse.che.api.user.gwt.client.UserServiceClient;
import org.eclipse.che.api.user.gwt.client.UserServiceClientImpl;
import org.eclipse.che.api.vfs.gwt.client.VfsServiceClient;
import org.eclipse.che.api.vfs.gwt.client.VfsServiceClientImpl;
import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClient;
import org.eclipse.che.api.workspace.gwt.client.WorkspaceServiceClientImpl;

import org.eclipse.che.ide.about.AboutView;
import org.eclipse.che.ide.actions.find.FindActionViewImpl;
import org.eclipse.che.ide.core.editor.EditorAgentImpl;
import org.eclipse.che.ide.extension.ExtensionManagerViewImpl;
import org.eclipse.che.ide.extension.ExtensionRegistry;
import org.eclipse.che.ide.filetypes.FileTypeRegistryImpl;
import org.eclipse.che.ide.logger.AnalyticsEventLoggerExt;
import org.eclipse.che.ide.menu.MainMenuView;
import org.eclipse.che.ide.menu.MainMenuViewImpl;
import org.eclipse.che.ide.menu.StatusPanelGroupView;
import org.eclipse.che.ide.menu.StatusPanelGroupViewImpl;
import org.eclipse.che.ide.navigation.NavigateToFileView;
import org.eclipse.che.ide.notification.NotificationManagerView;
import org.eclipse.che.ide.notification.NotificationManagerViewImpl;
import org.eclipse.che.ide.openproject.OpenProjectView;
import org.eclipse.che.ide.openproject.OpenProjectViewImpl;
import org.eclipse.che.ide.outline.OutlinePartPresenter;
import org.eclipse.che.ide.part.PartStackViewImpl;
import org.eclipse.che.ide.part.console.ConsolePartPresenter;
import org.eclipse.che.ide.part.editor.EditorPartStackView;
import org.eclipse.che.ide.part.projectexplorer.ProjectExplorerView;
import org.eclipse.che.ide.part.projectexplorer.ProjectExplorerViewImpl;
import org.eclipse.che.ide.preferences.PreferencesManagerImpl;
import org.eclipse.che.ide.preferences.PreferencesViewImpl;
import org.eclipse.che.ide.projectimport.wizard.ImportWizardRegistryImpl;
import org.eclipse.che.ide.projecttype.BlankProjectWizardRegistrar;
import org.eclipse.che.ide.projecttype.wizard.ProjectWizardRegistryImpl;
import org.eclipse.che.ide.theme.AppearanceViewImpl;
import org.eclipse.che.ide.theme.ThemeAgentImpl;
import org.eclipse.che.ide.toolbar.ToolbarPresenter;
import org.eclipse.che.ide.toolbar.ToolbarView;
import org.eclipse.che.ide.toolbar.ToolbarViewImpl;
import org.eclipse.che.ide.upload.file.UploadFileView;
import org.eclipse.che.ide.upload.file.UploadFileViewImpl;
import org.eclipse.che.ide.upload.folder.UploadFolderFromZipView;
import org.eclipse.che.ide.upload.folder.UploadFolderFromZipViewImpl;
import org.eclipse.che.ide.workspace.WorkspacePresenter;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.about.AboutViewImpl;
import org.eclipse.che.ide.actions.ActionManagerImpl;
import org.eclipse.che.ide.actions.find.FindActionView;

import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.build.BuildContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorRegistry;
import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.filetypes.FileType;
import org.eclipse.che.ide.api.filetypes.FileTypeRegistry;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.api.keybinding.KeyBindingAgent;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.ConsolePart;
import org.eclipse.che.ide.api.parts.EditorPartStack;
import org.eclipse.che.ide.api.parts.OutlinePart;
import org.eclipse.che.ide.api.parts.PartStack;
import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.PartStackView;
import org.eclipse.che.ide.api.parts.ProjectExplorerPart;
import org.eclipse.che.ide.api.parts.WorkBenchView;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.api.project.wizard.ImportWizardRegistrar;
import org.eclipse.che.ide.api.project.wizard.ImportWizardRegistry;
import org.eclipse.che.ide.api.project.tree.TreeStructureProviderRegistry;
import org.eclipse.che.ide.api.project.tree.generic.NodeFactory;
import org.eclipse.che.ide.api.project.type.ProjectTemplateRegistry;
import org.eclipse.che.ide.api.project.type.ProjectTypeRegistry;
import org.eclipse.che.ide.api.project.type.wizard.PreSelectedProjectTypeManager;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistrar;
import org.eclipse.che.ide.api.project.type.wizard.ProjectWizardRegistry;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.ide.api.theme.Theme;
import org.eclipse.che.ide.api.theme.ThemeAgent;
import org.eclipse.che.ide.build.BuildContextImpl;
import org.eclipse.che.ide.core.StandardComponentInitializer;
import org.eclipse.che.ide.core.editor.EditorRegistryImpl;
import org.eclipse.che.ide.extension.ExtensionManagerPresenter;
import org.eclipse.che.ide.extension.ExtensionManagerView;
import org.eclipse.che.ide.icon.IconRegistryImpl;
import org.eclipse.che.ide.keybinding.KeyBindingManager;
import org.eclipse.che.ide.logger.AnalyticsEventLoggerImpl;
import org.eclipse.che.ide.navigation.NavigateToFileViewImpl;
import org.eclipse.che.ide.notification.NotificationManagerImpl;
import org.eclipse.che.ide.outline.OutlinePartView;
import org.eclipse.che.ide.outline.OutlinePartViewImpl;
import org.eclipse.che.ide.part.FocusManager;
import org.eclipse.che.ide.part.PartStackPresenter;
import org.eclipse.che.ide.part.console.ConsolePartView;
import org.eclipse.che.ide.part.console.ConsolePartViewImpl;
import org.eclipse.che.ide.part.editor.EditorPartStackPresenter;
import org.eclipse.che.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import org.eclipse.che.ide.preferences.PreferencesView;
import org.eclipse.che.ide.privacy.PrivacyPresenter;
import org.eclipse.che.ide.projectimport.wizard.ImportProjectNotificationSubscriberImpl;
import org.eclipse.che.ide.projectimport.wizard.ImportWizardFactory;
import org.eclipse.che.ide.projectimport.zip.ZipImportWizardRegistrar;
import org.eclipse.che.ide.projecttree.TreeStructureProviderRegistryImpl;
import org.eclipse.che.ide.projecttype.ProjectTemplateRegistryImpl;
import org.eclipse.che.ide.projecttype.ProjectTypeRegistryImpl;
import org.eclipse.che.ide.projecttype.wizard.PreSelectedProjectTypeManagerImpl;
import org.eclipse.che.ide.projecttype.wizard.ProjectWizardFactory;

import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.selection.SelectionAgentImpl;
import org.eclipse.che.ide.texteditor.openedfiles.ListOpenedFilesView;
import org.eclipse.che.ide.texteditor.openedfiles.ListOpenedFilesViewImpl;
import org.eclipse.che.ide.theme.AppearancePresenter;
import org.eclipse.che.ide.theme.AppearanceView;
import org.eclipse.che.ide.theme.DarkTheme;
import org.eclipse.che.ide.toolbar.MainToolbar;
import org.eclipse.che.ide.toolbar.ToolbarMainPresenter;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialog;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogFooter;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogView;
import org.eclipse.che.ide.ui.dialogs.choice.ChoiceDialogViewImpl;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialog;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogFooter;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogView;
import org.eclipse.che.ide.ui.dialogs.confirm.ConfirmDialogViewImpl;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogFooter;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogView;
import org.eclipse.che.ide.ui.dialogs.input.InputDialogViewImpl;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialog;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogFooter;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogPresenter;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogView;
import org.eclipse.che.ide.ui.dialogs.message.MessageDialogViewImpl;
import org.eclipse.che.ide.ui.loader.IdeLoader;
import org.eclipse.che.ide.ui.zeroClipboard.ClipboardButtonBuilder;
import org.eclipse.che.ide.ui.zeroClipboard.ClipboardButtonBuilderImpl;
import org.eclipse.che.ide.util.Config;
import org.eclipse.che.ide.util.executor.UserActivityManager;
import org.eclipse.che.ide.websocket.MessageBus;
import org.eclipse.che.ide.websocket.MessageBusImpl;
import org.eclipse.che.ide.workspace.PartStackPresenterFactory;
import org.eclipse.che.ide.workspace.PartStackViewFactory;
import org.eclipse.che.ide.workspace.WorkBenchViewImpl;
import org.eclipse.che.ide.workspace.WorkspaceView;
import org.eclipse.che.ide.workspace.WorkspaceViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.gwt.user.client.Window;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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
        bind(AsyncRequestLoader.class).to(IdeLoader.class).in(Singleton.class);
        bind(Resources.class).in(Singleton.class);
        bind(ExtensionRegistry.class).in(Singleton.class);
        bind(StandardComponentInitializer.class).in(Singleton.class);
        bind(BuildContext.class).to(BuildContextImpl.class).in(Singleton.class);
        bind(ClipboardButtonBuilder.class).to(ClipboardButtonBuilderImpl.class);

        install(new GinFactoryModuleBuilder().implement(PartStackView.class, PartStackViewImpl.class).build(PartStackViewFactory.class));
        install(new GinFactoryModuleBuilder().implement(PartStack.class, PartStackPresenter.class).build(PartStackPresenterFactory.class));

        bind(PreferencesManager.class).to(PreferencesManagerImpl.class).in(Singleton.class);
        bind(NotificationManager.class).to(NotificationManagerImpl.class).in(Singleton.class);
        bind(ThemeAgent.class).to(ThemeAgentImpl.class).in(Singleton.class);
        bind(MessageBus.class).to(MessageBusImpl.class).in(Singleton.class);
        bind(FileTypeRegistry.class).to(FileTypeRegistryImpl.class).in(Singleton.class);

        bind(AnalyticsEventLogger.class).to(AnalyticsEventLoggerImpl.class).in(Singleton.class);
        bind(AnalyticsEventLoggerExt.class).to(AnalyticsEventLoggerImpl.class).in(Singleton.class);

        configureProjectWizard();
        configureImportWizard();
        configurePlatformApiGwtClients();
        configureApiBinding();
        configureCoreUI();
        configureEditorAPI();
        configureProjectTree();
    }

    private void configureProjectWizard() {
        GinMultibinder.newSetBinder(binder(), ProjectWizardRegistrar.class).addBinding().to(BlankProjectWizardRegistrar.class);
        bind(ProjectWizardRegistry.class).to(ProjectWizardRegistryImpl.class).in(Singleton.class);
        install(new GinFactoryModuleBuilder().build(ProjectWizardFactory.class));
        bind(PreSelectedProjectTypeManager.class).to(PreSelectedProjectTypeManagerImpl.class).in(Singleton.class);
    }

    private void configureImportWizard() {
        GinMultibinder.newSetBinder(binder(), ImportWizardRegistrar.class).addBinding().to(ZipImportWizardRegistrar.class);
        bind(ImportWizardRegistry.class).to(ImportWizardRegistryImpl.class).in(Singleton.class);
        install(new GinFactoryModuleBuilder().build(ImportWizardFactory.class));
        bind(ImportProjectNotificationSubscriber.class).to(ImportProjectNotificationSubscriberImpl.class);
    }

    /** Configure GWT-clients for Codenvy Platform API services */
    private void configurePlatformApiGwtClients() {
        bind(UserServiceClient.class).to(UserServiceClientImpl.class).in(Singleton.class);
        bind(UserProfileServiceClient.class).to(UserProfileServiceClientImpl.class).in(Singleton.class);
        bind(AccountServiceClient.class).to(AccountServiceClientImpl.class).in(Singleton.class);
        bind(FactoryServiceClient.class).to(FactoryServiceClientImpl.class).in(Singleton.class);
        bind(WorkspaceServiceClient.class).to(WorkspaceServiceClientImpl.class).in(Singleton.class);
        bind(VfsServiceClient.class).to(VfsServiceClientImpl.class).in(Singleton.class);
        bind(ProjectServiceClient.class).to(ProjectServiceClientImpl.class).in(Singleton.class);
        bind(ProjectImportersServiceClient.class).to(ProjectImportersServiceClientImpl.class).in(Singleton.class);
        bind(ProjectTypeServiceClient.class).to(ProjectTypeServiceClientImpl.class).in(Singleton.class);
        bind(ProjectTemplateServiceClient.class).to(ProjectTemplateServiceClientImpl.class).in(Singleton.class);
        bind(BuilderServiceClient.class).to(BuilderServiceClientImpl.class).in(Singleton.class);
        bind(RunnerServiceClient.class).to(RunnerServiceClientImpl.class).in(Singleton.class);

        bind(ProjectTypeRegistry.class).to(ProjectTypeRegistryImpl.class).in(Singleton.class);
        bind(ProjectTemplateRegistry.class).to(ProjectTemplateRegistryImpl.class).in(Singleton.class);
    }

    /** API Bindings, binds API interfaces to the implementations */
    private void configureApiBinding() {
        // Agents
        bind(KeyBindingAgent.class).to(KeyBindingManager.class).in(Singleton.class);
        bind(SelectionAgent.class).to(SelectionAgentImpl.class).in(Singleton.class);
        bind(WorkspaceAgent.class).to(WorkspacePresenter.class).in(Singleton.class);
        bind(IconRegistry.class).to(IconRegistryImpl.class).in(Singleton.class);
        // UI Model
        bind(EditorPartStack.class).to(EditorPartStackPresenter.class).in(Singleton.class);
        // Parts
        bind(ConsolePart.class).to(ConsolePartPresenter.class).in(Singleton.class);
        bind(OutlinePart.class).to(OutlinePartPresenter.class).in(Singleton.class);
        bind(ProjectExplorerPart.class).to(ProjectExplorerPartPresenter.class).in(Singleton.class);
        bind(ActionManager.class).to(ActionManagerImpl.class).in(Singleton.class);
    }

    /** Configure Core UI components, resources and views */
    protected void configureCoreUI() {
        GinMultibinder<PreferencePagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencePagePresenter.class);
        prefBinder.addBinding().to(AppearancePresenter.class);
        prefBinder.addBinding().to(ExtensionManagerPresenter.class);

        GinMultibinder<Theme> themeBinder = GinMultibinder.newSetBinder(binder(), Theme.class);
        themeBinder.addBinding().to(DarkTheme.class);

        // Resources
        bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);
        // Views
        bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
        bind(WorkBenchView.class).to(WorkBenchViewImpl.class).in(Singleton.class);
        bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);
        bind(StatusPanelGroupView.class).to(StatusPanelGroupViewImpl.class).in(Singleton.class);

        bind(ToolbarView.class).to(ToolbarViewImpl.class);
        bind(ToolbarPresenter.class).annotatedWith(MainToolbar.class).to(ToolbarMainPresenter.class).in(Singleton.class);

        bind(NotificationManagerView.class).to(NotificationManagerViewImpl.class).in(Singleton.class);

        bind(EditorPartStackView.class);
        bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);
        bind(ConsolePartView.class).to(ConsolePartViewImpl.class).in(Singleton.class);

        bind(MessageDialogFooter.class);
        bind(MessageDialogView.class).to(MessageDialogViewImpl.class);
        bind(ConfirmDialogFooter.class);
        bind(ConfirmDialogView.class).to(ConfirmDialogViewImpl.class);
        bind(ChoiceDialogFooter.class);
        bind(ChoiceDialogView.class).to(ChoiceDialogViewImpl.class);
        bind(InputDialogFooter.class);
        bind(InputDialogView.class).to(InputDialogViewImpl.class);
        install(new GinFactoryModuleBuilder().implement(MessageDialog.class, MessageDialogPresenter.class)
                                             .implement(ConfirmDialog.class, ConfirmDialogPresenter.class)
                                             .implement(InputDialog.class, InputDialogPresenter.class)
                                             .implement(ChoiceDialog.class, ChoiceDialogPresenter.class)
                                             .build(DialogFactory.class));

        bind(OpenProjectView.class).to(OpenProjectViewImpl.class);
        bind(UploadFileView.class).to(UploadFileViewImpl.class);
        bind(UploadFolderFromZipView.class).to(UploadFolderFromZipViewImpl.class);
        bind(PreferencesView.class).to(PreferencesViewImpl.class).in(Singleton.class);
        bind(NavigateToFileView.class).to(NavigateToFileViewImpl.class).in(Singleton.class);
        bind(AboutView.class).to(AboutViewImpl.class);
        bind(ListOpenedFilesView.class).to(ListOpenedFilesViewImpl.class);

        bind(ExtensionManagerView.class).to(ExtensionManagerViewImpl.class).in(Singleton.class);
        bind(AppearanceView.class).to(AppearanceViewImpl.class).in(Singleton.class);
        bind(FindActionView.class).to(FindActionViewImpl.class).in(Singleton.class);

        bind(PrivacyPresenter.class).asEagerSingleton();
    }

    /** Configures binding for Editor API */
    protected void configureEditorAPI() {
        bind(EditorAgent.class).to(EditorAgentImpl.class).in(Singleton.class);

        bind(EditorRegistry.class).to(EditorRegistryImpl.class).in(Singleton.class);
        bind(UserActivityManager.class).in(Singleton.class);
        bind(OutlinePartView.class).to(OutlinePartViewImpl.class).in(Singleton.class);
    }

    /** Configure bindings for project's tree. */
    private void configureProjectTree() {
        bind(TreeStructureProviderRegistry.class).to(TreeStructureProviderRegistryImpl.class).in(Singleton.class);
        install(new GinFactoryModuleBuilder().build(NodeFactory.class));
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
    protected PartStackPresenter.PartStackEventHandler providePartStackEventHandler(FocusManager partAgentPresenter) {
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
