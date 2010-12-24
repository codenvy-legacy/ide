/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.authentication;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class IdeSessionTimeoutFilter implements Filter
{
   private static final Log LOG = ExoLogger.getLogger(IdeSessionTimeoutFilter.class.getName());

   private String timeoutPage = "../login/login.jsp";

   public void init(FilterConfig filterConfig) throws ServletException
   {
      if (filterConfig != null)
         timeoutPage = filterConfig.getInitParameter("timeoutPage");

   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
      ServletException
   {
      if ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse))
      {
         HttpServletRequest httpServletRequest = (HttpServletRequest)request;
         HttpServletResponse httpServletResponse = (HttpServletResponse)response;
         if (isSessionInvalid(httpServletRequest))
         {
            String timeoutUrl = httpServletRequest.getContextPath() + "/" + getTimeoutPage();
            LOG.info("Session is invalid! redirecting to timeoutpage : " + timeoutUrl);
            httpServletResponse.sendRedirect(timeoutUrl);
            return;
         }
      }
      filterChain.doFilter(request, response);
   }

   private boolean isSessionInvalid(HttpServletRequest httpServletRequest)
   {
      boolean sessionInValid =
         (httpServletRequest.getRequestedSessionId() != null) && !httpServletRequest.isRequestedSessionIdValid();
      return sessionInValid;
   }

   public void destroy()
   {
   }

   public String getTimeoutPage()
   {
      return timeoutPage;
   }

}
