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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */

//IDE-133
public class RESTServiceDeployExistPathTest extends BaseTest
{

   private static final String FIRST_NAME = System.currentTimeMillis() + ".groovy";

   private static final String SECOND_NAME = System.currentTimeMillis() + "copy.groovy";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";

   @Test
   public void testDeployExistPath() throws Exception
   {
      
      Thread.sleep(TestConstants.SLEEP);
      //TODO*****************change**************change add folder for locked file
      createFolder("Test");
      //*************************************
      runCommandFromMenuNewOnToolbar("REST Service");
      //createFileFromToolbar("REST Service");
      Thread.sleep(TestConstants.SLEEP);
      Thread.sleep(TestConstants.SLEEP);
      saveAsUsingToolbarButton(FIRST_NAME);
      Thread.sleep(TestConstants.SLEEP);

      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);

      saveAsUsingToolbarButton(SECOND_NAME);

      openFileFromNavigationTreeWithCodeEditor(FIRST_NAME, false);

      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      String mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");
      assertTrue(mess.startsWith("[ERROR]"));
      assertTrue(mess.contains(SECOND_NAME + " deploy failed. Error (400: Bad Request)"));

            
      //***************fix GOTO static string message****************     
      //      assertTrue(mess
      //         .contains("Can't bind script " + SECOND_NAME + ", it is not root resource or root resource with the same URI pattern already registered"));

      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/");

      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/");

      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      mess = selenium.getText("//div[contains(@eventproxy,'Record_3')]");

      assertTrue(mess.contains("[INFO]"));

      assertTrue(mess.contains(SECOND_NAME + " deployed successfully."));
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
   }
   
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FIRST_NAME);
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL + SECOND_NAME);
         VirtualFileSystemUtils.delete(URL + FIRST_NAME);
         VirtualFileSystemUtils.delete(URL + SECOND_NAME);
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
