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

import java.util.List;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplication
{
   private String name;
   private List<String> uris;
   private int instances;
   private int runningInstances;
   private String state;
   private List<String> services;
   private String version;
   private List<String> env;
   private CloudfoundryApplicationResources resources;
   private Staging staging;

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public List<String> getUris()
   {
      return uris;
   }

   public void setUris(List<String> uris)
   {
      this.uris = uris;
   }

   public int getInstances()
   {
      return instances;
   }

   public void setInstances(int instances)
   {
      this.instances = instances;
   }

   public int getRunningInstances()
   {
      return runningInstances;
   }

   public void setRunningInstances(int runningInstances)
   {
      this.runningInstances = runningInstances;
   }

   public String getState()
   {
      return state;
   }

   public void setState(String state)
   {
      this.state = state;
   }

   public List<String> getServices()
   {
      return services;
   }

   public void setServices(List<String> services)
   {
      this.services = services;
   }

   public String getVersion()
   {
      return version;
   }

   public void setVersion(String version)
   {
      this.version = version;
   }

   public List<String> getEnv()
   {
      return env;
   }

   public void setEnv(List<String> env)
   {
      this.env = env;
   }

   public CloudfoundryApplicationResources getResources()
   {
      return resources;
   }

   public void setResources(CloudfoundryApplicationResources resources)
   {
      this.resources = resources;
   }

   public Staging getStaging()
   {
      return staging;
   }

   public void setStaging(Staging staging)
   {
      this.staging = staging;
   }
}
