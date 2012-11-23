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
package org.exoplatform.ide.vfs.server.impl.memory;

import org.apache.commons.io.IOUtils;
import org.everrest.core.impl.ContainerResponse;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: ImportTest.java Nov 21, 2012 vetal $
 *
 */
public class ImportTest extends MemoryFileSystemTest
{

   public void testImportProject() throws Exception
   {
      String path = SERVICE_URI + "import/" + testRoot.getId();
      ContainerResponse response =
         launcher.service(
            "POST",
            path,
            BASE_URI,
            null,
            IOUtils.toByteArray(Thread.currentThread().getContextClassLoader()
               .getResourceAsStream("exo-ide-client.zip")), null);
      System.out.println("ImportTest :: " + response.getEntity());
      assertEquals(204, response.getStatus());
   }

}
