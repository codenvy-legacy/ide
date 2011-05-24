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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 1, 2010 $
 *
 */

public class RenameClosedFileTest extends BaseTest
{

   private static final String FOLDER_NAME = "RenameClosedFileTest";

   private static final String ORIG_FILE_NAME = "fileforrename.txt";

   private static final String RENAMED_FILE_NAME = "Renamed Test File.groovy";

   private static final String FILE_CONTENT = "file for rename";

   private static final String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + ORIG_FILE_NAME;

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(PATH, MimeType.TEXT_PLAIN, WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
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
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //IDE-121 Rename Closed File
   @Test
   public void testRenameClosedFile() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      

      /*
       * 1. Refresh test folder and select file
       */
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);

      /*
       * 2. Run menu command File > Rename
       */
      IDE.RENAME_DIALOG.callFromMenu();
      IDE.RENAME_DIALOG.setFileName(RENAMED_FILE_NAME);
      IDE.RENAME_DIALOG.clickRenameButton();

      /*
       * 3. Assert File was renamed successfully.
       */
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);

      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME).getStatusCode());
   }

   @Test
   public void testChangeMimeType() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");

      /*
       * 1. Refresh Folder and select File
       */
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);

      /*
       * 2. Run menu command File > Rename, change mime-type in Rename dialog and click Rename button
       */
      IDE.RENAME_DIALOG.callFromMenu();
      IDE.RENAME_DIALOG.setMimeType(MimeType.TEXT_XML);
      IDE.RENAME_DIALOG.clickRenameButton();

      /*
       * 3. Open File in editor, check it's content
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      final String textFromEditor = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals(FILE_CONTENT, textFromEditor);

      /*
       * 4. Show Properties and check for mime-type was changed successfully.
       */
      IDE.PROPERTIES.openProperties();
      assertEquals(MimeType.TEXT_XML, IDE.PROPERTIES.getContentType());

      /*
       * 5. Close Properties and Editor
       */
      IDE.PROPERTIES.closeProperties();
      IDE.EDITOR.closeFile(0);
   }

   @Test
   public void testRenameAndChangeMimeType() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      
      /*
       * 1. Refresh Folder and select File
       */
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      /*
       * 2. Run menu command File > Rename, change name and mime-type in Rename dialog and click Rename button
       */
      IDE.RENAME_DIALOG.callFromMenu();
      IDE.RENAME_DIALOG.setFileName(RENAMED_FILE_NAME);
      IDE.RENAME_DIALOG.setMimeType(MimeType.TEXT_XML);
      IDE.RENAME_DIALOG.clickRenameButton();
      
      /*
       * 3. Check file was renamed successfully
       */
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER_NAME + "/" + ORIG_FILE_NAME);
      
      /*
       * 4. Open file in editor, show it's properties and check for mime-type
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER_NAME + "/" + RENAMED_FILE_NAME);
      final String textFromEditor = IDE.EDITOR.getTextFromCodeEditor(0);
      assertEquals(FILE_CONTENT, textFromEditor);

      IDE.PROPERTIES.openProperties();
      assertEquals(MimeType.TEXT_XML, IDE.PROPERTIES.getContentType());
      
      /*
       * 5. Close Properties and Editor
       */
      IDE.PROPERTIES.closeProperties();
      IDE.EDITOR.closeFile(0);
   }

}
