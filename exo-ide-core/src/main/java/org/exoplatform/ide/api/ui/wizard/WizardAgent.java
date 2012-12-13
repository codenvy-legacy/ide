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
package org.exoplatform.ide.api.ui.wizard;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;

import org.exoplatform.ide.extension.SDK;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * Provides register wizards for creating new project and new resource.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 *
 */
@SDK(title = "ide.api.ui.wizard.newresource")
public interface WizardAgent
{
   /**
    * Registers new wizard for creating new project.
    *
    * @param title
    * @param description
    * @param primaryNature
    * @param icon
    * @param wizardPage first wizard page
    * @param natures
    */
   void registerNewProjectWizard(String title, String description, String primaryNature, ImageResource icon,
      Provider<? extends WizardPagePresenter> wizardPage, JsonArray<String> natures);

   /**
    * Registers new wizard for creating new resource.
    * 
    * @param category
    * @param title
    * @param icon
    * @param wizardPage
    */
   void registerNewResourceWizard(String category, String title, ImageResource icon,
      Provider<? extends WizardPagePresenter> wizardPage);
}