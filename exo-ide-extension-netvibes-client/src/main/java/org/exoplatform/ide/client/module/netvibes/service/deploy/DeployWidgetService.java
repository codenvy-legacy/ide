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
package org.exoplatform.ide.client.module.netvibes.service.deploy;

import org.exoplatform.ide.client.module.netvibes.model.DeployWidget;
import org.exoplatform.ide.client.module.netvibes.service.deploy.event.WidgetCategoriesReceivedEvent;
import org.exoplatform.ide.client.module.netvibes.service.deploy.event.WidgetDeployResultReceivedEvent;

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
    * Gets the list of available widget's categories.
    * The location of the categories must be pointed in the service implementation.
    * When response with categories is received, then {@link WidgetCategoriesReceivedEvent} event will be fired.
    */
   public abstract void getCategories();

   /**
    * Deploys widget to Netvibes Ecosystem.
    * When deploy result is received, {@link WidgetDeployResultReceivedEvent} will be fired.
    * 
    * @param deployWidget deploy data used for deploy
    * @param login user's login in Netvibes Ecosystem
    * @param password user's pasword in Netvibes Ecosystem
    */
   public abstract void deploy(DeployWidget deployWidget, String login, String password);
}
