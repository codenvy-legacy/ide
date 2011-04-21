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

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class PreviewHtmlFileTest extends BaseTest
{
   private final static String FILE_NAME = "PreviewHtmlFile.html";

   private final static String FOLDER_NAME = PreviewHtmlFileTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/PreviewHtmlFile.html";
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
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

   /**
    * IDE-65: Preview HTML File 
    * @throws Exception
    */
   @Test
   public void previewHtmlFile() throws Exception
   {
      waitForRootElement();

      /*
       * 1. create HTML file
       */
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      Thread.sleep(TestConstants.SLEEP);

      /*
       * 2. "Preview" button must be disabled
       */
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.Run.SHOW_PREVIEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW, false);

      /*
       * 3. open "PreviewHtmlFileTest/PreviewHtmlFile.html" file
       */
      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

      /*
       * 4. "Preview" button must be enabled
       */
      IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW, true);
      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.Run.SHOW_PREVIEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW, true);

      /*
       * 5. Click on "Preview" button
       */
      IDE.toolbar().runCommand(ToolbarCommands.Run.SHOW_PREVIEW);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);

      /*
       * 6/ "Preview" must be opened.
       */

      IDE.preview().checkIsOpened(true);
      IDE.preview().selectIFrame(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      assertTrue(selenium.isElementPresent("//p/b/i[text()='Changed Content.']"));
      assertTrue(selenium.isElementPresent("//img[@src='http://www.google.com.ua/intl/en_com/images/logo_plain.png']"));
      IDE.selectMainFrame();

      /*
       * 7. Close "Preview".
       */
      IDE.preview().close();

      /*
       * 8. Close "PreviewHtmlFile.html" and check "Preview" button.
       */
      IDE.editor().closeTab(1);

      IDE.toolbar().checkButtonExistAtRight(ToolbarCommands.Run.SHOW_PREVIEW, true);
      IDE.toolbar().assertButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW, false);
      IDE.menu().checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW, false);

      /*
       * 9. Reopen "PreviewHtmlFile.html" and click "Preview".
       */
      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      IDE.menu().runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW);

      /*
       * 10. Check "Preview" again.
       */
      IDE.preview().checkIsOpened(true);
      IDE.preview().selectIFrame(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      assertTrue(selenium.isElementPresent("//p/b/i[text()='Changed Content.']"));
      assertTrue(selenium.isElementPresent("//img[@src='http://www.google.com.ua/intl/en_com/images/logo_plain.png']"));
      IDE.selectMainFrame();

      /*
       * 11. Close all tabs in editor.
       */
      IDE.editor().closeTab(1);
      IDE.editor().closeTab(0);
      
      IDE.SaveAs().checkSaveAsIsOpened(true);
      IDE.SaveAs().clickNo();
      
      IDE.preview().close();
   }

}
