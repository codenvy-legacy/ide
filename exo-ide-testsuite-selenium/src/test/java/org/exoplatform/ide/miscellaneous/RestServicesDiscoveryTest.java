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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.restservice.RESTServiceDefaultHTTPParametersTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 22, 2010 3:49:01 PM evgen $
 *
 */
public class RestServicesDiscoveryTest extends BaseTest
{

   private final static String FILE_NAME = "Rest.grs";

   private final static String TEST_FOLDER = RESTServiceDefaultHTTPParametersTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/miscellaneous/rest_service_discovery.groovy";
      try
      {
         //**************TODO***********change add folder for locked file
         VirtualFileSystemUtils.mkcol(URL);
         //***********************************************************

         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
         Thread.sleep(TestConstants.SLEEP_SHORT);
         Utils.deployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testRestServicesDiscovery() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      
      // open folder
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(TEST_FOLDER);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);     
      Thread.sleep(TestConstants.SLEEP);      
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      IDE.menu().runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      Thread.sleep(TestConstants.SLEEP);
      IDE.menu().runCommand(MenuCommands.Help.HELP, MenuCommands.Help.REST_SERVICES);
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideRestServiceDiscovery\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideRestServiceDiscoveryOkButton\"]/"));

      assertEquals("/aa", getTitle(0, 0));

      openNode(0, 0);

      openNode(1, 0);

      assertEquals("/testService11", getTitle(1, 0));

      //      assertEquals("/Inner/{pathParam}", selenium.getText("scLocator=//TreeGrid[ID=\"ideRestServiceTreeGrid\"]/body/row[2]/col[0]"));
      assertEquals("/Inner/{pathParam}", getTitle(2, 0));

      openNode(2, 0);

      assertEquals("GET", getTitle(3, 0));

      assertEquals("POST", getTitle(4, 0));

      assertEquals("OPTIONS", getTitle(5, 0));

      //      clickNode(1);

      assertFalse(selenium.isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/"));
      assertFalse(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideRestServiceDiscoveryForm\"]/item[name=ideResponseType]/element"));
      assertFalse(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideRestServiceDiscoveryForm\"]/item[name=ideRequestType]/element"));

      clickNode(3);

      assertEquals("application/xml",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideRestServiceDiscoveryForm\"]/item[name=ideRequestType]/element"));

      assertEquals("*/*",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideRestServiceDiscoveryForm\"]/item[name=ideResponseType]/element"));

      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/row[name=Test-Header]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/row[name=TestQueryParam%201]/col[0]"));
      assertTrue(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/row[name=pathParam||default=pathParam%20Default]/col[0]"));

      clickNode(5);
      
      assertEquals("n/a",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideRestServiceDiscoveryForm\"]/item[name=ideRequestType]/element"));

      assertEquals("application/vnd.sun.wadl+xml",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideRestServiceDiscoveryForm\"]/item[name=ideResponseType]/element"));

      assertFalse(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/row[name=Test-Header]/col[0]"));
      assertFalse(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/row[name=TestQueryParam%201]/col[0]"));
      assertFalse(selenium
         .isElementPresent("scLocator=//ListGrid[ID=\"ideRestServiceDiscoveryParameters\"]/body/row[name=pathParam||default=pathParam%20Default]/col[0]"));
      assertTrue(selenium.isElementPresent("//input[@name='ideRequestType' and @class='textItemDisabled']"));
      selenium.click("scLocator=//IButton[ID=\"ideRestServiceDiscoveryOkButton\"]/");

   }

   /**
    * click the outline item node
    * @param rowNumber startign from 0
    * @throws Exception
    */
   protected static void clickNode(int rowNumber) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideRestServiceTreeGrid\"]/body/row[" + String.valueOf(rowNumber)
         + "]/col[0]");
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * @throws InterruptedException
    */
   private void openNode(int row, int col) throws InterruptedException
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideRestServiceTreeGrid\"]/body/row[" + String.valueOf(row) + "]/col["
         + String.valueOf(col) + "]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   private String getTitle(int row, int col)
   {
      return selenium.getText("scLocator=//TreeGrid[ID=\"ideRestServiceTreeGrid\"]/body/row[" + String.valueOf(row)
         + "]/col[" + String.valueOf(col) + "]");
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);
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
