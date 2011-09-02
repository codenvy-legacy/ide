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
package org.exoplatform.ide.operation.folder;

import static org.junit.Assert.assertEquals;

import org.everrest.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateFolderWithNonLatinSymbolsTest extends BaseTest
{
   private static String FOLDER_NAME = "Папка з кирилічними символами";


   /**
    * Test added to Ignore, because at the moment not solved a problem with encoding Cyrillic characters to URL.
    * For example: create new file with cyrillic name, save him, and get URL in IDE. In URL IDE we  shall see 
    * encoding characters in file name
    * @throws Exception
    */
   @Ignore
   @Test
   public void testCreateFolderWithNonLatinSymbols() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      // Create folder with Cyrillic name
      IDE.NAVIGATION.createFolder(FOLDER_NAME);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      //Check in repository
      assertEquals(200, VirtualFileSystemUtils.get(WS_URL + URLEncoder.encode(FOLDER_NAME, "UTF-8")).getStatusCode());
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.deleteSelectedItems();
      
      IDE.WORKSPACE.waitForItemNotPresent(WS_URL + FOLDER_NAME + "/");
      
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + URLEncoder.encode(FOLDER_NAME, "UTF-8") + "/");
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + URLEncoder.encode(FOLDER_NAME, "UTF-8"));
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
}
