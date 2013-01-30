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
import java.util.Map;
import java.util.Set;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;

/**
 * Check, that can open locked file only in read-only mode.
 * 
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2010 $
 * 
 */
public class OpenLockedFileTest extends BaseTest
{
   private static final String PROJECT = OpenLockedFileTest.class.getSimpleName();

   private static final String FOLDER_NAME = OpenLockedFileTest.class.getSimpleName();

   static final String FILE_NAME = "file-" + OpenLockedFileTest.class.getSimpleName();

   @Before
   public void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/browse/locks/test.html";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, WS_URL + PROJECT + "/" + FOLDER_NAME + "/"
            + FILE_NAME);
      }
      catch (IOException e)
      {
      }
   }

   @After
   public void tearDown()
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
   public void testOpenLockedFile() throws Exception
   {
      // step 1 open and lock file. Check state of the file
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();

      IDE.TOOLBAR.runCommand(ToolbarCommands.Editor.LOCK_FILE);
      IDE.LOADER.waitClosed();
      IDE.LOCK_FILE.waitUnLockCommandActive();

      // step 2 delete lock Cookies and check states buttons and files after
      // delete
      deleteLockTokensCookies();
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.clickOpenCloseButton(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.selectTab("File opened in read only mode. Use SaveAs command.");
      IDE.EDITOR.waitActiveFile();
      IDE.LOCK_FILE.waitLockCommandNotActive();
      IDE.MENU.clickOnCommand(MenuCommands.Edit.EDIT_MENU);
      IDE.MENU.clickOnLockLayer();
      IDE.LOCK_FILE.waitLockIconOnTabView(1);
      IDE.LOCK_FILE.waitLockIconViewOnFileInProjecrExplorer(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
   }

   private void deleteLockTokensCookies()
   {
      Set<Cookie> allCookies = driver.manage().getCookies();
      for (Cookie loadedCookie : allCookies)
      {
         if (loadedCookie.getName().contains("lock"))
         {
            driver.manage().deleteCookieNamed(loadedCookie.getName());
         }
      }
   }

}
