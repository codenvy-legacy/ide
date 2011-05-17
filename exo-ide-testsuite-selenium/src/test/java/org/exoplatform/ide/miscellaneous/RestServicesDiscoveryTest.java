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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.restservice.RESTServiceDefaultHTTPParametersTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

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
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.REST_SERVICES);
      waitForElementPresent("//div[@view-id=\"ideResrServicesDiscoveryView\"]");
      assertTrue(selenium.isElementPresent("//div[@view-id=\"ideResrServicesDiscoveryView\"]"));
      assertTrue(selenium.isElementPresent("exoRestServicesDiscoveryOkButton"));
      
      openNode(Utils.md5("/aa"));
      waitForElementPresent(Utils.md5("/aa/testService11/"));
      openNode(Utils.md5("/aa/testService11/"));
      waitForElementPresent(Utils.md5("/aa/testService11/Inner/{pathParam}"));

      openNode(Utils.md5("/aa/testService11/Inner/{pathParam}"));
      waitForElementPresent(Utils.md5("/aa/testService11/Inner/{pathParam}") + ":POST");
      assertTrue(selenium.isElementPresent(Utils.md5("/aa/testService11/Inner/{pathParam}") + ":POST"));
      assertTrue(selenium.isElementPresent(Utils.md5("/aa/testService11/Inner/{pathParam}") + ":GET"));

      selectNode(Utils.md5("/aa/testService11/Inner/{pathParam}"));

      assertFalse(selenium.isVisible("ideRestServiceDiscoveryParameters"));
      assertFalse(selenium.isVisible("ideResponseType"));
      assertFalse(selenium.isVisible("ideRequestType"));

      selectNode(Utils.md5("/aa/testService11/Inner/{pathParam}") + ":POST");

      assertEquals("application/xml", selenium.getValue("ideRequestType"));

      assertEquals("*/*", selenium.getValue("ideResponseType"));

      selectNode(Utils.md5("/aa/testService11/") + ":OPTIONS");

      assertEquals("n/a", selenium.getValue("ideRequestType"));

      assertEquals("application/vnd.sun.wadl+xml", selenium.getValue("ideResponseType"));

      selenium.click("exoRestServicesDiscoveryOkButton");

   }

   private void openNode(String id)
   {
      String locator = "//div[@id='" + id + "']/table/tbody/tr/td[1]/img";
      selenium.clickAt(locator, "0");
   }

   /**
    * click the item node
    * @param rowID
    * @throws Exception
    */
   protected static void selectNode(String rowID) throws Exception
   {
      selenium.clickAt(rowID, "0");
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
