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
package org.exoplatform.ide.operation.cutcopy;

import static org.junit.Assert.assertEquals;

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
import java.util.UUID;

/**
 * IDE-113:Copy folders and files.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class CopyFoldersAndFilesTest extends BaseTest
{
   
   private static final String FOLDER_1 = CopyFoldersAndFilesTest.class.getSimpleName() + "-1";
   
   private static final String FOLDER_1_URL = WS_URL + FOLDER_1 + "/";
   
   private static final String FOLDER_1_1 = CopyFoldersAndFilesTest.class.getSimpleName() + "-1-1";
   
   private static final String FOLDER_1_2 = CopyFoldersAndFilesTest.class.getSimpleName() + "-1-2";
   
   private static final String FILE_GADGET = "gadget_xml";
   
   private static final String FILE_GROOVY = "test_groovy";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private static final String FOLDER_1_2_URL = URL + FOLDER_1 + "/" + FOLDER_1_2 + "/";

   private static final String FOLDER_1_1_URL = URL + FOLDER_1 + "/" + FOLDER_1_1 + "/";
   
   private static final String RANDOM_CONTENT_1 = UUID.randomUUID().toString();
   
   private static final String RANDOM_CONTENT_2 = UUID.randomUUID().toString();
   
   /**
    * BeforeClass create such structure:
    * FOLDER_1
    *    FILE_GADGET - file with sample content
    *    FILE_GROOVY - file with sample content
    *    FOLDER_1_1
    *    FOLDER_1_2
    */
   @BeforeClass
   public static void setUp()
   {
      
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_1);
         VirtualFileSystemUtils.mkcol(FOLDER_1_1_URL);
         VirtualFileSystemUtils.mkcol(FOLDER_1_2_URL);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_1.getBytes(), MimeType.GOOGLE_GADGET, URL + FOLDER_1 + "/" + FILE_GADGET);
         VirtualFileSystemUtils.put(RANDOM_CONTENT_2.getBytes(), MimeType.APPLICATION_GROOVY, URL + FOLDER_1 + "/" + FILE_GROOVY);
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
         VirtualFileSystemUtils.delete(URL + FOLDER_1);
         VirtualFileSystemUtils.delete(URL + FOLDER_1_2);
         VirtualFileSystemUtils.delete(URL + FILE_GROOVY);
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
   
   /*
    * Create folder "/Test 1"
    * Create Google Gadget
    * Select folder "/Test 1"
    * Save file as "gadget_xml"
    * Close editor
    * Create Groovy Script
    * Select folder "/Test 1"
    * Save file as "test_groovy"
    * Close editor
    * Create folder "/Test 1/Test 1-1"
    * Create folder "/Test 1/Test 1-2"
    * Open file "/Test 1/test_groovy"
    * Open file "/Test 1/gadget_xml"
    * Select all files in folder "/Test 1"
    * Run menu command "Edit/Copy"
    * 
    * Select file "/Test 1/gadget_xml"
    * Delete selected item
    * Select folder "/Test 1/Test 1-1"
    * Delete selected item
    * 
    * Select root
    * Run menu command "Edit/Paste"
    * Run menu command "View/Get URL" and receive WebDAV url of root
    * 
    * Navigate to received URL and check items "test_groovy" and "Test 1-2" for existing
    * 
    * Go Back
    * 
    * Check file is opened
    * Check content of opened file
    * 
    * Check state of "Edit/Paste" command ( it must be false ) 
    * 
    * Select all items in root
    * Delete selected items
    * 
    */

   /**
    * IDE-113.
    * 
    * @throws Exception
    */
   @Test
   public void testCopyFoldersAndFiles() throws Exception
   {
      waitForRootElement();
      IDE.navigator().selectItem(FOLDER_1_URL);

      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      openFileFromNavigationTreeWithCodeEditor(FILE_GROOVY, false);

      openFileFromNavigationTreeWithCodeEditor(FILE_GADGET, false);      

      //Select "test 1/gadget.xml", "test 1/test.groovy", "test 1/test 1.1",  "test 1/test 1.2" items in the Workspace Panel.
      selenium.controlKeyDown();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.navigator().selectItem(FOLDER_1_URL + FILE_GADGET);
      IDE.navigator().selectItem(FOLDER_1_URL + FILE_GROOVY);
      IDE.navigator().selectItem(FOLDER_1_1_URL);
      IDE.navigator().selectItem(FOLDER_1_2_URL);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.toolbar().assertButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      
      // Call the "Edit->Copy Items" topmenu command.
      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.COPY_MENU);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);

      IDE.navigator().selectItem(FOLDER_1_1_URL);
      deleteSelectedItems();

      IDE.navigator().selectItem(FOLDER_1_URL + FILE_GADGET);
      deleteSelectedItems();

      //Delete "test 1/test 1.1" folder and "test 1/gadget.xml" file. Select root item. Click on "Paste" button.
      selectRootOfWorkspaceTree();

      IDE.menu().runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU);
      
      checkFilesAndFoldersOnServer();

      checkCodeEditorOpened(0);

      assertEquals(RANDOM_CONTENT_2, getTextFromCodeEditor(0));
      
      IDE.menu().checkCommandVisibility(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, true);
      IDE.menu().checkCommandEnabled(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.PASTE_MENU, false);
      
      IDE.toolbar().assertButtonExistAtLeft(MenuCommands.Edit.PASTE_TOOLBAR, true);
      IDE.toolbar().assertButtonEnabled(MenuCommands.Edit.PASTE_TOOLBAR, false);
      
   }
   
   private void checkFilesAndFoldersOnServer() throws Exception
   {
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FOLDER_1).getStatusCode());
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FOLDER_1_2).getStatusCode());
      final HTTPResponse fileResponse1 = VirtualFileSystemUtils.get(URL + FILE_GROOVY);
      assertEquals(HTTPStatus.OK, fileResponse1.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, new String(fileResponse1.getData()));
      
      //children of FOLDER_1
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(FOLDER_1_2_URL).getStatusCode());
      final HTTPResponse fileResponse2 = VirtualFileSystemUtils.get(URL + FOLDER_1 + "/" + FILE_GROOVY);
      assertEquals(HTTPStatus.OK, fileResponse2.getStatusCode());
      assertEquals(RANDOM_CONTENT_2, new String(fileResponse2.getData()));
   }

}
