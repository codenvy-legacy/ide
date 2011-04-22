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
package org.exoplatform.ide.operation.browse;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 21, 2010 4:12:19 PM evgen $
 *
 */
// http://jira.exoplatform.org/browse/IDE-241
public class MoveCursorInEditBox extends BaseTest
{

   @Test
   public void testCursorNavigationInEditBox() throws Exception
   {
      waitForRootElement();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FOLDER);
      String locator =
         "//form[@id=\"ideCreateFolderFormDynamicForm\"]/div/input[@name=\"ideCreateFolderFormNameField\"]";
      selenium.focus(locator);

      IDE.EDITOR.deleteFileContent();
      selenium.typeKeys(locator, "FooBr");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_LEFT);
      selenium.typeKeys(locator, "a");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertEquals("FooBar", selenium.getValue(locator));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_HOME);

      selenium.typeKeys(locator, "eXo ");
      assertEquals("eXo FooBar", selenium.getValue(locator));

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);

      selenium.typeKeys(locator, " UA");
      assertEquals("eXo FooBar UA", selenium.getValue(locator));

   }

}
