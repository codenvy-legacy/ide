/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.api.ui.paas;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.extension.SDK;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.preferences.PreferencesPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * Provides register new PaaS.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.paas")
public interface PaaSAgent
{
   /**
    * Registers new PaaS.
    * 
    * @param id
    * @param title
    * @param image
    * @param providesTemplate
    * @param supportedProjectTypes
    * @param wizardPage
    * @param preferencePage
    */
   public void registerPaaS(String id, String title, ImageResource image, boolean providesTemplate,
      JsonArray<String> supportedProjectTypes, WizardPagePresenter wizardPage, PreferencesPagePresenter preferencePage);
}