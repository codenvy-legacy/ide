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
package org.exoplatform.ide.extension.gadget.server.rest;

import org.exoplatform.ide.extension.gadget.server.shindig.oauth.TokenRequest;
import org.exoplatform.ide.extension.gadget.server.shindig.oauth.TokenResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/shindig/securitytoken")
public class RestSecurityTokenGenerator {

    @POST
    @Path("/createToken")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public TokenResponse createToken(TokenRequest tokenRequest) {

//       BlobCrypter blobCrypter = getBlobCrypter();
//       BlobCrypterSecurityToken t = new BlobCrypterSecurityToken(blobCrypter, container, null);
//
//       t.setAppUrl(gadgetURL);
//       t.setModuleId(moduleId);
//       t.setOwnerId(owner);
//       t.setViewerId(viewer);
//       t.setTrustedJson("trusted");

        //TODO : need study more detail hoe to work with token in 2.5.x version
        // currently allow anonymous access (tokens don't check)
        TokenResponse response = new TokenResponse();
        response.setModuleId(tokenRequest.getModuleId());
        response.setGadgetURL(tokenRequest.getGadgetURL());
        return new TokenResponse();
    }

}
