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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.view.View;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ApplicationsView extends View<ApplicationsView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void doClose();

      public void doShow();

      public void doStartApplication(CloudFoundryApplication app);

      public void doStopApplication(CloudFoundryApplication app);

      public void doRestartApplication(CloudFoundryApplication app);

      public void doDeleteApplication(CloudFoundryApplication app);
   }

   public void setApplications(JsonArray<CloudFoundryApplication> apps);

   public String getServer();

   public void setServer(String server);

   public void setServers(JsonArray<String> servers);

   /**
    *Close dialog.
    */
   public void close();

   /**
    * Show dialog.
    */
   public void showDialog();
}