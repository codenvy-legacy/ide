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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.BeforeClass;

import static org.junit.Assert.*;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2010 $
 *
 */
public abstract class LockFileAbstract extends BaseTest
{

   @BeforeClass
   public static void clearCookies()
   {
      deleteCookies();
   }

   /**
    * Check item is locked in browser tree
    * 
    * @param fileName
    * @param isLocked
    */
   protected void checkFileLocking(String fileName, boolean isLocked)
   {
      if (isLocked)
      {
         assertTrue(selenium.isElementPresent("//nobr[contains(text(),\""+fileName+"\")]/img[@id='resourceLocked']"));
//         assertTrue(selenium.isTextPresent(fileName + "   [ Locked ]"));
      }
      else
         //assertTrue(selenium.isElementPresent("//img[@id='resourceLocked']"));
      {
         assertFalse(selenium.isElementPresent("//nobr[contains(text(),\""+fileName+"\")]/img[@id='resourceLocked']"));
//         assertFalse(selenium.isTextPresent(fileName + "   [ Locked ]"));
      }
   }

   protected void deleteLockTokensCookies()
   {
      String cookieName= "eXo-IDE-" + USER_NAME + "-lock-tokens_map";
      if (selenium.isCookiePresent(cookieName))
      {
         selenium.deleteCookie(cookieName, "path=/, recurse=true");
      }
      else
      {
         fail("There no cookies with lock tokens!");
      }
   }

   protected void checkIsFileReadOnlyInEditorTab(String fileName)
   {
      assertTrue(selenium.isElementPresent("//div[@eventproxy='ideEditorFormTabSet_tabBar']//span[contains(text(), '"+fileName+"')]//img[@id='fileReadonly']"));
   }

   /**
    * @throws Exception
    * @throws InterruptedException
    */
   protected void checkCantSaveLockedFile(String fileName) throws Exception, InterruptedException
   {
      checkIsFileReadOnlyInEditorTab(fileName);
   
      IDE.editor().typeTextIntoEditor(0, "change dasda111");
   
      IDE.menu().checkCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE, false);
   }

}
