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

import static org.junit.Assert.*;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class ForbidDeployUndeployTest extends BaseTest
{
   private final static String FILE_GROOVY = "RestServiceExample.groovy";
   
   private static String FOLDER_NAME;
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";

      try
      {
         FOLDER_NAME = UUID.randomUUID().toString();
         VirtualFileSystemUtils.mkcol(URL +  FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(filePath + FILE_GROOVY, MimeType.GROOVY_SERVICE, URL + FOLDER_NAME + "/" + FILE_GROOVY);
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
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME + "/");
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
   public void testErrorWhenTryToDoForbiddenDeployUndeploy() throws Exception
   {
      //----- 1 ---------------
      //login as john
      Thread.sleep(TestConstants.SLEEP);
      logout();
      Thread.sleep(TestConstants.SLEEP);
      standaloneLogin("john");
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 2 ---------------
      //open REST Service file
      selectRootOfWorkspaceTree();
      runToolbarButton(ToolbarCommands.File.REFRESH);
      openOrCloseFolder(FOLDER_NAME);
      selectItemInWorkspaceTree(FILE_GROOVY);
      openFileFromNavigationTreeWithCodeEditor(FILE_GROOVY, false);
      
      //----- 3 ---------------
      //try to deploy REST Service
      runToolbarButton(ToolbarCommands.Run.DEPLOY_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //check error appears
      final String deployErrorMsg = selenium.getText(
         "//div[@eventproxy='ideOutputForm']/div[1][contains(@eventproxy, 'OutputRecord')]//font[@color='#880000']");
      final String expectedDeployErrorMsg = "[ERROR] " + URL + FOLDER_NAME + "/" + FILE_GROOVY + " deploy failed. Error (403: Forbidden)\n"
         + "You do not have access rights to this resource, please contact your administrator.";
      assertEquals(expectedDeployErrorMsg, deployErrorMsg);
      
      //----- 4 ---------------
      //try to undeploy REST Service
      runToolbarButton(ToolbarCommands.Run.UNDEPLOY_GROOVY_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //check error appears
      final String undeployErrorMsg = selenium.getText(
         "//div[@eventproxy='ideOutputForm']/div[2][contains(@eventproxy, 'OutputRecord')]//font[@color='#880000']");
      final String expectedUndeployErrorMsg = "[ERROR] " + URL + FOLDER_NAME + "/" + FILE_GROOVY + " undeploy failed. Error (403: Forbidden)\n"
         + "You do not have access rights to this resource, please contact your administrator.";
      assertEquals(expectedUndeployErrorMsg, undeployErrorMsg);
   }
   
}
