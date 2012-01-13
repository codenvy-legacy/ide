/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.netvibes.client.service.deploy;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.netvibes.client.model.Categories;
import org.exoplatform.ide.extension.netvibes.client.model.DeployWidget;
import org.exoplatform.ide.extension.netvibes.client.service.deploy.callback.WidgetDeployCallback;

/**
 * Service, contains operations for deploying UWA widget to Netvibes Ecosystem.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 30, 2010 $
 * 
 */
public abstract class DeployWidgetService
{
   /**
    * Service instance.
    */
   private static DeployWidgetService instance;

   /**
    * @return {@link DeployWidgetService} instance
    */
   public static DeployWidgetService getInstance()
   {
      return instance;
   }

   /**
    * Default constructor.
    */
   protected DeployWidgetService()
   {
      instance = this;
   }

   /**
    * Gets the list of available widget's categories. The location of the categories must be pointed in the service
    * implementation.
    * 
    * @param callback - the callback code which the user has to implement
    */
   public abstract void getCategories(AsyncRequestCallback<Categories> callback);

   /**
    * Deploys widget to Netvibes Ecosystem.
    * 
    * @param deployWidget deploy data used for deploy
    * @param login user's login in Netvibes Ecosystem
    * @param password user's pasword in Netvibes Ecosystem
    * @param callback - the callback code which the user has to implement
    */
   public abstract void deploy(DeployWidget deployWidget, String login, String password, WidgetDeployCallback callback);
}
