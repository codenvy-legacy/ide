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
package com.codenvy.ide.extension.cloudfoundry.client.apps;

import com.codenvy.ide.mvp.View;

import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link ApplicationsPresenter}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ApplicationsView extends View<ApplicationsView.ActionDelegate>
{
   /**
    * Needs for delegate some function into Applications view.
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
       * having pressed the Show button.
       */
      public void onShowClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Start button.
       * 
       * @param app current application what need to start.
       */
      public void onStartClicked(CloudFoundryApplication app);

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Stop button.
       * 
       * @param app current application what need to stop.
       */
      public void onStopClicked(CloudFoundryApplication app);

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Restart button.
       * 
       * @param app current application what need to restart.
       */
      public void onRestartClicked(CloudFoundryApplication app);

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Delete button.
       * 
       * @param app current application what need to delete.
       */
      public void onDeleteClicked(CloudFoundryApplication app);
   }

   /**
    * Sets available application into special place on the view.
    * 
    * @param apps list of available applications.
    */
   public void setApplications(JsonArray<CloudFoundryApplication> apps);

   /**
    * Returns selected server's name.
    * 
    * @return
    */
   public String getServer();

   /**
    * Select new server's name.
    * 
    * @param server
    */
   public void setServer(String server);

   /**
    * Sets list of server names.
    * 
    * @param servers
    */
   public void setServers(JsonArray<String> servers);

   /**
    * Returns whether the view is shown.
    * 
    * @return <code>true</code> if the view is shown, and 
    * <code>false</code> otherwise
    */
   public boolean isShown();

   /**
    * Close dialog.
    */
   public void close();

   /**
    * Show dialog.
    */
   public void showDialog();
}