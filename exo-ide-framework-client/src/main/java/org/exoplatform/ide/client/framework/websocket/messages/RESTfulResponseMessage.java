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
package org.exoplatform.ide.client.framework.websocket.messages;


import java.util.List;

/**
 * RESTful response messages that can be used to track the request.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTfulResponseMessage.java Nov 8, 2012 6:29:19 PM azatsarynnyy $
 *
 */
public interface RESTfulResponseMessage
{
   /**
    * Get message UUID. If specified for request message then response message gets the same UUID.
    *
    * @return message unique identifier
    */
   String getUuid();

   /**
    * Set message UUID. If specified for request message then response message gets the same UUID.
    *
    * @param uuid
    *    message unique identifier
    */
   void setUuid(String uuid);

   /**
    * Get message body.
    *
    * @return message body
    */
   String getBody();

   /**
    * Set message body.
    *
    * @param body
    *    message body
    */
   void setBody(String body);

   /**
    * Get response code.
    *
    * @return response code
    */
   int getResponseCode();

   /**
    * Get response code.
    *
    * @param responseCode
    *    response code
    */
   void setResponseCode(int responseCode);

   /**
    * Get name of HTTP method specified for resource method, e.g. GET, POST, PUT, etc.
    *
    * @return name of HTTP method
    */
   String getMethod();

   /**
    * Set name of HTTP method specified for resource method, e.g. GET, POST, PUT, etc.
    *
    * @param method
    *    name of HTTP method
    */
   void setMethod(String method);

   /**
    * Get resource path.
    *
    * @return resource path
    */
   String getPath();

   /**
    * Set resource path.
    *
    * @param path
    *    resource path
    */
   void setPath(String path);

   /**
    * Get HTTP headers.
    *
    * @return HTTP headers
    */
   List<Pair> getHeaders();

   /**
    * Set HTTP headers.
    *
    * @param headers
    *    HTTP headers
    */
   void setHeaders(List<Pair> headers);
}
