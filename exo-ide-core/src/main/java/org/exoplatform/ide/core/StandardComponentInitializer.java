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

import com.google.gwt.user.client.ui.Image;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.ui.keybinding.KeyBindingAgent;
import org.exoplatform.ide.command.SaveAllCommand;
import org.exoplatform.ide.command.SaveCommand;
import org.exoplatform.ide.command.ShowNewFolderWizardCommand;
import org.exoplatform.ide.command.ShowNewProjectWizardCommand;
import org.exoplatform.ide.command.ShowNewResourceWizardCommand;
import org.exoplatform.ide.command.ShowPreferenceCommand;
import org.exoplatform.ide.command.ToggleItemCommand;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.keybinding.KeyBuilder;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.toolbar.ToggleItemExpression;
import org.exoplatform.ide.toolbar.ToolbarPresenter;
import org.exoplatform.ide.wizard.WizardAgentImpl;
import org.exoplatform.ide.wizard.newfile.NewTextFilePagePresenter;
import org.exoplatform.ide.wizard.newfolder.NewFolderPagePresenter;
import org.exoplatform.ide.wizard.newgenericproject.NewGenericProjectPagePresenter;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc)
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class StandardComponentInitializer
{

   /**
    *
    */
   @Inject
   public StandardComponentInitializer(MainMenuPresenter menuPresenter, SaveCommand saveCommand,
      SaveAllCommand saveAllCommand, ShowNewResourceWizardCommand newFileCommand,
      ShowNewFolderWizardCommand newFolderCommand, ShowNewProjectWizardCommand newProjectCommand,
      WizardAgentImpl wizardAgent, Provider<NewGenericProjectPagePresenter> genericProjectProvider,
      Provider<NewFolderPagePresenter> newFolderProvider, Provider<NewTextFilePagePresenter> newTextFileProvider,
      Resources resources, KeyBindingAgent keyBindingAgent, ShowPreferenceCommand showPreferencesCommand,
      ToolbarPresenter toolbarPresenter, ExpressionManager expressionManager, EventBus eventBus)
   {
      wizardAgent.registerNewProjectWizard("Generic Project", "Create generic project", "",
         resources.genericProjectIcon(), genericProjectProvider, JsonCollections.<String> createArray());
      // TODO change icon
      wizardAgent.registerNewResourceWizard("General", "Folder", resources.folder(), newFolderProvider);
      wizardAgent.registerNewResourceWizard("General", "Text file", resources.file(), newTextFileProvider);

      ToggleItemExpression toggleState = new ToggleItemExpression(expressionManager, true);
      ToggleItemCommand command = new ToggleItemCommand(resources, eventBus, null, null, toggleState);
      menuPresenter.addMenuItem("File/Checked item", command);
      
      menuPresenter.addMenuItem("File/New/Project", newProjectCommand);
      menuPresenter.addMenuItem("File/New/Folder", newFolderCommand);
      menuPresenter.addMenuItem("File/New/Other", newFileCommand);

      menuPresenter.addMenuItem("File/Save", saveCommand);
      menuPresenter.addMenuItem("File/Save All", saveAllCommand);

      menuPresenter.addMenuItem("Window/Preferences", showPreferencesCommand);

      keyBindingAgent.getGlobal().addKeyBinding(new KeyBuilder().action().charCode('s').build(), saveCommand);
      keyBindingAgent.getGlobal().addKeyBinding(new KeyBuilder().action().charCode('S').build(), saveAllCommand);

      // add items to Toolbar
      toolbarPresenter.addDropDownItem("General/New", new Image(resources.file()), "Create new resources");
      toolbarPresenter.addItem("General/New/Project", newProjectCommand);
      toolbarPresenter.addItem("General/New/File", newFileCommand);
      toolbarPresenter.addItem("General/New/Folder", newFolderCommand);

      toolbarPresenter.addItem("General/Save", saveCommand);
      toolbarPresenter.addItem("General/Save All", saveAllCommand);
   }
}
