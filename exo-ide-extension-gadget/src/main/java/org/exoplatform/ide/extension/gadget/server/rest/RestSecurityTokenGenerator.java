/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
