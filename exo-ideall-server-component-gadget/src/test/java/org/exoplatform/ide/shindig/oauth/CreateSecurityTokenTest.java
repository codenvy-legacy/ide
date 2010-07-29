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

import java.io.IOException;

import org.apache.shindig.common.crypto.BlobCrypterException;
import org.exoplatform.ide.AbstractResourceTest;
import org.exoplatform.ide.shindig.oauth.SecurityTokenGenerator;
import org.exoplatform.ide.shindig.oauth.TokenRequest;
import org.exoplatform.ide.shindig.oauth.TokenResponse;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateSecurityTokenTest extends AbstractResourceTest
{
   private String gadgetURL = "http://loaclhost:8080/gadgets/somegadget.xml";

   private String owner = "root";

   private String viewer = "root";

   private Long moduleId = 0L;

   private String container = "default";

   private String domain = null;
   
   private String keyFile = "src/test/resources/conf/key.txt";

   
   public void testCreateSecurityToken() throws IOException, BlobCrypterException
   {
      TokenRequest tokenRequest = new TokenRequest(gadgetURL, owner, viewer, moduleId, container, domain);
      System.out.println("CreateSecurityTokenTest.testCreateSecurityToken()" + tokenRequest.toString());
      TokenResponse token = SecurityTokenGenerator.createToken(tokenRequest, keyFile);
      System.out.println("CreateSecurityTokenTest.testCreateSecurityToken()"  + token.toString());
   }
   
}
