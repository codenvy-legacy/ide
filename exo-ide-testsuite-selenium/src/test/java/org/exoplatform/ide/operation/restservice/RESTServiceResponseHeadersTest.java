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
import static org.junit.Assert.assertFalse;
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
public class RESTServiceResponseHeadersTest extends BaseTest
{

   private final static String FILE_NAME = "ResponseHeaders.groovy";
   private final static String FOLDER_NAME = RESTServiceResponseHeadersTest.class.getSimpleName();
   
   private final static String URL = BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/";

   
   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/ResponseHeaders.groovy";
      try
      {
         //TODO***************change******************
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
         //**********************************
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
   public void testResponseHeaders() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
       IDE.NAVIGATION.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      selectFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
          
    
      assertEquals("[INFO] " + BASE_URL + "rest/private/" + WEBDAV_CONTEXT + "/repository/dev-monit/"+FOLDER_NAME+"/"+ FILE_NAME
         + " deployed successfully.", selenium.getText("//div[contains(@eventproxy,'Record_0')]"));

      IDE.REST_SERVICE.launchRestService();

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), '/test/testgroovy/{name}')]");

      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "/test/testgroovy/Evgen");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      String mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");

      assertTrue(mess.contains("[OUTPUT] - -Status - - - - - - - -"));

      assertTrue(mess.contains("200 OK"));
      assertTrue(mess.contains("Content-Type : */*"));
      assertTrue(mess.contains("- -Text - - - - - - - - -"));
      assertTrue(mess.contains("Hello Evgen"));
   }
   
   
   
   protected void selectFolder(String folderName) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + folderName
         + "]/col[1]/open");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }
   
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL);
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
