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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 20, 2010 $
 *
 */
public class DeployNodeTypeTest extends AbstractDataObjectTest
{

   private final static String FOLDER_NAME = DeployNodeTypeTest.class.getSimpleName();
   
   private static final String FILE_NAME = DeployNodeTypeTest.class.getSimpleName() + ".groovy";

   /**
    * Create test folder and test data object file.
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put("src/test/resources/org/exoplatform/ide/operation/chromattic/A.groovy",
            MimeType.CHROMATTIC_DATA_OBJECT, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Clean result of each test.
    * 
    * @throws Exception
    */
   @After
   public void cleanTest() throws Exception
   {
      IDE.EDITOR.closeFile(0);
   }

   /**
    * Tests the appearance of deploy node type dialog window.
    */
   @Test
   public void testGenerateNodeTypeForm() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, true, TestConstants.WAIT_PERIOD * 10);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true, TestConstants.WAIT_PERIOD * 10);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForDeployNodeTypeDialog();

      //check, that Deploy Node Type form is present
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_FORMAT_FIELD_NAME));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_ALREADY_EXIST_FIELD_NAME));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_CANCEL_BUTTON_ID));

      //Click "Cancel" button
      selenium.click(DEPLOY_NODE_TYPE_CANCEL_BUTTON_ID);
      waitForDeployNodeTypeDialogNotPresent();

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForDeployNodeTypeDialog();

      //Select CND format:
      selenium.select(DEPLOY_NODE_TYPE_FORMAT_FIELD_NAME, "label=CND");

      //Click deploy button:
      selenium.click(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID);
      waitForDeployNodeTypeDialogNotPresent();

      //Check error message that CND format is not supported:
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      IDE.WARNING_DIALOG.checkIsOpened("Unsupported content type:text/x-jcr-cnd");
      IDE.WARNING_DIALOG.clickOk();

      IDE.EDITOR.closeFile(0);
   }
   
   /**
    * Test deploy node type with ignore if exist behavior.
    * 
    * @throws Exception
    */
   @Test
   public void testDeployIgnoreIfExist() throws Exception
   {
      refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

      //Wait while buttons will be enabled
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, true, TestConstants.WAIT_PERIOD * 10);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true, TestConstants.WAIT_PERIOD * 10);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForDeployNodeTypeDialog();

      //Click deploy button:
      selenium.click(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID);
      waitForDeployNodeTypeDialogNotPresent();

      IDE.INFORMATION_DIALOG.waitForInfoDialog("Node type successfully deployed.");
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitForInfoDialogNotPresent();

      //check, that there is no view with generated code
      assertFalse(selenium.isElementPresent(IDE_GENERATED_TYPE_PREVIEW_VIEW_LOCATOR));

      IDE.EDITOR.closeFile(0);
   }
   
   /**
    * Test deploy node type with fail if exist behavior.
    * 
    * @throws Exception
    */
   @Test
   public void testDeployFailIfExist() throws Exception
   {
      refresh();
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      IDE.EDITOR.waitTabPresent(0);

      //Wait while buttons will be enabled
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.PREVIEW_NODE_TYPE, true, TestConstants.WAIT_PERIOD * 10);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true, TestConstants.WAIT_PERIOD * 10);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForDeployNodeTypeDialog();

      //Select "fail if exist" behavior:
      selenium.select(DEPLOY_NODE_TYPE_ALREADY_EXIST_FIELD_NAME, "label=fail if exists");

      //Click deploy button:
      selenium.click(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID);
      waitForDeployNodeTypeDialogNotPresent();

      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      IDE.WARNING_DIALOG.clickOk();

      //check, that there is no view with generated code
      assertFalse(selenium.isElementPresent(IDE_GENERATED_TYPE_PREVIEW_VIEW_LOCATOR));

      IDE.EDITOR.closeFile(0);
   }

}
