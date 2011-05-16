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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */

//IDE-24
public class RESTServiceVaditionCorrectTest extends BaseTest
{
   private final static String FILE_NAME = "VaditionCorrectTest.groovy";

   private final static String FOLDER = RESTServiceVaditionCorrectTest.class.getSimpleName();
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";

   private static final String VALID_SCRIPT =
      "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
         + "import javax.ws.rs.PathParam\n \n" + "@Path(\"/\")\n" + "public class HelloWorld {\n" + "@GET\n"
         + "@Path(\"helloworld/{name}\")\n" + "public String hello(@PathParam(\"name\") String name) {\n"
         + "return \"Hello \" + name\n" + "}\n" + "}\n";

   @BeforeClass
   public static void setUp()
   {

      try
      {
         VirtualFileSystemUtils.mkcol(URL);   
         VirtualFileSystemUtils.put(VALID_SCRIPT.getBytes(), MimeType.GROOVY_SERVICE,
            TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

  @Test
   public void testValidaton() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
       IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      
      IDE.NAVIGATION.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + FILE_NAME);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      IDE.REST_SERVICE.validate(FILE_NAME, 1);
   }
  
   
  
  
  @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}
