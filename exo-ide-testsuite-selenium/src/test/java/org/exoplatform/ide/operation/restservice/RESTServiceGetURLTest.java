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
public class RESTServiceGetURLTest extends BaseTest
{

   private static final String FILE_NAME = "RESTServiceGetURL.grs";

   private final static String PROJECT = RESTServiceGetURLTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy";
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
   public void testGetUrl() throws Exception
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

      IDE.REST_SERVICE.launchRestService();

      //IDE.REST_SERVICE.openPathList();
      IDE.REST_SERVICE.selectPath("/testService");
      IDE.REST_SERVICE.openGetURLForm();

      String url = IDE.REST_SERVICE.getUrlFromGetURLForm();

      assertTrue((BASE_URL + REST_CONTEXT_IDE + "/testService").equals(url));
      //Close form
      IDE.REST_SERVICE.closeGetURLForm();

      IDE.REST_SERVICE.selectPath("/testService/Inner/{pathParam}");

      IDE.REST_SERVICE.openGetURLForm();
      url = IDE.REST_SERVICE.getUrlFromGetURLForm();

      assertTrue((BASE_URL + REST_CONTEXT_IDE + "/testService/Inner/{pathParam}").equals(url));

      //Close form
      IDE.REST_SERVICE.closeGetURLForm();

      IDE.REST_SERVICE.selectPath("/testService/Inner/{param}/node/{paramList: .+}");

      IDE.REST_SERVICE.openGetURLForm();
      url = IDE.REST_SERVICE.getUrlFromGetURLForm();

      assertTrue((BASE_URL + REST_CONTEXT_IDE + "/testService/Inner/{param}/node/{paramList: .+}").equals(url));

      //Close form
      IDE.REST_SERVICE.closeGetURLForm();
      IDE.REST_SERVICE.closeForm();

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      IDE.OUTPUT.waitForMessageShow(2, 5);
      assertTrue(IDE.OUTPUT.getOutputMessage(2).contains(
      /*TODO PROJECT + "/" + FILE_NAME +*/" undeployed successfully."));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         //     Utils.undeployService(BASE_URL, REST_CONTEXT, url);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
