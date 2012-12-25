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

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.command.SaveAllCommand;
import org.exoplatform.ide.command.SaveCommand;
import org.exoplatform.ide.command.ShowNewFolderWizardCommand;
import org.exoplatform.ide.command.ShowNewProjectWizardCommand;
import org.exoplatform.ide.command.ShowNewResourceWizardCommand;
import org.exoplatform.ide.command.ShowPreferenceCommand;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.wizard.WizardAgentImpl;
import org.exoplatform.ide.wizard.newfile.NewTextFilePagePresenter;
import org.exoplatform.ide.wizard.newfolder.NewFolderPagePresenter;
import org.exoplatform.ide.wizard.newgenericproject.NewGenericProjectPagePresenter;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc) 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
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
      Resources resources, ShowPreferenceCommand showPreferencesCommand)
   {
      wizardAgent.registerNewProjectWizard("Generic Project", "Create generic project", "",
         resources.genericProjectIcon(), genericProjectProvider, JsonCollections.<String> createArray());
      // TODO change icon
      wizardAgent.registerNewResourceWizard("General", "Folder", resources.folder(), newFolderProvider);
      wizardAgent.registerNewResourceWizard("General", "Text file", resources.file(), newTextFileProvider);

      menuPresenter.addMenuItem("File/New/Project", newProjectCommand);
      menuPresenter.addMenuItem("File/New/Folder", newFolderCommand);
      menuPresenter.addMenuItem("File/New/Other", newFileCommand);

      menuPresenter.addMenuItem("File/Save", saveCommand);
      menuPresenter.addMenuItem("File/Save All", saveAllCommand);

      menuPresenter.addMenuItem("Window/Preferences", showPreferencesCommand);
   }
}
