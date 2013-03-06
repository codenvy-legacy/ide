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
package com.codenvy.ide.wizard;

import com.codenvy.ide.api.ui.wizard.WizardAgent;
import com.codenvy.ide.wizard.newproject.NewProjectWizardData;
import com.codenvy.ide.wizard.newresource.NewResourceWizardData;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Provider;


import javax.inject.Singleton;

/**
 * Implements register wizards and returns all available wizard.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class WizardAgentImpl implements WizardAgent
{
   private final JsonArray<NewProjectWizardData> newProjectWizardDatas;

   private final JsonArray<NewResourceWizardData> newResourceWizardDatas;

   /**
    * Create WizardAgent
    */
   @Inject
   public WizardAgentImpl()
   {
      newProjectWizardDatas = JsonCollections.createArray();
      newResourceWizardDatas = JsonCollections.createArray();
   }

   /**
    * {@inheritDoc}
    */
   public void registerNewProjectWizard(String title, String description, String primaryNature, ImageResource icon,
                              Provider<? extends WizardPagePresenter> wizardPage, JsonArray<String> natures)
   {
      NewProjectWizardData newProjectWizardData =
         new NewProjectWizardData(title, description, primaryNature, icon, wizardPage, natures);
      newProjectWizardDatas.add(newProjectWizardData);
   }

   /**
    * Returns all registered wizards for creating new project.
    *
    * @return
    */
   public JsonArray<NewProjectWizardData> getNewProjectWizards()
   {
      return newProjectWizardDatas;
   }

   /**
    * {@inheritDoc}
    */
   public void registerNewResourceWizard(String category, String title, ImageResource icon,
      Provider<? extends WizardPagePresenter> wizardPage)
   {
      NewResourceWizardData newResourceWizardData = new NewResourceWizardData(title, category, icon, wizardPage);
      newResourceWizardDatas.add(newResourceWizardData);
   }

   /**
    * Returns all registered wizards for creating new resource.
    * 
    * @return
    */
   public JsonArray<NewResourceWizardData> getNewResourceWizards()
   {
      return newResourceWizardDatas;
   }
}