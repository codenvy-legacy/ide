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

import static org.junit.Assert.assertTrue;

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
public class OpenLockedFileTest extends LockFileAbstract
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";
   
   private static final String FOLDER_NAME = "lockedFiles";
   
   private static final String FILE_NAME = "aldfnlaksfdbgjksdbkhgs";
   
   @Test
   public void testOpenLockedFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      createFolder(FOLDER_NAME);

      createFileFromToolbar(MenuCommands.New.GOOGLE_GADGET_FILE);

      saveAsByTopMenu(FILE_NAME);
      
      checkFileLocking(FILE_NAME, true);
      
      deleteLockTokensCookies();
      
      selenium.refresh();
      selenium.waitForPageToLoad("10000");
      Thread.sleep(TestConstants.SLEEP);
      
      checkCantSaveLockedFile();
      
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.GO_TO_FOLDER);
      Thread.sleep(TestConstants.SLEEP);
      
      closeUnsavedFileAndDoNotSave("0");
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      checkCantSaveLockedFile();
      
      closeUnsavedFileAndDoNotSave("0");
      
   }

   /**
    * @throws Exception
    * @throws InterruptedException
    */
   private void checkCantSaveLockedFile() throws Exception, InterruptedException
   {
      checkIsFileReadOnlyInEditorTab(FILE_NAME);
      
      typeTextIntoEditor(0, "change content !!11");
      
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/body/"));
      
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
   }
   
   @AfterClass
   public static void tierDown()
   {
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
