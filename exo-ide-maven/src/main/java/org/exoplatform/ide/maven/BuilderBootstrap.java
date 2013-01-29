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

      final String BUILDER_REPOSITORY =
         (System.getProperty(BuildService.BUILDER_REPOSITORY) != null) ?
            System.getProperty(BuildService.BUILDER_REPOSITORY) :
            ctx.getInitParameter(BuildService.BUILDER_REPOSITORY);

      final String BUILDER_PUBLISH_REPOSITORY =
         (System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY) != null) ?
            System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY) :
            ctx.getInitParameter(BuildService.BUILDER_PUBLISH_REPOSITORY);

      final String BUILDER_PUBLISH_REPOSITORY_URL =
         (System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY_URL) != null) ?
            System.getProperty(BuildService.BUILDER_PUBLISH_REPOSITORY_URL) :
            ctx.getInitParameter(BuildService.BUILDER_PUBLISH_REPOSITORY_URL);

      final Integer BUILDER_TIMEOUT =
         (System.getProperty(BuildService.BUILDER_TIMEOUT) != null) ?
            getNumber(System.getProperty(BuildService.BUILDER_TIMEOUT)) :
            getNumber(ctx.getInitParameter(BuildService.BUILDER_TIMEOUT));

      final Integer BUILDER_WORKERS_NUMBER =
         (System.getProperty(BuildService.BUILDER_WORKERS_NUMBER) != null) ?
            getNumber(System.getProperty(BuildService.BUILDER_WORKERS_NUMBER)) :
            getNumber(ctx.getInitParameter(BuildService.BUILDER_WORKERS_NUMBER));

      final Integer BUILDER_QUEUE_SIZE =
         (System.getProperty(BuildService.BUILDER_QUEUE_SIZE) != null) ?
            getNumber(System.getProperty(BuildService.BUILDER_QUEUE_SIZE)) :
            getNumber(ctx.getInitParameter(BuildService.BUILDER_QUEUE_SIZE));

      final Integer BUILDER_CLEAN_RESULT_DELAY_TIME =
         (System.getProperty(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME) != null) ?
            getNumber(System.getProperty(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME)) :
            getNumber(ctx.getInitParameter(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME));

      config.put(BuildService.BUILDER_REPOSITORY, BUILDER_REPOSITORY);

      config.put(BuildService.BUILDER_PUBLISH_REPOSITORY, BUILDER_PUBLISH_REPOSITORY);

      config.put(BuildService.BUILDER_PUBLISH_REPOSITORY_URL, BUILDER_PUBLISH_REPOSITORY_URL);

      config.put(BuildService.BUILDER_TIMEOUT, BUILDER_TIMEOUT);

      config.put(BuildService.BUILDER_WORKERS_NUMBER, BUILDER_WORKERS_NUMBER);

      config.put(BuildService.BUILDER_QUEUE_SIZE, BUILDER_QUEUE_SIZE);

      config.put(BuildService.BUILDER_CLEAN_RESULT_DELAY_TIME, BUILDER_CLEAN_RESULT_DELAY_TIME);

      BuildService buildService = new BuildService(config);
      ctx.setAttribute(BuildService.class.getName(), buildService);
   }

   private Integer getNumber(String value)
   {
      if (value != null)
      {
         try
         {
            return Integer.valueOf(value);
         }
         catch (NumberFormatException ignored)
         {
         }
      }
      return null;
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
