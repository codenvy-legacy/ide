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

import org.apache.shindig.common.crypto.BlobCrypterException;
import org.exoplatform.ide.shindig.KeyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

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
         return SecurityTokenGenerator.createToken(tokenRequest,KeyCreator.getKeyFilePath());
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
  
}
