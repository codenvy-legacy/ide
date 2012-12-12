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
package org.exoplatform.ide.wizard.newresource;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * Aggregate information about registered wizard for creating new file.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewResourceWizardData
{
   private String title;

   private String category;

   private ImageResource icon;

   private Provider<? extends WizardPagePresenter> wizardPage;

   /**
    * Create wizard's data
    * 
    * @param title
    * @param category
    * @param icon
    * @param wizardPage
    */
   public NewResourceWizardData(String title, String category, ImageResource icon,
      Provider<? extends WizardPagePresenter> wizardPage)
   {
      super();
      this.title = title;
      this.category = category;
      this.icon = icon;
      this.wizardPage = wizardPage;
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
    * Returns wizard's page presenter.
    * 
    * @return
    */
   public WizardPagePresenter getWizardPage()
   {
      return wizardPage.get();
   }

   /**
    * Returns wizard's category.
    * 
    * @return
    */
   public String getCategory()
   {
      return category;
   }

   /**
    * Returns wizard's icon.
    * 
    * @return the wizard's icon, or <code>null</code> if nones
    */
   public ImageResource getIcon()
   {
      return icon;
   }
}