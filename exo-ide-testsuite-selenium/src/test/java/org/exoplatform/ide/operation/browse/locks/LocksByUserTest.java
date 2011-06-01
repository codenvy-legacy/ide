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
package org.exoplatform.ide.operation.browse.locks;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test that file, locked by another user, became unchangable
 * and marked by special icon in navigation tree.
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class LocksByUserTest extends LockFileAbstract
{

   private final static String FOLDER_NAME = LocksByUserTest.class.getSimpleName();

   private final static String FILE_NAME = "file-" + LocksByUserTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tierDown()
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

   @Test
   public void testLocksByUser() throws Exception
   {
      //fix for run tests where new session starts after 7 testcases passed  
      logout();
      standaloneLogin(TestConstants.Users.ROOT);
      
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      //----- 1 --------
      //open file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

      //----- 2 --------
      //lock file
      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);

      //----- 3 --------
      //logout
      logout();

      //----- 4 --------
      //login under another user
      standaloneLogin(TestConstants.Users.JOHN);
      IDE.WORKSPACE.waitForRootItem();

      //----- 5 --------
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      checkFileLocking(WS_URL + FOLDER_NAME + "/" + FILE_NAME, true);
      //open file
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

      checkCantSaveLockedFile();
   }

}
