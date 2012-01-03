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
public class RESTServiceGetURLTest extends BaseTest
{

   private static final String FILE_NAME = "RESTServiceGetURL.groovy";

   private final static String TEST_FOLDER = RESTServiceGetURLTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         //TODO*****************change**************change add folder for locked file
         String url =
            BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER + "/";
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, url + FILE_NAME);
         //**********************
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testGetUrl() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);

      //TODO**********change************
      IDE.WORKSPACE.clickOpenIconOfFolder(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(TestConstants.SLEEP);
      //****************************

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      IDE.REST_SERVICE.launchRestService();

      //IDE.REST_SERVICE.openPathList();
      IDE.REST_SERVICE.selectPath("/testService");
      IDE.REST_SERVICE.openGetURLForm();

      String url = IDE.REST_SERVICE.getUrlFromGetURLForm();

      assertTrue((BASE_URL + "IDE/rest/private/testService").equals(url));
      //Close form
      IDE.REST_SERVICE.closeGetURLForm();

      
      IDE.REST_SERVICE.selectPath("/testService/Inner/{pathParam}");

      IDE.REST_SERVICE.openGetURLForm();
      url = IDE.REST_SERVICE.getUrlFromGetURLForm();

      assertTrue((BASE_URL + "IDE/rest/private/testService/Inner/{pathParam}").equals(url));

      //Close form
      IDE.REST_SERVICE.closeGetURLForm();
      
      IDE.REST_SERVICE.selectPath("/testService/Inner/{param}/node/{paramList: .+}");

      IDE.REST_SERVICE.openGetURLForm();
      url = IDE.REST_SERVICE.getUrlFromGetURLForm();

      assertTrue((BASE_URL + "IDE/rest/private/testService/Inner/{param}/node/{paramList: .+}").equals(url));

      //Close form
      IDE.REST_SERVICE.closeGetURLForm();

      IDE.REST_SERVICE.closeForm();

   }

   @AfterClass
   public static void tearDown()
   {
      String url =
         BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + TEST_FOLDER + "/"
            + FILE_NAME;
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, url);
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
