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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.view.View;

import java.util.List;

/**
 *
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ManageServicesView extends View<ManageServicesView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void onAddClicked();

      public void onDeleteClicked();

      public void onCloseClicked();

      public void onUnbindServiceClicked(String service);

      public void onBindServiceClicked(ProvisionedService service);
   }

   public void enableDeleteButton(boolean enabled);

   public void setProvisionedServices(List<ProvisionedService> services);

   public void setBoundedServices(List<String> services);

   public void showDialog();

   public void close();
}