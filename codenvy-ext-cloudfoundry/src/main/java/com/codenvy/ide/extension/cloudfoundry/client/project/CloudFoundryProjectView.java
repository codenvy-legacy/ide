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
 * The view of {@link CloudFoundryProjectPresenter}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface CloudFoundryProjectView extends View<CloudFoundryProjectView.ActionDelegate>
{
   /**
    * Needs for delegate some function into CloudFoundryProject view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Close button.
       */
      public void onCloseClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Update button.
       */
      public void onUpdateClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Logs button.
       */
      public void onLogsClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Services button.
       */
      public void onServicesClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Delete button.
       */
      public void onDeleteClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Info button.
       */
      public void onInfoClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Start button.
       */
      public void onStartClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Stop button.
       */
      public void onStopClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Restart button.
       */
      public void onRestartClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Edit memory button.
       */
      public void onEditMemoryClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Edit url button.
       */
      public void onEditUrlClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Edit instances button.
       */
      public void onEditInstancesClicked();
   }

   /**
    * Returns application name.
    * 
    * @return application name
    */
   public String getApplicationName();

   /**
    * Sets application name
    * 
    * @param name
    */
   public void setApplicationName(String name);

   /**
    * Returns application model.
    * 
    * @return application model
    */
   public String getApplicationModel();

   /**
    * Sets application model
    * 
    * @param model
    */
   public void setApplicationModel(String model);

   /**
    * Returns application url.
    * 
    * @return application url
    */
   public String getApplicationUrl();

   /**
    * Sets application url.
    * 
    * @param url
    */
   public void setApplicationUrl(String url);

   /**
    * Returns application stack.
    * 
    * @return application stack
    */
   public String getApplicationStack();

   /**
    * Sets application stack.
    * 
    * @param stack
    */
   public void setApplicationStack(String stack);

   /**
    * Returns application instances.
    * 
    * @return application instances
    */
   public String getApplicationInstances();

   /**
    * Sets application instances.
    * 
    * @param instances
    */
   public void setApplicationInstances(String instances);

   /**
    * Returns application memory.
    * 
    * @return application memory
    */
   public String getApplicationMemory();

   /**
    * Sets application memory.
    * 
    * @param memory
    */
   public void setApplicationMemory(String memory);

   /**
    * Returns application status.
    * 
    * @return application status
    */
   public String getApplicationStatus();

   /**
    * Sets application status.
    * 
    * @param status
    */
   public void setApplicationStatus(String status);

   /**
    * Sets whether Start button is enabled.
    * 
    * @param enable <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   public void setEnabledStartButton(boolean enabled);

   /**
    * Sets whether Stop button is enabled.
    * 
    * @param enable <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   public void setEnabledStopButton(boolean enabled);

   /**
    * Sets whether Restart button is enabled.
    * 
    * @param enable <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   public void setEnabledRestartButton(boolean enabled);
   
   /**
    * Returns whether the view is shown.
    * 
    * @return <code>true</code> if the view is shown, and 
    * <code>false</code> otherwise
    */
   public boolean isShown();

   /**
    * Show dialog.
    */
   public void showDialog();

   /**
    * Close dialog.
    */
   public void close();
}