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
import static org.junit.Assert.assertFalse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 1, 2010 $
 *
 */
public class RenameOpenedFileTest extends BaseTest
{

   private final static String PROJECT = RenameOpenedFileTest.class.getSimpleName();

   private final static String FILE1 = "fileforrename.txt";

   private final static String FILE2 = "Renamed Test File.groovy";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + FILE1;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.put(PATH, MimeType.TEXT_PLAIN, WS_URL + PROJECT + "/" + FILE1);
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

   //IDE-81 Rename Opened File 
   @Test
   public void testRenameOpenedFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE1);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE1);

      

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      IDE.RENAME.waitOpened();

    //Try to rename opened file - warning message must be shown and Mime Type field disabled.
      assertEquals("Can't change mime-type to opened file", IDE.RENAME.getWarningMessage());
      assertFalse(IDE.RENAME.isMimeTypeFieldEnabled());
      assertFalse(IDE.RENAME.isRenameButtonEnabled());

      IDE.RENAME.setNewName(FILE2);
      IDE.RENAME.clickRenameButton();
      IDE.RENAME.waitClosed();

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE2);
      IDE.PROJECT.EXPLORER.isItemPresent(PROJECT + "/" + FILE1);

      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FILE1).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + FILE2.replaceAll(" ", "%20")).getStatusCode());

      assertEquals(FILE2, IDE.EDITOR.getTabTitle(1));
      IDE.EDITOR.closeFile(1);
   }

}
