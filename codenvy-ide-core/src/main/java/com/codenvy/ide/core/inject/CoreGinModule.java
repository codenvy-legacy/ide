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

import com.codenvy.ide.Resources;
import com.codenvy.ide.actions.ActionManagerImpl;
import com.codenvy.ide.api.editor.*;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.parts.*;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView;
import com.codenvy.ide.api.ui.workspace.EditorPartStack;
import com.codenvy.ide.api.ui.workspace.PartStack;
import com.codenvy.ide.api.ui.workspace.PartStackView;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.contexmenu.ContextMenuView;
import com.codenvy.ide.contexmenu.ContextMenuViewImpl;
import com.codenvy.ide.core.StandardComponentInitializer;
import com.codenvy.ide.core.editor.DefaultEditorProvider;
import com.codenvy.ide.core.editor.EditorAgentImpl;
import com.codenvy.ide.core.editor.EditorRegistryImpl;
import com.codenvy.ide.core.editor.ResourceDocumentProvider;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.keybinding.KeyBindingManager;
import com.codenvy.ide.menu.MainMenuView;
import com.codenvy.ide.menu.MainMenuViewImpl;
import com.codenvy.ide.notification.NotificationManagerImpl;
import com.codenvy.ide.notification.NotificationManagerView;
import com.codenvy.ide.notification.NotificationManagerViewImpl;
import com.codenvy.ide.openproject.OpenProjectView;
import com.codenvy.ide.openproject.OpenProjectViewImpl;
import com.codenvy.ide.outline.OutlinePartPresenter;
import com.codenvy.ide.outline.OutlinePartView;
import com.codenvy.ide.outline.OutlinePartViewImpl;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.part.*;
import com.codenvy.ide.part.PartStackPresenter.PartStackEventHandler;
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
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.codenvy.ide.resources.ResourceProviderComponent;
import com.codenvy.ide.resources.model.GenericModelProvider;
import com.codenvy.ide.search.SearchPartPresenter;
import com.codenvy.ide.search.SearchPartView;
import com.codenvy.ide.search.SearchPartViewImpl;
import com.codenvy.ide.selection.SelectionAgentImpl;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.toolbar.ToolbarView;
import com.codenvy.ide.toolbar.ToolbarViewImpl;
import com.codenvy.ide.ui.loader.IdeLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.user.UserClientServiceImpl;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.MessageBusImpl;
import com.codenvy.ide.welcome.WelcomePartPresenter;
import com.codenvy.ide.welcome.WelcomePartView;
import com.codenvy.ide.welcome.WelcomePartViewImpl;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newfile.NewGenericFilePageViewImpl;
import com.codenvy.ide.wizard.newproject.NewProjectPageView;
import com.codenvy.ide.wizard.newproject.NewProjectPageViewImpl;
import com.codenvy.ide.wizard.newproject.ProjectTypeAgentImpl;
import com.codenvy.ide.wizard.newresource.NewResourcePageView;
import com.codenvy.ide.wizard.newresource.NewResourcePageViewImpl;
import com.codenvy.ide.wizard.template.TemplateAgentImpl;
import com.codenvy.ide.wizard.template.TemplatePageView;
import com.codenvy.ide.wizard.template.TemplatePageViewImpl;
import com.codenvy.ide.workspace.*;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.user.client.Window;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/** @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> */
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
        bind(UserClientService.class).to(UserClientServiceImpl.class).in(Singleton.class);
        bind(PreferencesManager.class).to(PreferencesManagerImpl.class).in(Singleton.class);
        bind(MessageBus.class).to(MessageBusImpl.class).in(Singleton.class);
        bind(NotificationManager.class).to(NotificationManagerImpl.class).in(Singleton.class);
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
        bind(WizardAgent.class).to(WizardAgentImpl.class).in(Singleton.class);
        bind(PaaSAgent.class).to(PaaSAgentImpl.class).in(Singleton.class);
        bind(TemplateAgent.class).to(TemplateAgentImpl.class).in(Singleton.class);
        bind(ProjectTypeAgent.class).to(ProjectTypeAgentImpl.class).in(Singleton.class);
        // UI Model
        bind(EditorPartStack.class).to(EditorPartStackPresenter.class).in(Singleton.class);
        // Parts
        bind(ConsolePart.class).to(ConsolePartPresenter.class).in(Singleton.class);
        bind(WelcomePart.class).to(WelcomePartPresenter.class).in(Singleton.class);
        bind(OutlinePart.class).to(OutlinePartPresenter.class).in(Singleton.class);
        bind(SearchPart.class).to(SearchPartPresenter.class).in(Singleton.class);
        bind(ProjectExplorerPart.class).to(ProjectExplorerPartPresenter.class).in(Singleton.class);
        bind(ActionManager.class).to(ActionManagerImpl.class).in(Singleton.class);

    }

    /** Configures binding for Editor API */
    protected void editorAPIconfigure() {
        bind(DocumentFactory.class).to(DocumentFactoryImpl.class).in(Singleton.class);
        bind(CodenvyTextEditor.class).to(TextEditorPresenter.class);
        bind(EditorAgent.class).to(EditorAgentImpl.class).in(Singleton.class);

        bind(EditorRegistry.class).to(EditorRegistryImpl.class).in(Singleton.class);
        bind(EditorProvider.class).annotatedWith(Names.named("defaulEditor")).to(DefaultEditorProvider.class);
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

    /** Configure Core UI components, resouces and views */
    protected void coreUiConfigure() {
        // Resources

        bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);
        // Views
        bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
        bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);
        bind(NewGenericFilePageView.class).to(NewGenericFilePageViewImpl.class).in(Singleton.class);
        bind(ToolbarView.class).to(ToolbarViewImpl.class).in(Singleton.class);
        bind(ContextMenuView.class).to(ContextMenuViewImpl.class).in(Singleton.class);
        bind(NotificationManagerView.class).to(NotificationManagerViewImpl.class).in(Singleton.class);
//        bind(PartStackView.class).to(PartStackViewImpl.class);
        bind(PartStackView.class).annotatedWith(Names.named("editorPartStack")).to(EditorPartStackView.class);
        bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);
        bind(ConsolePartView.class).to(ConsolePartViewImpl.class).in(Singleton.class);
        bind(SearchPartView.class).to(SearchPartViewImpl.class).in(Singleton.class);

        bind(TemplatePageView.class).to(TemplatePageViewImpl.class);
        bind(NewResourcePageView.class).to(NewResourcePageViewImpl.class);
        bind(NewProjectPageView.class).to(NewProjectPageViewImpl.class);
        bind(OpenProjectView.class).to(OpenProjectViewImpl.class);
        bind(PreferencesView.class).to(PreferencesViewImpl.class).in(Singleton.class);
        bind(WelcomePartView.class).to(WelcomePartViewImpl.class).in(Singleton.class);
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
        return "/ide/rest";
    }

    @Provides
    @Named("websocketUrl")
    @Singleton
    protected String provideDefaultWebsocketUrl() {
        boolean isSecureConnection = Window.Location.getProtocol().equals("https:");
        return (isSecureConnection ? "wss://" : "ws://") + Window.Location.getHost() + "/ide/websocket/" + Utils.getWorkspaceName();
    }
}