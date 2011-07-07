/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.ruby.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.junit.Test;

import java.io.File;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RubyAppServiceTest extends Base
{

   @Test
   public void testCreate() throws Exception
   {
      ContainerResponse cres =
         launcher.service("POST", "/ide/application/ruby/create?name=myRuby&workdir=rest/repository/dev-monit", "",
            null, null, null, null);

      System.out.println(cres.getStatus());
      assertEquals(HTTPStatus.OK, cres.getStatus());

      File app = new File("target/git/repository/dev-monit/myRuby");
      assertTrue(app.exists());
      assertTrue(app.list().length > 0);
   }

}
