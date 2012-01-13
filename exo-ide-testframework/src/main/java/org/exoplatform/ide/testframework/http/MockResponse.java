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
package org.exoplatform.ide.testframework.http;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class MockResponse extends Response
{

   /**
    * Response text.
    */
   private String text = new String();

   /**
    * Status text.
    */
   private String statusText = new String();

   /**
    * Status code.
    */
   private int statusCode;

   /**
    * Headers.
    */
   private Header[] headers;

   /**
    * 
    */
   public MockResponse()
   {
   }

   /**
    * @param text
    * @param statusText
    * @param statusCode
    * @param headers
    */
   public MockResponse(String text, String statusText, int statusCode, Header[] headers)
   {
      this.text = text;
      this.statusText = statusText;
      this.statusCode = statusCode;
      this.headers = headers;
   }

   /**
    * @param text
    * @param statusCode
    */
   public MockResponse(String text, int statusCode)
   {
      this.text = text;
      this.statusCode = statusCode;
   }

   /**
    * @param text
    */
   public MockResponse(String text)
   {
      this.text = text;
   }

   /**
    * @param text the text to set
    */
   public void setText(String text)
   {
      this.text = text;
   }

   /**
    * @param statusText the statusText to set
    */
   public void setStatusText(String statusText)
   {
      this.statusText = statusText;
   }

   /**
    * @param statusCode the statusCode to set
    */
   public void setStatusCode(int statusCode)
   {
      this.statusCode = statusCode;
   }

   /**
    * @param headers the headers to set
    */
   public void setHeaders(Header[] headers)
   {
      this.headers = headers;
   }

   @Override
   public String getHeader(String header)
   {
      for (Header h : headers)
      {
         if (header.equals(h.getName()))
         {
            return h.getValue();
         }
      }
      return null;
   }

   @Override
   public Header[] getHeaders()
   {
      return headers;
   }

   @Override
   public String getHeadersAsString()
   {
      String result = new String();
      for (Header header : headers)
      {
         result += "\n" + header.getName() + " : " + header.getValue();
      }
      return result.substring(1);
   }

   @Override
   public int getStatusCode()
   {
      return statusCode;
   }

   @Override
   public String getStatusText()
   {
      return statusText;
   }

   @Override
   public String getText()
   {
      return text;
   }

}
