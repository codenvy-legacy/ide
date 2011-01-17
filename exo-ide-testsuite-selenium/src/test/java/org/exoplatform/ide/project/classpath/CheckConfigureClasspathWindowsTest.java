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
package org.exoplatform.ide.project.classpath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Check, that Configure Classpath and Choose source dialog windows
 * work correctly: buttons actions work correctly, and new entries 
 * are added to .groovyclasspath file of project.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 13, 2011 $
 *
 */
public class CheckConfigureClasspathWindowsTest extends BaseTest
{
   
   private static final String FOLDER_NAME = CheckConfigureClasspathWindowsTest.class.getSimpleName() + "-folder";
   
   private static final String PROJECT_NAME = CheckConfigureClasspathWindowsTest.class.getSimpleName() + "-project";
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static final String CLASSPATH_FILE_CONTENT = "{\n\"entries\": [\n]\n}";
   
   private static final String CLASSPATH_FILE_NAME = ".groovyclasspath";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.mkcol(URL + PROJECT_NAME);
         VirtualFileSystemUtils.put(CLASSPATH_FILE_CONTENT.getBytes(), MimeType.APPLICATION_JSON, 
            URL + PROJECT_NAME + "/" + CLASSPATH_FILE_NAME);
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
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
         VirtualFileSystemUtils.delete(URL + PROJECT_NAME);
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
   public void testConfigureClasspathAndChooseSourceWindows() throws Exception
   {
      waitForRootElement();
      
      /*
       * 0. Check, there is no .groovyclasspath file in workspace directory
       */
      assertEquals(HTTPStatus.NOT_FOUND, VirtualFileSystemUtils.get(URL + ".groovyclasspath").getStatusCode());
      
      /*
       * 1. Try to configure classpath for simple folder
       */
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH, true);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      
      /*
       * Error dialog appeared
       */
      IDE.dialogs().checkOneBtnDialog("Error");
      
      final String errorMsg = "Groovy class path location is not found.\n" 
         + " Possible reason : Project is not selected in browser tree.";
      
      IDE.dialogs().checkTextInDialog(errorMsg);
      IDE.dialogs().clickOkButton();
      
