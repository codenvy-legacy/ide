/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.core;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.ui.keybinding.KeyBindingAgent;
import org.exoplatform.ide.api.ui.paas.PaaSAgent;
import org.exoplatform.ide.api.ui.toolbar.ToolbarAgent;
import org.exoplatform.ide.command.OpenProjectCommand;
import org.exoplatform.ide.command.SaveAllCommand;
import org.exoplatform.ide.command.SaveCommand;
import org.exoplatform.ide.command.ShowNewFolderWizardCommand;
import org.exoplatform.ide.command.ShowNewProjectWizardCommand;
import org.exoplatform.ide.command.ShowNewResourceWizardCommand;
import org.exoplatform.ide.command.ShowOpenPerspectiveDialog;
import org.exoplatform.ide.command.ShowPreferenceCommand;
import org.exoplatform.ide.command.ToggleItemCommand;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.keybinding.KeyBuilder;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.toolbar.ToggleItemExpression;
import org.exoplatform.ide.wizard.WizardAgentImpl;
import org.exoplatform.ide.wizard.newfile.NewTextFilePagePresenter;
import org.exoplatform.ide.wizard.newfolder.NewFolderPagePresenter;
import org.exoplatform.ide.wizard.newgenericproject.NewGenericProjectPagePresenter;
import org.exoplatform.ide.wizard.warproject.NewWarProjectPagePresenter;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc)
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class StandardComponentInitializer
{

   /**
    * Instantiates {@link StandardComponentInitializer} an creates standard content
    */
   @Inject
   public StandardComponentInitializer(MainMenuPresenter menu, SaveCommand saveCommand, SaveAllCommand saveAllCommand,
      ShowNewResourceWizardCommand newFileCommand, ShowNewFolderWizardCommand newFolderCommand,
      ShowNewProjectWizardCommand newProjectCommand, WizardAgentImpl wizard,
      Provider<NewGenericProjectPagePresenter> genericProjectProvider,
      Provider<NewFolderPagePresenter> newFolderProvider, Provider<NewTextFilePagePresenter> newTextFileProvider,
      Resources resources, KeyBindingAgent keyBinding, ShowPreferenceCommand showPreferencesCommand,
      OpenProjectCommand openProjectCommand, ToolbarAgent toolbar, ExpressionManager expressionManager,
      EventBus eventBus, ShowOpenPerspectiveDialog openPerspectiveCommand, PaaSAgent paasAgent,
      Provider<NewWarProjectPagePresenter> warProjectProvider)
   {
      wizard.registerNewProjectWizard("Generic Project", "Create generic project", "", resources.genericProjectIcon(),
         genericProjectProvider, JsonCollections.<String> createArray());
      wizard.registerNewProjectWizard("Java Web Application (WAR)", "Create web application", "War",
         resources.genericProjectIcon(), warProjectProvider, JsonCollections.<String> createArray("java", "War"));

      // TODO change icon
      wizard.registerNewResourceWizard("General", "Folder", resources.folder(), newFolderProvider);
      wizard.registerNewResourceWizard("General", "Text file", resources.file(), newTextFileProvider);

      menu.addMenuItem("File/New/Project", newProjectCommand);
      menu.addMenuItem("File/New/Folder", newFolderCommand);
      menu.addMenuItem("File/New/Other", newFileCommand);

      menu.addMenuItem("File/Open Project", openProjectCommand);

      menu.addMenuItem("File/Save", saveCommand);
      menu.addMenuItem("File/Save All", saveAllCommand);

      menu.addMenuItem("Window/Preferences", showPreferencesCommand);
      menu.addMenuItem("Window/Open perspective", openPerspectiveCommand);

      keyBinding.getGlobal().addKeyBinding(new KeyBuilder().action().charCode('s').build(), saveCommand);
      keyBinding.getGlobal().addKeyBinding(new KeyBuilder().action().charCode('S').build(), saveAllCommand);

      // add items to Toolbar
      toolbar.addDropDownItem("General/New", resources.file(), "Create new resources");
      toolbar.addItem("General/New/Project", newProjectCommand);
      toolbar.addItem("General/New/File", newFileCommand);
      toolbar.addItem("General/New/Folder", newFolderCommand);

      toolbar.addItem("General/Save", saveCommand);
      toolbar.addItem("General/Save All", saveAllCommand);

      // TODO test toggle items
      ToggleItemExpression toggleState = new ToggleItemExpression(expressionManager, true);
      ToggleItemCommand command = new ToggleItemCommand(resources, eventBus, null, null, toggleState);
      menu.addMenuItem("File/Checked item", command);
      toolbar.addToggleItem("Test/Checked item", command);
      toolbar.addDropDownItem("Test/New", resources.file(), "Test item");
      toolbar.addToggleItem("Test/New/Checked item", command);

      // TODO 
      paasAgent.registerPaaS("None", "None", null, false, JsonCollections.<String> createArray("", "java", "War"),
         null, null);
   }
}
