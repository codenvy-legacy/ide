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
package org.exoplatform.ide.git.client;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 11:42:28 AM anya $
 * 
 */
public class MockResponse extends Response
{
   private String text;

   /**
    * 
    */
   public MockResponse(String text)
   {
      this.text = text;
   }

   /**
    * @see com.google.gwt.http.client.Response#getHeader(java.lang.String)
    */
   @Override
   public String getHeader(String header)
   {
      return "";
   }

   /**
    * @see com.google.gwt.http.client.Response#getHeaders()
    */
   @Override
   public Header[] getHeaders()
   {
      return new Header[0];
   }

   /**
    * @see com.google.gwt.http.client.Response#getHeadersAsString()
    */
   @Override
   public String getHeadersAsString()
   {
      return "";
   }

   /**
    * @see com.google.gwt.http.client.Response#getStatusCode()
    */
   @Override
   public int getStatusCode()
   {
      return 0;
   }

   /**
    * @see com.google.gwt.http.client.Response#getStatusText()
    */
   @Override
   public String getStatusText()
   {
      return "";
   }

   /**
    * @see com.google.gwt.http.client.Response#getText()
    */
   @Override
   public String getText()
   {
      return text;
   }
}
