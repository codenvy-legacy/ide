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
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Response;
import org.junit.After;
import org.junit.Before;
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

   private static final String CREATED_PROJECT_NAME = CheckConfigureClasspathWindowsTest.class.getSimpleName()
      + "-created";

   private static final String CLASSPATH_FILE_CONTENT = "{\n\"entries\": [\n]\n}";

   private static final String CLASSPATH_FILE_NAME = ".groovyclasspath";

   /**
    * Error message, that shown, when you try to configure classpath to no project folder.
    */
   private static final String ERROR_MSG = "Classpath settings not found.\n Probably you are not in project.";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME);
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + PROJECT_NAME);
         VirtualFileSystemUtils.put(CLASSPATH_FILE_CONTENT.getBytes(), MimeType.APPLICATION_JSON, WORKSPACE_URL
            + PROJECT_NAME + "/" + CLASSPATH_FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
         VirtualFileSystemUtils.delete(WORKSPACE_URL + PROJECT_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + CREATED_PROJECT_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testConfigureClasspathAndChooseSourceWindows() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      /*
       * 0. Check, there is no .groovyclasspath file in workspace directory
       */
      assertEquals(404, VirtualFileSystemUtils.get(WORKSPACE_URL + ".groovyclasspath").getStatusCode());

      /*
       * 1. Try to configure classpath for simple folder
       */
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH, true);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);

      /*
       * Error dialog appeared
       */
      IDE.WARNING_DIALOG.waitOpened();
      assertTrue(IDE.WARNING_DIALOG.getWarningMessage().contains(ERROR_MSG));
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      /*
       * 2. Try to configure classpath for workspace
       */
      IDE.WORKSPACE.selectRootItem();
      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH, true);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);

      /*
       * Error dialog appeared
       */
      IDE.WARNING_DIALOG.waitOpened();
      assertTrue(IDE.WARNING_DIALOG.getWarningMessage().contains(ERROR_MSG));
      IDE.WARNING_DIALOG.clickOk();
      IDE.WARNING_DIALOG.waitClosed();

      /*
       * 3. Try to configure classpath for project.
       * Check, that .groovyclasspath in project folder is hidden
       */
      IDE.WORKSPACE.selectItem(WS_URL + PROJECT_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + PROJECT_NAME + "/" + CLASSPATH_FILE_NAME);

      IDE.MENU.checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH, true);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      IDE.PROJECT.CLASSPATH.waitForClasspathDialogOpen();

      /*
       * "Configure classpath" dialog appeared
       */
      IDE.PROJECT.CLASSPATH.checkConfigureClasspathDialog();
      IDE.PROJECT.CLASSPATH.checkAddButtonEnabledState(true);
      IDE.PROJECT.CLASSPATH.checkRemoveButtonEnabledState(false);
      IDE.PROJECT.CLASSPATH.checkSaveButtonEnabledState(true);
      IDE.PROJECT.CLASSPATH.checkCancelButtonEnabledState(true);

      /*
       * Classpath list grid must be empty
       */
      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(0);

      /*
       * 4. Click "Add..." button and check "Choose source path" dialog
       */
      IDE.PROJECT.CLASSPATH.clickAddButton();
      IDE.PROJECT.CLASSPATH.waitForChooseSourceViewOpened();
      IDE.PROJECT.CLASSPATH.checkChooseSourceWindow();
      /*
       * Check, that all workspaces are present in list grid
       */
      String[] workspaces = {WS_NAME, WS_NAME_2};
      IDE.PROJECT.CLASSPATH.checkElementsInChooseSourceTreeGrid(workspaces);
      IDE.PROJECT.CLASSPATH.checkChooseSourceOkButtonEnabledState(false);
      IDE.PROJECT.CLASSPATH.checkChooseSourceCancelButtonEnabledState(true);

      /*
       * 5. Select default workspace. "Ok" button is disabled.
       */
      IDE.PROJECT.CLASSPATH.selectItemInChooseSourceTree(WS_NAME);
      IDE.PROJECT.CLASSPATH.checkChooseSourceOkButtonEnabledState(false);

      /*
       * 6. Open default workspace. Select folder. "Ok" button is enabled.
       */
      IDE.PROJECT.CLASSPATH.openFolderInChooseSourceTree(WS_NAME);

      IDE.PROJECT.CLASSPATH.selectItemInChooseSourceTree(FOLDER_NAME);
      IDE.PROJECT.CLASSPATH.checkChooseSourceOkButtonEnabledState(true);

      IDE.PROJECT.CLASSPATH.clickChooseSourceOkButton();

      /*
       * "Choose source" window dissapeared.
       * New item in Configure Classpath list grid appeared.
       */
      IDE.PROJECT.CLASSPATH.waitForChooseSourceViewClosed();
      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(1);

      final String folderPathForClasspath = WS_NAME + "#" + "/" + FOLDER_NAME + "/";

      assertEquals(folderPathForClasspath, IDE.PROJECT.CLASSPATH.getPathByIndex(1));

      /*
       * 7. Select folder path and check, that remove button is enabled
       */
      IDE.PROJECT.CLASSPATH.selectRowInListGrid(1);
      IDE.PROJECT.CLASSPATH.waitRemoveButtonEnabled(true);
      IDE.PROJECT.CLASSPATH.checkSaveButtonEnabledState(true);

      /*
       * 8. Click remove button and check, that element was removed
       */
      IDE.PROJECT.CLASSPATH.clickRemoveButton();

      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(0);

      /*
       * 9. Add element to list grid. Than click cancel button 
       * to check, that all changes will be canceled.
       */
      IDE.PROJECT.CLASSPATH.clickAddButton();
      IDE.PROJECT.CLASSPATH.waitForChooseSourceViewOpened();
      IDE.PROJECT.CLASSPATH.checkChooseSourceWindow();

      IDE.PROJECT.CLASSPATH.openFolderInChooseSourceTree(WS_NAME);

      IDE.PROJECT.CLASSPATH.selectItemInChooseSourceTree(FOLDER_NAME);
      IDE.PROJECT.CLASSPATH.checkChooseSourceOkButtonEnabledState(true);

      IDE.PROJECT.CLASSPATH.clickChooseSourceOkButton();
      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(1);

      IDE.PROJECT.CLASSPATH.clickCancelButton();

      /*
       * 10. Open form and check, that it is empty
       */
      IDE.WORKSPACE.selectItem(WS_URL + PROJECT_NAME + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      IDE.PROJECT.CLASSPATH.waitForClasspathDialogOpen();

      /*
       * "Configure classpath" dialog appeared
       */
      IDE.PROJECT.CLASSPATH.checkConfigureClasspathDialog();

      /*
       * Classpath list grid must be empty
       */
      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(0);

      /*
       * 11. Add element to list grid. And click save button
       */
      IDE.PROJECT.CLASSPATH.clickAddButton();
      IDE.PROJECT.CLASSPATH.waitForChooseSourceViewOpened();
      IDE.PROJECT.CLASSPATH.checkChooseSourceWindow();

      IDE.PROJECT.CLASSPATH.openFolderInChooseSourceTree(WS_NAME);

      IDE.PROJECT.CLASSPATH.selectItemInChooseSourceTree(FOLDER_NAME);
      IDE.PROJECT.CLASSPATH.checkChooseSourceOkButtonEnabledState(true);

      IDE.PROJECT.CLASSPATH.clickChooseSourceOkButton();
      IDE.PROJECT.CLASSPATH.waitForChooseSourceViewClosed();
      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(1);

      IDE.PROJECT.CLASSPATH.clickSaveButton();

      Thread.sleep(1000);
      /*
       * Check file .groovyclasspath, that entry was added.
       */
      final String expectedContent =
         "{\"entries\":[{\"kind\":\"dir\", \"path\":\"" + WS_NAME + "#/" + FOLDER_NAME + "/\"}]}";
      Response response = VirtualFileSystemUtils.get(WORKSPACE_URL + PROJECT_NAME + "/" + ".groovyclasspath");

      assertEquals(expectedContent, response.getData());
   }

   /**
    * Check, that Configure classpath window dialog appeared,
    * when new project created.
    * 
    * @throws Exception
    */
   @Test
   public void testConfigureClasspathAppeared() throws Exception
   {
      refresh();
      IDE.WORKSPACE.waitForRootItem();
      
      //1. Create new project: New -> Project from template
      //TODO no such functionality : IDE.TEMPLATES.createProjectFromTemplate(Templates.DEFAULT_PROJECT_TEMPLATE_NAME, CREATED_PROJECT_NAME);
      
      IDE.PROJECT.CLASSPATH.waitForClasspathDialogOpen();
      //Check, Configure Classpath window appeared.
      IDE.PROJECT.CLASSPATH.checkConfigureClasspathDialog();

      //Check, there is project folder in Classpath listgrid

      IDE.PROJECT.CLASSPATH.checkItemsCountInClasspathGrid(1);
      final String firstResource = IDE.PROJECT.CLASSPATH.getPathByIndex(1);
      assertEquals(WS_NAME + "#" + "/" + CREATED_PROJECT_NAME + "/", firstResource);

      // 2. Close form.
      IDE.PROJECT.CLASSPATH.clickCancelButton();
      IDE.PROJECT.CLASSPATH.waitForClasspathDialogClose();

      // Check new project created.

      IDE.NAVIGATION.assertItemVisible(WS_URL + CREATED_PROJECT_NAME + "/");

      // Check .groovyclasspath file
      final String expectedContent =
         "{\"entries\":[{\"kind\":\"dir\", \"path\":\"" + WS_NAME + "#/" + CREATED_PROJECT_NAME + "/" + "\"}]}";
      Response response =
         VirtualFileSystemUtils.get(WORKSPACE_URL + CREATED_PROJECT_NAME + "/" + ".groovyclasspath");

      assertEquals(expectedContent, response.getData());
   }

}
