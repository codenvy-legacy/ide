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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
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
   private final static String PROJECT = PreviewHtmlFileTest.class.getSimpleName();

   private final static String FILE_NAME = "PreviewHtmlFile.html";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/PreviewHtmlFile.html";
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, WS_URL + PROJECT + "/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
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
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      /*
       * 1. create HTML file
       */
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.HTML_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.html");

      /*
       * 2. "Preview" button must be disabled
       */
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.SHOW_PREVIEW));
      assertFalse(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW));
      assertFalse(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW));

      /*
       * 3. open "PreviewHtmlFileTest/PreviewHtmlFile.html" file
       */
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      /*
       * 4. "Preview" button must be enabled
       */
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW));
      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.SHOW_PREVIEW));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW));

      /*
       * 5. Click on "Preview" button
       */
      IDE.TOOLBAR.runCommand(ToolbarCommands.Run.SHOW_PREVIEW);
      IDE.PREVIEW.waitHtmlPreviewOpened();
      
      
    //TODO  rework following all steps when ready preview:
      
      IDE.PREVIEW.selectIFrame(PROJECT + "/" + FILE_NAME);

      assertTrue(selenium().isElementPresent("//p/b/i[text()='Changed Content.']"));
      assertTrue(selenium()
         .isElementPresent("//img[@src='http://www.google.com.ua/intl/en_com/images/logo_plain.png']"));
      IDE.selectMainFrame();

      /*
       * 7. Close "Preview".
       */
      IDE.PREVIEW.close();

      /*
       * 8. Close "PreviewHtmlFile.html" and check "Preview" button.
       */
      IDE.EDITOR.closeFile(1);

      assertTrue(IDE.TOOLBAR.isButtonPresentAtRight(ToolbarCommands.Run.SHOW_PREVIEW));
      IDE.TOOLBAR.assertButtonEnabled(ToolbarCommands.Run.SHOW_PREVIEW, false);
      IDE.MENU.checkCommandEnabled(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW, false);

      /*
       * 9. Reopen "PreviewHtmlFile.html" and click "Preview".
       */
      IDE.NAVIGATION.selectAndRefreshFolder(PROJECT + "/");
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + PROJECT + "/" + FILE_NAME, false);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW);

      /*
       * 10. Check "Preview" again.
       */
      IDE.PREVIEW.checkPreviewHTMLIsOpened(true);
      IDE.PREVIEW.selectIFrame(WS_URL_IDE + PROJECT + "/" + FILE_NAME);
      assertTrue(selenium().isElementPresent("//p/b/i[text()='Changed Content.']"));
      assertTrue(selenium()
         .isElementPresent("//img[@src='http://www.google.com.ua/intl/en_com/images/logo_plain.png']"));
      IDE.selectMainFrame();

      /*
       * 11. Close all tabs in editor.
       */
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.closeTabIgnoringChanges(0);

      IDE.PREVIEW.close();
   }

}
