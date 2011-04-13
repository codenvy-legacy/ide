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
public class RESTServiceOutputTest extends BaseTest
{

   private final static String FILE_NAME = "RESTServiceOutputTest.groovy";

   private final static String TEST_FOLDER = RESTServiceOutputTest.class.getSimpleName();
   
   private final static String URL = BASE_URL +  REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceOutput.groovy";
      try
      {
       //TODO***********change************
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
      //*************************
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
   public void testOutput() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
       IDE.navigator().selectItem(WS_URL);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      //TODO***********change************
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().clickOpenIconOfFolder(URL);
      Thread.sleep(TestConstants.SLEEP);
      //****************************
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      IDE.menu().runCommand("Run", MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.toolbar().runCommand(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      //Expect 1
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      assertEquals("/overralTest/Inner/node/{paramList: .+}", selenium
         .getValue("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element"));

      assertEquals("POST", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/textbox"));

      assertEquals("application/xml;charset=utf-8", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      assertEquals("Test Query Parameter 1", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[1]"));

      assertEquals("string", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[2]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[4]"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");

      assertEquals("Test-Header1", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[1]"));

      assertEquals("string", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[2]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[4]"));

      assertFalse(selenium.isElementPresent("//td[@class='tabTitleSelectedDisabled']"));

      //Step 3
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), '/overralTest')]");

      //Expected 2
      assertEquals("/overralTest", selenium
         .getValue("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element"));

      assertEquals("OPTIONS", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      assertEquals("", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/textbox"));

      assertEquals("application/vnd.sun.wadl+xml", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      assertEquals("No items to show.", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body"));

      assertEquals("No items to show.", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body"));

      //Step 4
      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      //Expected 3
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      Thread.sleep(TestConstants.SLEEP);

      String mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");

      assertTrue(mess
         .contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\">"));

      //Step 5
      IDE.toolbar().runCommand(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      for (int i = 0; i < 15; i++)
      {
         selenium.keyPress(
            "scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element", "\\8");
      }

      selenium.typeKeys("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "param1");

      selenium
         .click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab||title=Header%20Parameter||index=1]/");
//      selenium
//         .click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[name=Test-Header1]/col[fieldName=value||4]");
//      selenium
//         .click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[name=Test-Header1]/col[fieldName=value||4]");
      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.keyPress("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]", "\\13");
//      selenium.typeKeys("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/editRowForm/item[name=value||title=value||value=||index=4||Class=TextItem]/element", "123");
      selenium
         .type(
            "scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/editRowForm/item[name=value||title=value||value=test||index=4||Class=TextItem]/element",
            "test");
      //Expected 4
      assertEquals("POST", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/textbox"));

      assertEquals("application/xml;charset=utf-8", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      assertEquals("Test Query Parameter 1", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[1]"));

      assertEquals("string", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[2]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[4]"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");

      assertEquals("Test-Header1", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[1]"));

      assertEquals("string", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[2]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[3]"));

      assertEquals("test", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[4]"));

      //Step 6
      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      //Expected 5
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      Thread.sleep(TestConstants.SLEEP);
      mess = selenium.getText("//div[contains(@eventproxy,'Record_2')]");

      assertTrue(mess.contains("Param List 1:param1; Test Query Parameter 1: ; Test-Header 1: test; Body:"));

      IDE.toolbar().runCommand(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      //Step 7
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      for (int i = 0; i < 15; i++)
      {
         selenium.keyPress(
            "scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element", "\\8");
      }

      selenium.typeKeys("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "param1");

//      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[fieldName=value||4]");
//      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.keyPress("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[fieldName=value||4]", "\\13");
      selenium.type("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/editRowForm/item[name=value]/element",
         "value 1");
      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");
//      selenium
//         .click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[name=Test-Header1]/col[fieldName=value||4]");
//      selenium
//         .click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[name=Test-Header1]/col[fieldName=value||4]");
      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.keyPress("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]", "\\13");
      selenium.type("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/editRowForm/item[name=value]/element",
         "value 2");
      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceBodyTab]/");
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceBodyForm\"]/item[name=ideGroovyServiceBodyFormText]/element");
      selenium.type(
         "scLocator=//DynamicForm[ID=\"ideGroovyServiceBodyForm\"]/item[name=ideGroovyServiceBodyFormText]/element",
         "Ð¿Ñ€Ð¸ÐºÐ»Ð°Ð´ Ð¿Ð¾Ð²Ñ–Ð´Ð¾Ð¼Ð»ÐµÐ½Ð½Ñ�");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      //Expected 8
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      Thread.sleep(TestConstants.SLEEP);
      mess = selenium.getText("//div[contains(@eventproxy,'Record_3')]");

      assertTrue(mess
         .contains("Param List 1:param1; Test Query Parameter 1: value 1; Test-Header 1: value 2; Body:Ð¿Ñ€Ð¸ÐºÐ»Ð°Ð´ Ð¿Ð¾Ð²Ñ–Ð´Ð¾Ð¼Ð»ÐµÐ½Ð½Ñ�"));

      IDE.toolbar().runCommand(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      //Step 10
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "/overralTest/Inner/node/param1/param2/param3");
      Thread.sleep(TestConstants.SLEEP);
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/[icon='picker']");

      selenium.click("//nobr[contains(text(), 'GET')]");

      assertEquals("", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/textbox"));

      assertEquals("text/html", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      assertEquals("Test Query Parameter 2", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[1]"));

      assertEquals("string", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[2]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[4]"));

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceHeaderTab]/");

      assertEquals("Test-Header2", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[1]"));

      assertEquals("string", selenium
         .getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[2]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[3]"));

      assertEquals("", selenium.getText("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[4]"));

      assertFalse(selenium.isElementPresent("//td[@class='tabTitleSelectedDisabled']"));

      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.keyPress("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/body/row[0]/col[fieldName=value||4]", "\\13");
      selenium.type("scLocator=//ListGrid[ID=\"ideGroovyServiceHeaderTable\"]/editRowForm/item[name=value]/element",
         "value 2");
      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceQueryTable]/");

      selenium.click("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[fieldName=value||4]");
      selenium.keyPress("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/body/row[0]/col[fieldName=value||4]", "\\13");
      selenium.type("scLocator=//ListGrid[ID=\"ideGroovyServiceQueryTable\"]/editRowForm/item[name=value]/element",
         "value 1");
      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      //Expected 11
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      Thread.sleep(TestConstants.SLEEP);
      mess = selenium.getText("//div[contains(@eventproxy,'Record_4')]");

      assertTrue(mess.contains("Param List 2:param1/param2/param3; Test Query Parameter 2: ; Test-Header 2: value 2"));

      

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
