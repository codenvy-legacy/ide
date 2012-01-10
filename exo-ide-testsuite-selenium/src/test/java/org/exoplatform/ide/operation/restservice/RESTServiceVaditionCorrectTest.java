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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */

//IDE-24
public class RESTServiceVaditionCorrectTest extends BaseTest
{
   private final static String FILE_NAME = "VaditionCorrectTest.groovy";

   private final static String PROJECT = RESTServiceVaditionCorrectTest.class.getSimpleName();

   private static final String VALID_SCRIPT = "// simple groovy script\n" + "import javax.ws.rs.Path\n"
      + "import javax.ws.rs.GET\n" + "import javax.ws.rs.PathParam\n \n" + "@Path(\"/\")\n"
      + "public class HelloWorld {\n" + "@GET\n" + "@Path(\"helloworld/{name}\")\n"
      + "public String hello(@PathParam(\"name\") String name) {\n" + "return \"Hello \" + name\n" + "}\n" + "}\n";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFile(link, FILE_NAME, MimeType.GROOVY_SERVICE, VALID_SCRIPT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testValidaton() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      String message = IDE.REST_SERVICE.validate(1);
      assertTrue(message.contains(FILE_NAME + " validated successfully."));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
