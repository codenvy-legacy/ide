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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudFoundry client resources (images).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientBundle.java Jul 12, 2011 10:24:35 AM vereshchaka $
 *
 */
public interface CloudFoundryClientBundle extends ClientBundle
{
   CloudFoundryClientBundle INSTANCE = GWT.<CloudFoundryClientBundle> create(CloudFoundryClientBundle.class);
   
   /*
    * Buttons
    */
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/ok_Disabled.png")
   ImageResource okButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/delete.png")
   ImageResource deleteButton();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/delete_Disabled.png")
   ImageResource deleteButtonDisabled();
   
   /*
    * cloudfoundry controls
    */
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry.png")
   ImageResource cloudFoundry();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry_Disabled.png")
   ImageResource cloudFoundryDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/initializeApp.png")
   ImageResource createApp();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/initializeApp_Disabled.png")
   ImageResource createAppDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/startApp.png")
   ImageResource startApp();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/startApp_Disabled.png")
   ImageResource startAppDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/restartApp.png")
   ImageResource restartApp();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/restartApp_Disabled.png")
   ImageResource restartAppDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/updateApp.png")
   ImageResource updateApp();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/updateApp_Disabled.png")
   ImageResource updateAppDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/stopApp.png")
   ImageResource stopApp();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/stopApp_Disabled.png")
   ImageResource stopAppDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/appInfo.png")
   ImageResource applicationInfo();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/appInfo_Disabled.png")
   ImageResource applicationInfoDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/deleteApp.png")
   ImageResource deleteApplication();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/deleteApp_Disabled.png")
   ImageResource deleteApplicationDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/renameApp.png")
   ImageResource renameApplication();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/renameApp_Disabled.png")
   ImageResource renameApplicationDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_map_url.png")
   ImageResource mapUrl();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_map_url_Disabled.png")
   ImageResource mapUrlDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_unmap_url.png")
   ImageResource unmapUrl();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_unmap_url_Disabled.png")
   ImageResource unmapUrlDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_instances.png")
   ImageResource appInstances();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_instances_Disabled.png")
   ImageResource appInstancesDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_memory.png")
   ImageResource appMemory();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_memory_Disabled.png")
   ImageResource appMemoryDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/switchAccount.png")
   ImageResource switchAccount();

   @Source("org/exoplatform/ide/extension/cloudfoundry/images/switchAccount_Disabled.png")
   ImageResource switchAccountDisabled();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/apps-list.png")
   ImageResource appsList();
   
   @Source("org/exoplatform/ide/extension/cloudfoundry/images/apps-list_Disabled.png")
   ImageResource appsListDisabled();

}
