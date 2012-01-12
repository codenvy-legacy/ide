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
public class RESTServiceComplexMediaTypeTest extends BaseTest
{

   private final static String FILE_NAME = "ComplexMediaType.grs";

   private final static String PROJECT = RESTServiceComplexMediaTypeTest.class.getSimpleName();

   /**
    * Create REST service for test in test folder.
    */
   @Before
   public void beforeTest()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/ComplexMediaTypes.groovy";
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
   public void testComplexMediaType() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.REST_SERVICE.deploy(PROJECT + "/" + FILE_NAME, 1);

      // Call the "Run->Launch REST Service" topmenu command
      IDE.REST_SERVICE.launchRestService();

      assertTrue(IDE.REST_SERVICE.isPathPresent("/testMediaTypes"));
      assertTrue(IDE.REST_SERVICE.isPathPresent("/testMediaTypes/InnerPath"));

      IDE.REST_SERVICE.selectPath("/testMediaTypes");
      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitForMessageShow(2, 5);

      // Check received message:
      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess
         .contains("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><application xmlns=\"http://research.sun.com/wadl/2006/10\">"));

      IDE.REST_SERVICE.launchRestService();

      // Choose path:
      IDE.REST_SERVICE.selectPath("/testMediaTypes/InnerPath");
      assertEquals("POST", IDE.REST_SERVICE.getMethodValue());

      assertTrue(IDE.REST_SERVICE.isRequestMediaTypeContainsValues("application/json", "text/plain"));
      IDE.REST_SERVICE.setRequestMediaTypeValue("text/plain");

      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeValue());

      IDE.REST_SERVICE.setRequestMediaTypeValue("application/json");
      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeValue());

      IDE.REST_SERVICE.selectPath("/testMediaTypes/InnerPath");
      IDE.REST_SERVICE.selectBodyTab();
      IDE.REST_SERVICE.typeToBodyField("{\"value\" : \"value4\"}");
      IDE.REST_SERVICE.sendRequest();

      IDE.OUTPUT.waitForMessageShow(3, 5);
      // Check received message:
      mess = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(mess.contains("Body: value4"));

      IDE.REST_SERVICE.launchRestService();
      IDE.REST_SERVICE.selectPath("/testMediaTypes");
      IDE.REST_SERVICE.selectPath("/testMediaTypes/InnerPath");
      assertEquals("POST", IDE.REST_SERVICE.getMethodValue());
      assertTrue(IDE.REST_SERVICE.isRequestMediaTypeContainsValues("application/json", "text/plain"));
      IDE.REST_SERVICE.setRequestMediaTypeValue("text/plain");

      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeValue());
      IDE.REST_SERVICE.setRequestMediaTypeValue("text/plain");
      assertEquals("text/plain", IDE.REST_SERVICE.getResponseMediaTypeValue());

      IDE.REST_SERVICE.selectBodyTab();
      IDE.REST_SERVICE.typeToBodyField("{\"value\" : \"value4\"}");

      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitForMessageShow(4, 5);
      // Check received message:
      mess = IDE.OUTPUT.getOutputMessage(4);
      assertTrue(mess.contains("{\"value\" : \"value4\"}"));
   }

   /**
    * Clear test results.
    * @throws Exception 
    */
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
