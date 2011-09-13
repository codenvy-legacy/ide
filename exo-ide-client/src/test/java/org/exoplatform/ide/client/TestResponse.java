///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.client;
//
//import com.google.gwt.http.client.Header;
//import com.google.gwt.http.client.Response;
//
///**
// * Created by The eXo Platform SAS.
// *	
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id:   ${date} ${time}
// *
// */
//public class TestResponse extends Response
//{
//
//   /**
//    * Response text.  
//    */
//   private String text;
//
//   /**
//    * Status text. 
//    */
//   private String statusText;
//
//   /**
//    * Status code.
//    */
//   private int statusCode;
//
//   /**
//    * Headers.
//    */
//   private Header[] headers;
//
//   /**
//    * 
//    */
//   public TestResponse()
//   {
//
//   }
//
//   /**
//    * @param text
//    */
//   public TestResponse(String text)
//   {
//      this.text = text;
//   }
//
//   /**
//    * @param headers
//    * @param statusCode
//    * @param statusText
//    * @param text
//    */
//   public TestResponse(Header[] headers, int statusCode, String statusText, String text)
//   {
//      super();
//      this.headers = headers;
//      this.statusCode = statusCode;
//      this.statusText = statusText;
//      this.text = text;
//   }
//
//   /**
//    * @see com.google.gwt.http.client.Response#getHeader(java.lang.String)
//    */
//   @Override
//   public String getHeader(String header) throws NullPointerException, IllegalArgumentException
//   {
//      if (header == null)
//         throw new NullPointerException();
//
//      if (!"".equals(header))
//         throw new IllegalArgumentException();
//
//      for (Header h : headers)
//      {
//         if (header.equals(h.getName()))
//         {
//            return h.getValue();
//         }
//      }
//      return null;
//   }
//
//   /**
//    * @see com.google.gwt.http.client.Response#getHeaders()
//    */
//   @Override
//   public Header[] getHeaders()
//   {
//      return headers;
//   }
//
//   /**
//    * @see com.google.gwt.http.client.Response#getHeadersAsString()
//    */
//   @Override
//   public String getHeadersAsString()
//   {
//      String result = new String();
//      for (Header header : headers)
//      {
//         result += "\n" + header.getName() + " : " + header.getValue();
//      }
//      return result.substring(1);
//   }
//
//   /**
//    * @see com.google.gwt.http.client.Response#getStatusCode()
//    */
//   @Override
//   public int getStatusCode()
//   {
//      return statusCode;
//   }
//
//   /**
//    * @see com.google.gwt.http.client.Response#getStatusText()
//    */
//   @Override
//   public String getStatusText()
//   {
//      return statusText;
//   }
//
//   /**
//    * @see com.google.gwt.http.client.Response#getText()
//    */
//   @Override
//   public String getText()
//   {
//      return text;
//   }
//}
