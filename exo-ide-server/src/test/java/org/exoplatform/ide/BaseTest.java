/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide;

import org.everrest.core.RequestHandler;
import org.everrest.core.tools.ResourceLauncher;
import org.exoplatform.container.StandaloneContainer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public abstract class BaseTest 
{

   protected StandaloneContainer container;

   public ResourceLauncher launcher;

   public void setUp() throws Exception
   {
      String containerConf = BaseTest.class.getResource("/conf/standalone/test-configuration.xml").toString();

      StandaloneContainer.addConfigurationURL(containerConf);

      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
            .getResource("login.conf").toString());
      if (System.getProperty("org.exoplatform.ide.server.user-config-path") == null)
         System.setProperty("org.exoplatform.ide.server.user-config-path", "/ide-home/users/");

      RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      launcher = new ResourceLauncher(handler);
      
   }
}
