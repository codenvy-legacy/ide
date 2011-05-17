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

   private static final String FILE = "file-" + ClosingAndSaveAsFileTest.class.getSimpleName();

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

      //----- 1 ----------
      //open 2 new files
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);

      //----- 2 ----------
      //try to close xml file
      IDE.EDITOR.closeTab(1);

      //save file as dialog appears
      IDE.SAVE_AS.checkIsOpened(true);

      //close save file as dialog
      IDE.SAVE_AS.clickCancel();

      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(1);

      //----- 3 ----------
      //select first file (text file)
      IDE.EDITOR.selectTab(0);

      //save file from tab
      saveAsUsingToolbarButton(FILE);

      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(0);
      IDE.EDITOR.checkCodeEditorOpened(1);
      assertEquals(FILE, IDE.EDITOR.getTabTitle(0));
      
      // close editor
      IDE.EDITOR.closeTab(1);
      IDE.EDITOR.closeTab(0);
   }

   /*
   @Test
   public void testSaveAsFileAfterTryingToCloseNewFile() throws Exception
   {
      refresh();
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER + "/");

      //----- 1 ----------
      //open new file
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);

      //----- 2 ----------
      //try to close xml file
      IDE.EDITOR.closeTab(0);
      //save file as dialog appears
      SaveFileUtils.checkSaveAsDialog(true);
      //close save file as dialog
      closeForm(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR);
      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(0);

      //----- 3 ----------
      //try to save as new xml file
      saveAsUsingToolbarButton(FILE);
      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(0);
   }
   */

}
