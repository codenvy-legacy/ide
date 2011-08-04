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
package org.exoplatform.cloudshell.client.crash;

import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

import java.util.HashMap;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 2, 2011 1:59:50 PM anya $
 *
 */
public class CRaSHClientService
{
   private static CRaSHClientService service;

   private final String REST_CONTEXT = "rest/private/";
   
   private final String COMMAND_PATH = "ide/crash/command";

   private final String WELCOME_PATH = "ide/crash/welcome";

   private final String COMPLETE_PATH = "ide/crash/complete";

   public static CRaSHClientService getService()
   {
      if (service == null)
      {
         service = new CRaSHClientService();
      }
      return service;
   }

   public void welcome(CRaSHOutputAsyncRequestCallback callback)
   {
      String url = REST_CONTEXT + WELCOME_PATH;

      CRaSHOutput output = new CRaSHOutput();
      CRaSHOutputUnmarshaller unmarshaller = new CRaSHOutputUnmarshaller(output);

      callback.setResult(output);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, new EmptyLoader()).send(callback);
   }

   public void processCommand(String command, CRaSHOutputAsyncRequestCallback callback)
   {
      String url = REST_CONTEXT + COMMAND_PATH;

      CRaSHOutput output = new CRaSHOutput();
      CRaSHOutputUnmarshaller unmarshaller = new CRaSHOutputUnmarshaller(output);

      callback.setResult(output);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.POST, url, new EmptyLoader()).data(command).send(callback);
   }

   public void complete(String str, CRaSHCompleteListAsyncRequestCallback callback)
   {
      String url = REST_CONTEXT + COMPLETE_PATH;

      HashMap<String, String> completeList = new HashMap<String, String>();
      CRaSHCompleteListUnmarshaller unmarshaller = new CRaSHCompleteListUnmarshaller(completeList);
      
      callback.setResult(completeList);
      callback.setPayload(unmarshaller);
      AsyncRequest.build(RequestBuilder.POST, url, new EmptyLoader()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).data(str).send(callback);
   }
}
