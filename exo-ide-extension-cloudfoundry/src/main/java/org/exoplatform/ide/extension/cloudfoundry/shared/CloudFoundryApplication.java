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
package org.exoplatform.ide.extension.cloudfoundry.shared;

import java.util.List;

/**
 * Cloud Foundry application info.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 16, 2012 12:21:15 AM azatsarynnyy $
 *
 */
public interface CloudFoundryApplication
{

   public String getName();

   public void setName(String name);

   public List<String> getUris();

   public void setUris(List<String> uris);

   public int getInstances();

   public void setInstances(int instances);

   public int getRunningInstances();

   public void setRunningInstances(int runningInstances);

   public String getState();

   public void setState(String state);

   public List<String> getServices();

   public void setServices(List<String> services);

   public String getVersion();

   public void setVersion(String version);

   public List<String> getEnv();

   public void setEnv(List<String> env);

   public CloudFoundryApplicationResources getResources();

   public void setResources(CloudFoundryApplicationResources resources);

   public Staging getStaging();

   public void setStaging(Staging staging);

}