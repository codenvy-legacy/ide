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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.view.View;

import java.util.List;

/**
 * The view of {@link ManageServicesPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ManageServicesView extends View<ManageServicesView.ActionDelegate>
{
   /**
    * Needs for delegate some function into ManageServices view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Add button.
       */
      public void onAddClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Delete button.
       */
      public void onDeleteClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Close button.
       */
      public void onCloseClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Unbind service button.
       * 
       * @param service service what needs to unbind
       */
      public void onUnbindServiceClicked(String service);

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Bind service button.
       * 
       * @param service service what needs to bind
       */
      public void onBindServiceClicked(ProvisionedService service);

      /**
       * Performs any actions appropriate in response to the user 
       * having selected other service.
       * 
       * @param service selected service
       */
      public void onSelectedService(ProvisionedService service);
   }

   /**
    * Sets whether Delete button is enabled.
    * 
    * @param enable <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   public void setEnableDeleteButton(boolean enabled);

   /**
    * Sets provisioned services.
    * 
    * @param services
    */
   public void setProvisionedServices(List<ProvisionedService> services);

   /**
    * Sets bounded services.
    * 
    * @param services
    */
   public void setBoundedServices(List<String> services);

   /**
    * Show dialog.
    */
   public void showDialog();

   /**
    * Close dialog.
    */
   public void close();
}