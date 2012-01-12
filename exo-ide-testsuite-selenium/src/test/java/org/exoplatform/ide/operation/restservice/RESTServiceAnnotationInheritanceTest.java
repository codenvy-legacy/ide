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
public class RESTServiceAnnotationInheritanceTest extends BaseTest
{
   private final static String FILE_NAME = "AnnotationInheritance.grs";

   private final static String PROJECT = RESTServiceAnnotationInheritanceTest.class.getSimpleName();

   @Before
   public void beforeTest()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/AnnotationInheritance.groovy";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testAnnotationInheritance() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(1, 5);

      //Call the "Run->Launch REST Service" topmenu command
      IDE.REST_SERVICE.launchRestService();

      assertEquals("/testAnnotationInheritance/InnerPath/{pathParam}", IDE.REST_SERVICE.getPathFieldValue());

      assertParameters();

      IDE.REST_SERVICE.isValuePresentInPathList("/testAnnotationInheritance");
      IDE.REST_SERVICE.isValuePresentInPathList("/testAnnotationInheritance/InnerPath/{pathParam}");
      IDE.REST_SERVICE.isValuePresentInPathList("/testAnnotationInheritance/InnerPath/{pathParam}");
      IDE.REST_SERVICE.typeToPathField("/testAnnotationInheritance/InnerPath/Ñ‚ÐµÑ�Ñ‚");

      assertParameters();

      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitForMessageShow(2, 10);
      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess.contains("PathParam:Ñ‚ÐµÑ�Ñ‚"));
      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
   }

   /**
    * Check parameters
    */
   private void assertParameters()
   {
      assertEquals("POST", IDE.REST_SERVICE.getMethodValue());
      assertEquals("text/plain", IDE.REST_SERVICE.getRequestMediaTypeFieldValue());
      assertEquals("text/html", IDE.REST_SERVICE.getResponseMediaTypeValue());
      assertEquals(0, IDE.REST_SERVICE.getQueryParameterCount());
      IDE.REST_SERVICE.selectHeaderParametersTab();
      assertEquals(0, IDE.REST_SERVICE.getHeaderParameterCount());
      IDE.REST_SERVICE.selectQueryParametersTab();
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
      }
   }
}
