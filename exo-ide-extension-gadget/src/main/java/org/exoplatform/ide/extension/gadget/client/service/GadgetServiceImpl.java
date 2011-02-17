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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.extension.gadget.client.service.marshal.GadgetMetadataUnmarshaler;
import org.exoplatform.ide.extension.gadget.client.service.marshal.TokenRequestMarshaler;
import org.exoplatform.ide.extension.gadget.client.service.marshal.TokenResponseUnmarshal;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class GadgetServiceImpl extends GadgetService
{

   private static final String CONTEXT = "/ide/gadget";

   private static final String DEPLOY = "/deploy";

   private static final String UNDEPLOY = "/undeploy";

   private HandlerManager eventBus;

   private Loader loader;

   private String restServiceContext;

   private String gadgetServer;

   private String publicContext;

   public GadgetServiceImpl(HandlerManager eventBus, Loader loader, String restServiceContext, String gadgetServer,
      String publicContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restServiceContext = restServiceContext;
      this.gadgetServer = gadgetServer;
      this.publicContext = publicContext;
   }

   public void getGadgetMetadata(TokenResponse tokenResponse, GadgetMetadataCallback gadgetCallback)
   {
      String data =
         "{\"context\":{\"country\":\"" + "us" + "\",\"language\":\"" + "en" + "\"},\"gadgets\":[" + "{\"moduleId\":"
            + tokenResponse.getModuleId() + ",\"url\":\"" + tokenResponse.getGadgetURL() + "\",\"prefs\":[]}]}";
      // Send data
      GadgetMetadata metadata = new GadgetMetadata();
      metadata.setSecurityToken(tokenResponse.getSecurityToken());
      GadgetMetadataUnmarshaler unmarshaller = new GadgetMetadataUnmarshaler(metadata);
//      GadgetMetadaRecievedEvent event = new GadgetMetadaRecievedEvent(metadata);
      gadgetCallback.setMetadata(metadata);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, gadgetCallback);

      String url = gadgetServer + "metadata";
      
      AsyncRequest.build(RequestBuilder.POST, url, loader).data(data).send(callback);
   }

   public void getSecurityToken(TokenRequest request, SecurityTokenCallback securityTokenCallback)
   {
      TokenResponse tokenResponse = new TokenResponse();
//      SecurityTokenRecievedEvent postEvent = new SecurityTokenRecievedEvent(tokenResponse);
      securityTokenCallback.setTokenResponse(tokenResponse);
      TokenResponseUnmarshal unmarshal = new TokenResponseUnmarshal(tokenResponse);
      TokenRequestMarshaler marshaler = new TokenRequestMarshaler(request);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshal, securityTokenCallback);

      String url = restServiceContext + "/services/shindig/securitytoken/createToken";
      //String url = Configuration.getInstance().getContext() + "/services/shindig/securitytoken/createToken";
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
         .data(marshaler).send(callback);
   }

   @Override
   public void deployGadget(String href, DeployUndeployGadgetCallback gadgetCallback)
   {
      String url =
         restServiceContext +
         /*Configuration.getInstance().getContext() + */CONTEXT + DEPLOY + "?" + QueryParams.GADGET_URL + "="
            + URL.encodeComponent(href) + "&" + QueryParams.PRIVATE_CONTEXT + "=" + URL.encodeComponent(restServiceContext)
            + "&" + QueryParams.PUBLIC_CONTEXT + "=" + URL.encodeComponent(publicContext);
//      GadgetDeployResultEvent event = new GadgetDeployResultEvent(url);
      gadgetCallback.setUrl(url);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, gadgetCallback);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

   @Override
   public void undeployGadget(String href, DeployUndeployGadgetCallback gadgetCallback)
   {
      String url =
         restServiceContext + /*Configuration.getInstance().getContext() + */CONTEXT + UNDEPLOY + "?"
            + QueryParams.GADGET_URL + "=" + URL.encodeComponent(href) + "&" + QueryParams.PRIVATE_CONTEXT + "="
            + URL.encodeComponent(restServiceContext) + "&" + QueryParams.PUBLIC_CONTEXT + "="
            + URL.encodeComponent(publicContext);
//      GadgetUndeployResultEvent event = new GadgetUndeployResultEvent(url);
      gadgetCallback.setUrl(url);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, gadgetCallback);
      AsyncRequest.build(RequestBuilder.POST, url, loader).send(callback);
   }

}
