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
package org.exoplatform.cloudshell.client;

import com.google.gwt.http.client.RequestBuilder;

import org.exoplatform.cloudshell.client.marshal.LoginMarshaller;
import org.exoplatform.cloudshell.client.marshal.StringUnmarshaller;
import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 4, 2011 4:31:13 PM anya $
 *
 */
public class ShellService
{
   private static ShellService service;

   private final String REST_CONTEXT = "rest/private/";

   private final String RESOURCES_PATH = "ide/cli/resources";

   public static ShellService getService()
   {
      if (service == null)
      {
         service = new ShellService();
      }
      return service;
   }

   public void getCommands(AsyncRequestCallback<Set<CLIResource>> callback)
   {
      String url = REST_CONTEXT + RESOURCES_PATH;

      Set<CLIResource> resources = new HashSet<CLIResource>();

      CLIResourceUnmarshaller unmarshaller = new CLIResourceUnmarshaller(resources);
      callback.setResult(resources);
      callback.setPayload(unmarshaller);

      AsyncRequest.build(RequestBuilder.GET, url, new EmptyLoader())
         .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
   }

   public void login(String command, AsyncRequestCallback<String> callback)
   {
      String url = REST_CONTEXT + "ide/crash/command";
      LoginMarshaller marshaller = new LoginMarshaller(command);
      callback.setPayload(new StringUnmarshaller(callback));

      AsyncRequest.build(RequestBuilder.POST, url, new EmptyLoader()).data(marshaller)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);

   }

   public void processCommand(String cmd)
   {
      //CloudShell.getCommands();

   }

   protected CLIResource findCommand()
   {
      //TODO
      return null;
   }
}
