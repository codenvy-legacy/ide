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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
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

   private static String FOLDER_NAME;
   
   private static final String FILE_NAME_1 = "file-" + ClosingAndSaveAsFileTest.class.getSimpleName() + "-" + System.currentTimeMillis();
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @Before
   public void setUp()
   {
      FOLDER_NAME = ClosingAndSaveAsFileTest.class.getSimpleName() + "-" + System.currentTimeMillis();
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME + "/");
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
   
   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME + "/");
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
   
   //http://jira.exoplatform.com/browse/IDE-412
   @Test
   public void testClosingAnsSaveAsFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/");
      
      //----- 1 ----------
      //open 2 new files
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.TEXT_FILE);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      
      //----- 2 ----------
      //try to close xml file
     IDE.EDITOR.closeTab(1);
      //save file as dialog appears
      SaveFileUtils.checkSaveAsDialog(true);
      //close save file as dialog
      closeForm(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR);
      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(1);
      
      //----- 3 ----------
      //select first file (text file)
     IDE.EDITOR.selectTab(0);
      //save file from tab
      saveAsUsingToolbarButton(FILE_NAME_1);
      
      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(0);
      IDE.EDITOR.checkCodeEditorOpened(1);
      assertEquals(FILE_NAME_1,IDE.EDITOR.getTabTitle(0));
   }
   
   @Test
   public void testSaveAsFileAfterTryingToCloseNewFile() throws Exception
   {
      refresh();
      IDE.NAVIGATION.selectItem(WS_URL + FOLDER_NAME + "/");
      
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
      saveAsUsingToolbarButton(FILE_NAME_1);
      //file stays in editor panel
      IDE.EDITOR.checkCodeEditorOpened(0);
   }
   
}
