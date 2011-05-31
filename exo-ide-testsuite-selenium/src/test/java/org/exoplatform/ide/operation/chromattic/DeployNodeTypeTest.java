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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.WarningDialog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 20, 2010 $
 *
 */
public class DeployNodeTypeTest extends BaseTest
{
   //---- Locators ------------
   public static final String IDE_GENERATED_TYPE_PREVIEW_VIEW_LOCATOR = "//div[@view-id='ideGeneratedTypePreviewView']";
   
   public static final String DEPLOY_NODE_TYPE_DIALOG_ID = "ideDeployNodeTypeForm";
   
   public static final String DEPLOY_NODE_TYPE_FORMAT_FIELD_NAME = "ideDeployNodeTypeFormFormatField";
   
   public static final String DEPLOY_NODE_TYPE_ALREADY_EXIST_FIELD_NAME = "ideDeployNodeTypeFormAlreadyExistBehaviorField";
   
   public static final String DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID = "ideDeployNodeTypeFormDeployButton";
   
   public static final String DEPLOY_NODE_TYPE_CANCEL_BUTTON_ID = "ideDeployNodeTypeFormCancelButton";
   
   //---- Variables ------------
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
    * Tests the appearance of deploy node type dialog window.
    */
   @Test
   public void testGenerateNodeTypeForm() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(0);

      //Check controls are present and enabled:
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);
      IDE.TOOLBAR.checkButtonExistAtRight(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE, true);
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      //check, that Deploy Node Type form is present
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_FORMAT_FIELD_NAME));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_ALREADY_EXIST_FIELD_NAME));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID));
      assertTrue(selenium.isElementPresent(DEPLOY_NODE_TYPE_CANCEL_BUTTON_ID));

      //Click "Cancel" button
      selenium.click(DEPLOY_NODE_TYPE_CANCEL_BUTTON_ID);
      waitForElementNotPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      //Select CND format:
      selenium.select(DEPLOY_NODE_TYPE_FORMAT_FIELD_NAME, "label=CND");

      //Click deploy button:
      selenium.click(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID);
      waitForElementNotPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      //Check error message that CND format is not supported:
      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      assertTrue(selenium.isElementPresent(WarningDialog.WARNING_DIALOG_ID));
      assertTrue(selenium.isElementPresent(WarningDialog.OK_BUTTON_ID));
      assertTrue(selenium.isTextPresent("Unsupported content type:text/x-jcr-cnd"));
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
      selenium.refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(0);

      //Wait while button will be enabled
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      //Click deploy button:
      selenium.click(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID);
      waitForElementNotPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

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
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(0);

      //Wait while button will be enabled
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.Run.DEPLOY_NODE_TYPE, true);

      //Click preview node type button and check dialog window appears
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      waitForElementPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      //Select "fail if exist" behavior:
      selenium.select(DEPLOY_NODE_TYPE_ALREADY_EXIST_FIELD_NAME, "label=fail if exists");

      //Click deploy button:
      selenium.click(DEPLOY_NODE_TYPE_DEPLOY_BUTTON_ID);
      waitForElementNotPresent(DEPLOY_NODE_TYPE_DIALOG_ID);

      IDE.WARNING_DIALOG.waitForWarningDialogOpened();
      IDE.WARNING_DIALOG.clickOk();

      //check, that there is no view with generated code
      assertFalse(selenium.isElementPresent(IDE_GENERATED_TYPE_PREVIEW_VIEW_LOCATOR));

      IDE.EDITOR.closeFile(0);
   }

}
