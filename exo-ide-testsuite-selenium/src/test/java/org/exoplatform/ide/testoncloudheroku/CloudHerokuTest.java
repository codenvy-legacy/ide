/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.testoncloudheroku;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import junit.framework.Assert;

import org.exoplatform.ide.TestConstants;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.AWTException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CloudHerokuTest
{
   //URL your tenant for testing
  
   
   private static final String APPLICATION_URL = "http://maxus.cloud-ide.com/";

   //Create selenium
   public static final Selenium selenium = new DefaultSelenium("localhost", 4444, "*chrome",
      "http://maxus.cloud-ide.com/");

   //Locators for the login page:
   private static final String EMAIL_ON_LOGINPAGE_LOCATOR = "//td/input[@name=\"j_username\"]";

   private static final String PASS_ON_LOGINPAGE_LOCATOR = "//td/input[@name=\"j_password\"]";

   private static final String LOGIN_FOR_MAXUS_DOMEN = "tarrantella@gmail.com";

   private static final String PASS_FOR_MAXUS_DOMEN = "89aef0efa63a";

   private static final String LOGIN_BUTTON_ON_LOGINPAGE_LOCATOR =
      "//div[@class='buttonblock']/input[@class='image-button']";

   //Locators delete form
   private static final String DELETE_BUTTON = "ideDeleteItemFormOkButton";

   private static final String DELETE_FORM = "//div[@view-id=\"ideDeleteItemsView\"]";

   //Locators for the profile page:
   private static final String FIRST_NAME_INPUT_LOCATOR = "first_name";

   private static final String LAST_NAME_INPUT_LOCATOR = "last_name";

   private static final String EMAIL_NAME_INPUT_LOCATOR = "email";

   private static final String PHONE_NAME_INPUT_LOCATOR = "phone_work";

   private static final String COMPANY_NAME_INPUT_LOCATOR = "company";

   private static final String POSITION_NAME_INPUT_LOCATOR = "title";

   private static final String ILL_DOLATER_BUTTON_LOCATOR = "//div[@id=\"profile-buttons\"]/input[@id=\"delaySubmit\"]";

   private static final String SUBMIT_BUTTON_LOCATOR = "//div[@id=\"profile-buttons\"]/input[@id='submitProfile']";

   //IDE Locators for teneant maxus:
   //tenant URL
   private static final String URL = "http://maxus.cloud-ide.com/IDE/rest/private/jcr/maxus/dev-monit/";

   //hash id default examples projects
   private static final String HASH_ID_LINKEDIN_CONTACTS_PROJECT = "navigation-9558f981417b0f1d4bb502705a2d3767";

   private static final String HASH_ID_SHOPPING_CARD_PROJECT = "navigation-ecad7b936b1fbcdbaffb7d29ffb93551";

   private static final String TWITTER_TREND_PROJECT = "navigation-4b4ff992a824ff3f2272cc8309ad5ad9";

   private static final String THREE_PREFIX_FOR_MD5 = "navigation-";

   //folder name 
   private static final String FOLDER_NAME = "CloudHerokuTets";

   //Locators for git:
   private static final String GIT_FORM = "//div[@view-id=\"ideInitRepositoryView\"]";

   private static final String GIT_INIT_BUTTON = "ideInitRepositoryViewInitButton";

   private static final String GIT_CANCEL_BUTTON = "ideInitRepositoryViewCancelButton";

   private static final String GIT_ADD_FORM = "//div[@view-id=\"ideAddToIndexView\"]";

   private static final String GIT_ADD_BUTTON_FORM = "ideAddToIndexViewAddButton";

   private static final String GIT_CANCEL_BUTTON_FORM = "ideAddToIndexViewCancelButton";

   private static final String GIT_REMOTE_PUSH = "//td[@class='exo-popupMenuTitleField']/nobr[text()=\"Push...\"]";

   private static final String ADD_SUCCESS = "[INFO] Successfully added to index.";

   private static final String COMMIT_FORM = "//textarea[@name=\"ideCommitViewMessageField\"]";

   private static final String COMMIT_SUCCESS = "[INFO] Commited with revision ";

   private static final String PUSH_SUCCESS = "[INFO] Successfully pushed to remote repository";

   private static final String PUSH_FORM = "//div[@view-id=\"idePushToRemoteView\"]";

   private static final String PUSH_BUTTON = "idePushToRemoteViewPushButton";

   //Locators for output message:
   private static final String IDE_OUTPUT_CONTENT = "//div[@id='ideOutputContent']/div[1]/";

   private static final String IDE_INIT_GIT_REPOZITORY = "[INFO] Repository was successfully initialized.";

   //Locators for save Form
   private static final String INPUT_FIELD_ON_SAVE_FORM = "//input[@name=\"ideAskForValueViewValueField\"]";

   private static final String SAVE_FORM = "ideAskForValueView-window";

   private static final String YES_BUTTON_ON_SAVE_FORM = "ideAskForValueViewYesButton";

   //Locators for Pass Heroku calls
   private static final String HEROKU_DEPLOY_KEY_LOCATOR =
      "//td[@class='exo-popupMenuTitleField']/nobr[text()=\"Deploy public key...\"]";

   private static final String HEROKU_CREATE_APP_LOCATOR =
      "//td[@class='exo-popupMenuTitleField']/nobr[text()=\"Create application...\"]";

   private static final String HEROKU_DELETE_APP_LOCATOR =
      "//td[@class='exo-popupMenuTitleField']/nobr[text()=\"Delete application...\"]";

   private static final String HEROKU_APP_INFO_LOCATOR =
      "//td[@class='exo-popupMenuTitleField']/nobr[text()=\"Application info...\"]";

   private static final String HEROKU_LOGIN_FORM = "ideLoginView-window";

   private static final String HEROKU_LOGIN_BUTTON = "ideLoginViewLoginButton";

   private static final String HEROKU_INPUT_LOGIN_FIELD = "//input[@name=\"ideLoginViewEmailField\"]";

   private static final String HEROKU_INPUT_PASS_FIELD = "//input[@name=\"ideLoginViewPasswordField\"]";

   private static final String MAXUS_LOGIN_ON_HEROKU = "maxura@ukr.net";

   private static final String MAXUS_PASS_ON_HEROKU = "vfrcbv_1978";

   private static final String OUTPUT_MASSAGE_LOCATOR = "//div[@id='ideOutputContent']/div[%1s]/";

   private static final String KEYS_DEPLOYED = "[INFO] Public keys are successfully deployed on Heroku.";

   private static final String KREATE_HEROKU_APP_FORM_LOCATOR = "//div[@view-id=\"ideCreateApplicationView\"]";

   private static final String HEROKU_APP_CREATEBUTTON_LOCATOR = "ideCreateApplicationViewCreateButton";

   private static final String CANCEL_BUTTON_LOCATOR = "ideCreateApplicationViewCancelButton";

   private static final String HEROKU_INFO_FORM_LOCATOR = "//div[@view-id=\"ideApplicationInfoView\"]";

   private static final String HEROKU_INFO_WEBURL_LOCATOR = "//table[@id=\"ideApplicationInfoGrid\"]/tbody/tr[2]/td[2]";

   private static final String HEROKU_INFO_MAME_LOCATOR = "//table[@id=\"ideApplicationInfoGrid\"]/tbody/tr/td[2]";

   private static final String HEROKU_INFO_CLOSE_LOCATOR =
      "//div[@id=\"ideApplicationInfoView-window\"]/div/table/tbody/tr/td[2]//div/img";

   private static final String CLEAR_OUTPUTPANEL_LOCATOR =
      "//div[@class=\"exoIconButtonPanel\" and @title=\"Clear output\"]/img";

   String HEROKU_APP_NAME = "";

   String HEROKU_APP_URL = "";

   private static final String CHANGED_RUBY_MESSAGE = "run lambda {" + "\n"
      + "|env| [200, {'Content-Type'=>'text/plain'}, StringIO.new(\"Message Hello World is change!\")] }";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         selenium.start("commandLineFlags=--disable-web-security");
         selenium.windowFocus();
         selenium.windowMaximize();
         selenium.open(APPLICATION_URL);
         selenium.waitForPageToLoad("3000");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void clearTestApp() throws Exception
   {
      deleteFolderWithAppAfterTest();
   }

   @Test
   public void typeWithSeleniumTypeMethod() throws Exception
   {
      //check appear login page
      waitForElementPresent(EMAIL_ON_LOGINPAGE_LOCATOR);
      waitForElementPresent(PASS_ON_LOGINPAGE_LOCATOR);

      // enter login and password, click on login button
      selenium.type(EMAIL_ON_LOGINPAGE_LOCATOR, LOGIN_FOR_MAXUS_DOMEN);
      selenium.type(PASS_ON_LOGINPAGE_LOCATOR, PASS_FOR_MAXUS_DOMEN);
      selenium.click(LOGIN_BUTTON_ON_LOGINPAGE_LOCATOR);

      //waiting  for appearing of the profile page, check the profile page, 
      //and click on the "i'll do it later button"
      waitForElementPresent(ILL_DOLATER_BUTTON_LOCATOR);
      checkProfilePageElement();
      selenium.click(ILL_DOLATER_BUTTON_LOCATOR);

      //wait for redraw last element in the tree IDE
      waitForElementPresent(TWITTER_TREND_PROJECT);

      //and check the next elements in three 
      selenium.isElementPresent(HASH_ID_LINKEDIN_CONTACTS_PROJECT);
      selenium.isElementPresent(HASH_ID_SHOPPING_CARD_PROJECT);

      //if the IDE  is loading,  we create the project. Creating a new folder.
      createFolder(FOLDER_NAME);
      waitForElementPresent("navigation-" + md5old(URL + FOLDER_NAME + "/"));

      //select folder with application
      selectElementInTree(URL + FOLDER_NAME + "/");

      //run command git-> initialize reposetory
      runCommandFromPopUpMenu("Git", "Initialize Repository");

      //wait appearing init form and button
      waitForElementPresent(GIT_FORM);
      waitForElementPresent(GIT_INIT_BUTTON);

      //click on init button and check initialize in gitrepo
      selenium.click(GIT_INIT_BUTTON);
      waitForElementNotPresent(GIT_FORM);
      checkInizializeGit();

      //selecting created folder and creating test file
      selectElementInTree(URL + FOLDER_NAME + "/");
      createTestRubyFile();
      waitElementInTree(URL + FOLDER_NAME + "/" + "config.ru");
      selectElementInTree(URL + FOLDER_NAME + "/");

      //deploy a public key for Heroku
      runAndValidateDeployPublickKeyMenu();

      //add to git index and commit
      //create Heroku Application
      addAndCommitToGit(3, 4);
      createHerokuApp();

      //run Heroku application info. 
      //And set to global variables Url and Name of the Application   
      runCommandFromPopUpMenu("PaaS", "Heroku");
      selenium.click(HEROKU_APP_INFO_LOCATOR);
      waitForElementPresent(HEROKU_INFO_FORM_LOCATOR);
      HEROKU_APP_NAME = getHerokuAppInfo(HEROKU_INFO_MAME_LOCATOR);
      HEROKU_APP_URL = getHerokuAppInfo(HEROKU_INFO_WEBURL_LOCATOR);
      selenium.click(HEROKU_INFO_CLOSE_LOCATOR);
      waitForElementNotPresent(HEROKU_INFO_FORM_LOCATOR);

      // Validate creating application in Heroku page
      checkCreateAppInHerokuPage();

      //select folder with app and push to git repo
      selectElementInTree(URL + FOLDER_NAME + "/");
      pushToGit(1);

      //check the application on Heroku
      chekApplicationOnHerokuPage("Hello World!");
      selectElementInTree(URL + FOLDER_NAME + "/");

      //reopen of the ruby file. Change code.
      runCommandFromToolbar("Refresh Selected Folder");
      waitElementInTree(URL + FOLDER_NAME + "/" + "config.ru");
      selenium.doubleClick("navigation-" + md5old(URL + FOLDER_NAME + "/" + "config.ru"));
      waitForElementPresent("//body[@class='editbox']");
      Thread.sleep(3000);

      //A gotoline form call,  for set cursor in code editor
      gotoFirstLineInEditor();
      deleteFileContentInEditor();
      typeText("//body[@class='editbox']", CHANGED_RUBY_MESSAGE);
      runCommandFromToolbar("Save");

      //pause for saving
      Thread.sleep(1500);

      // add to index and commit
      addAndCommitToGit(1, 2);
      waitElementInTree(URL + FOLDER_NAME + "/");
      selectElementInTree(URL + FOLDER_NAME + "/");

      //push of changed ruby file
      pushToGit(3);

      //checks the change application
      chekApplicationOnHerokuPage("Message Hello World is change!");

      //destroy Test Application
      destroyHerokuApp(1);

   }

   //Used methods
   //------------------------------------------------------------------------------------------

   //method Add, and Commit to git repository heroku app and validate it.
   /**
    * @param numberAddMess. The number in the output panel for add message 
    * @param numberCommitMess The number in the output panel for commit message
    * @throws Exception
    */
   public void addAndCommitToGit(int numberAddMess, int numberCommitMess) throws Exception
   {
      //add application to index
      runCommandFromPopUpMenu("Git", "Add...");
      waitForElementPresent(GIT_ADD_FORM);
      selenium.click(GIT_ADD_BUTTON_FORM);
      waitForElementNotPresent(GIT_ADD_FORM);
      checkAddToGit(numberAddMess);

      //commit application to git
      runCommandFromPopUpMenu("Git", "Commit...");
      waitForElementPresent(COMMIT_FORM);
      selenium.type(COMMIT_FORM, "maxus commit");
      selenium.click("ideCommitViewCommitButton");
      waitForElementNotPresent("//div[@view-id=\"ideCommitView\"]");
      checkCommitToGit(numberCommitMess);
   }

   //method push app to romoute repository and validate process
   /**
    * method for push application to heroku 
    * remote repository
    * @throws Exception
    */
   public void pushToGit(int numberMessage) throws Exception
   {
      runCommandFromPopUpMenu("Git", "Remote");
      selenium.click(GIT_REMOTE_PUSH);
      waitForElementPresent(PUSH_FORM);
      waitForElementPresent(PUSH_BUTTON);
      selenium.click(PUSH_BUTTON);
      Thread.sleep(5000);
      //if after first click app don't push
      if (selenium.isElementPresent(PUSH_FORM))
      {
         selenium.click(PUSH_BUTTON);
         Thread.sleep(5000);
         waitForElementNotPresent(PUSH_FORM);
         String chekPushMess = getOutputMessageText(numberMessage);

         Assert.assertTrue(chekPushMess.startsWith(PUSH_SUCCESS));
      }
      else
      {

         waitForElementNotPresent(PUSH_FORM);
         String chekPushMess = getOutputMessageText(numberMessage);

         Assert.assertTrue(chekPushMess.startsWith(PUSH_SUCCESS));
      }
   }

   //method for checking of executing app on Heroku
   /**
    * @param outtext. Text on the Heroku page for check.
    * @throws Exception
    */
   public void chekApplicationOnHerokuPage(String outtext) throws Exception
   {
      selenium.open(HEROKU_APP_URL);
      Thread.sleep(5000);
      selenium.waitForPageToLoad("5000");
      selenium.isElementPresent(outtext);
      selenium.goBack();
      Thread.sleep(5000);
      waitElementInTree(URL + FOLDER_NAME + "/");
   }

   // method create test application in IDE
   /**
    * Creating and validate Heroku application in IDE
    * @throws Exception
    */
   public void createHerokuApp() throws Exception
   {
      runCommandFromPopUpMenu("PaaS", "Heroku");
      selenium.click(HEROKU_CREATE_APP_LOCATOR);
      waitForElementPresent(HEROKU_APP_CREATEBUTTON_LOCATOR);
      selenium.click(HEROKU_APP_CREATEBUTTON_LOCATOR);
      String createherocuappmess = getOutputMessageText(5);
      assertTrue(createherocuappmess.contains("[INFO] Application "));
      assertTrue(createherocuappmess.contains("webUr"));
      assertTrue(createherocuappmess.contains("name"));

   }

   // method for getting of a text messages
   /**
    * @param locatorinfo 
    * @return
    * @throws Exception
    */
   public String getHerokuAppInfo(String locatorinfo) throws Exception
   {
      String infoApp = selenium.getText(locatorinfo);
      return infoApp;
   }

   //method for checking of creating app on the Heroku server 
   //and of return to the IDE
   /**
    * @throws Exception
    */
   public void checkCreateAppInHerokuPage() throws Exception
   {
      selenium.open("https://api.heroku.com/myapps");
      waitForElementPresent("//input[@value=\"Log In\" and @name=\"commit\"]");
      typeToInput("//div/input[@name=\"email\"]", MAXUS_LOGIN_ON_HEROKU, false);
      typeToInput("//div/input[@name=\"password\"]", MAXUS_PASS_ON_HEROKU, false);
      selenium.click("//input[@value=\"Log In\" and @name=\"commit\"]");
      selenium.waitForPageToLoad("20000");
      selenium.isTextPresent(HEROKU_APP_NAME);
      selenium.goBack();
      Thread.sleep(1000);
      selenium.goBack();
      Thread.sleep(8000);;
      waitElementInTree(URL + FOLDER_NAME + "/");
   }

   //method for destroying and validation of deleting the Heroku application  
   /**
    * 
    * @param nummessage 
    *  message on the output panel
    * @throws Exception
    */
   public void destroyHerokuApp(int nummessage) throws Exception
   {
      selectElementInTree(URL + FOLDER_NAME + "/");
      runCommandFromPopUpMenu("PaaS", "Heroku");
      selenium.click(HEROKU_DELETE_APP_LOCATOR);
      waitForElementPresent("exoAskDialog");
      waitForElementPresent("YesButton");
      selenium.click("YesButton");
      waitForElementNotPresent("exoAskDialog");
      String destoyHerocuAapp = getOutputMessageText(nummessage);
      //1
      Assert.assertEquals(destoyHerocuAapp, "[INFO] Application is successfully deleted on Heroku.");
   }

   //method login in heroku domen. Create public key in IDE and check creating.
   /**
    * method for creating piblic key for Heroku test
    * @throws Exception
    */
   public void runAndValidateDeployPublickKeyMenu() throws Exception
   {
      runCommandFromPopUpMenu("PaaS", "Heroku");
      selenium.click(HEROKU_DEPLOY_KEY_LOCATOR);
      // If the key has already been created
      if (checkCreateKey())
      {
         System.out.print("<<<<<<<<<<<<<<<<<" + "The key is deployed without logging" + "<<<:");
      }
      // if key creating first time
      else
      {
         waitForElementPresent(HEROKU_LOGIN_FORM);
         typeToInput(HEROKU_INPUT_LOGIN_FIELD, MAXUS_LOGIN_ON_HEROKU, false);
         typeToInput(HEROKU_INPUT_LOGIN_FIELD, MAXUS_PASS_ON_HEROKU, false);
         waitForElementPresent(HEROKU_LOGIN_BUTTON);
         selenium.click(HEROKU_LOGIN_BUTTON);
         waitForElementNotPresent(HEROKU_LOGIN_FORM);
         checkCreateKey();
      }
   }

   //method for check create public key on heroku
   /**
    * method to check the message on the establishment of the public key
    * if key is created - return true, else false 
    * @return
    * @throws Exception
    */
   public boolean checkCreateKey() throws Exception
   {

      waitForElementPresent("//div[@id='ideOutputContent']/div[2]/");

      if (selenium.isElementPresent("//div[@id='ideOutputContent']/div[2]/"))
      {
         String createKeyMessage = selenium.getText("//div[@id='ideOutputContent']/div[2]/");
         Assert.assertEquals(KEYS_DEPLOYED, createKeyMessage);
         return true;
      }
      else
      {
         return false;
      }
   }

   //method for creating test of the file
   /** 
    * method type of testing ruby code into editor
    * and save of the test file
    * @throws Exception
    */
   public void createTestRubyFile() throws Exception
   {
      String locator = "//table[@class='exo-popupMenuTable']//tbody//td//nobr[text()='" + "Ruby File" + "']";
      String tabsetlocator = "//div[@panel-id='editor']" + "//td[@tab-bar-index='0']";

      runCommandFromToolbar("New");

      selenium.click(locator);
      waitForElementPresent("//span[text()=\".sayHello\"]");
      deleteFileContentInEditor();

      typeText("//body[@class='editbox']", "run lambda {" + "\n"
         + "|env| [200, {'Content-Type'=>'text/plain'}, StringIO.new(\"Hello World!\")] }");
      runCommandFromToolbar("Save As...");
      waitForElementPresent(YES_BUTTON_ON_SAVE_FORM);
      typeToInput(INPUT_FIELD_ON_SAVE_FORM, "config.ru", true);
      selenium.click(YES_BUTTON_ON_SAVE_FORM);
      waitForElementNotPresent(SAVE_FORM);
   }

   //method for clear content in code editor
   /**
    * method clear content in code editor
    * @throws Exception
    */
   public void deleteFileContentInEditor() throws Exception
   {
      selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);

      selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DELETE);

      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   //method waiting element in IDE tree 
   /**
   * 
   * @param full url to element in IDE tree
   * @throws Exception
   */
   public void waitElementInTree(String url) throws Exception
   {
      waitForElementPresent(THREE_PREFIX_FOR_MD5 + md5old(url));
   }

   //method for select element in IDE tree 
   /**
   * 
   * @param full url to element in IDE tree
   * @throws Exception
   */
   public static void selectElementInTree(String url) throws Exception
   {
      selenium.clickAt(THREE_PREFIX_FOR_MD5 + md5old(url), "1,1");
   }

   //method for checking of initialize the git repository
   /**
    * text get from output panel and check message
    * @throws Exception
    */
   public String getOutputMessageText(int messageNumber) throws Exception
   {
      String locator = String.format(OUTPUT_MASSAGE_LOCATOR, messageNumber);
      waitForElementPresent(locator);
      return selenium.getText(locator);
   }

   // method for clearing messages in output panel
   /**
    * @throws Exception
    */
   public void clearOutputMessageText() throws Exception
   {
      selenium.click(CLEAR_OUTPUTPANEL_LOCATOR);
      waitForElementNotPresent(IDE_OUTPUT_CONTENT);
   }

   // method for checking initialize in the git repo
   /**
    * @throws Exception
    */
   public void checkInizializeGit() throws Exception
   {
      waitForElementPresent(IDE_OUTPUT_CONTENT);
      String initializeGitMess = selenium.getText(IDE_OUTPUT_CONTENT);
      Assert.assertEquals(initializeGitMess, IDE_INIT_GIT_REPOZITORY);
   }

   // method for checking adding in the git repo
   /**
    * @param numbermessage
    * @throws Exception
    */
   public void checkAddToGit(int numbermessage) throws Exception
   {
      String addMessage = getOutputMessageText(numbermessage);
      Assert.assertEquals(addMessage, ADD_SUCCESS);
   }

   // method for checking commit in the git repo
   /**
    * @param numbermessage
    * @throws Exception
    */
   public void checkCommitToGit(int numbermessage) throws Exception
   {
      String commitMessage = getOutputMessageText(numbermessage);
      Assert.assertTrue(commitMessage.startsWith(COMMIT_SUCCESS));
   }

   // method for checking push in the git repo
   /**
    * @param numbermessage
    * @throws Exception
    */
   public void checkPushToGit(int numbermessage) throws Exception
   {
      String addMessage = getOutputMessageText(numbermessage);
      Assert.assertEquals(addMessage, PUSH_SUCCESS);
   }

   //method is run new from toolbar menu
   /**
    * @param buttonTitle
    * @throws Exception
    */
   public static void runCommandFromToolbar(String buttonTitle) throws Exception
   {
      String locator =
         "//div[@class=\"exoToolbarPanel\" and @id=\"exoIDEToolbar\"]//div[@title=\"" + buttonTitle + "\"]";
      selenium.click(locator);

      if ("New".equals(buttonTitle))
      {
         waitForElementPresent("//div[@id='menu-lock-layer-id']//table[@class='exo-popupMenuTable']");
      }
   }

   //method for checking of basic elements of the profile page
   /**
    * @throws AWTException
    * @throws InterruptedException
    */
   private void checkProfilePageElement() throws AWTException, InterruptedException
   {
      selenium.isElementPresent(FIRST_NAME_INPUT_LOCATOR);
      selenium.isElementPresent(EMAIL_NAME_INPUT_LOCATOR);
      selenium.isElementPresent(PHONE_NAME_INPUT_LOCATOR);
      selenium.isElementPresent(COMPANY_NAME_INPUT_LOCATOR);
      selenium.isElementPresent(POSITION_NAME_INPUT_LOCATOR);
      selenium.isElementPresent(ILL_DOLATER_BUTTON_LOCATOR);
      selenium.isElementPresent(SUBMIT_BUTTON_LOCATOR);
   }

   //method for waiting element on page
   /**
    * @param locator
    * @throws Exception
    */
   public static void waitForElementPresent(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();

      while (true)
      {
         if (selenium.isElementPresent(locator))
         {
            break;
         }

         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("timeout for element " + locator);
         }

         Thread.sleep(1);
      }
   }

   //method for run createfolder menu from tollbar menu IDE
   /**
    * @param menuItemName
    * @throws Exception
    */
   public void runNewFolderCommand(String menuItemName) throws Exception
   {
      runCommandFromToolbar("New");

      String locator = "//table[@class='exo-popupMenuTable']//tbody//td//nobr[text()='" + menuItemName + "']";
      selenium.click(locator);
      waitForElementPresent("//div[@id='ideCreateFolderForm']");

   }

   //method for run menu and command from popup menu
   /**
    * @param topMenuName
    * @param commandName
    * @throws Exception
    */
   public void runCommandFromPopUpMenu(String topMenuName, String commandName) throws Exception
   {
      String menuItemLocator = "//td[@class='exo-popupMenuTitleField']/nobr[text()='" + commandName + "']";

      selenium.mouseDown("//td[@class='exo-menuBarItem' and text()='" + topMenuName + "']");
      waitForElementPresent(menuItemLocator);
      selenium.click(menuItemLocator);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   //method for type text 
   /**
    * @param locator
    * @param text
    * @throws Exception
    */
   private void typeText(String locator, String text) throws Exception
   {
      for (int i = 0; i < text.length(); i++)
      {
         char symbol = text.charAt(i);
         if (symbol == 'y')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_Y);
         }
         else if (symbol == '\n')
         {
            Thread.sleep(300);
            selenium.keyDown(locator, "\\13");
            selenium.keyUp(locator, "\\13");
            Thread.sleep(300);
         }
         else if (symbol == '.')
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PERIOD);
         }
         else
         {
            selenium.typeKeys(locator, String.valueOf(symbol));
         }
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }

      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   //method for waiting disappear menu
   /**
    * @param locator
    * @throws Exception
    */
   protected static void waitForElementNotPresent(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();
      while (true)
      {
         if (!selenium.isElementPresent(locator))
         {
            break;
         }

         long time = System.currentTimeMillis() - startTime;
         if (time > TestConstants.TIMEOUT)
         {
            fail("Timeout for element > " + locator);
         }

         Thread.sleep(1);
      }
   }

   //method for typing into input menus
   /**
    * @param locator
    * @param text
    * @param clear
    * @throws Exception
    */
   public void typeToInput(String locator, String text, boolean clear) throws Exception
   {
      selenium.focus(locator);
      selenium.click(locator);

      if (clear)
      {
         selenium.keyDownNative("" + java.awt.event.KeyEvent.VK_CONTROL);
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
         selenium.keyUpNative("" + java.awt.event.KeyEvent.VK_CONTROL);
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
         selenium.type(locator, "");
      }

      typeText(locator, text);
   }

   //method for creating folders with users name
   /**
    * @param folderName
    * @throws Exception
    */
   public void createFolder(String folderName) throws Exception
   {
      runNewFolderCommand("Folder...");
      waitForElementPresent("//div[@id='ideCreateFolderForm']");

      //Check creation form elements
      waitForElementPresent("ideCreateFolderFormNameField");
      waitForElementPresent("ideCreateFolderFormCreateButton");
      waitForElementPresent("ideCreateFolderFormCancelButton");

      typeToInput("ideCreateFolderFormNameField", folderName, true);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      selenium.click("ideCreateFolderFormCreateButton");
      waitForElementNotPresent("ideCreateFolderForm");
   }

   // method for deleting the folder after test
   /**
    * @throws Exception
    * method for delete the folder with app after test
    */
   public static void deleteFolderWithAppAfterTest() throws Exception
   {
      selectElementInTree(URL + FOLDER_NAME + "/");
      runCommandFromToolbar("Delete Item(s)...");
      waitForElementPresent(DELETE_BUTTON);
      selenium.click(DELETE_BUTTON);
      waitForElementNotPresent(DELETE_FORM);
      waitForElementNotPresent("navigation-" + md5old(URL + FOLDER_NAME + "/"));
   }

   /**
    * Encode string in md5 hash
    * @param string to encode
    * @return md5 hash of string
    */
   public static String md5old(String string)
   {
      MessageDigest m;
      try
      {
         m = MessageDigest.getInstance("MD5");
         m.reset();
         m.update(string.getBytes());
         byte[] digest = m.digest();
         BigInteger bigInt = new BigInteger(1, digest);
         String hashtext = bigInt.toString(16);
         // Now we need to zero pad it if you actually want the full 32 chars.
         while (hashtext.length() < 32)
         {
            hashtext = "0" + hashtext;
         }
         return hashtext;
      }
      catch (NoSuchAlgorithmException e)
      {
         e.printStackTrace();
         fail();
      }
      return "";

   }

   // method for calling of the "gotoline" menu and and goto to the first line
   /**
    * @throws Exception
    * calling of the "gotoline" menu and and goto to the first line in codeeditor
    */
   public void gotoFirstLineInEditor() throws Exception
   {
      runCommandFromPopUpMenu("Edit", "Go to Line...");
      waitForElementPresent("//input[@name=\"ideGoToLineFormLineNumberField\"]");
      typeToInput("//input[@name=\"ideGoToLineFormLineNumberField\"]", "1", false);
      selenium.click("ideGoToLineFormGoButton");
      waitForElementNotPresent("ideGoToLineForm");

   }

}
