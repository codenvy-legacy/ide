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

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceOutputErrorTest extends BaseTest
{

   private final static String FILE_NAME = "OutputErrorTest.groovy";
   
   private final static String TEST_FOLDER = "Outputerror";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER + "/";
   
   
   @BeforeClass
   public static void setUp()
   {
      
      String filePath ="src/test/resources/org/exoplatform/ide/operation/restservice/OutputError.groovy";
      try
      {
        //**************change**********
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE,TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, URL + FILE_NAME);
        //*****************************
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
   public void testOutputError() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(TEST_FOLDER);
      
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE, true);
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/body/"));
      
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      launchRestService();

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium
         .isElementPresent("//nobr[contains(text(), '/outputError/Inner/{first}/{second}/node/{paramList: .+}')]"));
      assertTrue(selenium.isElementPresent("//nobr[contains(text(), '/outputError')]"));

      selenium.click("//nobr[contains(text(), '/outputError')]");

      assertEquals("OPTIONS", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      assertEquals("", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/textbox"));

      assertEquals("application/vnd.sun.wadl+xml", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      assertEquals("No items to show.", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");
      assertEquals("No items to show.", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceQueryTab]/");


      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "wrong address/outputError");
      Thread.sleep(TestConstants.SLEEP);
      
      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/body/"));

      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), '/outputError/Inner/{first}/{second}/node/{paramList: .+}')]");

      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "/outputError/Inner/first/second/node/node1/node2/node3");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/[icon='picker']");

      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.click("//nobr[contains(text(), 'GET')]");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      Thread.sleep(TestConstants.SLEEP);
      String mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");

      assertTrue(mess.contains("First Param:first; Second Param:second; Param List:node1/node2/node3"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      String url = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
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
