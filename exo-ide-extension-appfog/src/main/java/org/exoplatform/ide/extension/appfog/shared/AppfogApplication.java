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
package org.exoplatform.ide.extension.appfog.shared;

import org.exoplatform.ide.extension.cloudfoundry.shared.ApplicationMetaInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplicationResources;
import org.exoplatform.ide.extension.cloudfoundry.shared.Staging;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AppfogApplication
{
   String getName();

   void setName(String name);

   List<String> getUris();

   void setUris(List<String> uris);

   int getInstances();

   void setInstances(int instances);

   int getRunningInstances();

   void setRunningInstances(int runningInstances);

   String getState();

   void setState(String state);

   List<String> getServices();

   void setServices(List<String> services);

   String getVersion();

   void setVersion(String version);

   List<String> getEnv();

   void setEnv(List<String> env);

   CloudFoundryApplicationResources getResources();

   void setResources(CloudFoundryApplicationResources resources);

   Staging getStaging();

   void setStaging(Staging staging);

   // Switch debug mode.
   String getDebug();

   void setDebug(String debug);
   // ------------------

   ApplicationMetaInfo getMeta();

   void setMeta(ApplicationMetaInfo mi);

   Infra getInfra();

   void setInfra(Infra infra);
}
