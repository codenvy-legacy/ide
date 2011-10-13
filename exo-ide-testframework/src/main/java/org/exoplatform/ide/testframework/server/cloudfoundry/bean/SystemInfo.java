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
package org.exoplatform.ide.testframework.server.cloudfoundry.bean;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SystemInfo
{
   private SystemResources limits;
   private SystemResources usage;
   /** Cloud platform description. */
   private String description;
   /** User email. */
   private String user;
   /** Cloud platform version. */
   private String version;
   /** Cloud platform name. */
   private String name;
   /** Support email address. */
   private String support;

   public SystemResources getUsage()
   {
      return usage;
   }

   public void setUsage(SystemResources usage)
   {
      this.usage = usage;
   }

   public SystemResources getLimits()
   {
      return limits;
   }

   public void setLimits(SystemResources limits)
   {
      this.limits = limits;
   }

   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public String getUser()
   {
      return user;
   }

   public void setUser(String user)
   {
      this.user = user;
   }

   public String getVersion()
   {
      return version;
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getSupport()
   {
      return support;
   }

   public void setSupport(String support)
   {
      this.support = support;
   }
}
