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
package org.exoplatform.ide.extension.gadget.client.service;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.gadget.client.service.marshal.GadgetMetadataUnmarshaler;
import org.exoplatform.ide.extension.gadget.client.service.marshal.TokenRequestMarshaler;
import org.exoplatform.ide.extension.gadget.client.service.marshal.TokenResponseUnmarshal;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetServiceImpl extends GadgetService
{

   private HandlerManager eventBus;

   private Loader loader;

   private String restServiceContext;

   private String gadgetServer;


   public GadgetServiceImpl(HandlerManager eventBus, Loader loader, String restServiceContext, String gadgetServer,
      String publicContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
      this.gadgetServer = gadgetServer;
   }

   public void getGadgetMetadata(TokenResponse tokenResponse, AsyncRequestCallback<GadgetMetadata>callback)
   {
      String data =
         "{\"context\":{\"country\":\"" + "us" + "\",\"language\":\"" + "en" + "\"},\"gadgets\":[" + "{\"moduleId\":"
            + tokenResponse.getModuleId() + ",\"url\":\"" + tokenResponse.getGadgetURL() + "\",\"prefs\":[]}]}";
      // Send data
      GadgetMetadata metadata = new GadgetMetadata();
      metadata.setSecurityToken(tokenResponse.getSecurityToken());
      GadgetMetadataUnmarshaler unmarshaller = new GadgetMetadataUnmarshaler(metadata);
      callback.setResult(metadata);
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshaller);

      String url = gadgetServer + "metadata";
      
      AsyncRequest.build(RequestBuilder.POST, url, loader).data(data).send(callback);
   }

   public void getSecurityToken(TokenRequest request, AsyncRequestCallback<TokenResponse> callback)
   {
      TokenResponse tokenResponse = new TokenResponse();
      callback.setResult(tokenResponse);
      TokenResponseUnmarshal unmarshal = new TokenResponseUnmarshal(tokenResponse);
      TokenRequestMarshaler marshaler = new TokenRequestMarshaler(request);
      
      callback.setEventBus(eventBus);
      callback.setPayload(unmarshal);

      String url = restServiceContext + "/services/shindig/securitytoken/createToken";
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .data(marshaler).send(callback);
   }

}