      /*
       * 2. Try to configure classpath for workspace
       */
      selectRootOfWorkspaceTree();
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH, true);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      
      /*
       * Error dialog appeared
       */
      IDE.dialogs().checkOneBtnDialog("Error");
      IDE.dialogs().checkTextInDialog(errorMsg);
      IDE.dialogs().clickOkButton();
      
      /*
       * 3. Try to configure classpath for project.
       * Check, that .groovyclasspath in project folder is hidden
       */
      selectItemInWorkspaceTree(PROJECT_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertElementNotPresentInWorkspaceTree(CLASSPATH_FILE_NAME);
      
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH, true);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      Thread.sleep(TestConstants.SLEEP);
      
      /*
       * "Configure classpath" dialog appeared
       */
      ClasspathUtils.checkConfigureClasspathDialog();
      ClasspathUtils.checkConfigureClasspathButtonEnabled(ClasspathUtils.TITLES.ADD, true);
      ClasspathUtils.checkConfigureClasspathButtonEnabled(ClasspathUtils.TITLES.REMOVE, false);
      ClasspathUtils.checkConfigureClasspathButtonEnabled(ClasspathUtils.TITLES.SAVE, true);
      ClasspathUtils.checkConfigureClasspathButtonEnabled(ClasspathUtils.TITLES.CANCEL, true);
      assertEquals(ClasspathUtils.TITLES.CLASSPATH_DIALOG_TITLE,
         selenium.getText(ClasspathUtils.Locators.SC_CONFIGURE_CLASSPATH_DIALOG_HEADER));
      
      /*
       * Classpath list grid must be empty
       */
      assertFalse(selenium.isElementPresent(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      /*
       * 4. Click "Add..." button and check "Choose source path" dialog
       */
      ClasspathUtils.clickAdd();
      ClasspathUtils.checkChooseSourceWindow();
      /*
       * Check, that all workspaces are present in list grid
       */
      ClasspathUtils.checkElementsInChooseSourceTreeGrid(WORKSPACES);
      ClasspathUtils.checkChooseSourceButtonEnabled(ClasspathUtils.TITLES.OK, false);
      ClasspathUtils.checkChooseSourceButtonEnabled(ClasspathUtils.TITLES.CANCEL, true);
      
      /*
       * 5. Select default workspace. "Ok" button is disabled.
       */
      ClasspathUtils.selectItemInChooseSourceTreegrid(WS_NAME);
      ClasspathUtils.checkChooseSourceButtonEnabled(ClasspathUtils.TITLES.OK, false);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      /*
       * 6. Open default workspace. Select folder. "Ok" button is enabled.
       */
      ClasspathUtils.openFolderInChooseSourceTreegrid(WS_NAME);
      
      ClasspathUtils.selectItemInChooseSourceTreegrid(FOLDER_NAME);
      ClasspathUtils.checkChooseSourceButtonEnabled(ClasspathUtils.TITLES.OK, true);
      
      ClasspathUtils.clickOk();
      
      /*
       * "Choose source" window dissapeared.
       * New item in Configure Classpath list grid appeared.
       */
      assertFalse(selenium.isElementPresent(ClasspathUtils.Locators.SC_CHOOSE_SOURCE_WINDOW));
      assertTrue(selenium.isElementPresent(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      final String folderPathForClasspath = WEBDAV_CONTEXT + "://" + REPO_NAME + "/" + WS_NAME + "#/" + FOLDER_NAME + "/";
      
      assertEquals(folderPathForClasspath, selenium.getText(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      /*
       * 7. Select folder path and check, that remove button is enabled
       */
      selenium.click(ClasspathUtils.getScListGridEntryLocator(0, 0));
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      ClasspathUtils.checkConfigureClasspathButtonEnabled(ClasspathUtils.TITLES.REMOVE, true);
      ClasspathUtils.checkConfigureClasspathButtonEnabled(ClasspathUtils.TITLES.SAVE, true);
      
      /*
       * 8. Click remove button and check, that element was removed
       */
      ClasspathUtils.clickRemove();
      
      assertFalse(selenium.isElementPresent(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      /*
       * 9. Add element to list grid. Than click cancel button 
       * to check, that all changes will be canceled.
       */
      ClasspathUtils.clickAdd();
      ClasspathUtils.checkChooseSourceWindow();
      
      ClasspathUtils.openFolderInChooseSourceTreegrid(WS_NAME);
      
      ClasspathUtils.selectItemInChooseSourceTreegrid(FOLDER_NAME);
      ClasspathUtils.checkChooseSourceButtonEnabled(ClasspathUtils.TITLES.OK, true);
      
      ClasspathUtils.clickOk();
      assertTrue(selenium.isElementPresent(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      ClasspathUtils.clickCancel();
      
      /*
       * 10. Open form and check, that it is empty
       */
      selectItemInWorkspaceTree(PROJECT_NAME);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      Thread.sleep(TestConstants.SLEEP);
      
      /*
       * "Configure classpath" dialog appeared
       */
      ClasspathUtils.checkConfigureClasspathDialog();
      
      /*
       * Classpath list grid must be empty
       */
      assertFalse(selenium.isElementPresent(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      /*
       * 11. Add element to list grid. And click save button
       */
      ClasspathUtils.clickAdd();
      ClasspathUtils.checkChooseSourceWindow();
      
      ClasspathUtils.openFolderInChooseSourceTreegrid(WS_NAME);
      
      ClasspathUtils.selectItemInChooseSourceTreegrid(FOLDER_NAME);
      ClasspathUtils.checkChooseSourceButtonEnabled(ClasspathUtils.TITLES.OK, true);
      
      ClasspathUtils.clickOk();
      assertTrue(selenium.isElementPresent(ClasspathUtils.getScListGridEntryLocator(0, 0)));
      
      ClasspathUtils.clickSave();
      
      /*
       * Check file .groovyclasspath, that entry was added.
       */
      final String expectedContent = "{\"entries\":[{\"kind\":\"dir\", \"path\":\"" 
         + WEBDAV_CONTEXT + "://" + REPO_NAME + "/" + WS_NAME + "#/" + FOLDER_NAME + "/\"}]}";
      HTTPResponse response = VirtualFileSystemUtils.get(URL + PROJECT_NAME + "/" + ".groovyclasspath");
      final String content = new String(response.getData());
      
      assertEquals(expectedContent, content);
   }

}
