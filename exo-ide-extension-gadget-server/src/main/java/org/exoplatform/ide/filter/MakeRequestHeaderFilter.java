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
package org.exoplatform.ide.filter;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.HttpHeaders;

/**
 * This filter adds to the gadget's makeRequest request user's session
 * info, passing the value of "Cookie" header to "headers" query parameter.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jan 24, 2011 4:29:51 PM anya $
 *
 */
public class MakeRequestHeaderFilter implements Filter
{
   private final String HEADERS_PARAMETER = "headers";

   /**
    * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    */
   @Override
   public void init(FilterConfig filterConfig) throws ServletException
   {
   }

   /**
    * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
    */
   @Override
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest)request;
      //Get user session info from "Cookie" header:
      String cookie = httpRequest.getHeader(HttpHeaders.COOKIE);
      if (cookie == null)
      {
         chain.doFilter(httpRequest, response);
         return;
      }
      //Set the value of "Cookie" header to "headers" request query paramater:
      String headers = httpRequest.getParameter(HEADERS_PARAMETER);
      String cookieHeader = HttpHeaders.COOKIE + "=" + URLEncoder.encode(cookie, "UTF-8");
      cookieHeader = (headers == null || headers.length() <= 0) ? cookieHeader : "&" + cookieHeader;
      headers += cookieHeader;

      RequestWrapper requestWrapper = new RequestWrapper(httpRequest, headers);
      chain.doFilter(requestWrapper, response);
   }

   /**
    * @see javax.servlet.Filter#destroy()
    */
   @Override
   public void destroy()
   {
   }

   /**
    * The wrapper for the {@link HttpServletRequest} to override 
    * the getParameter(String name) method to be able to change the value
    * of the "headers" parameter. 
    * 
    * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
    * @version $Id:  Jan 25, 2011 12:50:24 PM anya $
    *
    */
   private class RequestWrapper extends HttpServletRequestWrapper
   {

      /**
       * The value of "headers" parameter.
       */
      private String headersParameter;

      /**
       * @param request http servlet request to wrap
       * @param headersParameter the value of "headers" parameter
       */
      public RequestWrapper(HttpServletRequest request, String headersParameter)
      {
         super(request);
         this.headersParameter = headersParameter;
      }

      /**
       * @see javax.servlet.ServletRequestWrapper#getParameter(java.lang.String)
       */
      @Override
      public String getParameter(String name)
      {
         if (name.equals(HEADERS_PARAMETER) && headersParameter != null)
         {
            return headersParameter;
         }
         return super.getParameter(name);
      }
   }
}
