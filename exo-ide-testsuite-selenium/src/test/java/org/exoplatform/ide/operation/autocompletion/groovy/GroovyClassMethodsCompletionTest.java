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
package org.exoplatform.ide.operation.autocompletion.groovy;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.core.CodeAssistant;
import org.junit.Test;

/**
 * Test to check, that autocomplete form
 * works correctly inside method in Groovy class.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 8, 2010 2:36:49 PM evgen $
 *
 */
public class GroovyClassMethodsCompletionTest extends BaseTest
{

   @Test
   public void testGroovyClassMethodCompletion() throws Exception
   {
      waitForRootElement();
      
      /*
       * 1. Open REST Service file.
       */
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      Thread.sleep(TestConstants.SLEEP);

      /*
       * 2. Go inside hello() method.
       */
      for (int i = 0; i < 9; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }
      selenium.keyDown(Locators.EDITOR_LOCATOR, "" + java.awt.event.KeyEvent.VK_END);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      
      /*
       * 3. Type text "Collections."
       */
     IDE.EDITOR.typeTextIntoEditor(0, "Collections.");

      /*
       * 4. Call autocomplete form.
       */
     IDE.EDITOR.runHotkeyWithinEditor(0, true, false, java.awt.event.KeyEvent.VK_SPACE);
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent(CodeAssistant.Locators.PANEL_ID));

      /*
       * 5. Type to the input field text "so".
       */
      selenium.focus(CodeAssistant.Locators.INPUT);
      IDE.CODEASSISTANT.typeToInput("so");

      /*
       * Check, that to elements are found:
       * sort(List):void
       * sort(List, Comparator):void
       */
      IDE.CODEASSISTANT.checkElementPresent("sort(List):void");
      IDE.CODEASSISTANT.checkElementPresent("sort(List, Comparator):void");

      /*
       * 6. Select sort(List, Comparator):void element
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      /*
       * 7. Press Enter to instert element info editor
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      /*
       * Check, that autocomplete form dissapeared, and new text in editor appeared.
       */
      assertFalse(selenium.isElementPresent(CodeAssistant.Locators.PANEL_ID));
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("Collections.sort(List, Comparator)"));
      
      /*
       * 8. Close file
       */
     IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
   }

}
