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

import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Upload.FormName;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for deploy and undeploy gadget (full test must be passed under portal).
 * 
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GadgetDeployUndeployTest extends BaseTest
{

   private static final String FILE_NAME = "GoogleMapsGadget.xml";

   private static final String FOLDER_NAME = GadgetDeployUndeployTest.class.getSimpleName();

   private static final String FILE_DEPLOY_URL = "/" + REST_CONTEXT + "/ideall/gadget/deploy?gadgetURL="
      + ENTRY_POINT_URL + WS_NAME + "/" + FILE_NAME + "&privateContext=/" + REST_CONTEXT + "&publicContext=/rest";

   private static final String FILE_UNDEPLOY_URL = "/" + REST_CONTEXT + "/ideall/gadget/undeploy?gadgetURL="
      + ENTRY_POINT_URL + WS_NAME + "/" + FILE_NAME + "&privateContext=/" + REST_CONTEXT + "&publicContext=/rest";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME + "/";

   @Test
   public void testGadgetDeployUndeploy() throws Exception
   {
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      //open gadget with required text
      String filePath =
         "src/test/resources/org/exoplatform/ide/operation/gadget/gadgetDeployUndeployTest/GoogleMapsGadget.xml";

      VirtualFileSystemUtils.mkcol(URL);
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.waitForItem(URL);
      IDE.NAVIGATION.selectItem(URL);

      IDE.UPLOAD.open(FormName.OPEN_LOCAL_FILE, filePath, MimeType.GOOGLE_GADGET);
      waitForElementPresent("//div[contains(@url, loader-background-element.png)]");
      waitForElementNotPresent("//div[contains(@url, loader-background-element.png)]");
      IDE.EDITOR.waitTabPresent(0);

      // gadget deploy/undeploy command should be disabled
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_GADGET, false);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.UNDEPLOY_GADGET, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, false);

      // save gadget in the root folder by clicking on "Ctrl+S" hotkey
      IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_S);
      IDE.SAVE_AS.waitForDialog();
      IDE.SAVE_AS.clickYes();
      waitForElementPresent("//div[contains(@url, loader-background-element.png)]");
      waitForElementNotPresent("//div[contains(@url, loader-background-element.png)]");

      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_GADGET, true, TestConstants.WAIT_PERIOD);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.UNDEPLOY_GADGET, true, TestConstants.WAIT_PERIOD);

      // gadget deploy/undeploy command should become enabled
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_GADGET, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.UNDEPLOY_GADGET, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_GADGET, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET, true);

      if (!isRunIdeUnderPortal())
      {
         return;
      }

      // deploy gadget
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);

      assertTrue(selenium.isTextPresent("[INFO] " + FILE_DEPLOY_URL + " deployed successfully."));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, ToolbarCommands.Run.DEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);

      assertTrue(selenium.isTextPresent("[INFO] " + FILE_DEPLOY_URL + " deployed successfully."));

      // TODO verify gadget presence in the portal

      // undeploy gadget
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.UNDEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);

      assertTrue(selenium.isTextPresent("[INFO] " + FILE_UNDEPLOY_URL + " undeployed successfully."));

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_GADGET);
      Thread.sleep(3 * TestConstants.PAGE_LOAD_PERIOD);
      assertTrue(selenium.isTextPresent("[ERROR] " + FILE_UNDEPLOY_URL
         + " undeploy failed. Error (404: Not Found)\nNo such gadget"));

      // TODO verify gadget absence in the portal

   }

   /**
   * Clean up cookie, registry, repository after each test of in the each class:<br>
   *   - selenium.deleteAllVisibleCookies();<br>
   *   - cleanRegistry();<br>
   *   - cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");<>
   * @throws IOException
    * @throws ModuleException 
   */
   @After
   public void testTearDown() throws IOException, ModuleException
   {
      deleteCookies();
      cleanRegistry();
      VirtualFileSystemUtils.delete(URL);
   }
}
