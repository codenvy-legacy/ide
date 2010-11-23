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
package org.exoplatform.ide.shindig.oauth;

import org.apache.shindig.common.crypto.BlobCrypterException;
import org.exoplatform.container.monitor.jvm.J2EEServerInfo;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Path("/services/shindig/securitytoken")
public class RestSecurityTokenGenerator implements ResourceContainer
{
   /**
     * Class logger.
     */
   private final Log log = ExoLogger.getLogger("rest.RestSecurityTokenGenerator");
   
   
   @POST
   @Path("/createToken")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public TokenResponse createToken(TokenRequest tokenRequest)  
   {
      try
      {
         return SecurityTokenGenerator.createToken(tokenRequest,getKeyFilePath());
      }
      catch (IOException e)
      {
         if (log.isDebugEnabled())
            log.error(e.getMessage(), e);
         throw new WebApplicationException(e);
      }
      catch (BlobCrypterException e)
      {
         if (log.isDebugEnabled())
            log.error(e.getMessage(), e);
         throw new WebApplicationException(e);
      } 
      
   }
   
   private String getKeyFilePath(){
      J2EEServerInfo info = new J2EEServerInfo();
      String confPath = info.getExoConfigurationDirectory();
      File keyFile = null;
      
      if (confPath != null) {
         File confDir = new File(confPath);
         if (confDir != null && confDir.exists() && confDir.isDirectory()) {
            keyFile = new File(confDir, "gadgets/key.txt");
         }
      }

      if (keyFile == null) {
         keyFile = new File("key.txt");
      }
      
      return keyFile.getAbsolutePath();
  }

  
}
