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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceGetURLTest extends BaseTest
{

   private static final String FILE_NAME = "RESTServiceGetURL.groovy";

   private final static String TEST_FOLDER = RESTServiceGetURLTest.class.getSimpleName() ;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         //TODO*****************change**************change add folder for locked file
         String url = BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER + "/";
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, url + FILE_NAME);
         //**********************
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
   public void testGetUrl() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
       IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);

      //TODO**********change************
      IDE.navigator().clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(TestConstants.SLEEP);
      //****************************

      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      IDE.menu().runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.toolbar().runCommand(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), '/testService')]");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceGetURL\"]");

      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGetRestServiceURLForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideGetRestServiceURLFormOkButton\"]"));

      String url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetRestServiceURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");

      assertTrue((BASE_URL + "rest/private/testService").equals(url));
      //Close form
      selenium.click("scLocator=//IButton[ID=\"ideGetRestServiceURLFormOkButton\"]");

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("//nobr[contains(text(), '/testService/Inner/{pathParam}')]");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceGetURL\"]");

      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGetRestServiceURLForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideGetRestServiceURLFormOkButton\"]"));

      url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetRestServiceURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");

      assertTrue((BASE_URL + "rest/private/testService/Inner/{pathParam}").equals(url));

      //Close form
      selenium.click("scLocator=//IButton[ID=\"ideGetRestServiceURLFormOkButton\"]");

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("//nobr[contains(text(), '/testService/Inner/{param}/node/{paramList: .+}')]");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceGetURL\"]");

      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGetRestServiceURLForm\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideGetRestServiceURLFormOkButton\"]"));

      url =
         selenium
            .getValue("scLocator=//Window[ID=\"ideGetRestServiceURLForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideGetItemURLFormURLField]/element");

      assertTrue((BASE_URL + "rest/private/testService/Inner/{param}/node/{paramList: .+}").equals(url));

      //Close form
      selenium.click("scLocator=//IButton[ID=\"ideGetRestServiceURLFormOkButton\"]");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceCancel\"]/");

   }

   @AfterClass
   public static void tearDown()
   {
      String url = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, url);
         VirtualFileSystemUtils.delete(url);
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
