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
package org.exoplatform.ide.operation.netvibes;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for deploying netvibes widget.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 3, 2010 $
 *
 */
public class DeployNetvibesWidgetTest extends BaseTest
{
   //----Ids of buttons, placed on Deploy UWA widget form-------------------
   
   private static final String DEPLOY_BUTTON_ID = "ideDeployUwaWidgetFormDeployWidgetButton";
   
   private static final String NEXT_BUTTON_ID = "ideDeployUwaWidgetFormNextStepButton";
   
   private static final String CANCEL_BUTTON_ID = "ideDeployUwaWidgetFormCancelButton";
   
   private static final String BACK_BUTTON_ID = "ideDeployUwaWidgetFormPrevStepButton";
   
   //----Other elements of form-----------------
   
   private static final String DEPLOY_WIDGET_FORM_ID = "ideDeployUwaWidgetForm";
   
   private static final String URL_FIELD_NAME = "ideDeployUwaWidgetFormUrlField";
   
   private static final String TITLE_FIELD_NAME = "ideDeployUwaWidgetFormTitleField";
   
   private static final String DESCRIPTION_FIELD_NAME = "ideDeployUwaWidgetFormDescriptionField";
   
   private static final String VERSION_FIELD_NAME = "ideDeployUwaWidgetFormVersionField";
   
   private static final String KEYWORDS_FIELD_NAME = "ideDeployUwaWidgetFormKeywordsField";
   
   private static final String TUMBNAIL_FIELD_NAME = "ideDeployUwaWidgetFormTumbnailField";
   
   private static final String LANGUAGE_FIELD_NAME = "ideDeployUwaWidgetFormLanguageField";
   
   private static final String REGION_FIELD_NAME = "ideDeployUwaWidgetFormRegionField";
   
   private static final String CATEGORY_FIELD_NAME = "ideDeployUwaWidgetFormCategoryField";
   
   private static final String LOGIN_FIELD_NAME = "ideDeployUwaWidgetFormLoginField";
   
   private static final String PASSWORD_FIELD_NAME = "ideDeployUwaWidgetFormPasswordField";
   
   private static final String API_KEY_FIELD_NAME = "ideDeployUwaWidgetFormApiKeyField";
   
   private static final String SECRET_FIELD_NAME = "ideDeployUwaWidgetFormSecretField";
   
   //----Test constants----------------

   private final static String FILE_NAME1 = DeployNetvibesWidgetTest.class.getSimpleName() + "1.html";

   private final static String FILE_NAME2 = DeployNetvibesWidgetTest.class.getSimpleName() + "2.html";

   private final static String TEST_FOLDER = DeployNetvibesWidgetTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
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
   public void testDeployFormSteps() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      //Create new UWA widget
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      IDE.EDITOR.waitTabPresent(0);
      checkDeployWidgetControlState(true, false);

