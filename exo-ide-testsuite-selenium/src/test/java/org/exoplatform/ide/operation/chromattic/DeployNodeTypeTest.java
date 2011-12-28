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
package org.exoplatform.ide.operation.chromattic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 20, 2010 $
 *
 */
public class DeployNodeTypeTest extends BaseTest
{
   private final static String PROJECT = DeployNodeTypeTest.class.getSimpleName();

   private static final String FILE_NAME = DeployNodeTypeTest.class.getSimpleName() + ".cmtc";

   /**
    * Create test folder and test data object file.
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, "application/x-chromattic+groovy",
            "src/test/resources/org/exoplatform/ide/operation/chromattic/A.groovy");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Clear tests results.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Tests the appearance of deploy node type dialog window.
    */
   @Test
   public void testGenerateNodeTypeView() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.DEPLOY_NODE_TYPE));
      assertTrue(IDE.MENU.isCommandVisible(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE));
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE));

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      IDE.DEPLOY_NODE_TYPE.waitOpened();
      IDE.DEPLOY_NODE_TYPE.clickCancelButton();
      IDE.DEPLOY_NODE_TYPE.waitClosed();

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      IDE.DEPLOY_NODE_TYPE.waitOpened();

      //Select CND format:
      IDE.DEPLOY_NODE_TYPE.selectNodeTypeFormat("CND");

      //Click deploy button:
      IDE.DEPLOY_NODE_TYPE.clickDeployButton();

      //Check error message that CND format is not supported:
      IDE.WARNING_DIALOG.waitOpened();
      assertTrue(IDE.WARNING_DIALOG.getWarningMessage().contains("Unsupported content type:text/x-jcr-cnd"));
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
   }

   /**
    * Test deploy node type with ignore if exist behavior.
    * 
    * @throws Exception
    */
   @Test
   public void testDeployIgnoreIfExist() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      IDE.DEPLOY_NODE_TYPE.waitOpened();

      //Click deploy button:
      IDE.DEPLOY_NODE_TYPE.selectAlreadyExists("ignore if exists");
      IDE.DEPLOY_NODE_TYPE.clickDeployButton();
      IDE.DEPLOY_NODE_TYPE.waitClosed();

      IDE.INFORMATION_DIALOG.waitOpened();
      assertEquals("Node type successfully deployed.", IDE.INFORMATION_DIALOG.getMessage());
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
   }

   /**
    * Test deploy node type with fail if exist behavior.
    * 
    * @throws Exception
    */
   @Test
   public void testDeployFailIfExist() throws Exception
   {
      driver.navigate().refresh();

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      IDE.DEPLOY_NODE_TYPE.waitOpened();

      //Select "fail if exist" behavior:
      IDE.DEPLOY_NODE_TYPE.selectAlreadyExists("fail if exists");

      //Click deploy button:
      IDE.DEPLOY_NODE_TYPE.clickDeployButton();
      IDE.DEPLOY_NODE_TYPE.waitClosed();

      IDE.WARNING_DIALOG.waitOpened();
      IDE.WARNING_DIALOG.clickOk();

      //check, that there is no view with generated code

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.EDITOR.waitTabNotPresent(FILE_NAME);
   }

}
