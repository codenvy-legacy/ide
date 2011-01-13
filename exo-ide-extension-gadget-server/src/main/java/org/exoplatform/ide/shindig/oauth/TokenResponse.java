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
package org.exoplatform.ide.shindig.oauth;

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
      + "\",\""+ SECURITY_TOKEN + "\":\"" + securityToken
      + "\",\""+ MODULE_ID + "\":\"" + moduleId
      + "\"}";
      return json;
   }
}
