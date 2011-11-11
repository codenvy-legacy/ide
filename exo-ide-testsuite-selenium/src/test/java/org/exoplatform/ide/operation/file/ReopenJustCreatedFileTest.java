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
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * http://jira.exoplatform.com/browse/IDE-412
 * 
 * Create new Netvibes file and save it. Then close and reopen file.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class ReopenJustCreatedFileTest extends BaseTest
{
   private static final String PROJECT = ReopenJustCreatedFileTest.class.getSimpleName();

   private static final String NETVIBES_FILE_NAME = "file-" + ReopenJustCreatedFileTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
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

   @Test
   public void testReopenJustCreatedFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.NETVIBES_WIDGET);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.html");
      assertTrue(IDE.EDITOR.isFileContentChanged("Untitled file.html"));

      IDE.EDITOR.saveAs(1, NETVIBES_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + NETVIBES_FILE_NAME);
      IDE.EDITOR.closeFile(1);
      IDE.EDITOR.waitTabNotPresent(1);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + NETVIBES_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + NETVIBES_FILE_NAME);

      assertEquals(NETVIBES_FILE_NAME, IDE.EDITOR.getTabTitle(1));
   }

}
