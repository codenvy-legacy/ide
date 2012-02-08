/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Perspective;
import org.exoplatform.ide.operation.file.OpeningSavingAndClosingFilesTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MaximizeRestoreTest extends BaseTest
{
   private static String PROJECT = OpeningSavingAndClosingFilesTest.class.getSimpleName();

   private static String path = "src/test/resources/org/exoplatform/ide/miscellaneous/SampleHtmlFile.html";

   private static String FILE_NAME = OpeningSavingAndClosingFilesTest.class.getSimpleName() + ".html";

   @BeforeClass
   public static void setUp()
   {

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.TEXT_HTML, path);
      }
      catch (IOException e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void maximizeRestoreNavigationPanel() throws Exception
   {
      //step 1 check maximize/restore for Navigation panel 
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PERSPECTIVE.maximizePanel(Perspective.Panel.NAVIGATION);
      IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.NAVIGATION);
      assertTrue(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.NAVIGATION));
      IDE.PERSPECTIVE.restorePanel(Perspective.Panel.NAVIGATION);
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.NAVIGATION));

      //step 2 check maximize/restore for Editor panel
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      IDE.PERSPECTIVE.maximizePanel(Perspective.Panel.EDITOR);
      IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.EDITOR);
      assertTrue(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.EDITOR));
      IDE.PERSPECTIVE.restorePanel(Perspective.Panel.EDITOR);
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.EDITOR));

      //step 3 check maximize/restore for Editor panel
      IDE.TOOLBAR.runCommand(MenuCommands.Run.SHOW_PREVIEW);
      IDE.PREVIEW.waitHtmlPreviewOpened();
      
      IDE.PERSPECTIVE.maximizePanel(Perspective.Panel.OPERATION);
      assertTrue(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.OPERATION));
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.EDITOR));
      IDE.PERSPECTIVE.restorePanel(Perspective.Panel.OPERATION);
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.OPERATION));
    
      //step 4 check maximize/restore for Outline panel
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.PERSPECTIVE.maximizePanel(Perspective.Panel.INFORMATION);
      assertTrue(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.INFORMATION));
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.EDITOR));
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.OPERATION));
      IDE.PERSPECTIVE.restorePanel(Perspective.Panel.INFORMATION);
      assertFalse(IDE.PERSPECTIVE.isPanelMaximized(Perspective.Panel.OPERATION));
    
   }

}
