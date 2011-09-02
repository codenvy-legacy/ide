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

import org.everrest.http.client.ModuleException;
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
public class RESTServiceOutputTest extends BaseTest
{

   private final static String FILE_NAME = "RESTServiceOutputTest.groovy";

   private final static String TEST_FOLDER = RESTServiceOutputTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

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
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      //TODO***********change************
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      Thread.sleep(TestConstants.SLEEP);
      //****************************
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);

      IDE.REST_SERVICE.deploy(TEST_FOLDER + "/" + FILE_NAME, 1);
      IDE.REST_SERVICE.launchRestService();

      //Expect 1
      checkFields("/overralTest/Inner/node/{paramList: .+}", "POST", "text/plain", "application/xml;charset=utf-8");

      checkQueryParameter(1, "Test Query Parameter 1", "string", "", "");

      IDE.REST_SERVICE.selectHeaderParametersTab();

      checkHeaderParameter(1, "Test-Header1", "string", "", "");

      //Step 3
      IDE.REST_SERVICE.selectPathValue("/overralTest");
      checkFields("/overralTest", "OPTIONS", "", "application/vnd.sun.wadl+xml");

      //Step 4
      IDE.REST_SERVICE.sendRequst();

      //Expected 3

      String mess = IDE.OUTPUT.getOutputMessageText(2);

      assertTrue(mess
         .contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\">"));

      //Step 5
      IDE.REST_SERVICE.launchRestService();

      typeToPathField();

      IDE.REST_SERVICE.selectHeaderParametersTab();
      IDE.REST_SERVICE.typeToHeaderParameterValue(1, "test");

      checkFields("/overralTest/Inner/node/param1", "POST", "text/plain", "application/xml;charset=utf-8");

      IDE.REST_SERVICE.selectQueryParametersTab();
      checkQueryParameter(1, "Test Query Parameter 1", "string", "", "");

      IDE.REST_SERVICE.selectHeaderParametersTab();

      checkHeaderParameter(1, "Test-Header1", "string", "", "test");
      //Step 6
      IDE.REST_SERVICE.sendRequst();
      mess = IDE.OUTPUT.getOutputMessageText(3);

      assertTrue(mess.contains("Param List 1:param1; Test Query Parameter 1: ; Test-Header 1: test; Body:"));

      IDE.REST_SERVICE.launchRestService();

      //Step 7
      typeToPathField();

      IDE.REST_SERVICE.typeToQueryParameterValue(1, "value 1");

      IDE.REST_SERVICE.selectHeaderParametersTab();
      IDE.REST_SERVICE.typeToHeaderParameterValue(1, "value 2");

      IDE.REST_SERVICE.selectBodyTab();

      IDE.REST_SERVICE.typeToBodyField("Ð¿Ñ€Ð¸ÐºÐ»Ð°Ð´ Ð¿Ð¾Ð²Ñ–Ð´Ð¾Ð¼Ð»ÐµÐ½Ð½Ñ�");
      IDE.REST_SERVICE.sendRequst();

      //Expected 8
      mess = IDE.OUTPUT.getOutputMessageText(4);

      assertTrue(mess
         .contains("Param List 1:param1; Test Query Parameter 1: value 1; Test-Header 1: value 2; Body:Ð¿Ñ€Ð¸ÐºÐ»Ð°Ð´ Ð¿Ð¾Ð²Ñ–Ð´Ð¾Ð¼Ð»ÐµÐ½Ð½Ñ�"));

      IDE.REST_SERVICE.launchRestService();
      //Step 10
      IDE.REST_SERVICE.typeToPathField("/overralTest/Inner/node/param1/param2/param3");

      IDE.REST_SERVICE.setMethodFieldValue("GET");
      assertEquals("", IDE.REST_SERVICE.getRequestMediaTypeFieldValue());
      assertEquals("text/html", IDE.REST_SERVICE.getResponseMediaTypeFieldValue());

      checkQueryParameter(1, "Test Query Parameter 2", "string", "", "");

      IDE.REST_SERVICE.selectHeaderParametersTab();

      checkHeaderParameter(1, "Test-Header2", "string", "", "");

      IDE.REST_SERVICE.typeToHeaderParameterValue(1, "value 2");

      IDE.REST_SERVICE.selectQueryParametersTab();

      IDE.REST_SERVICE.typeToQueryParameterValue(1, "value 1");

      IDE.REST_SERVICE.sendRequst();

      //Expected 11
      mess = IDE.OUTPUT.getOutputMessageText(5);

      assertTrue(mess
         .contains("Param List 2:param1/param2/param3; Test Query Parameter 2: value 1; Test-Header 2: value 2"));

   }

   /**
    * 
    */
   private void typeToPathField()
   {
      selenium().focus(IDE.REST_SERVICE.REST_SERVICE_PATH);

      for (int i = 0; i < 15; i++)
      {
         selenium().keyPress(IDE.REST_SERVICE.REST_SERVICE_PATH, "\\8");
      }

      selenium().typeKeys(IDE.REST_SERVICE.REST_SERVICE_PATH, "param1");
   }

   private void checkFields(String path, String method, String request, String response)
   {
      
      System.out.print("\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<:"+IDE.REST_SERVICE.getPathFieldValue()+"\n");
      assertEquals(path, IDE.REST_SERVICE.getPathFieldValue());
      assertEquals(method, IDE.REST_SERVICE.getMethodFieldValue());
      assertEquals(request, IDE.REST_SERVICE.getRequestMediaTypeFieldValue());
      assertEquals(response, IDE.REST_SERVICE.getResponseMediaTypeFieldValue());
   }

   private void checkQueryParameter(int parameterIndex, String name, String type, String defaultValue, String value)
   {
      assertEquals(name, IDE.REST_SERVICE.getQueryParameterName(parameterIndex));
      assertEquals(type, IDE.REST_SERVICE.getQueryParameterType(parameterIndex));
      assertEquals(defaultValue, IDE.REST_SERVICE.getQueryParameterDefaultValue(parameterIndex));
      assertEquals(value, IDE.REST_SERVICE.getQueryParameterValue(parameterIndex));
   }

   private void checkHeaderParameter(int parameterIndex, String name, String type, String defaultValue, String value)
   {
      assertEquals(name, IDE.REST_SERVICE.getHeaderParameterName(parameterIndex));
      assertEquals(type, IDE.REST_SERVICE.getHeaderParameterType(parameterIndex));
      assertEquals(defaultValue, IDE.REST_SERVICE.getHeaderParameterDefaultValue(parameterIndex));
      assertEquals(value, IDE.REST_SERVICE.getHeaderParameterValue(parameterIndex));
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
