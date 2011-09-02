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
package org.exoplatform.ide;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.thoughtworks.selenium.Selenium;

import org.everrest.http.client.HTTPConnection;
import org.everrest.http.client.HTTPResponse;
import org.everrest.http.client.ModuleException;
import org.everrest.http.client.ProtocolNotSuppException;
import org.exoplatform.ide.utils.InternetExplorerUtil;
import org.exoplatform.ide.utils.TextUtil;
import org.exoplatform.ide.utils.WebKitUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id:   ${date} ${time}
 *
 */
@RunWith(RCRunner.class)
public abstract class BaseTest
{
   public static final ResourceBundle IDE_SETTINGS = ResourceBundle.getBundle("conf/ide-selenium");

   public static final String SELENIUM_HOST = IDE_SETTINGS.getString("selenium.host");

   public static final String SELENIUM_PORT = IDE_SETTINGS.getString("selenium.port");

   public static final String GIT_PATH = IDE_SETTINGS.getString("git.location");

   /**
    * Default workspace.
    */
   public static final String WS_NAME = IDE_SETTINGS.getString("ide.ws.name");

   /**
    * Second workspace. Needed in some tests.
    */
   protected static final String WS_NAME_2 = IDE_SETTINGS.getString("ide.ws.name2");

   public static String IDE_HOST = IDE_SETTINGS.getString("ide.host");

   public static final int IDE_PORT = Integer.valueOf(IDE_SETTINGS.getString("ide.port"));

   public static String BASE_URL = "http://" + IDE_HOST + ":" + IDE_PORT + "/";

   //   protected static final String USER_NAME = "__anonim";
   // For portal 
   public static final String USER_NAME = IDE_SETTINGS.getString("ide.user.root.name");

   public static final String USER_PASSWORD = IDE_SETTINGS.getString("ide.user.root.password");

   protected static String APPLICATION_URL = BASE_URL + IDE_SETTINGS.getString("ide.app.url");

   public static final String REST_CONTEXT = IDE_SETTINGS.getString("ide.rest.context");

   public static final String REPO_NAME = IDE_SETTINGS.getString("ide.repository.name");

   public static final String WEBDAV_CONTEXT = IDE_SETTINGS.getString("ide.webdav.context");

   public static String ENTRY_POINT_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/";

   //this two variables add after change in URL IDE
   public static final String REST_CONTEXT_IDE = IDE_SETTINGS.getString("ide.rest.contenxt.ide");

   public static String ENTRY_POINT_URL_IDE = BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME
      + "/";

   /**
    * Default workspace URL.
    */
   public static final String WS_URL = ENTRY_POINT_URL + WS_NAME + "/";

   //this variable add after change in URL IDE
   public static final String WS_URL_IDE = ENTRY_POINT_URL_IDE + WS_NAME + "/";

   protected static final String REGISTER_IN_PORTAL = BASE_URL + "portal/private";

   protected static final EnumBrowserCommand BROWSER_COMMAND = EnumBrowserCommand.valueOf(IDE_SETTINGS
      .getString("selenium.browser.commad"));

   public Selenium selenium()
   {
      return session();
   }

   /**
    * URL of default workspace in IDE.
    */
   protected static final String WORKSPACE_URL = ENTRY_POINT_URL + WS_NAME + "/";

   private static int maxRunTestsOnOneSession = 5;

   private static int testsCounter = 0;

   private static boolean beforeClass = false;
   
   public IDE IDE = new IDE(selenium(), WS_URL);

