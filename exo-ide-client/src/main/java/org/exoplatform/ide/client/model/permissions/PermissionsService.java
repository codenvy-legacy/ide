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
package org.exoplatform.ide.client.model.permissions;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.permissions.event.ItemPermissionSetEvent;
import org.exoplatform.ide.client.model.permissions.event.ItemPermissionsResivedEvent;
import org.exoplatform.ide.client.model.permissions.marshal.ItemPermissionsUnmarshaller;
import org.exoplatform.ide.client.model.permissions.marshal.PermissionsMarshaller;
import org.exoplatform.ide.client.model.permissions.marshal.PermissionsUnmarshaller;
import org.exoplatform.ide.client.framework.module.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 29, 2010 $
 *
 */
public class PermissionsService
{
   private static final String PERMISSIONS_REVICE = "/ide/permission";

   private HandlerManager eventBus;

   private Loader loader;

   private String restServiceContext;

   private static PermissionsService instance;

   /**
    * @param eventBus
    * @param loader
    * @param restServiceContext
    */
   public PermissionsService(HandlerManager eventBus, Loader loader, String restServiceContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
      instance = this;
   }

   public static PermissionsService getInstanse()
   {
      return instance;
   }

   public void setPermissions(Item item, UserPermissionsStoradge permissions)
   {
      String url = restServiceContext + PERMISSIONS_REVICE + "/set/" ;

      ItemPermissionSetEvent event = new ItemPermissionSetEvent(item);

      PermissionsMarshaller marshaller = new PermissionsMarshaller();

      PermissionsUnmarshaller unmarshaller = new PermissionsUnmarshaller();

      int[] aceptStatus = new int[]{HTTPStatus.OK};

      String errorMessage = "Service is not deployed.";
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent(errorMessage);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent, aceptStatus);

      AsyncRequest.build(RequestBuilder.POST, url, loader).header("Item-Href", item.getHref())
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(marshaller).send(callback);
   }
   
   public void getPermissions(Item item)
   {
      String url = restServiceContext + PERMISSIONS_REVICE + "/get/";
      
      ItemPermissionsResivedEvent event = new ItemPermissionsResivedEvent();
      ItemPermissionsUnmarshaller unmarshaller = new ItemPermissionsUnmarshaller();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, new ExceptionThrownEvent());
      
      AsyncRequest.build(RequestBuilder.GET, url, loader).header("Item-Href", item.getHref()).send(callback);
   }

}
