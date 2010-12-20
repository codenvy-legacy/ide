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
   private static final String FILE_NAME = DeployNodeTypeTest.class.getSimpleName() + ".groovy";

   private final static String TEST_FOLDER = DeployNodeTypeTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   /**
    * Create test folder and test data object file.
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         String url = URL + TEST_FOLDER + "/";
         VirtualFileSystemUtils.mkcol(url);
         VirtualFileSystemUtils.put("src/test/resources/org/exoplatform/ide/operation/chromattic/A.groovy",
            MimeType.CHROMATTIC_DATA_OBJECT, url + FILE_NAME);
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

   /**
    * Clear tests results.
    */
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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

   /**
    * Clean result of each test.
    * 
    * @throws Exception
    */
   @After
   public void cleanTest() throws Exception
   {
      IDE.editor().closeTab(0);
   }

   /**
    * Tests the appearance of deploy node type dialog window.
    */
   @Test
   public void testGenerateNodeTypeForm() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(TEST_FOLDER);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //Check controls are present and enabled:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployNodeTypeFormPresent();

      //Click "Cancel" button
      selenium.click("scLocator=//IButton[ID=\"ideDeployNodeTypeFormCancelButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeployNodeTypeForm\"]"));

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployNodeTypeFormPresent();
      
      //Select CND format:
      selenium.click("scLocator=//DynamicForm[ID=\"ideDeployNodeTypeFormDynamicForm\"]/item[name=ideDeployNodeTypeFormFormatField]/[icon='picker']");
      selenium.click("scLocator=//DynamicForm[ID=\"ideDeployNodeTypeFormDynamicForm\"]/item[name=ideDeployNodeTypeFormFormatField]/pickList/body/row[ideDeployNodeTypeFormFormatField=CND||1]/col[0]");
      
      //Click deploy button:
      selenium.click("scLocator=//IButton[ID=\"ideDeployNodeTypeFormDeployButton\"]");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeployNodeTypeForm\"]"));
      //Check error message that CND format is not supported:
      String errorMessage = getMessageFromGloabalWarning();
      assertTrue(errorMessage.contains("Unsupported content type:text/x-jcr-cnd"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
      
      IDE.editor().closeTab(0);
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
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(TEST_FOLDER);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //Check controls are present and enabled:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployNodeTypeFormPresent();
      
      //Click deploy button:
      selenium.click("scLocator=//IButton[ID=\"ideDeployNodeTypeFormDeployButton\"]");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeployNodeTypeForm\"]"));
      
      String message = getMessageFromGloabalWarning();
      assertTrue(message.contains("Node type successfully deployed."));
      
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
      
      checkViewWithGeneratedCodePresent(false);
      
      IDE.editor().closeTab(0);
   }
   
   /**
    * Test deploy node type with fail if exist behavior.
    * 
    * @throws Exception
    */
   @Test
   public void testDeployFailIfExist() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      
      Thread.sleep(TestConstants.SLEEP);
      openOrCloseFolder(TEST_FOLDER);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);

      //Check controls are present and enabled:
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkPreviewNodeTypeButton(true, true);
      checkDeployNodeTypeButton(true, true);

      //Click preview node type button and check dialog window appears
      IDE.toolbar().runCommand(ToolbarCommands.Run.DEPLOY_NODE_TYPE);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployNodeTypeFormPresent();
      
      //Select "fail if exist" behavior:
      selenium.click("scLocator=//DynamicForm[ID=\"ideDeployNodeTypeFormDynamicForm\"]/item[name=ideDeployNodeTypeFormAlreadyExistBehaviorField]/[icon='picker']");
      selenium.click("scLocator=//DynamicForm[ID=\"ideDeployNodeTypeFormDynamicForm\"]/item[name=ideDeployNodeTypeFormAlreadyExistBehaviorField]/pickList/body/row[ideDeployNodeTypeFormAlreadyExistBehaviorField=2||1]/col[fieldName=ideDeployNodeTypeFormAlreadyExistBehaviorField||0]");
      
      //Click deploy button:
      selenium.click("scLocator=//IButton[ID=\"ideDeployNodeTypeFormDeployButton\"]");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideDeployNodeTypeForm\"]"));
      
      String message = getMessageFromGloabalWarning();
      assertTrue(message.contains("is already registered"));
      
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
      
      checkViewWithGeneratedCodePresent(false);
      
      IDE.editor().closeTab(0);
   }

}
