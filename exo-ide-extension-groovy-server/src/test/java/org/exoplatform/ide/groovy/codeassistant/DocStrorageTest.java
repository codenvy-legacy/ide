/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ide.groovy.codeassistant;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.groovy.Base;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.junit.Test;

import java.io.File;
import java.net.URLEncoder;

import javax.ws.rs.core.SecurityContext;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DocStrorageTest extends Base
{
   
   private String jar;
   
   public void setUp() throws Exception
   {
//     super.setUp();
//     jar = "src/test/resources/jsr311-api-1.0-sources.jar";
      super.setUp();
      jar = "src/test/resources/jsr311-api-1.0-sources.jar";
      File file = new File(jar);
      assertTrue(file.exists());
      jar = URLEncoder.encode(file.getAbsolutePath(),"UTF-8");
   }
   
   
   
   @Test
   public void testAddDocFormJar() throws Exception
   {
      EnvironmentContext ctx = new EnvironmentContext();
      ctx.put(SecurityContext.class, adminSecurityContext);
      ContainerResponse cres = launcher.service("POST", "/ide/doc-storage/jar-source?jar=" + jar, "", null, null, null, ctx);
      assertEquals(HTTPStatus.NO_CONTENT, cres.getStatus());
      assertTrue(root.hasNode("dev-doc/javax/javax.ws/javax.ws.rs/javax.ws.rs.POST"));
      assertTrue(root.hasNode("dev-doc/javax/javax.ws/javax.ws.rs/javax.ws.rs.GET"));
      assertTrue(root.hasNode("dev-doc/javax/javax.ws/javax.ws.rs/javax.ws.rs.ext/javax.ws.rs.ext.MessageBodyReader"));
      
   }

}
