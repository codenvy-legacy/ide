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
public class RESTServiceComplexMediaTypeTest extends BaseTest
{

   private final static String FILE_NAME = "ComplexMediaType.groovy";

   private final static String FOLDER_NAME = RESTServiceComplexMediaTypeTest.class.getSimpleName();

   //**************
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

   /**
    * Create REST service for test in test folder.
    */
   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/ComplexMediaTypes.groovy";
      try
      {
         //TODO*******change***************
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + FILE_NAME);
         //**********************
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
   }

   @Test
   public void testComplexMediaType() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.clickOpenIconOfFolder(URL);
      //Open REST Service file:
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      //Call the "Run->Launch REST Service" topmenu command
      IDE.REST_SERVICE.launchRestService();

      IDE.REST_SERVICE.openPathList();

      IDE.REST_SERVICE.checkPathListTextPresent("/testMediaTypes");
      IDE.REST_SERVICE.checkPathListTextPresent("/testMediaTypes/InnerPath");

      IDE.REST_SERVICE.selectPathSuggestPanelItem("/testMediaTypes");

      IDE.REST_SERVICE.sendRequst();

      //Expected 3
      assertFalse(selenium.isElementPresent(IDE.REST_SERVICE.REST_SERVICE_FORM));

      //Check received message:
      String mess = IDE.OUTPUT.getOutputMessageText(1);

      assertTrue(mess
         .contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\">"));

      IDE.REST_SERVICE.launchRestService();
      //Choose path:
      IDE.REST_SERVICE.openPathList();

      IDE.REST_SERVICE.selectPathSuggestPanelItem("/testMediaTypes/InnerPath");

      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("POST", IDE.REST_SERVICE.getMethodFieldValue());

      IDE.REST_SERVICE.checkRequestFieldContainsValues("application/json", "text/plain");

      IDE.REST_SERVICE.setRequestMediaTypeFieldValue("text/plain");

      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeFieldValue());

      IDE.REST_SERVICE.setRequestMediaTypeFieldValue("application/json");
      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeFieldValue());

      IDE.REST_SERVICE.typeToPathField("/testMediaTypes/InnerPath");
      Thread.sleep(TestConstants.SLEEP);

      IDE.REST_SERVICE.selectBodyTab();

      selenium.type(IDE.REST_SERVICE.BODY_TEXT_FIELD, "{\"value\" : \"value4\"}");
      IDE.REST_SERVICE.sendRequst();

      Thread.sleep(TestConstants.SLEEP);

      //Check received message:
      mess = IDE.OUTPUT.getOutputMessageText(2);

      assertTrue(mess.contains("Body: value4"));

      IDE.REST_SERVICE.launchRestService();

      IDE.REST_SERVICE.openPathList();
      IDE.REST_SERVICE.checkPathListTextPresent("/testMediaTypes/InnerPath");
      IDE.REST_SERVICE.selectPathSuggestPanelItem("/testMediaTypes/InnerPath");

      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("POST", IDE.REST_SERVICE.getMethodFieldValue());

      IDE.REST_SERVICE.checkRequestFieldContainsValues("application/json", "text/plain");
      IDE.REST_SERVICE.setRequestMediaTypeFieldValue("text/plain");

      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeFieldValue());

      IDE.REST_SERVICE.setRequestMediaTypeFieldValue("text/plain");

      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeFieldValue());

      IDE.REST_SERVICE.typeToPathField("/testMediaTypes/InnerPath");

      IDE.REST_SERVICE.selectBodyTab();
      selenium.type(IDE.REST_SERVICE.BODY_TEXT_FIELD, "{\"value\" : \"value4\"}");
      Thread.sleep(TestConstants.SLEEP);

      IDE.REST_SERVICE.sendRequst();

      Thread.sleep(TestConstants.SLEEP);

      //Check received message:
      mess = IDE.OUTPUT.getOutputMessageText(3);

      assertTrue(mess.contains("{\"value\" : \"value4\"}"));

      IDE.EDITOR.closeTab(0);
   }

   /**
    * Clear test results.
    */
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
