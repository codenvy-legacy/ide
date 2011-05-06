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
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 3, 2010 $
 *
 */
public class DeployNetvibesWidgetTest extends BaseTest
{

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
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/");

      //Create new UWA widget
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkDeployWidgetControlState(true, false);

      //Save file:
      saveAsUsingToolbarButton(FILE_NAME1);
      Thread.sleep(TestConstants.SLEEP);
      checkDeployWidgetControlState(true, true);

      //Call deploy widget control:
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_UWA_WIDGET);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployFormPresent(true);
      checkStepOneLayout();
      //Check buttons states
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, false);
      checkBackButtonState(false, false);

      //Set URL:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormMainDynamicForm\"]/item[name=ideDeployUwaWidgetFormUrlField]/element",
            "http://hello.com", true);
      //Check buttons states
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, true);
      checkBackButtonState(false, false);

      //Click "Next" button and go to step 2:
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormNextStepButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      checkStepTwoLayout();
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, false);
      checkBackButtonState(true, true);

      //Fill requiered fields:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormTitleField]/element",
            "Title", true);
      checkNextButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormDescriptionField]/element",
            "Description", true);

      checkNextButtonState(true, true);

      //Click "Next" button and go to step 3:
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormNextStepButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      checkStepThreeLayout();
      checkDeployButtonState(true, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(false, false);
      checkBackButtonState(true, true);

      //Fill requiered fields:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormLoginField]/element",
            "Login", true);
      checkDeployButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormPasswordField]/element",
            "Password", true);
      checkDeployButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormApiKeyField]/element",
            "ApiKey", true);
      checkDeployButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormSecretField]/element",
            "SecreteKey", true);
      checkDeployButtonState(true, true);

      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormCancelButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkDeployFormPresent(false);
   }

   @Test
   public void testDeployFormStepsNavigation() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisible(WS_URL + TEST_FOLDER + "/");

      //Create new UWA widget
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      checkDeployWidgetControlState(true, false);

      //Save file:
      saveAsUsingToolbarButton(FILE_NAME2);
      Thread.sleep(TestConstants.SLEEP);
      checkDeployWidgetControlState(true, true);

      //Call deploy widget control:
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.DEPLOY_UWA_WIDGET);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      checkDeployFormPresent(true);
      checkStepOneLayout();
      //Check buttons states
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, false);
      checkBackButtonState(false, false);

      //Set URL:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormMainDynamicForm\"]/item[name=ideDeployUwaWidgetFormUrlField]/element",
            "http://example", true);
      //Check buttons states
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, true);
      checkBackButtonState(false, false);

      //Click "Next" button and go to step 2:
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormNextStepButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      checkStepTwoLayout();
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, false);
      checkBackButtonState(true, true);

      //Fill requiered fields:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormTitleField]/element",
            "Test Title", true);
      checkNextButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormDescriptionField]/element",
            "Test Description", true);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormVersionField]/element",
            "1", true);

      checkNextButtonState(true, true);

      //Go back
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormPrevStepButton\"]");
      checkStepOneLayout();
      assertEquals(
         "http://example",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormMainDynamicForm\"]/item[name=ideDeployUwaWidgetFormUrlField]/element"));
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, true);
      checkBackButtonState(false, false);

      //Clear URL field:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormMainDynamicForm\"]/item[name=ideDeployUwaWidgetFormUrlField]/element",
            "", true);
      checkNextButtonState(true, false);

      //Fill URL field again:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormMainDynamicForm\"]/item[name=ideDeployUwaWidgetFormUrlField]/element",
            "http://example2", true);
      checkNextButtonState(true, true);

      //Click "Next" button and go to step 2:
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormNextStepButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      checkStepTwoLayout();
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, true);
      checkBackButtonState(true, true);

      //Check fields values:
      assertEquals(
         "Test Title",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormTitleField]/element"));

      assertEquals(
         "Test Description",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormDescriptionField]/element"));

      assertEquals(
         "1",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormVersionField]/element"));

      //Click "Next" button and go to step 3:
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormNextStepButton\"]");
      Thread.sleep(TestConstants.SLEEP);
      checkStepThreeLayout();
      checkDeployButtonState(true, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(false, false);
      checkBackButtonState(true, true);

      //Fill requiered fields:
      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormLoginField]/element",
            "Login", true);
      checkDeployButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormPasswordField]/element",
            "Password", true);
      checkDeployButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormApiKeyField]/element",
            "ApiKey", true);
      checkDeployButtonState(true, false);

      AbstractTextUtil
         .getInstance()
         .typeToInput(
            "scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormSecretField]/element",
            "SecreteKey", true);
      checkDeployButtonState(true, true);

      //Go back
      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormPrevStepButton\"]");
      checkStepTwoLayout();
      checkDeployButtonState(false, false);
      checkCancelButtonState(true, true);
      checkNextButtonState(true, true);
      checkBackButtonState(true, true);

      //Check fields values:
      assertEquals(
         "Test Title",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormTitleField]/element"));

      assertEquals(
         "Test Description",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormDescriptionField]/element"));

      assertEquals(
         "1",
         selenium
            .getValue("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormVersionField]/element"));

      selenium.click("scLocator=//IButton[ID=\"ideDeployUwaWidgetFormCancelButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      checkDeployFormPresent(false);
   }

   private void checkDeployWidgetControlState(boolean isVisible, boolean isEnabled) throws Exception
   {
      IDE.MENU.checkCommandVisibility(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_UWA_WIDGET, isVisible);
      if (isVisible)
      {
         IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.DEPLOY_UWA_WIDGET, isEnabled);
         IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_UWA_WIDGET, isEnabled);
      }
   }

   private void checkDeployFormPresent(boolean isPresent)
   {
      assertEquals(isPresent, selenium.isElementPresent("scLocator=//Window[ID=\"ideDeployUwaWidgetForm\"]"));
   }

   private void checkStepOneLayout()
   {
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormMainDynamicForm\"]/item[name=ideDeployUwaWidgetFormUrlField]/element"));
   }

   private void checkStepTwoLayout()
   {
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormTitleField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormDescriptionField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormVersionField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormKeywordsField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormTumbnailField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormLanguageField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormRegionField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormDetailsDynamicForm\"]/item[name=ideDeployUwaWidgetFormCategoryField]/element"));
   }

   private void checkStepThreeLayout()
   {
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormLoginField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormPasswordField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormApiKeyField]/element"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideDeployUwaWidgetFormPrivacyDynamicForm\"]/item[name=ideDeployUwaWidgetFormSecretField]/element"));
   }

   private void checkDeployButtonState(boolean isVisible, boolean isEnabled)
   {
      assertEquals(
         isVisible,
         !selenium
            .isElementPresent("//div[@eventproxy='ideDeployUwaWidgetFormDeployWidgetButton' and contains(@style, 'visibility: hidden')]/div[@eventproxy='ideDeployUwaWidgetFormDeployWidgetButton']"));

      if (isVisible)
      {
         assertEquals(isEnabled, isButtonEnabled("Deploy"));
      }
   }

   private void checkNextButtonState(boolean isVisible, boolean isEnabled)
   {
      assertEquals(
         isVisible,
         !selenium
            .isElementPresent("//div[@eventproxy='ideDeployUwaWidgetFormNextStepButton' and contains(@style, 'visibility: hidden')]/div[@eventproxy='ideDeployUwaWidgetFormNextStepButton']"));

      if (isVisible)
      {
         assertEquals(isEnabled, isButtonEnabled("Next"));
      }
   }

   private void checkBackButtonState(boolean isVisible, boolean isEnabled)
   {
      assertEquals(
         isVisible,
         !selenium
            .isElementPresent("//div[@eventproxy='ideDeployUwaWidgetFormPrevStepButton' and contains(@style, 'visibility: hidden')]/div[@eventproxy='ideDeployUwaWidgetFormPrevStepButton']"));

      if (isVisible)
      {
         assertEquals(isEnabled, isButtonEnabled("Back"));
      }
   }

   private void checkCancelButtonState(boolean isVisible, boolean isEnabled)
   {
      assertEquals(
         isVisible,
         !selenium
            .isElementPresent("//div[@eventproxy='ideDeployUwaWidgetFormCancelButton' and contains(@style, 'visibility: hidden')]/div[@eventproxy='ideDeployUwaWidgetFormCancelButton']"));

      if (isVisible)
      {
         assertEquals(isEnabled, isButtonEnabled("Cancel"));
      }
   }

   private boolean isButtonEnabled(String title)
   {
      if (selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitle' and text()='" + title + "']"))
      {
         return true;
      }
      else if (selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleOver' and text()='" + title
         + "']"))
      {
         return true;
      }
      else if (selenium.isElementPresent("//div[@class='windowBody']//td[@class='buttonTitleDisabled' and text()='"
         + title + "']"))
      {
         return false;
      }
      return false;
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
