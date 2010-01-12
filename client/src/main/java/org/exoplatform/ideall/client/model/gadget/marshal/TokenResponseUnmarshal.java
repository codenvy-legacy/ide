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
package org.exoplatform.ideall.client.model.gadget.marshal;

import org.exoplatform.gwt.commons.rest.Unmarshallable;
import org.exoplatform.ideall.client.model.gadget.TokenResponse;

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
   
   public TokenResponseUnmarshal(TokenResponse tokenResponse)
   {
      this.tokenResponse = tokenResponse;
   }
   
   /**
    * {@inheritDoc}
    */
   public void unmarshal(String body)
   {
      JSONObject jsonObj = (JSONObject)JSONParser.parse(body);
      
      if (jsonObj.containsKey(TokenResponse.GADGET_URL))
         tokenResponse.setGadgetURL(jsonObj.get(TokenResponse.GADGET_URL).isString().stringValue());
      if (jsonObj.containsKey(TokenResponse.SECURITY_TOKEN))
         tokenResponse.setSecurityToken(jsonObj.get(TokenResponse.SECURITY_TOKEN).isString().stringValue());
      if (jsonObj.containsKey(TokenResponse.MODULE_ID))
         tokenResponse.setModuleId((new Double(jsonObj.get(TokenResponse.MODULE_ID).isNumber().doubleValue())).longValue());
      else 
         tokenResponse.setModuleId(0L);
   }
}
