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
package org.exoplatform.ide.client.module.gadget.service;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class TokenResponse
{
   public static final String SECURITY_TOKEN = "securityToken";
   
   public static final String GADGET_URL = "gadgetURL";
   
   public static final String MODULE_ID =  "moduleId";
   
   private String securityToken;
   
   private String gadgetURL;
   
   private Long moduleId;
   
  
   
   public TokenResponse()
   {
   }
   
   public TokenResponse(String securityToken, String gadgetURL, Long moduleId)
   {
      this.securityToken = securityToken;
      this.gadgetURL = gadgetURL;
      this.moduleId = moduleId;
   }
   
   public String getSecurityToken()
   {
      return securityToken;
   }
   
   public void setSecurityToken(String securityToken)
   {
      this.securityToken = securityToken;
   }
   
   public String getGadgetURL()
   {
      return gadgetURL;
   }
   
   public void setGadgetURL(String gadgetURL)
   {
      this.gadgetURL = gadgetURL;
   }
   
   public void setModuleId(Long moduleId)
   {
      this.moduleId = moduleId;
   }
   
   public Long getModuleId()
   {
      return moduleId;
   }
   
   @Override
   public String toString()
   {
      String json = "{\"" + GADGET_URL + "\":\"" + gadgetURL
      + "\",\"" + SECURITY_TOKEN + "\":\"" + securityToken
      + "\",\""+ MODULE_ID + "\":\"" + moduleId
      + "\"}";
      return json;
   }
}
