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
package org.exoplatform.ide.extension.appfog.server.json;

import org.exoplatform.ide.extension.appfog.shared.Infra;
import org.exoplatform.ide.extension.cloudfoundry.server.json.CreateApplication;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateAppfogApplication extends CreateApplication
{
   /*
    * JSON output:
    * {
    *    name: "my-app",
    *    uris: ["my-app.aws.af.cm"],
    *    instances: 1,
    *    resources: {memory: 128},
    *    staging: {framework: "sinatra", runtime: "ruby18"},
    *    infra: {provider: "aws", name: "aws"}
    * }
    */

   /**
    * Usually take one parameter called "provider" with value of the name infrastructure.
    */
   private Infra infra;

   public CreateAppfogApplication(String name,
                                  int instances,
                                  String url,
                                  int memory,
                                  String framework,
                                  String runtime,
                                  String command,
                                  Infra infra)
   {
      super(name, instances, url, memory, framework, runtime, command);
      this.infra = infra;
   }

   public Infra getInfra()
   {
      return infra;
   }

   public void setInfra(Infra infra)
   {
      this.infra = infra;
   }
}
