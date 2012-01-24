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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.gadget.client.service.marshal.TokenRequestMarshaler;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class GadgetServiceImpl extends GadgetService
{

   private Loader loader;

   private String restServiceContext;

   private String gadgetServer;

   public GadgetServiceImpl(Loader loader, String restServiceContext, String gadgetServer,
      String publicContext)
   {
      this.loader = loader;
      this.restServiceContext = restServiceContext;
      this.gadgetServer = gadgetServer;
   }

   public void getGadgetMetadata(TokenResponse tokenResponse, AsyncRequestCallback<GadgetMetadata> callback) throws RequestException
   {
      String data =
         "{\"context\":{\"country\":\"" + "us" + "\",\"language\":\"" + "en" + "\"},\"gadgets\":[" + "{\"moduleId\":"
            + tokenResponse.getModuleId() + ",\"url\":\"" + tokenResponse.getGadgetURL() + "\",\"prefs\":[]}]}";

      String url = gadgetServer + "metadata";
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(data).send(callback);
   }

   public void getSecurityToken(TokenRequest request, AsyncRequestCallback<TokenResponse> callback) throws RequestException
   {
      TokenRequestMarshaler marshaler = new TokenRequestMarshaler(request);

      String url = restServiceContext + "/ide/shindig/securitytoken/createToken";
      AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .data(marshaler.marshal()).send(callback);
   }

}
