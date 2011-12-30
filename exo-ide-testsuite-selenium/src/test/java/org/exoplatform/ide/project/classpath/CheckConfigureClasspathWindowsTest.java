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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
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
   private static final String PROJECT = CheckConfigureClasspathWindowsTest.class.getSimpleName();
   
   private static final String PROJECT_2 = CheckConfigureClasspathWindowsTest.class.getSimpleName() + "-2";

   @Before
   public void setUp()
   {
      try
      {
         //create exo-app project with .groovyclasspath file
         String projectPath = "src/test/resources/org/exoplatform/ide/project/exo-app.zip";
         VirtualFileSystemUtils.importZipProject(PROJECT, projectPath);
         
         //create default project with no .groovyclasspath file
         VirtualFileSystemUtils.createDefaultProject(PROJECT_2);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT_2);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testConfigureClasspathAndChooseSourceWindows() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT_2);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT_2);
      
      assertFalse(IDE.MENU.isCommandVisible(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH));
      
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.CLOSE_PROJECT);

      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT_2);
      
      //open project to configure classpath
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      assertTrue(IDE.MENU.isCommandVisible(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH));
      
      //call configure classpath form
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      IDE.PROJECT.CLASSPATH.waitOpened();

      /*
       * "Configure classpath" dialog appeared
       */
      assertTrue(IDE.PROJECT.CLASSPATH.isSaveButtonEnabled());
      assertTrue(IDE.PROJECT.CLASSPATH.isCancelButtonEnabled());
      assertTrue(IDE.PROJECT.CLASSPATH.isAddButtonEnabled());
      assertFalse(IDE.PROJECT.CLASSPATH.isRemoveButtonEnabled());
      assertTrue(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT + "/"));
      
      /*PROJECT.
       * Click "Add..." button and check "Choose source path" dialog
       */
      IDE.PROJECT.CLASSPATH.clickAddButton();
      IDE.PROJECT.CLASSPATH_SOURCE.waitOpened();
      assertFalse(IDE.PROJECT.CLASSPATH_SOURCE.isOkButtonEnabled());
      assertTrue(IDE.PROJECT.CLASSPATH_SOURCE.isCancelButtonEnabled());
      assertTrue(IDE.PROJECT.CLASSPATH_SOURCE.isTreeVisible());
      assertTrue(IDE.PROJECT.CLASSPATH_SOURCE.isPathPresent(PROJECT));
      assertTrue(IDE.PROJECT.CLASSPATH_SOURCE.isPathPresent(PROJECT_2));
      //select not root item
      IDE.PROJECT.CLASSPATH_SOURCE.selectItem(PROJECT_2);
      IDE.PROJECT.CLASSPATH_SOURCE.waitOkButtonEnabled(true);
      //click ok button
      IDE.PROJECT.CLASSPATH_SOURCE.clickOkButton();
      //"Choose source" window dissapeared.
      IDE.PROJECT.CLASSPATH_SOURCE.waitClosed();
      //New item in Configure Classpath list grid appeared.
      assertTrue(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT + "/"));
      assertTrue(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT_2 + "/"));
      
      //Select folder path and check, that remove button is enabled
      IDE.PROJECT.CLASSPATH.selectPath(WS_NAME + "#/" + PROJECT_2 + "/");
      assertTrue(IDE.PROJECT.CLASSPATH.isRemoveButtonEnabled());
      assertTrue(IDE.PROJECT.CLASSPATH.isSaveButtonEnabled());
      
      //Click remove button and check, that element was removed
      IDE.PROJECT.CLASSPATH.clickRemoveButton();
      IDE.PROJECT.CLASSPATH.waitPathRemoved(WS_NAME + "#/" + PROJECT_2 + "/");
      
      /*
       * Add element to list grid. Than click cancel button 
       * to check, that all changes will be canceled.
       */
      IDE.PROJECT.CLASSPATH.clickAddButton();
      IDE.PROJECT.CLASSPATH_SOURCE.waitOpened();
      IDE.PROJECT.CLASSPATH_SOURCE.selectItem(PROJECT_2);
      IDE.PROJECT.CLASSPATH_SOURCE.waitOkButtonEnabled(true);
      IDE.PROJECT.CLASSPATH_SOURCE.clickOkButton();
      IDE.PROJECT.CLASSPATH_SOURCE.waitClosed();
      assertTrue(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT_2 + "/"));
      IDE.PROJECT.CLASSPATH.clickCancelButton();
      IDE.PROJECT.CLASSPATH.waitClosed();

      /*
       * Open form and check, that only one element is present
       */
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.CONFIGURE_CLASS_PATH);
      IDE.PROJECT.CLASSPATH.waitOpened();

      assertTrue(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT + "/"));
      assertFalse(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT_2 + "/"));

      /*
       * Add element to list grid. And click save button
       */
      IDE.PROJECT.CLASSPATH.clickAddButton();
      IDE.PROJECT.CLASSPATH_SOURCE.waitOpened();
      IDE.PROJECT.CLASSPATH_SOURCE.selectItem(PROJECT_2);
      IDE.PROJECT.CLASSPATH_SOURCE.waitOkButtonEnabled(true);
      IDE.PROJECT.CLASSPATH_SOURCE.clickOkButton();
      IDE.PROJECT.CLASSPATH_SOURCE.waitClosed();
      //New item in Configure Classpath list grid appeared.
      assertTrue(IDE.PROJECT.CLASSPATH.isPathPresent(WS_NAME + "#/" + PROJECT_2 + "/"));
      IDE.PROJECT.CLASSPATH.clickSaveButton();
      IDE.PROJECT.CLASSPATH.waitClosed();
      
      //Check file .groovyclasspath, that entry was added.
      Response response = VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + ".groovyclasspath");
      assertTrue(response.getData().contains(WS_NAME + "#/" + PROJECT_2 + "/"));
   }
}
