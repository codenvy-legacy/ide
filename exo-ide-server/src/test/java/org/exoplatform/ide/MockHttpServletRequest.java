/*
 * Copyright (C) 2009 eXo Platform SAS.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@SuppressWarnings("unchecked")
public class MockHttpServletRequest implements HttpServletRequest
{

   private String method;

   private int length;

   private InputStream data;

   private MultivaluedMap<String, String> headers;

   public MockHttpServletRequest(InputStream data, int length, String method, MultivaluedMap<String, String> headers)
   {
      this.data = data;
      this.length = length;
      this.method = method;
      this.headers = headers;
   }

   public String getAuthType()
   {
      return null;
   }

   public String getContextPath()
   {
      return "test";
   }

   public Cookie[] getCookies()
   {
      return null;
   }

   public long getDateHeader(String arg0)
   {
      return 0;
   }

   public String getHeader(String arg0)
   {
      return headers.getFirst(arg0);
   }

   public Enumeration getHeaderNames()
   {
      return new EnumerationImpl(headers.keySet().iterator());
   }

   public Enumeration getHeaders(String arg0)
   {
      return new EnumerationImpl(headers.get(arg0).iterator());
   }

   public int getIntHeader(String arg0)
   {
      return 0;
   }

   public String getMethod()
   {
      return method;
   }

   public String getPathInfo()
   {
      return null;
   }

   public String getPathTranslated()
   {
      return null;
   }

   public String getQueryString()
   {
      return null;
   }

   public String getRemoteUser()
   {
      return null;
   }

   public String getRequestURI()
   {
      return null;
   }

   public StringBuffer getRequestURL()
   {
      return null;
   }

   public String getRequestedSessionId()
   {
      return null;
   }

   public String getServletPath()
   {
      return null;
   }

   public HttpSession getSession()
   {
      return null;
   }

   public HttpSession getSession(boolean arg0)
   {
      return null;
   }

   public Principal getUserPrincipal()
   {
      return null;
   }

   public boolean isRequestedSessionIdFromCookie()
   {
      return false;
   }

   public boolean isRequestedSessionIdFromURL()
   {
      return false;
   }

   public boolean isRequestedSessionIdFromUrl()
   {
      return false;
   }

   public boolean isRequestedSessionIdValid()
   {
      return false;
   }

   public boolean isUserInRole(String arg0)
   {
      return false;
   }

   public Object getAttribute(String arg0)
   {
      return null;
   }

   public Enumeration getAttributeNames()
   {
      return null;
   }

   public String getCharacterEncoding()
   {
      return null;
   }

   public int getContentLength()
   {
      return length;
   }

   public String getContentType()
   {
      return headers.getFirst("content-type");
   }

   public ServletInputStream getInputStream() throws IOException
   {
      return new MockServletInputStream(data);
   }

   public Locale getLocale()
   {
      return null;
   }

   public Enumeration getLocales()
   {
      return null;
   }

   public String getParameter(String arg0)
   {
      return null;
   }

   public Map getParameterMap()
   {
      return null;
   }

   public Enumeration getParameterNames()
   {
      return null;
   }

   public String[] getParameterValues(String arg0)
   {
      return null;
   }

   public String getProtocol()
   {
      return null;
   }

   public BufferedReader getReader() throws IOException
   {
      return null;
   }

   public String getRealPath(String arg0)
   {
      return null;
   }

   public String getRemoteAddr()
   {
      return null;
   }

   public String getRemoteHost()
   {
      return null;
   }

   public RequestDispatcher getRequestDispatcher(String arg0)
   {
      return null;
   }

   public String getScheme()
   {
      return null;
   }

   public String getServerName()
   {
      return null;
   }

   public int getServerPort()
   {
      return 0;
   }

   public boolean isSecure()
   {
      return false;
   }

   public void removeAttribute(String arg0)
   {
   }

   public void setAttribute(String arg0, Object arg1)
   {
   }

   public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException
   {
   }

   public String getLocalAddr()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getLocalName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public int getLocalPort()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   public int getRemotePort()
   {
      // TODO Auto-generated method stub
      return 0;
   }

}

@SuppressWarnings("unchecked")
class EnumerationImpl implements Enumeration
{

   private final Iterator iter;

   public EnumerationImpl(Iterator iter)
   {
      this.iter = iter;
   }

   public boolean hasMoreElements()
   {
      return iter.hasNext();
   }

   public Object nextElement()
   {
      return iter.next();
   }
}

class MockServletInputStream extends ServletInputStream
{

   private final InputStream data;

   public MockServletInputStream(InputStream data)
   {
      this.data = data;
   }

   @Override
   public int read() throws IOException
   {
      return data.read();
   }

}
