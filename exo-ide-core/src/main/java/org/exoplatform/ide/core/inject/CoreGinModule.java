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
package org.exoplatform.ide.core.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.keybinding.KeyBindingAgent;
import org.exoplatform.ide.api.ui.menu.MainMenuAgent;
import org.exoplatform.ide.api.ui.paas.PaaSAgent;
import org.exoplatform.ide.api.ui.preferences.PreferencesAgent;
import org.exoplatform.ide.api.ui.toolbar.ToolbarAgent;
import org.exoplatform.ide.api.ui.wizard.WizardAgent;
import org.exoplatform.ide.api.ui.workspace.WorkspaceAgent;
import org.exoplatform.ide.core.StandardComponentInitializer;
import org.exoplatform.ide.core.editor.DefaultEditorProvider;
import org.exoplatform.ide.core.editor.EditorRegistry;
import org.exoplatform.ide.core.editor.ResourceDocumentProvider;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.editor.DocumentProvider;
import org.exoplatform.ide.editor.EditorProvider;
import org.exoplatform.ide.extension.ExtensionRegistry;
import org.exoplatform.ide.keybinding.KeyBindingManager;
import org.exoplatform.ide.loader.EmptyLoader;
import org.exoplatform.ide.loader.Loader;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.menu.MainMenuView;
import org.exoplatform.ide.menu.MainMenuViewImpl;
import org.exoplatform.ide.outline.OutlinePartPrenter;
import org.exoplatform.ide.outline.OutlinePartViewImpl;
import org.exoplatform.ide.paas.PaaSAgentImpl;
import org.exoplatform.ide.part.FocusManager;
import org.exoplatform.ide.part.PartStackPresenter;
import org.exoplatform.ide.part.PartStackPresenter.PartStackEventHandler;
import org.exoplatform.ide.part.PartStackUIResources;
import org.exoplatform.ide.part.PartStackView;
import org.exoplatform.ide.part.PartStackViewImpl;
import org.exoplatform.ide.part.projectexplorer.ProjectExplorerView;
import org.exoplatform.ide.part.projectexplorer.ProjectExplorerViewImpl;
import org.exoplatform.ide.perspective.WorkspacePresenter;
import org.exoplatform.ide.perspective.WorkspaceView;
import org.exoplatform.ide.perspective.WorkspaceViewImpl;
import org.exoplatform.ide.preferences.PreferencesAgentImpl;
import org.exoplatform.ide.resources.FileType;
import org.exoplatform.ide.resources.ModelProvider;
import org.exoplatform.ide.resources.ResourceProviderComponent;
import org.exoplatform.ide.resources.model.GenericModelProvider;
import org.exoplatform.ide.toolbar.ToolbarPresenter;
import org.exoplatform.ide.toolbar.ToolbarView;
import org.exoplatform.ide.toolbar.ToolbarViewImpl;
import org.exoplatform.ide.util.executor.UserActivityManager;
import org.exoplatform.ide.wizard.WizardAgentImpl;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
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

      resourcesAPIconfigure();

      uiAPIconfigure();

      editorAPIconfigure();
   }

   /**
    * Configures binding for Editor API
    */
   protected void editorAPIconfigure()
   {
      bind(EditorRegistry.class).in(Singleton.class);
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
      bind(PreferencesAgent.class).to(PreferencesAgentImpl.class).in(Singleton.class);

      bind(WorkspaceView.class).to(WorkspaceViewImpl.class).in(Singleton.class);
      bind(WorkspaceAgent.class).to(WorkspacePresenter.class).in(Singleton.class);

      // expression manager
      bind(ExpressionManager.class).in(Singleton.class);

      // main menu
      bind(MainMenuPresenter.class).in(Singleton.class);
      bind(MainMenuView.class).to(MainMenuViewImpl.class).in(Singleton.class);
      bind(MainMenuAgent.class).to(MainMenuPresenter.class).in(Singleton.class);

      // toolbar
      bind(ToolbarView.class).to(ToolbarViewImpl.class).in(Singleton.class);
      bind(ToolbarAgent.class).to(ToolbarPresenter.class).in(Singleton.class);

      // part agent
      bind(PartStackView.class).to(PartStackViewImpl.class);
      bind(PartStackPresenter.class); // no need to bind here

      // resources: images and css
      bind(PartStackUIResources.class).to(Resources.class).in(Singleton.class);

      // key binding
      bind(KeyBindingAgent.class).to(KeyBindingManager.class).in(Singleton.class);

      bind(ProjectExplorerView.class).to(ProjectExplorerViewImpl.class).in(Singleton.class);
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
