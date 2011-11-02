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
package org.exoplatform.ide.operation.autocompletion;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionJavaScriptDuplicationTest extends BaseTest
{

   @Test
   public void testDuplication() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);

      //      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR,
      //         );
      IDE.EDITOR.typeTextIntoEditor(0, "var a;\n \n function a() {\n}");

      Thread.sleep(TestConstants.SLEEP);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ARROW_UP.toString());
      Thread.sleep(TestConstants.SLEEP);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.ARROW_UP.toString());
      Thread.sleep(TestConstants.SLEEP);

      IDE.CODEASSISTANT.openForm();
      Thread.sleep(TestConstants.SLEEP);
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      //      Thread.sleep(TestConstants.SLEEP);
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      //      Thread.sleep(TestConstants.SLEEP);
      //
      //      IDE.CODEASSISTANT.openForm();
      //      Thread.sleep(TestConstants.SLEEP);
      //
      //      IDE.CODEASSISTANT.checkElementPresent("a");
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);
      //
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      //      Thread.sleep(TestConstants.SLEEP_SHORT);
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      //      Thread.sleep(TestConstants.SLEEP_SHORT);
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      //
      //      IDE.CODEASSISTANT.openForm();
      //      Thread.sleep(TestConstants.SLEEP);
      //
      //      IDE.CODEASSISTANT.checkElementPresent("a()");
      //      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ESCAPE);

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      //      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

}
