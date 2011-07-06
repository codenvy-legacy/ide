/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.shared;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SystemResources
{
   private int services;
   /** Number of application deployed under account. */
   private int apps;
   /** Memory available for all applications under account (in MB). */
   private int memory;

   public int getServices()
   {
      return services;
   }

   public void setServices(int services)
   {
      this.services = services;
   }

   public int getApps()
   {
      return apps;
   }

   public void setApps(int apps)
   {
      this.apps = apps;
   }

   public int getMemory()
   {
      return memory;
   }

   public void setMemory(int memory)
   {
      this.memory = memory;
   }
}
