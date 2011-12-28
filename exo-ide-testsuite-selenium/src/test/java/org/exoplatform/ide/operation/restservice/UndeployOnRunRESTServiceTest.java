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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2010 $
 *
 */
public class UndeployOnRunRESTServiceTest extends BaseTest
{

   private final static String FOLDER_NAME = UndeployOnRunRESTServiceTest.class.getSimpleName();

   private final static String SIMPLE_FILE_NAME = "RestServiceExample.groovy";

   private final static String FILE_NAME = "kjfdshglksfdghldsfbg.groovy";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath + SIMPLE_FILE_NAME, MimeType.GROOVY_SERVICE, URL + SIMPLE_FILE_NAME);
         VirtualFileSystemUtils.put(filePath + SIMPLE_FILE_NAME, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + SIMPLE_FILE_NAME);
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testUndeployOnRunRestService() throws Exception
   {
      //open file
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + SIMPLE_FILE_NAME);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + SIMPLE_FILE_NAME, false);

      //call Run Groovy Service command
      IDE.REST_SERVICE.runRESTServiceInSanbox();
      //close
      IDE.REST_SERVICE.sendRequst();
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //3
      String text = IDE.OUTPUT.getOutputMessage(4);
      assertTrue(text.endsWith(SIMPLE_FILE_NAME + " undeployed successfully."));

      IDE.EDITOR.closeFile(0);
   }

   @Test
   public void testUndeloyOnCancel() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("30000");
      IDE.WORKSPACE.waitForRootItem();
      //open file

      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      IDE.WORKSPACE.waitForItem(URL + FILE_NAME);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);

      //call Run Groovy Service command
      IDE.REST_SERVICE.runRESTServiceInSanbox();
      //close
      IDE.REST_SERVICE.closeForm();

      Thread.sleep(TestConstants.SLEEP_SHORT);
      //3
      String text = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(text.endsWith(FILE_NAME + " undeployed successfully."));

      IDE.EDITOR.closeFile(0);
   }

}
