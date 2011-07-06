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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CreateApplication
{
   /*
    * JSON output:
    * {
    *    name: "my-app",
    *    uris: ["my-app.cloudfoundry.com"],
    *    instances: 1,
    *    resources: {memory: 128},
    *    staging: {framework: "sinatra", runtime: null}
    * }
    */
   private final String name;
   private final int instances;
   private final String url;
   private final int memory;
   private final String framework;

   public CreateApplication(String name, int instances, String url, int memory, String framework)
   {
      this.name = name;
      this.instances = instances;
      this.url = url;
      this.memory = memory;
      this.framework = framework;
   }

   // Next getters for correct serialization to JSON format.

   /* Application name. */
   public String getName()
   {
      return name;
   }

   /* Number of VM instances used for application. */
   public int getInstances()
   {
      return instances;
   }

   /* Cloudfoundry API expected for array of string with one item. */
   public String[] getUris()
   {
      return new String[]{url};
   }

   /* Application resources. Just one parameter: memory size in MB. */
   public Map<String, Integer> getResources()
   {
      return Collections.singletonMap("memory", memory);
   }

   /* Staging, send framework name only, 'runtime' always null */
   public Map<String, String> getStaging()
   {
      Map<String, String> m = new HashMap<String, String>(2);
      m.put("framework", framework);
      m.put("runtime", null);
      return m;
   }

   @Override
   public String toString()
   {
      return "CreateApplication [name=" + getName() + ", instances=" + getInstances() + ", uris="
         + Arrays.toString(getUris()) + ", resources=" + getResources() + ", staging=" + getStaging() + "]";
   }
}
