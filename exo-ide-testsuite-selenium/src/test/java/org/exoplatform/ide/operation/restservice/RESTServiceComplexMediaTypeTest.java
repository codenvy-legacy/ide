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
public class RESTServiceComplexMediaTypeTest extends BaseTest
{

   private final static String FILE_NAME = "ComplexMediaType.groovy";
   
   private final static String FOLDER_NAME="Test";
   
 //**************
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME+ "/"; 
   
   @BeforeClass
   public static void setUp()
   {
      
      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/ComplexMediaTypes.groovy";
      try
      {
         //TODO*******change***************
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL+FILE_NAME);
         //**********************
         Utils.deployService(BASE_URL, REST_CONTEXT, URL+FILE_NAME);
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
   public void testComplexMediaType() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(FOLDER_NAME);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      //Call the "Run->Launch REST Service" topmenu command
      launchRestService();

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("//nobr[contains(text(), '/testMediaTypes')]"));
      assertTrue(selenium.isElementPresent("//nobr[contains(text(), '/testMediaTypes/InnerPath')]"));

      selenium.click("//nobr[contains(text(), '/testMediaTypes')]");

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      //Expected 3
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      Thread.sleep(TestConstants.SLEEP);

      String mess = selenium.getText("//div[contains(@eventproxy,'Record_0')]");

      assertTrue(mess
         .contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\">"));

      launchRestService();

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("//nobr[contains(text(), '/testMediaTypes/InnerPath')]");

      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("POST", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("//nobr[contains(text(), 'application/json')]"));
      assertTrue(selenium.isElementPresent("//nobr[contains(text(), 'text/plain')]"));

      selenium.click("//nobr[contains(text(), 'text/plain')]");
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));
    
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), 'application/json')]");
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "/testMediaTypes/InnerPath");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceBodyTab]/");
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceBodyForm\"]/item[name=ideGroovyServiceBodyFormText]/element");
      selenium.type(
         "scLocator=//DynamicForm[ID=\"ideGroovyServiceBodyForm\"]/item[name=ideGroovyServiceBodyFormText]/element",
         "{\"value\" : \"value4\"}");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      
      Thread.sleep(TestConstants.SLEEP);

      mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");

      assertTrue(mess.contains("Body: value4"));
      
      
      launchRestService();

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);
      selenium.click("//nobr[contains(text(), '/testMediaTypes/InnerPath')]");

      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("POST", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/textbox"));

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("//nobr[contains(text(), 'application/json')]"));
      assertTrue(selenium.isElementPresent("//nobr[contains(text(), 'text/plain')]"));

      selenium.click("//nobr[contains(text(), 'text/plain')]");
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));
    
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceRequest]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), 'text/plain')]");
      Thread.sleep(TestConstants.SLEEP);

      assertEquals("text/plain", selenium
         .getText("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceResponse]/textbox"));

      selenium.type("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath]/element",
         "/testMediaTypes/InnerPath");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//TabSet[ID=\"ideGroovyServiceTabSet\"]/tab[ID=ideGroovyServiceBodyTab]/");
      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceBodyForm\"]/item[name=ideGroovyServiceBodyFormText]/element");
      selenium.type(
         "scLocator=//DynamicForm[ID=\"ideGroovyServiceBodyForm\"]/item[name=ideGroovyServiceBodyFormText]/element",
         "{\"value\" : \"value4\"}");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      mess = selenium.getText("//div[contains(@eventproxy,'Record_2')]");

      assertTrue(mess.contains("{\"value\" : \"value4\"}"));
      
      
      closeTab("0");
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
