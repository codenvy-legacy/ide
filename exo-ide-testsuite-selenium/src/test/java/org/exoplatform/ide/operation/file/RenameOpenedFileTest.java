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

   private final static String FOLDER = RenameOpenedFileTest.class.getSimpleName();

   private final static String FILE1 = "fileforrename.txt";

   private final static String FILE2 = "Renamed Test File.groovy";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/" + FILE1;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER);
         VirtualFileSystemUtils.put(PATH, MimeType.TEXT_PLAIN, WS_URL + FOLDER + "/" + FILE1);
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
         VirtualFileSystemUtils.delete(WS_URL + FOLDER);
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
      //TODO fix problem see issue 805
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/");

      /*
       * 1. Open Folder
       */
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER + "/");

      /*
       * 2. Open File1
       */
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + FOLDER + "/" + FILE1);

      /*
       * 3. Rename opened file  
       */
      IDE.RENAME_DIALOG.callFromMenu();

      assertEquals("Can't change mime-type to opened file", IDE.RENAME_DIALOG.getWarningMessage());

      IDE.RENAME_DIALOG.setFileName(FILE2);

      IDE.RENAME_DIALOG.clickRenameButton();

      /*
       * 4. Assert file was renamed successfully.
       */
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER + "/" + FILE2);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + FOLDER + "/" + FILE1);

      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + FOLDER + "/" + FILE1).getStatusCode());
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + FOLDER + "/" + FILE2).getStatusCode());

      assertEquals(FILE2, IDE.EDITOR.getTabTitle(0));

      /*
       * 5. Close editor
       */

      IDE.EDITOR.closeFile(0);
   }

}
