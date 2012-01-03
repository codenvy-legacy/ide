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
import org.exoplatform.ide.Utils;
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
public class RESTServiceOutputErrorTest extends BaseTest
{

   private final static String FILE_NAME = "OutputErrorTest.groovy";

   private final static String TEST_FOLDER = RESTServiceOutputErrorTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/OutputError.groovy";
      try
      {

         //TODO**************change**********
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE,
            TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, URL + FILE_NAME);
         //*****************************
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testOutputError() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);

      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE, true);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE);

      IDE.WARNING_DIALOG.waitOpened();

      IDE.WARNING_DIALOG.clickOk();

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.REST_SERVICE.launchRestService();

      
      IDE.REST_SERVICE.selectPath("/outputError/Inner/{first}/{second}/node/{paramList: .+}");
      IDE.REST_SERVICE.selectPath("/outputError");

      IDE.REST_SERVICE.selectPath("/outputError");

      assertEquals("OPTIONS", IDE.REST_SERVICE.getMethodValue());

      assertEquals("", IDE.REST_SERVICE.getRequestMediaTypeFieldValue());

      assertEquals("application/vnd.sun.wadl+xml", IDE.REST_SERVICE.getResponseMediaTypeValue());

      assertEquals("",
         (IDE.REST_SERVICE.getQueryParameterName(1)));

      IDE.REST_SERVICE.selectHeaderParametersTab();
      assertEquals("",
         IDE.REST_SERVICE.getHeaderParameterName(1));

      IDE.REST_SERVICE.selectQueryParametersTab();

      IDE.REST_SERVICE.typeToPathField("wrong address/outputError");

      IDE.REST_SERVICE.clickSendButton();

      IDE.WARNING_DIALOG.waitOpened();

      IDE.WARNING_DIALOG.clickOk();

   

      IDE.REST_SERVICE.selectPath("/outputError/Inner/{first}/{second}/node/{paramList: .+}");
      IDE.REST_SERVICE.typeToPathField("/outputError/Inner/first/second/node/node1/node2/node3");

      IDE.REST_SERVICE.setMethodFieldValue("GET");

      IDE.REST_SERVICE.sendRequest();

      Thread.sleep(TestConstants.SLEEP);
      String mess = IDE.OUTPUT.getOutputMessage(2);

      assertTrue(mess.contains("First Param:first; Second Param:second; Param List:node1/node2/node3"));
   }

   @AfterClass
   public static void tearDown()
   {
      String url = URL + FILE_NAME;
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, url);
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
