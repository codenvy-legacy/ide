/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide;

import org.exoplatform.ide.commons.EnvironmentContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationRegistry;
import org.exoplatform.services.security.ConversationState;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SetEnvironmentContextFilter implements Filter
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger(SetEnvironmentContextFilter.class);

   /**
    * Set current {@link EnvironmentContext}
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      EnvironmentContext environment = new EnvironmentContext();
      environment.setEnvironmentVariable(EnvironmentContext.WORKSPACE, "default");
      EnvironmentContext.setCurrentEnvironment(environment);
      chain.doFilter(request, response);
   }
   
   

   /**
    * {@inheritDoc}
    */
   public void destroy()
   {
      // nothing to do.
   }

   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {
      // TODO Auto-generated method stub

   }

}
