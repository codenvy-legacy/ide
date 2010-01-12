/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.gadget;

import org.exoplatform.gwt.commons.rest.AsyncRequest;
import org.exoplatform.gwt.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwt.commons.rest.HTTPHeader;
import org.exoplatform.gwt.commons.rest.MimeType;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.gadget.event.GadgetMetadaRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.event.SecurityTokenRecievedEvent;
import org.exoplatform.ideall.client.model.gadget.marshal.GadgetMetadataUnmarshaler;
import org.exoplatform.ideall.client.model.gadget.marshal.TokenRequestMarshaler;
import org.exoplatform.ideall.client.model.gadget.marshal.TokenResponseUnmarshal;

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

   public GadgetServiceImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   public void getGadgetMetadata(TokenResponse tokenResponse)
   {
      String data =
         "{\"context\":{\"country\":\"" + "us" + "\",\"language\":\"" + "en" + "\"},\"gadgets\":[" + "{\"moduleId\":"
            + tokenResponse.getModuleId() + ",\"url\":\"" + tokenResponse.getGadgetURL() + "\",\"prefs\":[]}]}";
      // Send data
      GadgetMetadata metadata = new GadgetMetadata();
      metadata.setSecurityToken(tokenResponse.getSecurityToken());
      GadgetMetadataUnmarshaler unmarshaller = new GadgetMetadataUnmarshaler(metadata);
      GadgetMetadaRecievedEvent event = new GadgetMetadaRecievedEvent(metadata);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, Configuration.getInstance().getGadgetServer() + "metadata").data(data).send(callback);
   }
   
   public void getSecurityToken(TokenRequest request) {
      TokenResponse tokenResponse = new TokenResponse();
      SecurityTokenRecievedEvent postEvent = new SecurityTokenRecievedEvent(tokenResponse);
      TokenResponseUnmarshal unmarshal = new TokenResponseUnmarshal(tokenResponse);
      TokenRequestMarshaler marshaler = new TokenRequestMarshaler(request);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshal, postEvent);
      AsyncRequest.build(RequestBuilder.POST, Configuration.getInstance().getContext() + "/services/shindig/securitytoken/createToken").
                   header(HTTPHeader.CONTENT_TYPE,MimeType.APPLICATION_JSON).data(marshaler).send(callback);
   
   }

}
