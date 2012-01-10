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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
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
public class RESTServiceResponseHeadersTest extends BaseTest
{

   private final static String FILE_NAME = "ResponseHeaders.grs";

   private final static String PROJECT = RESTServiceResponseHeadersTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/ResponseHeaders.groovy";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }

   @Test
   public void testResponseHeaders() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 5);

      assertEquals("[INFO] " + "/" + PROJECT + "/" + FILE_NAME + " deployed successfully.",
         IDE.OUTPUT.getOutputMessage(1));

      IDE.REST_SERVICE.launchRestService();

      IDE.REST_SERVICE.selectPath("/test/testgroovy/{name}");

      IDE.REST_SERVICE.typeToPathField("/test/testgroovy/Evgen");
      IDE.REST_SERVICE.sendRequest();

      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
      String mess = IDE.OUTPUT.getOutputMessage(2);

      assertTrue(mess.contains("[OUTPUT] - -Status - - - - - - - -"));
      assertTrue(mess.contains("200 OK"));
      assertTrue(mess.contains("Content-Type : */*"));
      assertTrue(mess.contains("- -Text - - - - - - - - -"));
      assertTrue(mess.contains("Hello Evgen"));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(3, 5);
      assertTrue(IDE.OUTPUT.getOutputMessage(3).contains("/" + PROJECT + "/" + FILE_NAME + " undeployed successfully."));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         //TODO Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
