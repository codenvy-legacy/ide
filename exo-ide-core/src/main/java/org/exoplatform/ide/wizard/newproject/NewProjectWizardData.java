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
import com.google.gwt.user.client.ui.Image;
import com.google.inject.Provider;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * Aggregate information about registered wizard.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectWizardData
{
   private String title;

   private String description;

   private String primaryNature;

   private ImageResource icon;

   private Provider<WizardPagePresenter> wizardPage;

   private JsonArray<String> natures;

   /**
    * Create wizard's data
    * 
    * @param title
    * @param description
    * @param primaryNature
    * @param icon
    * @param wizardPage
    * @param natures
    */
   public NewProjectWizardData(String title, String description, String primaryNature, ImageResource icon,
      Provider<WizardPagePresenter> wizardPage, JsonArray<String> natures)
   {
      this.title = title;
      this.description = description;
      this.primaryNature = primaryNature;
      this.icon = icon;
      this.wizardPage = wizardPage;
      this.natures = natures;
   }

   /**
    * Returns wizard's title.
    * 
    * @return
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * Returns wizard's description.
    * 
    * @return
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Returns wizard's primary nature.
    * 
    * @return
    */
   public String getPrimaryNature()
   {
      return primaryNature;
   }

   /**
    * Returns wizard's page presenter.
    * 
    * @return
    */
   public WizardPagePresenter getWizardPage()
   {
      return wizardPage.get();
   }

   /**
    * Returns wizard's icon.
    * 
    * @return the wizard's icon, or <code>null</code> if none
    */
   public Image getIcon()
   {
      return icon == null ? null : new Image(icon);
   }

   /**
    * Returns natures.
    * 
    * @return
    */
   public JsonArray<String> getNatures()
   {
      return natures;
   }
}