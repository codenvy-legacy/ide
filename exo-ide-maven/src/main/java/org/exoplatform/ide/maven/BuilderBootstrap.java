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
package org.exoplatform.ide.maven;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BuilderBootstrap implements ServletContextListener
{
   @Override
   public void contextInitialized(ServletContextEvent sce)
   {
      Map<String, Object> config = new HashMap<String, Object>();
      ServletContext ctx = sce.getServletContext();
      String tmp = ctx.getInitParameter(BuildService.BUILD_MAVEN_GOALS);
      if (tmp != null)
      {
         config.put(BuildService.BUILD_MAVEN_GOALS, tmp.split(","));
      }

      tmp = ctx.getInitParameter(BuildService.BUILD_MAVEN_TIMEOUT);
      if (tmp != null)
      {
         try
         {
            config.put(BuildService.BUILD_MAVEN_TIMEOUT, Integer.valueOf(tmp));
         }
         catch (NumberFormatException ignored)
         {
         }
      }

      config.put(BuildService.BUILD_REPOSITORY, ctx.getInitParameter(BuildService.BUILD_REPOSITORY));

      BuildService buildService = new BuildService(config);
      ctx.setAttribute(BuildService.class.getName(), buildService);
   }

   @Override
   public void contextDestroyed(ServletContextEvent sce)
   {
      ServletContext ctx = sce.getServletContext();
      BuildService buildService = (BuildService)ctx.getAttribute(BuildService.class.getName());
      if (buildService != null)
      {
         buildService.shutdown();
      }
   }
}
