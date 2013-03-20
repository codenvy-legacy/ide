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
package com.codenvy.ide.extension.cloudfoundry.client.deploy;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.view.View;
import com.google.gwt.user.client.ui.Composite;

/**
 * The view of {@link DeployApplicationPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface DeployApplicationView extends View<DeployApplicationView.ActionDelegate>
{
   /**
    * Needs for delegate some function into DeployApplication view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having changed application name.
       */
      public void onNameChanged();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed url.
       */
      public void onUrlChanged();

      /**
       * Performs any actions appropriate in response to the user 
       * having changed server.
       */
      public void onServerChanged();
   }

   /**
    * Returns application name.
    * 
    * @return application name
    */
   public String getName();

   /**
    * Sets application name.
    * 
    * @param name
    */
   public void setName(String name);

   /**
    * Returns url.
    * 
    * @return url
    */
   public String getUrl();

   /**
    * Sets url.
    * 
    * @param url
    */
   public void setUrl(String url);

   /**
    * Returns server.
    * 
    * @return server
    */
   public String getServer();

   /**
    * Sets server.
    *  
    * @param server
    */
   public void setServer(String server);

   /**
    * Set the list of servers.
    * 
    * @param servers
    */
   void setServerValues(JsonArray<String> servers);

   /**
    * Returns view.
    * 
    * @return view
    */
   public Composite getView();
}