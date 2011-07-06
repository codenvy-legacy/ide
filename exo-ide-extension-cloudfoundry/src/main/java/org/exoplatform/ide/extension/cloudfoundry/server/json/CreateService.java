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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CreateService
{
   private String name;
   private String type;
   private String tier = "free";
   private String vendor;
   private String version;

   public CreateService(String name, String type, String tier, String vendor, String version)
   {
      this.name = name;
      this.type = type;
      this.tier = tier;
      this.vendor = vendor;
      this.version = version;
   }

   public CreateService(String name, String type, String vendor, String version)
   {
      this.name = name;
      this.type = type;
      this.vendor = vendor;
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

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public String getTier()
   {
      return tier;
   }

   public void setTier(String tier)
   {
      this.tier = tier;
   }

   public String getVendor()
   {
      return vendor;
   }

   public void setVendor(String vendor)
   {
      this.vendor = vendor;
   }

   public String getVersion()
   {
      return version;
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   @Override
   public String toString()
   {
      return "CreateService [name=" + name + ", type=" + type + ", tier=" + tier + ", vendor=" + vendor + ", version="
         + version + "]";
   }
}
