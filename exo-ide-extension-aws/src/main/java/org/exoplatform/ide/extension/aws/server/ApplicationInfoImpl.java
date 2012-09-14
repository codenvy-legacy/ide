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
package org.exoplatform.ide.extension.aws.server;

import org.exoplatform.ide.extension.aws.shared.ApplicationInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationInfoImpl implements ApplicationInfo
{
   private String name;
   private String description;
   private long created;
   private long updated;
   private List<String> versions;
   private List<String> configurationTemplates;

   public ApplicationInfoImpl(String name,
                              String description,
                              Date created,
                              Date updated,
                              List<String> versions,
                              List<String> configurationTemplates)
   {
      this(name, description, created == null ? -1 : created.getTime(), updated == null ? -1 : updated.getTime(),
         versions, configurationTemplates);
   }

   public ApplicationInfoImpl(String name,
                              String description,
                              long created,
                              long updated,
                              List<String> versions,
                              List<String> configurationTemplates)
   {
      this.name = name;
      this.description = description;
      this.created = created;
      this.updated = updated;
      this.versions = versions;
      this.configurationTemplates = configurationTemplates;
   }

   public ApplicationInfoImpl()
   {
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String getDescription()
   {
      return description;
   }

   @Override
   public void setDescription(String description)
   {
      this.description = description;
   }

   @Override
   public long getCreated()
   {
      return created;
   }

   @Override
   public void setCreated(long creationDate)
   {
      created = creationDate;
   }

   @Override
   public long getUpdated()
   {
      return updated;
   }

   @Override
   public void setUpdated(long modificationDate)
   {
      updated = modificationDate;
   }

   @Override
   public List<String> getVersions()
   {
      if (versions == null)
      {
         versions = new ArrayList<String>();
      }
      return versions;
   }

   @Override
   public void setVersions(List<String> versions)
   {
      this.versions = versions;
   }

   @Override
   public List<String> getConfigurationTemplates()
   {
      if (configurationTemplates == null)
      {
         configurationTemplates = new ArrayList<String>();
      }
      return configurationTemplates;
   }

   @Override
   public void setConfigurationTemplates(List<String> configurationTemplates)
   {
      this.configurationTemplates = configurationTemplates;
   }

   @Override
   public String toString()
   {
      return "ApplicationInfoImpl{" +
         "name='" + name + '\'' +
         ", description='" + description + '\'' +
         ", created=" + created +
         ", updated=" + updated +
         ", versions=" + versions +
         ", configurationTemplates=" + configurationTemplates +
         '}';
   }
}
