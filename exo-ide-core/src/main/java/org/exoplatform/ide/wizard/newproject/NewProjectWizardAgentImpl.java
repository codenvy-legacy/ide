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
package org.exoplatform.ide.wizard.newproject;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.exoplatform.ide.api.ui.wizard.NewProjectWizardAgent;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.wizard.WizardPagePresenter;

import javax.inject.Singleton;

/**
 * Implements register wizards and returns all available wizard.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewProjectWizardAgentImpl implements NewProjectWizardAgent
{
   private final JsonArray<NewProjectWizardData> newProjectWizardDatas;

   /**
    * Create NewProjectWizardContainer
    */
   @Inject
   public NewProjectWizardAgentImpl()
   {
      newProjectWizardDatas = JsonCollections.createArray();
   }

   /**
    * {@inheritDoc}
    */
   public void registerWizard(String title, String description, String primaryNature, ImageResource icon,
      Provider<WizardPagePresenter> wizardPage, JsonArray<String> natures)
   {
      NewProjectWizardData newProjectWizardData =
         new NewProjectWizardData(title, description, primaryNature, icon, wizardPage, natures);
      newProjectWizardDatas.add(newProjectWizardData);
   }

   /**
    * Returns all registered wizards.
    * 
    * @return all registered wizards
    */
   public JsonArray<NewProjectWizardData> getWizards()
   {
      return newProjectWizardDatas;
   }
}