   @Before
   public void startSelenium() throws Exception
   {
      if (beforeClass)
         return;

      beforeClass = true;
      startSeleniumSession(SELENIUM_HOST, Integer.parseInt(SELENIUM_PORT), BROWSER_COMMAND.toString(), BASE_URL);
      IDE = new IDE(selenium(), WS_URL);
      
      switch (BROWSER_COMMAND)
      {
         case GOOGLE_CHROME :
         case SAFARI :
            new WebKitUtil(selenium());
            break;

         case IE_EXPLORE_PROXY :
            new InternetExplorerUtil(selenium());
            break;

         default :
            new TextUtil(selenium());
      }

      //      testsCounter++;
      //      if (testsCounter % maxRunTestsOnOneSession == 1)
      //      {
      //         System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CASE 1");
      //         selenium().start();
      //         selenium().windowFocus();
      //         selenium().windowMaximize();
      //         selenium().open(APPLICATION_URL);
      //         selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      //         standaloneLogin(USER_NAME);
      //      }
      //      
      //      System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> CASE 2");
      //      selenium().open(APPLICATION_URL);
      //      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);         

      try
      {
         selenium().windowFocus();
         selenium().windowMaximize();
         selenium().open(APPLICATION_URL);
         selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);

         if (isRunIdeUnderPortal())
         {
            loginInPortal();
            selenium().open(APPLICATION_URL);
            selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
            Thread.sleep(TestConstants.IDE_LOAD_PERIOD);
            // selenium().selectFrame("//div[@id='eXo-IDE-container']//iframe");
            // selenium().selectFrame("remote_iframe_0");

            // selectMainForm()
            if (selenium().isElementPresent("//div[@id='eXo-IDE-container']"))
            {
               selenium().selectFrame("//div[@id='eXo-IDE-container']//iframe");
            }
            else
            {
               selenium().selectFrame("relative=top");
            }
         }
         else if (isRunIdeAsStandalone())
         {
            standaloneLogin(USER_NAME, USER_PASSWORD);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   protected void logout() throws Exception
   {
      if (isRunIdeUnderPortal())
      {
         //TODO
         //log out from ide
         fail("Can't logout under portal. Fix it!!!");
      }
      else if (isRunIdeAsStandalone())
      {
         standaloneLogout();
      }
   }

   private void standaloneLogout() throws Exception
   {
      selenium().clickAt("//a[contains(@href, '" + IDE_SETTINGS.getString("ide.logout.url") + "')]", "");
      selenium().waitForPageToLoad("" + TestConstants.IDE_INITIALIZATION_PERIOD);
   }

   protected void standaloneLogin(String userName, String password) throws Exception
   {
      waitForElementPresent("//input[@name='j_username']");
      selenium().type("//input[@name='j_username']", userName);
      selenium().type("//input[@name='j_password']", password);
      selenium().click("//input[@value='Log In']");
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
   }

   private void loginInPortal() throws Exception
   {
      selenium().open(REGISTER_IN_PORTAL);
      Thread.sleep(TestConstants.SLEEP);
      selenium().type("//input[@name='username']", USER_NAME);
      selenium().type("//input[@name='password']", USER_PASSWORD);
      selenium().click("//div[@id='UIPortalLoginFormAction']");
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
   }

   @AfterClass
   public static void stopSelenium()
   {
      //      if (testsCounter % maxRunTestsOnOneSession == 0)
      //      {
      //         selenium().stop();
      //      }

      closeSeleniumSession();
      beforeClass = false;
      //      try
      //      {
      //         standaloneLogout();
      //      }
      //      catch (Exception e)
      //      {
      //         // TODO Auto-generated catch block
      //         e.printStackTrace();
      //      }
   }

   /**
    * Get selected text from browser window.
    * Note: if use editor - select frame with it.
    * 
    * @return {@link String}
    */
   protected String getSelectedText()
   {
      return selenium().getEval("if (window.getSelection) { window.getSelection();}");
   }

   /**
    * Calls Save As command by clicking Save As... icon on toolbar.
    * <p/>
    * Checks is dialog appears, and do all elements are present in window.
    * <p/>
    * Enters name to text field and click Ok button.
    * <p/>
    * If name is null, will created with proposed default name.
    * <b>Use IDE.NAVIGATION.saveFileAs(...) against this method</b>
    * 
    * @param name file name
    * @throws Exception
    */
   @Deprecated
   protected void saveAsUsingToolbarButton(String name) throws Exception
   {
      IDE.TOOLBAR.runCommand("Save As...");
      SaveFileUtils.checkSaveAsDialogAndSave(name, true);
   }

   /**
    * Call Save As command using top menu File.
    * 
    * If name is null, will created with proposed default name.
    * 
    * @param name file name to save
    * @throws Exception
    */
   @Deprecated
   /*
    * Use IDE.NAVIGATION.saveFileAs(...) against this method
    */
   protected void saveAsByTopMenu(String name) throws Exception
   {
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS);

      SaveFileUtils.checkSaveAsDialogAndSave(name, true);
   }
   
   protected void saveCurrentFile() throws Exception
   {
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   /**
    * Clicks on New button on toolbar and then clicks on 
    * menuName from list
    * @param menuName
    */
   protected void callNewItemFromToolbar(String itemName) throws Exception
   {
      IDE.TOOLBAR.runCommand("New");

      String locator = "//td[@class=\"exo-popupMenuTitleField\"]//nobr[text()='" + itemName + "']";
      selenium().mouseOver(locator);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      String hoverLocator = "//td[@class=\"exo-popupMenuTitleFieldOver\"]//nobr[text()='" + itemName + "']";
      selenium().mouseDownAt(hoverLocator, "");
      //time to wait while gadget open new file
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

   /**
    * Get the URL of selected item.
    * 
    * @return {@link String} URL
    * @throws Exception
    */
   protected String getSelectedItemUrl() throws Exception
   {
      final String getItemUrlFormLocator = "//div[@view-id='ideGetItemURLForm']";
      final String okButtonId = "ideGetItemURLFormOkButton";
      final String urlFieldName = "ideGetItemURLFormURLField";
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.GET_URL);

      Thread.sleep(TestConstants.SLEEP);
      waitForElementPresent(getItemUrlFormLocator);
      assertTrue(selenium().isElementPresent(getItemUrlFormLocator));
      assertTrue(selenium().isElementPresent(okButtonId));
      assertTrue(selenium().isElementPresent(urlFieldName));

      final String url = selenium().getValue(urlFieldName);

      //Close form
      selenium().click(okButtonId);
      waitForElementNotPresent(getItemUrlFormLocator);
      assertFalse(selenium().isElementPresent(getItemUrlFormLocator));
      return url;
   }

   /**
    * Select "Workspace" tab in navigation panel
    */
   protected void selectWorkspaceTab()
   {
      selenium().click("//div[@panel-id='navigation']//td[text()='Workspace']");
   }

   /**
    * Get text shown in status bar.
    * 
    * @return {@link String} text
    */
   @Deprecated
   protected String getStatusbarText()
   {
      return selenium().getText("//table[@class='exo-statusText-table']");
   }

   /**
    * Use to create new file in selected folder.
    * 
    * @param menuCommand name of command from New button on toolbar
    * @param fileName name of file
    * @param tabIndex - index of tab, where new file will be opened (starts with 0)
    * @throws Exception
    */
   protected void createSaveAndCloseFile(String menuCommand, String fileName, int tabIndex) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(menuCommand);
      IDE.EDITOR.waitTabPresent(tabIndex);

      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.SAVE_AS, true);
      saveAsUsingToolbarButton(fileName);

      IDE.EDITOR.closeFile(tabIndex);
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Read file content.
    * 
    * @param file to read
    * @return String file content
    */
   protected String getFileContent(String filePath)
   {
      File file = new File(filePath);
      StringBuilder content = new StringBuilder();

      try
      {
         BufferedReader input = new BufferedReader(new FileReader(file));
         try
         {
            String line = null;

            while ((line = input.readLine()) != null)
            {
               content.append(line);
               content.append('\n');
            }
         }
         finally
         {
            input.close();
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
         assertTrue(false);
      }

      return content.toString();
   }

   /**
    * Read value from cursor position panel in status bar.
    * 
    * @return {@link String}
    */
   @Deprecated
   protected String getCursorPositionUsingStatusBar()
   {
      return selenium()
         .getText(
            "//div[@class='exo-statusText-panel']/table[@class='exo-statusText-table']//td[@class='exo-statusText-table-middle']/nobr");
   }

   /**
    * Use instead IDE.UPLOAD.open(String formName, String filePath, String mimeType);
    * 
    * @param formName name of the form
    * @param filePath path to file 
    * @param mimeType mime type of the file
    * @throws InterruptedException 
    */
   @Deprecated
   protected void uploadFile(String formName, String filePath, String mimeType) throws Exception
   {
      if (!MenuCommands.File.OPEN_LOCAL_FILE.equals(formName) && !MenuCommands.File.UPLOAD_FILE.equals(formName))
      {
         Assert.fail("Form name must be - " + MenuCommands.File.OPEN_LOCAL_FILE + " or - "
            + MenuCommands.File.UPLOAD_FILE);
      }

      IDE.MENU.runCommand(MenuCommands.File.FILE, formName);

      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium().isElementPresent("ideUploadForm"));
      assertTrue(selenium().isElementPresent("ideUploadFormBrowseButton"));
      try
      {
         File file = new File(filePath);
         selenium().type("//input[@type='file']", file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length()),
         selenium().getValue("ideUploadFormFilenameField"));

      selenium().type("ideUploadFormMimeTypeField", mimeType);
      assertTrue(selenium().isElementPresent("ideUploadFormUploadButton"));

      selenium().click("ideUploadFormUploadButton");
      Thread.sleep(TestConstants.SLEEP);

      assertFalse(selenium().isElementPresent("ideUploadForm"));
   }

   /**
    * Create file from template.
    * 
    * @param templateName
    * @param fileName
    * @throws Exception
    */
   protected void createFileFromTemplate(String templateName, String fileName) throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);

