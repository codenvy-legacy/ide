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
package com.codenvy.ide.extension.cloudfoundry.client.project;

import com.codenvy.ide.view.View;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CloudFoundryProjectView extends View<CloudFoundryProjectView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void onCloseClicked();

      public void onUpdateClicked();

      public void onLogsClicked();

      public void onServicesClicked();

      public void onDeleteClicked();

      public void onInfoClicked();

      public void onStartClicked();

      public void onStopClicked();

      public void onRestartClicked();

      public void onEditMemoryClicked();

      public void onEditUrlClicked();

      public void onEditInstancesClicked();
   }

   public String getApplicationName();

   public void setApplicationName(String name);

   public String getApplicationModel();

   public void setApplicationModel(String model);

   public String getApplicationUrl();

   public void setApplicationUrl(String url);

   public String getApplicationStack();

   public void setApplicationStack(String stack);

   public String getApplicationInstances();

   public void setApplicationInstances(String instances);

   public String getApplicationMemory();

   public void setApplicationMemory(String memory);

   public String getApplicationStatus();

   public void setApplicationStatus(String status);

   public void setStartButtonEnabled(boolean enabled);

   public void setStopButtonEnabled(boolean enabled);

   public void setRestartButtonEnabled(boolean enabled);
   
   public boolean isDisplayed();

   /**
    * Show dialog.
    */
   public void showDialog();

   /**
    * Close dialog.
    */
   public void close();
}