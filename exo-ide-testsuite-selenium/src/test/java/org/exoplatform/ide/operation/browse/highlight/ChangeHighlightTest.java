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
package org.exoplatform.ide.operation.browse.highlight;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 15, 2010 $
 *
 */
public class ChangeHighlightTest extends BaseTest
{
   private static String PROJECT = ChangeHighlightTest.class.getSimpleName();

   private static String FILE_NAME = "gadget";

   @Before
   public void setUp() throws Exception
   {
      VirtualFileSystemUtils.createDefaultProject(PROJECT);
   }

   @Test
   public void testChangeHighlihtTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      assertTrue(IDE.PROJECT.EXPLORER.isActive());

      //Open new file:
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      assertTrue(IDE.EDITOR.isActive(0));
      assertFalse(IDE.PROJECT.EXPLORER.isActive());

      //Close file:
      IDE.EDITOR.closeTabIgnoringChanges(1);
      IDE.EDITOR.waitTabNotPresent(1);
      assertTrue(IDE.PROJECT.EXPLORER.isActive());

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      assertTrue(IDE.PROJECT.EXPLORER.isActive());

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.xml");
      IDE.EDITOR.saveAs(1, FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      IDE.PREVIEW.waitGadgetPreviewOpened();

      assertTrue(IDE.PREVIEW.isGadgetPreviewOpened());
      assertTrue(IDE.PREVIEW.isGadgetPreviewActive());
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      assertTrue(IDE.PROJECT.EXPLORER.isActive());
   }

   @After
   public void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

}
