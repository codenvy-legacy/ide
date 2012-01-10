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
public class RESTServiceOutputErrorTest extends BaseTest
{

   private final static String FILE_NAME = "OutputErrorTest.grs";

   private final static String PROJECT = RESTServiceOutputErrorTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/OutputError.groovy";
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
   public void testOutputError() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE));
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE);

      IDE.WARNING_DIALOG.waitOpened();
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 5);

      IDE.REST_SERVICE.launchRestService();

      assertTrue(IDE.REST_SERVICE.isPathPresent("/outputError/Inner/{first}/{second}/node/{paramList: .+}"));
      assertTrue(IDE.REST_SERVICE.isPathPresent("/outputError"));
      IDE.REST_SERVICE.selectPath("/outputError");

      assertEquals("OPTIONS", IDE.REST_SERVICE.getMethodValue());
      assertEquals("", IDE.REST_SERVICE.getRequestMediaTypeFieldValue());
      assertEquals("application/vnd.sun.wadl+xml", IDE.REST_SERVICE.getResponseMediaTypeValue());
      assertEquals(0, (IDE.REST_SERVICE.getQueryParameterCount()));

      IDE.REST_SERVICE.selectHeaderParametersTab();
      assertEquals(0, IDE.REST_SERVICE.getHeaderParameterCount());

      IDE.REST_SERVICE.selectQueryParametersTab();
      IDE.REST_SERVICE.typeToPathField("wrong address/outputError");
      IDE.REST_SERVICE.clickSendButton();

      IDE.WARNING_DIALOG.waitOpened();
      IDE.WARNING_DIALOG.clickOk();

      IDE.REST_SERVICE.selectPath("/outputError/Inner/{first}/{second}/node/{paramList: .+}");
      IDE.REST_SERVICE.typeToPathField("/outputError/Inner/first/second/node/node1/node2/node3");

      IDE.REST_SERVICE.setMethodFieldValue("GET");

      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitForMessageShow(2, 5);

      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess.contains("First Param:first; Second Param:second; Param List:node1/node2/node3"));
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      try
      {
         IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