      useTemplateForm(templateName, fileName);
   }

   /**
    * If you have opened create file from template form,
    * use this method to create file.
    * 
    * @param templateName
    * @param fileName
    * @throws Exception
    */
   protected void useTemplateForm(String templateName, String fileName) throws Exception
   {
      assertTrue(selenium().isElementPresent(
         "//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName + "']"));

      selenium().mouseDownAt(
         "//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName + "']", "2,2");

      selenium().mouseUpAt(
         "//div[@class='windowBody']//table[@class='listTable']//nobr/span[@title='" + templateName + "']", "2,2");

      Thread.sleep(TestConstants.SLEEP_SHORT);

      if (fileName != null)
      {
         //type file name into name field
         selenium().type(
            "scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item["
               + "name=ideCreateFileFromTemplateFormFileNameField||title=File Name]/element", fileName);
      }

      //click Create Button
      selenium().click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
   }

   /*
    * set the focus to hidden input
    */
   public void clearFocus() throws Exception
   {
      selenium().focus(
         "//body/input[@class='gwt-TextBox' and contains(@style,'position: absolute; left: -100px; top: -100px;')]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /* protected static void cleanDefaultWorkspace()
    {
       cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
    }*/

   /* protected static void cleanRepository(String repositoryUrl)
    {
       HTTPConnection connection;
       URL url;
       try
       {
          url = new URL(BASE_URL + repositoryUrl);
          connection = Utils.getConnection(url);
          HTTPResponse response = connection.PropfindAllprop(BASE_URL + repositoryUrl, 1);
          ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getData());
          Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
          NodeList nodeList = document.getElementsByTagName("D:href");
          for (int i = 0; i < nodeList.getLength(); i++)
          {
             Node node = nodeList.item(i);
             String href = node.getFirstChild().getNodeValue();
             if (!href.equals(repositoryUrl))
             {
                connection = Utils.getConnection(url);
                response = connection.Delete(href);
             }

          }
       }
       catch (MalformedURLException e)
       {
          e.printStackTrace();
       }
       catch (ProtocolNotSuppException e)
       {
          e.printStackTrace();
       }
       catch (IOException e)
       {
          e.printStackTrace();
       }
       catch (ModuleException e)
       {
          e.printStackTrace();
       }
       catch (SAXException e)
       {
          e.printStackTrace();
       }
       catch (ParserConfigurationException e)
       {
          e.printStackTrace();
       }
       catch (FactoryConfigurationError e)
       {
          e.printStackTrace();
       }
    }*/

   protected static void cleanRegistry()
   {
      HTTPConnection connection;
      URL url;
      try
      {
         url = new URL(BASE_URL);
         connection = Utils.getConnection(url);
         HTTPResponse response = connection.Delete(BASE_URL + "rest/private/registry/repository/exo:users/root/IDE");
         System.out.println("cleanRegistry " + response.getStatusCode());
         connection = Utils.getConnection(url);
         response = connection.Delete(BASE_URL + "rest/private/registry/repository/exo:applications/IDE");
         System.out.println("cleanRegistry " + response.getStatusCode());
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (ProtocolNotSuppException e)
      {
         e.printStackTrace();
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

   @AfterClass
   public static void killFireFox()
   {
      try
      {
         if (System.getProperty("os.name").equals("Linux"))
         {
            Runtime.getRuntime().exec("killall firefox");
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   //   public enum IdeAddress {
   //      SHELL("http://127.0.0.1:8888/", "http://127.0.0.1:8888/IDE/Shell.html?gwt.codesvr=127.0.0.1:9997"), PORTAL(
   //         "http://127.0.0.1:8080/", "http://127.0.0.1:8080/portal/private/default/ide"), STANDALONE(
   //         "http://localhost:8080/", "http://localhost:8080/site/index.html");
   //
   //      private String baseUrl;
   //
   //      private String applicationUrl;
   //
   //      IdeAddress(String baseUrl, String applicationUrl)
   //      {
   //         this.baseUrl = baseUrl;
   //         this.applicationUrl = applicationUrl;
   //      }
   //
   //      public String getBaseUrl()
   //      {
   //         return this.baseUrl;
   //      }
   //
   //      public String getApplicationUrl()
   //      {
   //         return this.applicationUrl;
   //      }
   //
   //   }

   protected static boolean isRunIdeUnderPortal()
   {
      //      return APPLICATION_URL.equals(IdeAddress.PORTAL.getApplicationUrl());

      //now ide not run under portal
      return false;
   }

   protected static boolean isRunIdeAsStandalone()
   {
      return !isRunIdeAsShell();
   }

   protected static boolean isRunIdeAsShell()
   {
      return Boolean.valueOf(IDE_SETTINGS.getString("ide.run.in.shell"));
   }

   protected boolean isRunTestUnderWindowsOS()
   {
      return selenium().getEval("/Win/.test(navigator.platform)").equals("true");
   }

   /**
    * remove all cookies which can be stored by IDE
    */
   protected void deleteCookies()
   {
      if (selenium().isCookiePresent("eXo-IDE-" + USER_NAME + "-line-numbers_bool"))
      {
         selenium().deleteCookie("eXo-IDE-" + USER_NAME + "-line-numbers_bool", "path=/, recurse=true");
      }
      if (selenium().isCookiePresent("eXo-IDE-" + USER_NAME + "-opened-files_list"))
      {
         selenium().deleteCookie("eXo-IDE-" + USER_NAME + "-opened-files_list", "path=/, recurse=true");
      }
      if (selenium().isCookiePresent("eXo-IDE-" + USER_NAME + "-active-file_str"))
      {
         selenium().deleteCookie("eXo-IDE-" + USER_NAME + "-active-file_str", "path=/, recurse=true");
      }
      if (selenium().isCookiePresent("eXo-IDE-" + USER_NAME + "-line-numbers_bool"))
      {
         selenium().deleteCookie("eXo-IDE-" + USER_NAME + "-line-numbers_bool", "path=/, recurse=true");
      }
      if (selenium().isCookiePresent("eXo-IDE-" + USER_NAME + "-lock-tokens_map"))
      {
         selenium().deleteCookie("eXo-IDE-" + USER_NAME + "-lock-tokens_map", "path=/, recurse=true");
      }
   }

   private static final String SELECTED_WORKSPACE_LOCATOR = "//td[@class='cellSelected']//span";

   /**
    * 
    * @return non-active workspace name from "Select Workspace" form
    * @throws Exception 
    */
   public String getNonActiveWorkspaceName() throws Exception
   {
      String secondWorkspaceUrl = null;

      //runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);

      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);

      Thread.sleep(TestConstants.SLEEP);
      selenium().click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/");

      // click "UP" to go to previous workspace in the list
      selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_UP);
      selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_UP);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      // test if "Ok" button is enabled
      if (selenium().isElementPresent(
         "//div[@eventproxy='ideSelectWorkspaceFormOkButton']//td[@class='buttonTitle' and text()='OK']"))
      {
         secondWorkspaceUrl = selenium().getText(SELECTED_WORKSPACE_LOCATOR);
      }
      else
      {
         // click "DOWN" to go to next workspace in the list
         selenium().keyDownNative("" + java.awt.event.KeyEvent.VK_DOWN);
         selenium().keyUpNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.REDRAW_PERIOD);

         // test if "Ok" button is enabled
         if (selenium().isElementPresent(
            "//div[@eventproxy='ideSelectWorkspaceFormOkButton']//td[@class='buttonTitle' and text()='OK']"))
         {
            secondWorkspaceUrl = selenium().getText(SELECTED_WORKSPACE_LOCATOR);
         }
      }

      if ((secondWorkspaceUrl == null) || ("".equals(secondWorkspaceUrl)))
      {
         System.out.println("Error. It is impossible to recognise second workspace!");
      }

      // click the "Cancel" button
      selenium().click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormCancelButton\"]");

      // remove text before workspace name
      String secondWorkspaceName = secondWorkspaceUrl.toLowerCase().replace((ENTRY_POINT_URL).toLowerCase(), "");

      // remove ended '/'
      secondWorkspaceName = secondWorkspaceName.replace("/", "");

      return secondWorkspaceName;
   }

   /**
    * Select workspace from "Select Workspace" form by workspaceName 
    * @param workspaceName
    * @throws Exception
    * @throws InterruptedException
    */
   public void selectWorkspace(String workspaceName) throws Exception, InterruptedException
   {
      //      runTopMenuCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);
      //      Thread.sleep(TestConstants.SLEEP);

      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.SELECT_WORKSPACE);

      // selenium().click("scLocator=//ListGrid[ID=\"ideEntryPointListGrid\"]/body/row[entryPoint[contains(\"/" + workspaceName + "/\")]]/col[fieldName=entryPoint]");

      selenium().mouseDownAt(
         "//div[@eventproxy='ideEntryPointListGrid']//table[@class='listTable']//span[contains(text(), '/"
            + workspaceName + "/')]", "");
      selenium().mouseUpAt(
         "//div[@eventproxy='ideEntryPointListGrid']//table[@class='listTable']//span[contains(text(), '/"
            + workspaceName + "/')]", "");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      // test is "Ok" button enabled
      assertTrue(selenium().isElementPresent(
         "//div[@eventproxy='ideSelectWorkspaceFormOkButton']//td[@class='buttonTitle' and text()='OK']"));

      // click the "Ok" button 
      selenium().click("scLocator=//IButton[ID=\"ideSelectWorkspaceFormOkButton\"]");
      Thread.sleep(TestConstants.SLEEP);

      // test is workspace opened
      assertTrue(selenium().isTextPresent(workspaceName));
      Thread.sleep(TestConstants.SLEEP);
   }

   /**
    * Go to line with lineNumber in the Code Editor by using top menu command "Edit > Go to Line..."
    * @param lineNumber
    * @throws InterruptedException
    */
   public void goToLine(int lineNumber) throws Exception
   {
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.GO_TO_LINE);

      waitForElementPresent("//div[@view-id=\"ideGoToLineForm\"]");
      // Type line number
      selenium().type(Locators.GoToLineWindow.GOTO_LINE_FORM_TEXT_FIELD_LOCATOR, String.valueOf(lineNumber));
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);

      // click "Go" button
      selenium().click(Locators.GoToLineWindow.GOTO_LINE_FORM_GO_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }

   /**
    * Calls selenium refresh method and waits for {@link TestConstants}.IDE_LOAD_PERIOD seconds.
    * 
    * After waits for {@link TestConstants}.SLEEP seconds (while all elements are drawing).
    * 
    * @throws Exception
    */
   public void refresh() throws Exception
   {
      selenium().refresh();
      selenium().waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      //      Thread.sleep(TestConstants.SLEEP_SHORT);

      //Wait while "dev-monit" appears in navigation tree.
      //
      //Sometimes, test fails, becouse after refresh not all 
      //elements are appears in SLEEP tile.
      //Thats why, wait for WAIT_PERIOD for root element
      //of navigation tree.
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
   }

   /**
    * Wait while element present in IDE.
    * 
    * @param locator - element locator
    * @throws Exception
    */
   public void waitForElementPresent(String locator) throws Exception
   {
      long startTime = System.currentTimeMillis();

      while (true)
      {
         if (selenium().isElementPresent(locator))
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

   /**
    * Wait while element not present in IDE.
    * 
    * @param locator - element locator
    * @throws Exception
    */

   public void waitForElementNotPresent(String locator) throws Exception
   {
      for (int second = 0;; second++)
      {
         if (second >= 60)
            fail("timeout");

         try
         {
            if (!selenium().isElementPresent("locator"))
               break;
         }

         catch (Exception e)
         {
            fail("timeout for element " + locator);
         }

         Thread.sleep(TestConstants.REDRAW_PERIOD * 2);
      }
   }

   @AfterFailure
   public void captureScreenShotOnFailure(Throwable failure)
   {
      // Get test method name
      String testMethodName = null;
      for (StackTraceElement stackTrace : failure.getStackTrace())
      {
         if (stackTrace.getClassName().equals(this.getClass().getName()))
         {
            testMethodName = stackTrace.getMethodName();
            break;
         }
      }

      selenium().captureScreenshot("screenshots/" + this.getClass().getName() + "." + testMethodName + ".png");
   }

   /**
    * Click on close button of form.
    * 
    * @param locator locator of form
    * @throws Exception
    */
   protected void closeForm(String locator) throws Exception
   {
      selenium().click(locator + "CancelButton");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Wait, while loader dissapears (which meens, that operation is done).
    * @throws Exception
    */
   protected void waitForLoaderDissapeared() throws Exception
   {
      waitForElementPresent("//div[contains(@url, loader-background-element.png)]");
      waitForElementNotPresent("//div[contains(@url, loader-background-element.png)]");
   }

   /**
    * @param host
    */
   public static void updateAllUrls(String host)
   {
      IDE_HOST = host;
      BASE_URL = "http://" + IDE_HOST + ":" + IDE_PORT + "/";
      APPLICATION_URL = BASE_URL + IDE_SETTINGS.getString("ide.app.url");
      ENTRY_POINT_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/";
      ENTRY_POINT_URL_IDE = BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/";
   }
}