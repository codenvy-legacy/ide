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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceOutputTest extends BaseTest
{

   private final static String FILE_NAME = "RESTServiceOutputTest.grs";

   private final static String PROJECT = RESTServiceOutputTest.class.getSimpleName();

   @Before
   public void beforeTest()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceOutput.groovy";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testOutput() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.REST_SERVICE.deploy(PROJECT + "/" + FILE_NAME, 1);
      IDE.REST_SERVICE.launchRestService();

      checkFields("/overralTest/Inner/node/{paramList: .+}", "POST", "text/plain", "application/xml;charset=utf-8");
      checkQueryParameter(1, "Test Query Parameter 1", "string", "", "");
      IDE.REST_SERVICE.selectHeaderParametersTab();
      checkHeaderParameter(1, "Test-Header1", "string", "", "");

      IDE.REST_SERVICE.selectPath("/overralTest");
      checkFields("/overralTest", "OPTIONS", "", "application/vnd.sun.wadl+xml");

      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitForMessageShow(2, 5);
      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess
         .contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\">"));

      IDE.REST_SERVICE.launchRestService();
      IDE.REST_SERVICE.typeToPathField("/overralTest/Inner/node/param1");
      IDE.REST_SERVICE.selectHeaderParametersTab();
      IDE.REST_SERVICE.typeToHeaderParameterValue(1, "test");
      checkFields("/overralTest/Inner/node/param1", "POST", "text/plain", "application/xml;charset=utf-8");

      IDE.REST_SERVICE.selectQueryParametersTab();
      checkQueryParameter(1, "Test Query Parameter 1", "string", "", "");

      IDE.REST_SERVICE.selectHeaderParametersTab();
      checkHeaderParameter(1, "Test-Header1", "string", "", "test");

      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitForMessageShow(3, 5);
      mess = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(mess.contains("Param List 1:param1; Test Query Parameter 1: ; Test-Header 1: test; Body:"));

      IDE.REST_SERVICE.launchRestService();
      IDE.REST_SERVICE.typeToPathField("/overralTest/Inner/node/param1");
      IDE.REST_SERVICE.typeToQueryParameterValue(1, "value 1");
      IDE.REST_SERVICE.selectHeaderParametersTab();
      IDE.REST_SERVICE.typeToHeaderParameterValue(1, "value 2");

      IDE.REST_SERVICE.selectBodyTab();
      IDE.REST_SERVICE.typeToBodyField("Ð¿Ñ€Ð¸ÐºÐ»Ð°Ð´ Ð¿Ð¾Ð²Ñ–Ð´Ð¾Ð¼Ð»ÐµÐ½Ð½Ñ�");
      IDE.REST_SERVICE.sendRequest();

      IDE.OUTPUT.waitForMessageShow(4, 5);
      mess = IDE.OUTPUT.getOutputMessage(4);
      assertTrue(mess
         .contains("Param List 1:param1; Test Query Parameter 1: value 1; Test-Header 1: value 2; Body:Ð¿Ñ€Ð¸ÐºÐ»Ð°Ð´ Ð¿Ð¾Ð²Ñ–Ð´Ð¾Ð¼Ð»ÐµÐ½Ð½Ñ�"));

      IDE.REST_SERVICE.launchRestService();
      IDE.REST_SERVICE.typeToPathField("/overralTest/Inner/node/param1/param2/param3");
      IDE.REST_SERVICE.setMethodFieldValue("GET");
      assertEquals("", IDE.REST_SERVICE.getRequestMediaTypeFieldValue());
      assertEquals("text/html", IDE.REST_SERVICE.getResponseMediaTypeValue());
      checkQueryParameter(1, "Test Query Parameter 2", "string", "", "");
      IDE.REST_SERVICE.selectHeaderParametersTab();
      checkHeaderParameter(1, "Test-Header2", "string", "", "");
      IDE.REST_SERVICE.typeToHeaderParameterValue(1, "value 2");
      IDE.REST_SERVICE.selectQueryParametersTab();
      IDE.REST_SERVICE.typeToQueryParameterValue(1, "value 1");
      IDE.REST_SERVICE.sendRequest();

      IDE.OUTPUT.waitForMessageShow(5, 5);
      mess = IDE.OUTPUT.getOutputMessage(5);
      assertTrue(mess
         .contains("Param List 2:param1/param2/param3; Test Query Parameter 2: value 1; Test-Header 2: value 2"));
   }

   private void checkFields(String path, String method, String request, String response)
   {
      assertEquals(path, IDE.REST_SERVICE.getPathFieldValue());
      assertEquals(method, IDE.REST_SERVICE.getMethodValue());
      assertEquals(request, IDE.REST_SERVICE.getRequestMediaTypeFieldValue());
      assertEquals(response, IDE.REST_SERVICE.getResponseMediaTypeValue());
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

   @After
   public void afterTest() throws Exception
   {
      try
      {
         IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
