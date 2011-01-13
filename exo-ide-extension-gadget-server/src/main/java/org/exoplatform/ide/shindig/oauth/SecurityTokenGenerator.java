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

import java.io.File;
import java.io.IOException;

import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BasicBlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SecurityTokenGenerator
{

   public static TokenResponse createToken(TokenRequest tokenRequest, String keyFile) throws IOException,
      BlobCrypterException
   {
      BasicBlobCrypter crypter = new BasicBlobCrypter(new File(keyFile));

      BlobCrypterSecurityToken t = new BlobCrypterSecurityToken(crypter, tokenRequest.getContainer(), null);
      t.setAppUrl(tokenRequest.getGadgetURL());
      t.setModuleId(tokenRequest.getModuleId());
      t.setOwnerId(tokenRequest.getOwner());
      t.setViewerId(tokenRequest.getViewer());
      t.setTrustedJson("trusted");
      String securityToken = t.encrypt();
      TokenResponse tokenResponse =
         new TokenResponse(securityToken, tokenRequest.getGadgetURL(), tokenRequest.getModuleId());
      return tokenResponse;
   }
}