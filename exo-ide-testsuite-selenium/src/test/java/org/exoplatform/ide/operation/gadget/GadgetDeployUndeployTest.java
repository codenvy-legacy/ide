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
package org.exoplatform.ide.operation.gadget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GadgetDeployUndeployTest extends BaseTest
{

   private static final String FILE_NAME = "GoogleMapsGadget.xml";
   
   private static final String FOLDER_NAME="TestFolder";
   
   private static final String FILE_DEPLOY_URL = "/" + REST_CONTEXT + "/ideall/gadget/deploy?gadgetURL="
      + ENTRY_POINT_URL + WS_NAME + "/" + FILE_NAME + "&privateContext=/" + REST_CONTEXT + "&publicContext=/rest";

   private static final String FILE_UNDEPLOY_URL = "/" + REST_CONTEXT + "/ideall/gadget/undeploy?gadgetURL="
      + ENTRY_POINT_URL + WS_NAME + "/" + FILE_NAME + "&privateContext=/" + REST_CONTEXT + "&publicContext=/rest";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/";
   
   @Test
   public void testGadgetDeployUndeploy() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //open gadget with required text
      String filePath =
         "src/test/resources/org/exoplatform/ide/operation/gadget/gadgetDeployUndeployTest/GoogleMapsGadget.xml";

      Thread.sleep(TestConstants.SLEEP);
     
      //*********TODO******Add_and_select_folder_for_aploading_file
      VirtualFileSystemUtils.mkcol(URL);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //***********************
      
      uploadFile(MenuCommands.File.OPEN_LOCAL_FILE, filePath, MimeType.GOOGLE_GADGET);
      Thread.sleep(TestConstants.SLEEP);

      // gadget deploy/undeploy command should be disabled
      checkToolbarButtonState(ToolbarCommands.Run.DEPLOY_GADGET, false);
      checkToolbarButtonState(ToolbarCommands.Run.UNDEPLOY_GADGET, false);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, false);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, false);

      // save gadget in the root folder by clicking on "Ctrl+S" hotkey
      runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);

      // gadget deploy/undeploy command should become enabled
      checkToolbarButtonState(ToolbarCommands.Run.DEPLOY_GADGET, true);
      checkToolbarButtonState(ToolbarCommands.Run.UNDEPLOY_GADGET, true);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, true);
      checkMenuCommandState(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, true);

      if (!isRunIdeUnderPortal())
      {
         return;
      }

      // deploy gadget
      runToolbarButton(ToolbarCommands.Run.DEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);
      selenium.selectFrame("remote_iframe_0");
      String outputPanelMessage =
         java.net.URLDecoder.decode(selenium.getText("scLocator=//Label[ID=\"isc_OutputRecord_0\"]/"), "UTF-8");
      assertEquals("Verify is the gadget deploy service response is appropriate.", "[INFO] " + FILE_DEPLOY_URL
         + " deployed successfully.", outputPanelMessage);

      runTopMenuCommand(MenuCommands.Run.RUN, ToolbarCommands.Run.DEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);
      outputPanelMessage =
         java.net.URLDecoder.decode(selenium.getText("scLocator=//Label[ID=\"isc_OutputRecord_1\"]/"), "UTF-8");
      assertEquals("Verify is the gadget redeploy service response is appropriate.", "[INFO] " + FILE_DEPLOY_URL
         + " deployed successfully.", outputPanelMessage);

      // TODO verify gadget presence in the portal

      // undeploy gadget
      runToolbarButton(ToolbarCommands.Run.UNDEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);
      outputPanelMessage =
         java.net.URLDecoder.decode(selenium.getText("scLocator=//Label[ID=\"isc_OutputRecord_2\"]/"), "UTF-8");
      assertEquals("Verify is the gadget undeploy service response is appropriate.", "[INFO] " + FILE_UNDEPLOY_URL
         + " undeployed successfully.", outputPanelMessage);

      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);
      outputPanelMessage =
         java.net.URLDecoder.decode(selenium.getText("scLocator=//Label[ID=\"isc_OutputRecord_3\"]/"), "UTF-8");
      System.out.println("Was:" + outputPanelMessage);
      System.out.println("Expected:" + "[ERROR] " + FILE_UNDEPLOY_URL
         + " undeploy failed. Error (404: Not Found) No such gadget gadget");
      assertTrue(
         "Verify is the gadget reundeploy service response is appropriate.",
         outputPanelMessage.contains("[ERROR] " + FILE_UNDEPLOY_URL
            + " undeploy failed. Error (404: Not Found)\nNo such gadget"));

      // TODO verify gadget absence in the portal

   }

   /**
   * Clean up cookie, registry, repository after each test of in the each class:<br>
   *   - selenium.deleteAllVisibleCookies();<br>
   *   - cleanRegistry();<br>
   *   - cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");<>
   * @throws IOException
   */
   @After
   public void testTearDown() throws IOException
   {
      deleteCookies();
      cleanRegistry();
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
