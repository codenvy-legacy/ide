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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceDeployUndeployTest extends BaseTest
{

   private static String FILE_NAME = "DeployUndeployTest.groovy";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;

   @Test
   public void testDeployUndeploy() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //TODO*******************change add folder for locked file
      createFolder("Test");
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      //*****************************
      IDE.toolbar().runCommandFromNewPopupMenu("REST Service");
      Thread.sleep(TestConstants.SLEEP);

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      IDE.editor().closeTab(0);

      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      IDE.menu().runCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//VLayout[ID=\"ideOutputForm\"]/"));

      String mess = selenium.getText("//div[contains(@eventproxy,'Record_0')]");

      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " deployed successfully."));

      IDE.menu().runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);


    //**********fix TODO static string message 
      assertEquals("[INFO] http://127.0.0.1:8080/rest/private/"+WEBDAV_CONTEXT+"/repository/dev-monit/Test/" + FILE_NAME
         + " undeployed successfully.", selenium.getText("//div[contains(@eventproxy,'Record_1')]"));
   
      IDE.menu().runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      mess = selenium.getText("//div[contains(@eventproxy,'Record_2')]");
      assertTrue(mess.contains("[ERROR]"));
      assertTrue(mess.contains(FILE_NAME + " undeploy failed. Error (400: Bad Request)"));
      Thread.sleep(10000);
      
      //**********fix TODO static string message
      assertTrue(mess
         .contains("Can't unbind script Test"+ "/" + FILE_NAME + ", not bound or has wrong mapping to the resource class"));
      //****************************************
      IDE.editor().closeTab(0);

      IDE.navigator().assertItemPresent(URL);

      deleteSelectedItems();
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
