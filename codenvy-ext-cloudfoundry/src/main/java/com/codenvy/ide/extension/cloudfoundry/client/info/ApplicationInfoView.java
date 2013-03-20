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
package com.codenvy.ide.extension.cloudfoundry.client.info;

import com.codenvy.ide.view.View;

import java.util.List;

/**
 * The implementation of {@link ApplicationInfoPresenter}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ApplicationInfoView extends View<ApplicationInfoView.ActionDelegate>
{
   /**
    * Needs for delegate some function into CreateApplication view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Ok button.
       */
      public void onOKClicked();
   }

   /**
    * Sets application name.
    * 
    * @param name
    */
   public void setName(String name);

   /**
    * Sets application state.
    * 
    * @param state
    */
   public void setState(String state);

   /**
    * Sets application instances.
    * 
    * @param instances
    */
   public void setInstances(String instances);

   /**
    * Sets application version.
    * 
    * @param version
    */
   public void setVersion(String version);

   /**
    * Sets application disk.
    * 
    * @param disk
    */
   public void setDisk(String disk);

   /**
    * Sets application memory.
    * 
    * @param memory
    */
   public void setMemory(String memory);

   /**
    * Sets application stack.
    * 
    * @param stack
    */
   public void setStack(String stack);

   /**
    * Sets application model.
    * 
    * @param model
    */
   public void setModel(String model);

   /**
    * Sets application uris.
    * 
    * @param applications aplication uris
    */
   public void setApplicationUris(List<String> applications);

   /**
    * Sets application services.
    * 
    * @param services aplication services
    */
   public void setApplicationServices(List<String> services);

   /**
    * Sets application environments.
    * 
    * @param environments application enviroments.
    */
   public void setApplicationEnvironments(List<String> environments);

   /**
    * Show dialog.
    */
   public void showDialog();

   /**
    * Close dialog.
    */
   public void close();
}