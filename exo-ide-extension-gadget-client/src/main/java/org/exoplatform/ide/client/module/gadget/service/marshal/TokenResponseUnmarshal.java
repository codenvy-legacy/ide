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
package org.exoplatform.ide.client.module.gadget.service.marshal;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.module.gadget.service.TokenResponse;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TokenResponseUnmarshal implements Unmarshallable
{
   private TokenResponse tokenResponse;

   public TokenResponseUnmarshal(HandlerManager eventBus, TokenResponse tokenResponse)
   {
      this.tokenResponse = tokenResponse;
   }

   /**
    * {@inheritDoc}
    */
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         parseTokenResponse(response.getText());
      }
      catch (Exception exc)
      {
         String message = "Can't parse token response";
         throw new UnmarshallerException(message);       
      }
   }

   private void parseTokenResponse(String body)
   {
      JSONObject jsonObj = (JSONObject)JSONParser.parse(body);

      if (jsonObj.containsKey(TokenResponse.GADGET_URL))
         tokenResponse.setGadgetURL(jsonObj.get(TokenResponse.GADGET_URL).isString().stringValue());
      if (jsonObj.containsKey(TokenResponse.SECURITY_TOKEN))
         tokenResponse.setSecurityToken(jsonObj.get(TokenResponse.SECURITY_TOKEN).isString().stringValue());
      if (jsonObj.containsKey(TokenResponse.MODULE_ID))
         tokenResponse.setModuleId((new Double(jsonObj.get(TokenResponse.MODULE_ID).isNumber().doubleValue()))
            .longValue());
      else
         tokenResponse.setModuleId(0L);

   }
}
