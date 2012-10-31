/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;
import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;
import java.util.Arrays;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogServicesImpl implements AppfogServices
{
   private AppfogSystemService[] systemServices;
   private AppfogProvisionedService[] provisionedServices;

   public AppfogServicesImpl()
   {
   }

   public AppfogServicesImpl(AppfogSystemService[] systemServices, AppfogProvisionedService[] provisionedServices)
   {
      this.systemServices = systemServices;
      this.provisionedServices = provisionedServices;
   }

   @Override
   public AppfogSystemService[] getAppfogSystemService()
   {
      return systemServices;
   }

   @Override
   public void setAppfogSystemService(AppfogSystemService[] system)
   {
      this.systemServices = system;
   }

   @Override
   public AppfogProvisionedService[] getAppfogProvisionedService()
   {
      return provisionedServices;
   }

   @Override
   public void setAppfogProvisionedService(AppfogProvisionedService[] provisioned)
   {
      this.provisionedServices = provisioned;
   }

   @Override
   public String toString()
   {
      return "AppfogServicesImpl{" +
         "systemServices=" + (systemServices == null ? null : Arrays.asList(systemServices)) +
         ", provisionedServices=" + (provisionedServices == null ? null : Arrays.asList(provisionedServices)) +
         '}';
   }
}
