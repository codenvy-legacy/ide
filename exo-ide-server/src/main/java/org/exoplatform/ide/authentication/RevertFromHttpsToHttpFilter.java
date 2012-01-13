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
package org.exoplatform.ide.authentication;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter provide possibility return back to HTTP from HTTPS and visa versa for security resource. Problem with cookies: after
 * login on HHTPS resource we get cookie that set to encrypted path only, then we try switch back to HTTP or cookie became invalid
 * and we get other cookie for not encrypted path, in this case we MUST login twice. For resolve this problem we rewrite cookie
 * "JSESSIONID" in this filter we make cookie that we get in HTTPS connection valide for all connections.
 * sessionCookie.setSecure(false);
 * 
 * In initparams need set ports for HHTP & HTTPS and servlet path for SSL (encrypted connections) by default it "/rest/ssl"
 * 
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class RevertFromHttpsToHttpFilter implements Filter
{

   /**
    * 
    */
   private String sslServletPath = "/rest/ssl";

   /**
    * 
    */
   private String httpPort = null;

   /**
    * 
    */
   private String httpsPort = null;

   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {
      httpsPort = filterConfig.getInitParameter("HTTPS_PORT");
      httpPort = filterConfig.getInitParameter("HTTP_PORT");
      sslServletPath = filterConfig.getInitParameter("SSL_SERVLET_PATH");
   }

   /**
    * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
    */
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {

      final HttpServletRequest httpRequest = (HttpServletRequest)request;

      final HttpServletResponse httpResponse = (HttpServletResponse)response;
      final HttpSession session = httpRequest.getSession(false);

      Cookie[] cookies = httpRequest.getCookies();
      if (cookies != null)
      {
         for (int i = 0; i < cookies.length; i++)
         {
            if (cookies[i].getName().equals("JSESSIONID") && request.isSecure() && session != null)
            {
               final Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());
               sessionCookie.setMaxAge(cookies[i].getMaxAge());
               sessionCookie.setSecure(false);
               sessionCookie.setPath(cookies[i].getPath());
               httpResponse.addCookie(sessionCookie);

            }
         }
      }
      String servletPath = httpRequest.getServletPath();
      boolean secure = httpRequest.isSecure();

      if (secure && !servletPath.equalsIgnoreCase(sslServletPath) && httpRequest.getUserPrincipal() != null)
      {
         String location = null;
         if (httpPort == null)
            location = "http://" + request.getServerName() + httpRequest.getRequestURI();
         else
            location = "http://" + request.getServerName() + ":" + httpPort + httpRequest.getRequestURI();
         httpResponse.sendRedirect(location);
         return;
      }

      if (!secure && servletPath.equalsIgnoreCase(sslServletPath) && httpRequest.getUserPrincipal() != null)
      {
         String location = null;
         if (httpsPort == null)
            location = "https://" + request.getServerName() + httpRequest.getRequestURI();
         else
            location = "https://" + request.getServerName() + ":" + httpPort + httpRequest.getRequestURI();
         httpResponse.sendRedirect(location);
         return;
      }
      chain.doFilter(request, response);
   }

   @Override
   public void destroy()
   {
   }

}
