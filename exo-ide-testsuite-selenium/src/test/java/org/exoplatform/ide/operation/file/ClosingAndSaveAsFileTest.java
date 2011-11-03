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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class ClosingAndSaveAsFileTest extends BaseTest
{

   private static String FOLDER = ClosingAndSaveAsFileTest.class.getSimpleName();

   private static final String FILE = "testfile";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER + "/");
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
         VirtualFileSystemUtils.delete(WS_URL + FOLDER + "/");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //http://jira.exoplatform.com/browse/IDE-412
   @Test
   public void testClosingAnsSaveAsFile() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER + "/");
      
      String textFileURL = WS_URL_IDE + FOLDER + "/Untitled file.txt";
      String xmlFileURL = WS_URL_IDE + FOLDER + "/Untitled file.xml";
      
      /*
       * 1. Create TEXT and XML files.
       *     - TEXT and XML files must be opened.
       */
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      
      assertTrue(IDE.EDITOR.isFileOpened(textFileURL));
      assertTrue(IDE.EDITOR.isFileOpened(xmlFileURL));

      /*
       * 2. Click on Close XML file button ( index 1 )
       *     -  AskForValue dialog must be opened.
       */
      IDE.EDITOR.clickCloseEditorButton(1);
      assertTrue(IDE.ASK_FOR_VALUE_DIALOG.isOpened());
      
      /*
       * 3. Click "Cancel" button of dialog.
       *     - AskForValue dialog must be closed.
       *     - File must be still opened.
       */
      IDE.ASK_FOR_VALUE_DIALOG.clickCancelButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitForAskDialogNotPresent();
      assertTrue(IDE.EDITOR.isFileOpened(xmlFileURL));      

      /*
       * 4. Select TEXT file and Save it
       *     - File must be present in Workspace tree.
       *     - Text FILE and XML files must be still opened in editor.
       */
      IDE.EDITOR.selectTab(0);
      saveAsUsingToolbarButton(FILE);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/" + FILE);

      assertTrue(IDE.EDITOR.isTabOpened(0));
      assertTrue(IDE.EDITOR.isTabOpened(1));
      
      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(0);
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.checkCodeEditorOpened(1);
      assertEquals(FILE, IDE.EDITOR.getTabTitle(0));
      
      // close editor
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.closeFile(0);
   }

   @Test
   public void testSaveAsFileAfterTryingToCloseNewFile() throws Exception
   {
      /*
       * 1. Refresh page
       */
      refresh();
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/");

      /*
       * 2. Select folder and ctreate XML file.
       */
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER + "/");
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      
      String createdFileURL = WS_URL_IDE + FOLDER + "/Untitled file.xml";
      assertTrue(IDE.EDITOR.isFileOpened(createdFileURL));
      
      /*
       * 3. Click on Close file button
       *     - AskForValue dialog must be opened.
       */
      IDE.EDITOR.clickCloseEditorButton(0);
      assertTrue(IDE.ASK_FOR_VALUE_DIALOG.isOpened());

      /*
       * 5. Click on "Close" button.
       *     - AskForValue dialog must be closed.
       */
      IDE.ASK_FOR_VALUE_DIALOG.closeDialog();
      assertFalse(IDE.ASK_FOR_VALUE_DIALOG.isOpened());
      
      /*
       * 6. Click on Close file button
       *     -  AskForValue dialog must be opened.
       */
      IDE.EDITOR.saveAndCloseFile(0, "new XML file.xml");
      
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/new XML file.xml");
      
      assertFalse(IDE.EDITOR.isFileOpened(createdFileURL));
   }

}
