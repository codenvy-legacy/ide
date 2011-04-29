/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;

import static org.junit.Assert.fail;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenWith extends AbstractTestModule
{
   
   public void checkIsOpened() {
      fail();
   }
   
   public void checkIsOpened(boolean isOpened) {
      fail();
   }
   
   public void open() throws Exception {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);

      String locator = "//table[@id='ideOpenFileWithListGrid']";
      waitForElementPresent(locator);
   }

   /**
    * Open selected file with code mirror.
    * 
    * Method doesn't check is selected item in navigation tree is file.
    * It will fail, while calling "Open with" command.
    * 
    * @param checkDefault - is click on checkbox "Use by default"
    * @throws Exception
    */
   private void openSelectedFileWithCodeEditor(boolean checkDefault) throws Exception
   {
      IDE().MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_WITH);

      String locator = "//table[@id='ideOpenFileWithListGrid']";
      waitForElementPresent(locator);

      if (checkDefault)
      {
         //click on checkbox Use as default editor
         selenium().click("//span[@id='ideOpenFileWithDefaulCheckbox']/input");
         Thread.sleep(TestConstants.ANIMATION_PERIOD);
      }

      selenium().click("ideOpenFileWithOkButton");
      //time remaining to open editor
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
   }
   
   
}