      //Save file:
      saveAsUsingToolbarButton(FILE_NAME1);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME1);
      checkDeployWidgetControlState(true, true);

      //Call deploy widget control:
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_UWA_WIDGET);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployFormPresent(true);
      checkStepOneLayout();
      //Check buttons states
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);
      checkButtonStateById(BACK_BUTTON_ID, false, false);

      //Set URL:
      AbstractTextUtil.getInstance().typeToInput(URL_FIELD_NAME, "http://hello.com", true);
      //Check buttons states
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);
      checkButtonStateById(BACK_BUTTON_ID, false, false);

      //Click "Next" button and go to step 2:
      selenium.click(NEXT_BUTTON_ID);
      waitForElementPresent(TITLE_FIELD_NAME);
      
      checkStepTwoLayout();
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);
      checkButtonStateById(BACK_BUTTON_ID, true, true);

      //Fill requiered fields:
      AbstractTextUtil.getInstance().typeToInput(TITLE_FIELD_NAME, "Title", true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(DESCRIPTION_FIELD_NAME, "Description", true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);

      //Click "Next" button and go to step 3:
      selenium.click(NEXT_BUTTON_ID);
      waitForElementPresent(LOGIN_FIELD_NAME);
      checkStepThreeLayout();
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, false, false);
      checkButtonStateById(BACK_BUTTON_ID, true, true);

      //Fill requiered fields:
      AbstractTextUtil.getInstance().typeToInput(LOGIN_FIELD_NAME, "Login", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(PASSWORD_FIELD_NAME, "Password", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(API_KEY_FIELD_NAME, "ApiKey", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(SECRET_FIELD_NAME, "SecreteKey", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, true);

      selenium.click(CANCEL_BUTTON_ID);
      waitForElementNotPresent(DEPLOY_WIDGET_FORM_ID);
      checkDeployFormPresent(false);
   }

   @Test
   public void testDeployFormStepsNavigation() throws Exception
   {
      refresh();
      
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      
      //Create new UWA widget
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      IDE.EDITOR.waitTabPresent(0);
      checkDeployWidgetControlState(true, false);

      //Save file:
      saveAsUsingToolbarButton(FILE_NAME2);
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME2);
      checkDeployWidgetControlState(true, true);

      //Call deploy widget control:
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_UWA_WIDGET);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployFormPresent(true);
      checkStepOneLayout();
      //Check buttons states
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);
      checkButtonStateById(BACK_BUTTON_ID, false, false);

      //Set URL:
      AbstractTextUtil.getInstance().typeToInput(URL_FIELD_NAME, "http://example", true);
      //Check buttons states
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);
      checkButtonStateById(BACK_BUTTON_ID, false, false);

      //Click "Next" button and go to step 2:
      selenium.click(NEXT_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP);
      checkStepTwoLayout();
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);
      checkButtonStateById(BACK_BUTTON_ID, true, true);

      //Fill requiered fields:
      AbstractTextUtil.getInstance().typeToInput(TITLE_FIELD_NAME, "Test Title", true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(DESCRIPTION_FIELD_NAME, "Test Description", true);

      AbstractTextUtil.getInstance().typeToInput(VERSION_FIELD_NAME, "1", true);

      checkButtonStateById(NEXT_BUTTON_ID, true, true);

      //Go back
      selenium.click(BACK_BUTTON_ID);
      checkStepOneLayout();
      assertEquals("http://example", selenium.getValue(URL_FIELD_NAME));
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);
      checkButtonStateById(BACK_BUTTON_ID, false, false);

      //Clear URL field:
      AbstractTextUtil.getInstance().typeToInput(URL_FIELD_NAME, "", true);
      checkButtonStateById(NEXT_BUTTON_ID, true, false);

      //Fill URL field again:
      AbstractTextUtil.getInstance().typeToInput(URL_FIELD_NAME, "http://example2", true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);

      //Click "Next" button and go to step 2:
      selenium.click(NEXT_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP);
      checkStepTwoLayout();
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);
      checkButtonStateById(BACK_BUTTON_ID, true, true);

      //Check fields values:
      assertEquals("Test Title", selenium.getValue(TITLE_FIELD_NAME));
      assertEquals("Test Description", selenium.getValue(DESCRIPTION_FIELD_NAME));
      assertEquals("1", selenium.getValue(VERSION_FIELD_NAME));

      //Click "Next" button and go to step 3:
      selenium.click(NEXT_BUTTON_ID);
      Thread.sleep(TestConstants.SLEEP);
      checkStepThreeLayout();
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, false, false);
      checkButtonStateById(BACK_BUTTON_ID, true, true);

      //Fill requiered fields:
      AbstractTextUtil.getInstance().typeToInput(LOGIN_FIELD_NAME, "Login", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(PASSWORD_FIELD_NAME, "Password", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(API_KEY_FIELD_NAME, "ApiKey", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, false);

      AbstractTextUtil.getInstance().typeToInput(SECRET_FIELD_NAME, "SecreteKey", true);
      checkButtonStateById(DEPLOY_BUTTON_ID, true, true);

      //Go back
      selenium.click(BACK_BUTTON_ID);
      checkStepTwoLayout();
      checkButtonStateById(DEPLOY_BUTTON_ID, false, false);
      checkButtonStateById(CANCEL_BUTTON_ID, true, true);
      checkButtonStateById(NEXT_BUTTON_ID, true, true);
      checkButtonStateById(BACK_BUTTON_ID, true, true);

      //Check fields values:
      assertEquals("Test Title", selenium.getValue(TITLE_FIELD_NAME));
      assertEquals("Test Description", selenium.getValue(DESCRIPTION_FIELD_NAME));
      assertEquals("1", selenium.getValue(VERSION_FIELD_NAME));

      selenium.click(CANCEL_BUTTON_ID);
      waitForElementNotPresent(DEPLOY_WIDGET_FORM_ID);

      checkDeployFormPresent(false);
   }

   /**
    * Check the state of Deploy widget menu and toolbar button:
    * is visible, is enabled.
    * 
    * @param isVisible - are menu and button visible
    * @param isEnabled - are menu and button enabled
    * @throws Exception
    */
   private void checkDeployWidgetControlState(boolean isVisible, boolean isEnabled) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_UWA_WIDGET, isVisible);
      if (isVisible)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_UWA_WIDGET, isEnabled);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_UWA_WIDGET, isEnabled);
      }
   }

   /**
    * Check is deploy UWA widget form present.
    * 
    * @param isPresent - is present
    */
   private void checkDeployFormPresent(boolean isPresent)
   {
      assertEquals(isPresent, selenium.isElementPresent(DEPLOY_WIDGET_FORM_ID));
   }

   /**
    * Check, that input field for URL is present.
    */
   private void checkStepOneLayout()
   {
      assertTrue(selenium.isElementPresent(URL_FIELD_NAME));
   }

   private void checkStepTwoLayout()
   {
      assertTrue(selenium.isElementPresent(TITLE_FIELD_NAME));
      assertTrue(selenium.isElementPresent(DESCRIPTION_FIELD_NAME));
      assertTrue(selenium.isElementPresent(VERSION_FIELD_NAME));
      assertTrue(selenium.isElementPresent(KEYWORDS_FIELD_NAME));
      assertTrue(selenium.isElementPresent(TUMBNAIL_FIELD_NAME));
      assertTrue(selenium.isElementPresent(LANGUAGE_FIELD_NAME));
      assertTrue(selenium.isElementPresent(REGION_FIELD_NAME));
      assertTrue(selenium.isElementPresent(CATEGORY_FIELD_NAME));
   }

   private void checkStepThreeLayout()
   {
      assertTrue(selenium.isElementPresent(LOGIN_FIELD_NAME));
      assertTrue(selenium.isElementPresent(PASSWORD_FIELD_NAME));
      assertTrue(selenium.isElementPresent(API_KEY_FIELD_NAME));
      assertTrue(selenium.isElementPresent(SECRET_FIELD_NAME));
   }

   /**
    * Check the state of buttons, placed on Deploy UWA widget form.
    * 
    * @param buttonId - the is of button
    * @param isVisible - is button visible
    * @param isEnabled - is button enabled
    */
   private void checkButtonStateById(String buttonId, boolean isVisible, boolean isEnabled)
   {
      assertEquals(isVisible,
         !selenium.isElementPresent("//div[@id='" + buttonId + "' and contains(@style, 'display: none')]"));

      if (isVisible)
      {
         checkButtonEnabledById(buttonId, isEnabled);
      }
   }

   private void checkButtonEnabledById(String buttonId, boolean isEnabled)
   {
      assertTrue(selenium.isElementPresent("//div[@id='" + DEPLOY_WIDGET_FORM_ID + "']//div[@id='" + buttonId
         + "' and @button-enabled='" + isEnabled + "']"));
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
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
