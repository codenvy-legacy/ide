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
package org.exoplatform.ideall.client;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TestResponse extends Response
{

   /**
    * Response text.  
    */
   private String text;

   /**
    * Status text. 
    */
   private String statusText;

   /**
    * Status code.
    */
   private int statusCode;

   /**
    * 
    */
   public TestResponse()
   {

   }

   /**
    * @param text
    */
   public TestResponse(String text)
   {
      this.text = text;
   }

   /**
    * @see com.google.gwt.http.client.Response#getHeader(java.lang.String)
    */
   @Override
   public String getHeader(String arg0)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see com.google.gwt.http.client.Response#getHeaders()
    */
   @Override
   public Header[] getHeaders()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see com.google.gwt.http.client.Response#getHeadersAsString()
    */
   @Override
   public String getHeadersAsString()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see com.google.gwt.http.client.Response#getStatusCode()
    */
   @Override
   public int getStatusCode()
   {
      return statusCode;
   }

   /**
    * @param statusCode the statusCode to set
    */
   public void setStatusCode(int statusCode)
   {
      this.statusCode = statusCode;
   }

   /**
    * @see com.google.gwt.http.client.Response#getStatusText()
    */
   @Override
   public String getStatusText()
   {
      return statusText;
   }

   /**
    * @param statusText the statusText to set
    */
   public void setStatusText(String statusText)
   {
      this.statusText = statusText;
   }

   /**
    * @see com.google.gwt.http.client.Response#getText()
    */
   @Override
   public String getText()
   {
      return text;
   }

   /**
    * @param text the text to set
    */
   public void setText(String text)
   {
      this.text = text;
   }
}
