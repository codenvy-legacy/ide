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

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceFilterParametersTest extends BaseTest
{

   private final static String FILE_NAME = "FilterParametersTest.grs";

   private final static String PROJECT = RESTServiceFilterParametersTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/DefaultValues.groovy";
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
   public void testFilterParameters() throws Exception
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

      IDE.REST_SERVICE.selectHeaderParametersTab();

      IDE.REST_SERVICE.unCheckHeaderParameter(1);

      IDE.REST_SERVICE.sendRequest();
      IDE.OUTPUT.waitOpened();
      IDE.OUTPUT.waitForMessageShow(2, 5);
      String mess = IDE.OUTPUT.getOutputMessage(2);
      assertTrue(mess
         .contains("POST PathParam: {pathParam}; POST Test-Header: 3; POST TestQueryParam: false; POST Body:"));
   }

   @AfterClass
   public static void tearDown() throws Exception
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
