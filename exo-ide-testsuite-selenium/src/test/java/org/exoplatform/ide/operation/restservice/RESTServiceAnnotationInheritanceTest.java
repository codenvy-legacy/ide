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

// IDE-26 
public class RESTServiceAnnotationInheritanceTest extends BaseTest
{
   private final static String FILE_NAME = "AnnotationInheritance.groovy";

   private final static String FOLDER = RESTServiceAnnotationInheritanceTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/AnnotationInheritance.groovy";
      try
      {
         //*********TODO******************
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, "exo:groovyResourceContainer", URL + FILE_NAME);
         //********************************
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
   public void testAnnotationInheritance() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);

      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      //Call the "Run->Launch REST Service" topmenu command
      IDE.REST_SERVICE.launchRestService();

      assertEquals("/testAnnotationInheritance/InnerPath/{pathParam}", IDE.REST_SERVICE.getPathFieldValue());

      assertParameters();

     IDE.REST_SERVICE.openPathList();

      IDE.REST_SERVICE.checkPathListTextPresent("/testAnnotationInheritance");
      
      IDE.REST_SERVICE.checkPathListTextPresent("/testAnnotationInheritance/InnerPath/{pathParam}");

      IDE.REST_SERVICE.selectPathSuggestPanelItem("/testAnnotationInheritance/InnerPath/{pathParam}");

      IDE.REST_SERVICE.typeToPathField(
         "/testAnnotationInheritance/InnerPath/Ñ‚ÐµÑ�Ñ‚");
      Thread.sleep(TestConstants.SLEEP);

      assertParameters();

      IDE.REST_SERVICE.sendRequst();
      String mess = IDE.OUTPUT.getOutputMessageText(2);

      assertTrue(mess.contains("PathParam:Ñ‚ÐµÑ�Ñ‚"));
      
      IDE.EDITOR.closeFile(0);
   }

   /**
    * Check parameters
    */
   private void assertParameters()
   {
      assertEquals("POST", IDE.REST_SERVICE.getMethodFieldValue());

      assertEquals("text/plain", IDE.REST_SERVICE.getRequestMediaTypeFieldValue());

      assertEquals("text/html", IDE.REST_SERVICE.getResponseMediaTypeFieldValue());

      assertEquals("", selenium().getText(IDE.REST_SERVICE.QUERY_TABLE));

      IDE.REST_SERVICE.selectHeaderParametersTab();

      assertEquals("", selenium().getText(IDE.REST_SERVICE.HEADER_TABLE));

      IDE.REST_SERVICE.selectQueryParametersTab();
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
