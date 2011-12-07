/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudbees.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudBees client resources (images).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesClientBundle.java Jun 23, 2011 10:03:22 AM vereshchaka $
 *
 */
public interface CloudBeesClientBundle extends ClientBundle
{
   CloudBeesClientBundle INSTANCE = GWT.<CloudBeesClientBundle> create(CloudBeesClientBundle.class);
   
   /*
    * Buttons
    */
   @Source("org/exoplatform/ide/extension/cloudbees/images/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/cloudbees/images/ok_Disabled.png")
   ImageResource okButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/cloudbees/images/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/properties.png")
   ImageResource propertiesButton();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/properties_Disabled.png")
   ImageResource propertiesButtonDisabled();
   
   /*
    * CloudBees controls
    */
   @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees.png")
   ImageResource cloudBees();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees_Disabled.png")
   ImageResource cloudBeesDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/initializeApp.png")
   ImageResource initializeApp();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/initializeApp_Disabled.png")
   ImageResource initializeAppDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/appInfo.png")
   ImageResource applicationInfo();

   @Source("org/exoplatform/ide/extension/cloudbees/images/appInfo_Disabled.png")
   ImageResource applicationInfoDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/deleteApp.png")
   ImageResource deleteApplication();

   @Source("org/exoplatform/ide/extension/cloudbees/images/deleteApp_Disabled.png")
   ImageResource deleteApplicationDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/updateApp.png")
   ImageResource updateApplication();

   @Source("org/exoplatform/ide/extension/cloudbees/images/updateApp_Disabled.png")
   ImageResource updateApplicationDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/apps-list.png")
   ImageResource appList();
   
   @Source("org/exoplatform/ide/extension/cloudbees/images/apps-list_Disabled.png")
   ImageResource appListDisabled();

}
