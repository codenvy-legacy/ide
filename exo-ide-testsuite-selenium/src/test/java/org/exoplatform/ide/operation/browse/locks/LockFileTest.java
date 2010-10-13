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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2010 $
 *
 */
public class LockFileTest extends LockFileAbstract
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static String FOLDER_NAME = "locks";

   private static String FILE_NAME = "sdfsdfsdafsdag";

   @Test
   public void testLockFile() throws Exception
   {
      
      Thread.sleep(TestConstants.SLEEP);
      createFolder(FOLDER_NAME);

      runCommandFromMenuNewOnToolbar(MenuCommands.New.REST_SERVICE_FILE);

      saveAsByTopMenu(FILE_NAME);

      checkFileLocking(FILE_NAME, false);

      assertElementPresentInWorkspaceTree(FILE_NAME);

      closeTab("0");

      checkFileLocking(FILE_NAME, false);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      checkFileLocking(FILE_NAME, false);
      
      typeTextIntoEditor(0, "Test test test");
      
      saveCurrentFile();
      
      closeTab("0");
      
      checkFileLocking(FILE_NAME, false);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      selenium.refresh();
      selenium.waitForPageToLoad("10000");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      checkIsTabPresentInEditorTabset(FILE_NAME, true);
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      openOrCloseFolder(FOLDER_NAME);
      Thread.sleep(TestConstants.SLEEP);
            
      checkFileLocking(FILE_NAME, false);
      
      typeTextIntoEditor(0, "go go go test");
      
      saveCurrentFile();
      
      closeTab("0");
      
      checkFileLocking(FILE_NAME, false);
      
   }

   @AfterClass
   public static void tierDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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
