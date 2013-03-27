/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.core.inject;

import com.codenvy.ide.extension.ExtensionRegistry;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.editor.EditorRegistry;

import com.codenvy.ide.api.paas.PaaSAgent;


import com.codenvy.ide.api.expressions.ExpressionManager;

import com.codenvy.ide.api.extension.ExtensionGinModule;

import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ModelProvider;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.parts.OutlinePart;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.menu.MainMenuAgent;
import com.codenvy.ide.api.ui.menu.ToolbarAgent;
import com.codenvy.ide.api.ui.perspective.EditorPartStack;
import com.codenvy.ide.api.ui.perspective.PartStack;
import com.codenvy.ide.api.ui.perspective.WorkspaceAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.api.ui.wizard.newfile.NewGenericFilePageView;
import com.codenvy.ide.core.StandardComponentInitializer;
import com.codenvy.ide.core.editor.DefaultEditorProvider;
import com.codenvy.ide.core.editor.EditorAgentImpl;
import com.codenvy.ide.core.editor.EditorRegistryImpl;
import com.codenvy.ide.core.editor.ResourceDocumentProvider;
import com.codenvy.ide.core.expressions.ExpressionManagerImpl;
import com.codenvy.ide.keybinding.KeyBindingManager;
import com.codenvy.ide.loader.EmptyLoader;
import com.codenvy.ide.loader.Loader;
import com.codenvy.ide.menu.MainMenuPresenter;
import com.codenvy.ide.menu.MainMenuView;
import com.codenvy.ide.menu.MainMenuViewImpl;
import com.codenvy.ide.outline.OutlinePartPrenter;
import com.codenvy.ide.outline.OutlinePartViewImpl;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.part.EditorPartStackPresenter;
import com.codenvy.ide.part.FocusManager;
import com.codenvy.ide.part.PartStackPresenter;
import com.codenvy.ide.part.PartStackPresenter.PartStackEventHandler;
import com.codenvy.ide.part.PartStackUIResources;
import com.codenvy.ide.part.PartStackView;
import com.codenvy.ide.part.PartStackViewImpl;
import com.codenvy.ide.part.console.ConsolePartPresenter;
import com.codenvy.ide.part.console.ConsolePartView;
import com.codenvy.ide.part.console.ConsolePartViewImpl;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerPartPresenter;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerView;
import com.codenvy.ide.part.projectexplorer.ProjectExplorerViewImpl;
import com.codenvy.ide.perspective.WorkspacePresenter;
import com.codenvy.ide.perspective.WorkspaceView;
import com.codenvy.ide.perspective.WorkspaceViewImpl;
import com.codenvy.ide.preferences.PreferencesAgentImpl;
import com.codenvy.ide.resources.ResourceProviderComponent;
import com.codenvy.ide.resources.model.GenericModelProvider;
import com.codenvy.ide.selection.SelectionAgentImpl;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.DocumentFactoryImpl;
import com.codenvy.ide.texteditor.TextEditorPresenter;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.toolbar.ToolbarView;
import com.codenvy.ide.toolbar.ToolbarViewImpl;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.codenvy.ide.welcome.WelcomePartPresenter;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newfile.NewGenericFilePageViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@ExtensionGinModule
public class CoreGinModule extends AbstractGinModule
{

   /**
    * {@inheritDoc}
    */
   @Override
   protected void configure()
   {
      bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
      bind(Loader.class).to(EmptyLoader.class).in(Singleton.class);
      bind(Resources.class).in(Singleton.class);
      bind(ExtensionRegistry.class).in(Singleton.class);
      bind(StandardComponentInitializer.class).in(Singleton.class);
      bind(PaaSAgent.class).to(PaaSAgentImpl.class).in(Singleton.class);

      bind(SelectionAgent.class).to(SelectionAgentImpl.class).in(Singleton.class);

      resourcesAPIconfigure();

      uiModelConfigure();
      uiAPIconfigure();

      editorAPIconfigure();
   }

   /**
    * 
    */
   private void uiModelConfigure()
   {
      bind(PartStack.class).to(PartStackPresenter.class);
      bind(EditorPartStack.class).to(EditorPartStackPresenter.class).in(Singleton.class);

      bind(ConsolePart.class).to(ConsolePartPresenter.class).in(Singleton.class);
      bind(WelcomePart.class).to(WelcomePartPresenter.class).in(Singleton.class);
      bind(OutlinePart.class).to(OutlinePartPrenter.class).in(Singleton.class);
      bind(ProjectExplorerPart.class).to(ProjectExplorerPartPresenter.class).in(Singleton.class);

   }

   /**
    * Configures binding for Editor API
    */
   protected void editorAPIconfigure()
   {

      bind(DocumentFactory.class).to(DocumentFactoryImpl.class).in(Singleton.class);
      bind(CodenvyTextEditor.class).to(TextEditorPresenter.class);
      bind(EditorAgent.class).to(EditorAgentImpl.class).in(Singleton.class);

      bind(EditorRegistry.class).to(EditorRegistryImpl.class).in(Singleton.class);
      bind(EditorProvider.class).annotatedWith(Names.named("defaulEditor")).to(DefaultEditorProvider.class);
      bind(DocumentProvider.class).to(ResourceDocumentProvider.class).in(Singleton.class);
      bind(UserActivityManager.class).in(Singleton.class);
      bind(OutlinePartPrenter.OutlinePartView.class).to(OutlinePartViewImpl.class).in(Singleton.class);
   }

   /**
    * Configures binding for Resource API (Resource Manager) 
    */
   protected void resourcesAPIconfigure()
   {
      bind(ResourceProvider.class).to(ResourceProviderComponent.class).in(Singleton.class);
      // Generic Model Provider
      bind(ModelProvider.class).to(GenericModelProvider.class).in(Singleton.class);
   }

   protected void uiAPIconfigure()
   {
      bind(WizardAgent.class).to(WizardAgentImpl.class).in(Singleton.class);
      bind(NewGenericFilePageView.class).to(NewGenericFilePageViewImpl.class).in(Singleton.class);

      bind(PreferencesAgent.class).to(PreferencesAgentImpl.class).in(Singleton.class);

      bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
      bind(WorkspaceAgent.class).to(WorkspacePresenter.class).in(Singleton.class);

      // expression manager
      bind(ExpressionManager.class).to(ExpressionManagerImpl.class).in(Singleton.class);

      // main menu
      bind(MainMenuPresenter.class).in(Singleton.class);
      bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);
      bind(MainMenuAgent.class).to(MainMenuPresenter.class).in(Singleton.class);

      // toolbar
      bind(ToolbarView.class).to(ToolbarViewImpl.class).in(Singleton.class);
      bind(ToolbarAgent.class).to(ToolbarPresenter.class).in(Singleton.class);

      // part agent
      bind(PartStackView.class).to(PartStackViewImpl.class);

      // resources: images and css
      bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);

      // key binding
      bind(KeyBindingAgent.class).to(KeyBindingManager.class).in(Singleton.class);

      bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);

      bind(ConsolePartView.class).to(ConsolePartViewImpl.class).in(Singleton.class);
   }

   @Provides
   @Named("defaultFileType")
   @Singleton
   protected FileType provideDefaultFileType()
   {
      //TODO add icon for unknown file 
      return new FileType(null, null);
   }

   @Provides
   @Singleton
   protected PartStackEventHandler providePartStackEventHandler(FocusManager partAgentPresenter)
   {
      return partAgentPresenter.getPartStackHandler();
   }
}
